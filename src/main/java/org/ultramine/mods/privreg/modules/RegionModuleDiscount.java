package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.ultramine.mods.privreg.ClientUtils;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.modules.GuiModuleDiscount;
import org.ultramine.mods.privreg.gui.modules.GuiModuleSettings;

import java.util.List;

public class RegionModuleDiscount extends RegionModule
{
	private int discount;
	private String comment;

	public int getDiscount()
	{
		return discount;
	}

	public void setDiscount(int discount)
	{
		this.discount = discount % 101;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	@Override
	public void receiveSettingsServer(NBTTagCompound nbt, EntityPlayerMP player)
	{
		if(!player.hasPermission(PrivateRegions.ADMIN_PERMISSION))
			return;
		super.receiveSettingsServer(nbt, player);
	}

	@Override
	public List<String> getDisplayDesc()
	{
		List<String> desc = super.getDisplayDesc();
		desc.set(0, desc.get(0) + " \u00a7c" + getDiscount() + "%");
		return desc;
	}

	@Override
	protected void addEnergyCostDescLine(List<String> desc)
	{

	}

	@Override
	protected void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setByte("d", (byte)discount);
		nbt.setString("c", comment);
	}

	@Override
	protected void readFromNBT(NBTTagCompound nbt)
	{
		discount = nbt.getByte("d");
		comment = nbt.getString("c");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiModuleSettings createGuiHandler(GuiRegionModules parent)
	{
		if(!ClientUtils.isAdminClient())
			return null;
		return new GuiModuleDiscount(parent, this);
	}
}
