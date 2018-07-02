package org.ultramine.mods.privreg.modules;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.ultramine.economy.Accounts;
import org.ultramine.economy.CurrencyRegistry;
import org.ultramine.economy.IHoldings;
import org.ultramine.mods.privreg.Action;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.modules.GuiModuleSettings;
import org.ultramine.mods.privreg.gui.modules.GuiRentModule;
import org.ultramine.mods.privreg.owner.BasicOwner;
import org.ultramine.mods.privreg.owner.RegionOwnerStorage;
import org.ultramine.mods.privreg.packets.PacketGuiMessage;
import org.ultramine.mods.privreg.packets.PacketRegionModule;
import org.ultramine.mods.privreg.packets.PacketRegionOwner;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionRights;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class RegionModuleRent extends RegionModule
{
	public static final UUID TENANT_OWNER_UUID = new UUID(0, 1);

	private BasicOwner tenantsRights = new BasicOwner(new GameProfile(TENANT_OWNER_UUID, "tenant"));
	private RentMode mode = RentMode.RENT;
	private String areaName;
	private double rentalFee;
	private double sellPrice;
	private int maxDays;
	private boolean allowMultiple;

	private Map<UUID, Tenant> tenants = new HashMap<UUID, Tenant>();

	public RegionModuleRent()
	{
		tenantsRights.setRight(RegionRights.ALL_RIGHTS, true);
	}

	public RentMode getMode()
	{
		return mode;
	}

	public void setMode(RentMode mode)
	{
		this.mode = mode;
	}

	public String getAreaName()
	{
		return areaName;
	}

	public void setAreaName(String areaName)
	{
		this.areaName = areaName;
	}

	public double getRentalFee()
	{
		return rentalFee;
	}

	public void setRentalFee(double rentalFee)
	{
		this.rentalFee = rentalFee;
	}

	public double getSellPrice()
	{
		return sellPrice;
	}

	public void setSellPrice(double sellPrice)
	{
		this.sellPrice = sellPrice;
	}

	public int getMaxDays()
	{
		return maxDays;
	}

	public void setMaxDays(int maxDays)
	{
		this.maxDays = maxDays;
	}

	public boolean isAllowMultiple()
	{
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple)
	{
		this.allowMultiple = allowMultiple;
	}

	public int getTenantsCount()
	{
		return tenants.size();
	}

	public Map<UUID, Tenant> getTenants()
	{
		return tenants;
	}

	public boolean hasTenant(GameProfile profile)
	{
		return tenants.containsKey(profile.getId());
	}

	@Override
	public void onPlaceToRegion(Region region)
	{
		super.onPlaceToRegion(region);
		if(mode == RentMode.RENT && region.isServer())
			addTenantOwners();
	}

	@Override
	public void onRemoveFromRegion()
	{
		if(region != null)
		{
			if(mode == RentMode.RENT && region.isServer())
				removeTenantOwners();
		}
		super.onRemoveFromRegion();
	}

	@SideOnly(Side.SERVER)
	private void addAsOwner(Tenant tnt)
	{
		Map<UUID, BasicOwner> orig = region.getOwnerStorage().getRawOwners();
		if(!orig.containsKey(tnt.getProfile().getId()) && tnt.getEndTime() > System.currentTimeMillis())
		{
			BasicOwner tntOwner = new BasicOwner(tnt.getProfile());
			tntOwner.getRights().addAll(tenantsRights.getRights());
			orig.put(tnt.getProfile().getId(), tntOwner);
			region.sendToListeners(new PacketRegionOwner(region, tntOwner, Action.ADD));
		}
	}

	@SideOnly(Side.SERVER)
	private void addTenantOwners()
	{
		Map<UUID, BasicOwner> orig = region.getOwnerStorage().getRawOwners();
		Map<UUID, BasicOwner> copy = new HashMap<UUID, BasicOwner>(orig);
		orig.clear();
		Iterator<Map.Entry<UUID, BasicOwner>> it = copy.entrySet().iterator();
		for(int i = 0; it.hasNext(); i++)
		{
			if(i == 1)
				orig.put(tenantsRights.getProfile().getId(), tenantsRights);
			Map.Entry<UUID, BasicOwner> ent = it.next();
			orig.put(ent.getKey(), ent.getValue());
		}

		region.sendToListeners(new PacketRegionOwner(region, tenantsRights, Action.ADD));
		for(Tenant tnt : tenants.values())
			addAsOwner(tnt);
		region.setChanged(true);
	}

	@SideOnly(Side.SERVER)
	private void removeTenantOwners()
	{
		region.getOwnerStorage().remove(tenantsRights.getProfile());
		for(Tenant tnt : tenants.values())
		{
			if(region.getOwnerStorage().remove(tnt.getProfile()))
				region.sendToListeners(new PacketRegionOwner(region, new BasicOwner(tnt.getProfile()), Action.REMOVE));
		}
	}

	@Override
	@SideOnly(Side.SERVER)
	public void receiveSettingsServer(NBTTagCompound nbt, EntityPlayerMP player)
	{
		RentMode lastMode = mode;
		nbt.removeTag("l");
		nbt.removeTag("t");
		super.receiveSettingsServer(nbt, player);
		if(lastMode != mode)
		{
			if(mode == RentMode.RENT)
				addTenantOwners();
			else
				removeTenantOwners();
		}
	}

	@Override
	@SideOnly(Side.SERVER)
	public void onRegionUpdate()
	{
		if(mode == RentMode.RENT)
			for(Iterator<Tenant> it = tenants.values().iterator(); it.hasNext();)
			{
				Tenant tnt = it.next();
				if(tnt.getEndTime() <= System.currentTimeMillis())
				{
					region.getOwnerStorage().remove(tnt.getProfile());
					it.remove();
				}
			}
	}

	@SideOnly(Side.SERVER)
	public void doRentOrSell(EntityPlayerMP player, int hours)
	{
		if(maxDays != 0 && hours > maxDays*24)
		{
			new PacketGuiMessage(2).sendTo(player);
			return;
		}
		IHoldings holds = Accounts.getPlayer(player.getGameProfile()).getHoldingsOf(CurrencyRegistry.GSC);
		double amount = mode == RentMode.RENT ? rentalFee*hours/24d : sellPrice;
		if(!holds.hasEnough(amount))
		{
			new PacketGuiMessage(0).sendTo(player);
			return;
		}

		if(mode == RentMode.RENT)
		{
			if(tenants.containsKey(player.getGameProfile().getId()))
			{
				Tenant tnt = tenants.get(player.getGameProfile().getId());
				long endTime = tnt.getEndTime() + 1000*60*60*hours;
				if((endTime-System.currentTimeMillis())/1000/60/60 > maxDays*24)
				{
					new PacketGuiMessage(2).sendTo(player);
					return;
				}
				tnt.setEndTime(endTime);
			}
			else
			{
				if(tenants.size() > 0 && !allowMultiple)
				{
					new PacketGuiMessage(1).sendTo(player);
					return;
				}
				long time = System.currentTimeMillis();
				Tenant tnt = new Tenant(player.getGameProfile(), time, time+1000*60*60*hours);
				tenants.put(tnt.getProfile().getId(), tnt);
				addAsOwner(tnt);
			}
			holds.subtract(amount);
		}
		else
		{
			for(Iterator<BasicOwner> it = region.getOwnerStorage().getRawOwners().values().iterator(); it.hasNext();)
			{
				BasicOwner owner = it.next();
				if(owner.getProfile().getId() != RegionOwnerStorage.DEFAULT_OWNER_UUID)
				{
					it.remove();
					region.sendToListeners(new PacketRegionOwner(region, owner, Action.REMOVE));
				}
			}
			BasicOwner owner = new BasicOwner(player.getGameProfile());
			owner.setRight(RegionRights.CREATOR, true);
			region.getOwnerStorage().add(owner);
			region.sendToListeners(new PacketRegionOwner(region, owner, Action.ADD));
			holds.subtract(amount);
		}

		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		region.sendToListeners(new PacketRegionModule(Action.UPDATE, region.getID(), getRegistryId(), nbt));
	}

	@Override
	protected void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setTag("t", tenantsRights.toNBT());

		nbt.setByte("md", mode == RentMode.SELL ? (byte)0 : (byte)1);
		nbt.setString("n", areaName);
		nbt.setDouble("f", rentalFee);
		nbt.setDouble("p", sellPrice);
		nbt.setInteger("d", maxDays);
		nbt.setBoolean("ml", allowMultiple);
		NBTTagList list = new NBTTagList();
		for(Tenant t : tenants.values())
			list.appendTag(t.toNBT());
		nbt.setTag("l", list);
	}

	@Override
	protected void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt.hasKey("t"))
			tenantsRights = BasicOwner.fromNBT(nbt.getCompoundTag("t"));

		mode = nbt.getByte("md") == 0 ? RentMode.SELL : RentMode.RENT;
		areaName = nbt.getString("n");
		rentalFee = nbt.getDouble("f");
		sellPrice = nbt.getDouble("p");
		maxDays = nbt.getInteger("d");
		allowMultiple = nbt.getBoolean("m");
		if(nbt.hasKey("l"))
		{
			NBTTagList list = nbt.getTagList("l", 10);
			for(int i = 0; i < list.tagCount(); i++)
			{
				Tenant tnt = Tenant.fromNBT(list.getCompoundTagAt(i));
				tenants.put(tnt.getProfile().getId(), tnt);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiModuleSettings createGuiHandler(GuiRegionModules parent)
	{
		return new GuiRentModule(parent, this);
	}

	public enum RentMode
	{
		SELL, RENT
	}

	public static class Tenant
	{
		private final GameProfile profile;
		private long startTime;
		private long endTime;

		public Tenant(GameProfile profile, long time, long endTime)
		{
			this.profile = profile;
			this.startTime = time;
			this.endTime = endTime;
		}

		public GameProfile getProfile()
		{
			return profile;
		}

		public long getStartTime()
		{
			return startTime;
		}

		public void setStartTime(long startTime)
		{
			this.startTime = startTime;
		}

		public long getEndTime()
		{
			return endTime;
		}

		public void setEndTime(long endTime)
		{
			this.endTime = endTime;
		}

		public NBTTagCompound toNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong("m", profile.getId().getMostSignificantBits());
			nbt.setLong("l", profile.getId().getLeastSignificantBits());
			nbt.setString("n", profile.getName());
			nbt.setLong("t", startTime);
			nbt.setLong("e", endTime);
			return nbt;
		}

		public static Tenant fromNBT(NBTTagCompound nbt)
		{
			GameProfile profile = new GameProfile(new UUID(nbt.getLong("m"), nbt.getLong("l")), nbt.getString("n"));
			return new Tenant(profile, nbt.getLong("t"), nbt.getLong("e"));
		}
	}
}
