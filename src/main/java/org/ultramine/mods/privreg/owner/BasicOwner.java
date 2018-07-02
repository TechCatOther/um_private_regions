package org.ultramine.mods.privreg.owner;

import com.mojang.authlib.GameProfile;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

public class BasicOwner
{
	private final TIntSet rights = new TIntHashSet();
	private final GameProfile profile;

	public BasicOwner(GameProfile profile)
	{
		this.profile = profile;
	}

	public GameProfile getProfile()
	{
		return profile;
	}

	public TIntSet getRights()
	{
		return rights;
	}

	public boolean hasRight(OwnerRight right)
	{
		return rights.contains(right.getID());
	}

	public void setRight(OwnerRight right, boolean value)
	{
		if (value)
			rights.add(right.getID());
		else
			rights.remove(right.getID());
	}

	public NBTTagCompound toNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("n", profile.getName());
		nbt.setLong("m", profile.getId().getMostSignificantBits());
		nbt.setLong("l", profile.getId().getLeastSignificantBits());
		NBTTagList list = new NBTTagList();
		for(TIntIterator it = rights.iterator(); it.hasNext();)
		{
			OwnerRight right = RightRegistry.getRightByID(it.next());
			list.appendTag(new NBTTagString(right.getKey()));
		}
		nbt.setTag("r", list);
		return nbt;
	}

	public static BasicOwner fromNBT(NBTTagCompound nbt)
	{
		BasicOwner bo = new BasicOwner(new GameProfile(new UUID(nbt.getLong("m"), nbt.getLong("l")), nbt.getString("n")));
		NBTTagList list = nbt.getTagList("r", 8);
		for(int i = 0; i < list.tagCount(); i++)
		{
			OwnerRight right = RightRegistry.getRightByKey(list.getStringTagAt(i));
			if(right != null)
				bo.rights.add(right.getID());
		}
		return bo;
	}

	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeLong(profile.getId().getMostSignificantBits());
		buf.writeLong(profile.getId().getLeastSignificantBits());
		buf.writeStringToBuffer(profile.getName());
		buf.writeByte(rights.size());
		for(TIntIterator it = rights.iterator(); it.hasNext();)
			buf.writeByte(it.next());
	}

	public static BasicOwner read(PacketBuffer buf) throws IOException
	{
		BasicOwner bo = new BasicOwner(new GameProfile(new UUID(buf.readLong(), buf.readLong()), buf.readStringFromBuffer(16)));
		int size = buf.readByte();
		for(int i = 0; i < size; i++)
			bo.rights.add(buf.readByte());
		return bo;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof BasicOwner && ((BasicOwner)o).profile.getId().equals(profile.getId());
	}

	@Override
	public int hashCode()
	{
		return profile.getId().hashCode();
	}
}
