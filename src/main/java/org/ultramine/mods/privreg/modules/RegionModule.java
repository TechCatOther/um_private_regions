package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.Action;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.modules.GuiModuleSettings;
import org.ultramine.mods.privreg.packets.PacketRegionModule;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.regions.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ultramine.util.I18n.tlt;

public abstract class RegionModule
{
	private static final Logger log = LogManager.getLogger();

	protected Region region;
	protected RegionModulesRegistry.RegistryItem registryItem;

	protected RegionModule()
	{
		registryItem = RegionModulesRegistry.instance().getByModuleClass(this.getClass());
	}

	public List<String> getDisplayDesc()
	{
		List<String> desc = new ArrayList<String>();
		Collections.addAll(desc, StringUtils.splitByWholeSeparator(tlt("item.privreg.module."+registryItem.getName()+".desc"), "\\n"));
		addEnergyCostDescLine(desc);
		return desc;
	}

	protected void addEnergyCostDescLine(List<String> desc)
	{
		if(registryItem.getEnergyCost() > 0F)
			desc.add(tlt("item.um_privreg_module.desc1", registryItem.getEnergyCost()));
	}

	public final double countCost()
	{
		return countRawCost() * (1 - region.getDiscount());
	}

	public final int getRegistryId()
	{
		return registryItem.getId();
	}

	@SideOnly(Side.CLIENT)
	public GuiModuleSettings createGuiHandler(GuiRegionModules parent)
	{
		return null;
	}

	public final void saveToItemStack(ItemStack itemStack)
	{
		writeToNBT(getOrCreateItemStackNBT(itemStack));
	}

	public final PacketRegionModule createPacket(Action action)
	{
		if(region == null)
		{
			log.warn("cannot create packet for module without region");
			return null;
		}
		NBTTagCompound nbt = null;
		if (action != Action.REMOVE)
		{
			nbt = new NBTTagCompound();
			writeToNBT(nbt);
		}
		return new PacketRegionModule(action, region.getID(), getRegistryId(), nbt);
	}

	public static RegionModule wrapItemStack(ItemStack itemStack)
	{
		return createFromNBT(getOrCreateItemStackNBT(itemStack), itemStack.getItemDamage());
	}

	public static RegionModule createFromNBT(NBTTagCompound nbt, int registryId)
	{
		Class<? extends RegionModule> moduleClass = RegionModulesRegistry.instance().getById(registryId).getModuleClass();
		try
		{
			RegionModule module = moduleClass.newInstance();
			module.readFromNBT(nbt);
			return module;
		}
		catch (Exception e)
		{
			log.error("Failed to create instance of " + moduleClass.getSimpleName(), e);
			return null;
		}
	}

	public static NBTTagCompound getOrCreateItemStackNBT(ItemStack itemStack)
	{
		if (itemStack.stackTagCompound == null)
			itemStack.stackTagCompound = new NBTTagCompound();
		if (!itemStack.stackTagCompound.hasKey("module"))
			itemStack.stackTagCompound.setTag("module", new NBTTagCompound());
		return itemStack.stackTagCompound.getCompoundTag("module");
	}

	public void receiveSettingsServer(NBTTagCompound nbt, EntityPlayerMP player)
	{
		receiveSettings(nbt);
	}

	public void receiveSettings(NBTTagCompound nbt)
	{
		if (region == null)
		{
			log.error("module doesn't link to region");
			return;
		}

		readFromNBT(nbt);
		region.onModulesChange();
		if(region.isServer())
			region.sendToListeners(createPacket(Action.UPDATE));
	}

	public void onPlaceToRegion(Region region)
	{
		this.region = region;
	}

	public void onRemoveFromRegion()
	{
		region = null;
	}

	@SideOnly(Side.SERVER)
	public void onRegionActivate()
	{

	}

	@SideOnly(Side.SERVER)
	public void onRegionInactivate()
	{

	}

	@SideOnly(Side.SERVER)
	public void onAreaChanged(Rectangle last, ForgeDirection dir, int amount)
	{

	}

	@SideOnly(Side.SERVER)
	public void onRegionUpdate()
	{

	}

	public final boolean hasRegion()
	{
		return region != null;
	}

	protected double countRawCost()
	{
		return region.countBlocks() * registryItem.getEnergyCostPerBlock();
	}

	public final ItemStack toItemStack()
	{
		ItemStack is = new ItemStack(InitCommon.module, 1, getRegistryId());
		saveToItemStack(is);
		return is;
	}

//	protected final boolean isOp(String username)
//	{
//		if (!hasRegion())
//			return false;
//
//		if (region.isServer())
//			return CommonUtils.isOpServer(username);
//		else
//			return ClientUtils.isOpClient();
//	}

	protected void writeToNBT(NBTTagCompound nbt){}

	protected void readFromNBT(NBTTagCompound nbt){}
}
