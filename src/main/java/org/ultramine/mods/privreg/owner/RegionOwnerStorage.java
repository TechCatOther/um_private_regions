package org.ultramine.mods.privreg.owner;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import org.ultramine.mods.privreg.ClientUtils;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.server.PermissionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionOwnerStorage
{
	private static final boolean IS_SERVER = FMLCommonHandler.instance().getSide().isServer();
	public static final UUID DEFAULT_OWNER_UUID = new UUID(0, 0);

	protected final Map<UUID, BasicOwner> owners = new LinkedHashMap<UUID, BasicOwner>();
	protected BasicOwner defaultOwner = new BasicOwner(new GameProfile(DEFAULT_OWNER_UUID, "default"));
	@SideOnly(Side.CLIENT)
	private UUID clientID;

	public Region region;
	private final List<OwnerRight> editableRights = new ArrayList<OwnerRight>();

	public RegionOwnerStorage(Region region)
	{
		this.region = region;
	}

	public boolean isOwner(GameProfile profile)
	{
		return owners.containsKey(profile.getId());
	}

	public BasicOwner getOwner(GameProfile profile)
	{
		return owners.get(profile.getId());
	}

	public Map<UUID, BasicOwner> getRawOwners()
	{
		return owners;
	}

	public boolean add(BasicOwner owner)
	{
		if(isOwner(owner.getProfile()))
			return false;
		owners.put(owner.getProfile().getId(), owner);
		return true;
	}

	public boolean add(GameProfile profile)
	{
		return add(new BasicOwner(profile));
	}

	public boolean remove(GameProfile profile)
	{
		return owners.remove(profile.getId()) != null;
	}

	public boolean update(BasicOwner owner)
	{
		boolean isDefaultOwner = owner.getProfile().getId().equals(DEFAULT_OWNER_UUID);
		if(!isDefaultOwner && !isOwner(owner.getProfile()))
			return false;
		if(isDefaultOwner)
			defaultOwner = owner;
		else
			owners.put(owner.getProfile().getId(), owner);
		return true;
	}

	public boolean hasRight(GameProfile profile, OwnerRight right)
	{
		if(IS_SERVER && PermissionHandler.getInstance().hasGlobally(profile.getName(), PrivateRegions.ADMIN_PERMISSION))
			return true;
		if(region.hasParent() && region.getParent() instanceof Region)
			if(((Region)region.getParent()).hasRight(profile, right))
				return true;
		BasicOwner owner = owners.get(profile.getId());
		if(owner == null)
			owner = defaultOwner;
		return owner != null && (owner.hasRight(RegionRights.CREATOR) || (owner.hasRight(RegionRights.ALL_RIGHTS) && right != RegionRights.CREATOR) || owner.hasRight(right));
	}

	public boolean setOwnersRight(GameProfile profile, OwnerRight right, boolean value)
	{
		BasicOwner owner = owners.get(profile.getId());
		return owner != null && setOwnersRight(owners.get(profile.getId()), right, value);
	}

	protected boolean setOwnersRight(BasicOwner owner, OwnerRight right, boolean value)
	{
		owner.setRight(right, value);
		return true;
	}

	public Collection<GameProfile> getAllNames()
	{
		List<GameProfile> list = new ArrayList<GameProfile>(owners.size());
		for(BasicOwner owner : owners.values())
			list.add(owner.getProfile());
		return list;
	}

	public void registerRight(OwnerRight right, boolean status)
	{
		if (status)
			editableRights.add(right);
		else
			editableRights.remove(right);
	}

	@SideOnly(Side.CLIENT)
	public boolean hasRightClient(OwnerRight right)
	{
		if(ClientUtils.isAdminClient())
			return true;
		if(region.hasParent() && region.getParent() instanceof Region)
			if(((Region)region.getParent()).getOwnerStorage().hasRightClient(right))
				return true;
		if(clientID == null)
			clientID = Minecraft.getMinecraft().getSession().func_148256_e().getId();
		BasicOwner owner = owners.get(clientID);
		if(owner == null)
			owner = defaultOwner;
		return owner != null && (owner.hasRight(RegionRights.CREATOR) || (owner.hasRight(RegionRights.ALL_RIGHTS) && right != RegionRights.CREATOR) || owner.hasRight(right));
	}

	@SideOnly(Side.CLIENT)
	public boolean isOwnerClient()
	{
		if(clientID == null)
			clientID = Minecraft.getMinecraft().getSession().func_148256_e().getId();
		return owners.containsKey(clientID);
	}

	@SideOnly(Side.CLIENT)
	public boolean canEditClient(GameProfile profile)
	{
		if(ClientUtils.isAdminClient())
			return true;
		BasicOwner owner = owners.get(profile.getId());
		return owner != null && hasRightClient(RegionRights.EDIT_USERS) && !owner.hasRight(RegionRights.CREATOR);
	}

	@SideOnly(Side.CLIENT)
	public Collection<OwnerRight> getSupportedRights()
	{
		List<OwnerRight> rights = new ArrayList<OwnerRight>(RightRegistry.getRightCount());
		if(ClientUtils.isAdminClient())
			rights.add(RegionRights.CREATOR);

		rights.add(RegionRights.ALL_RIGHTS);
		rights.add(RegionRights.OPEN_BLOCK);
		rights.add(RegionRights.EDIT_MODULES);
		rights.add(RegionRights.EDIT_USERS);
		rights.add(RegionRights.RESIZE);
		rights.add(RegionRights.PLACE_SUBREGIONS);

		rights.addAll(editableRights);

		return rights;
	}

	public NBTTagCompound toNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("d", defaultOwner.toNBT());
		NBTTagList list = new NBTTagList();
		for(BasicOwner owner : owners.values())
			if(owner != defaultOwner)
				list.appendTag(owner.toNBT());
		nbt.setTag("l", list);
		return nbt;
	}

	public void fromNBT(NBTTagCompound nbt)
	{
		owners.clear();
		add(defaultOwner = BasicOwner.fromNBT(nbt.getCompoundTag("d")));
		NBTTagList list = nbt.getTagList("l", 10);
		for(int i = 0; i < list.tagCount(); i++)
			add(BasicOwner.fromNBT(list.getCompoundTagAt(i)));
	}

	public void write(PacketBuffer buf) throws IOException
	{
		defaultOwner.write(buf);
		buf.writeByte(owners.size());
		for(BasicOwner owner : owners.values())
			owner.write(buf);
	}

	public void read(PacketBuffer buf) throws IOException
	{
		owners.clear();
		defaultOwner = BasicOwner.read(buf);
		add(defaultOwner);
		int size = buf.readByte();
		for(int i = 0; i < size; i++)
			add(BasicOwner.read(buf));
	}
}
