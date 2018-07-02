package org.ultramine.mods.privreg.tiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import org.ultramine.gui.GuiScreenToGui;
import org.ultramine.gui.IGui;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.gui.GuiRent;
import org.ultramine.mods.privreg.gui.inv.ContainerRent;
import org.ultramine.mods.privreg.modules.RegionModuleRent;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.regions.IRegionManager;
import org.ultramine.util.IHasGui;

import static net.minecraft.util.EnumChatFormatting.RED;

public class TileRentStand extends TileEntity implements IHasGui
{
	private Region findRegion()
	{
		int x = xCoord;
		int z = zCoord;
		switch(getBlockMetadata())
		{
			case 0: z++; break;
			case 1: x--; break;
			case 2: z--; break;
			case 3: x++; break;
		}
		IRegionManager mgr = worldObj.isRemote ? RegionManagerClient.getInstance() : PrivateRegions.instance().getServerRegionManager(worldObj.provider.dimensionId);
		return mgr.getRegion(x, yCoord, z);
	}

	public Region getRegion()
	{
		Region region = findRegion();
		if(region != null && region.hasModule(RegionModuleRent.class))
			return region;
		return null;
	}

	public void onBlockPlacedBy(EntityLivingBase entity) //Server side only
	{
		if(!entity.isEntityPlayerMP())
		{
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			return;
		}
		EntityPlayerMP player = (EntityPlayerMP) entity;
		Region region = findRegion();
		if(region == null || !region.hasRight(player.getGameProfile(), RegionRights.EDIT_USERS) || !region.hasModule(RegionModuleRent.class))
		{
			worldObj.func_147480_a(xCoord, yCoord, zCoord, true);
			player.addChatMessage(new ChatComponentTranslation("Регион не обнаружен, вам не принадлежит или не сдается в аренду").setChatStyle(new ChatStyle().setColor(RED)));
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean activateClient(EntityPlayer player)
	{
		return true;
	}

	@SideOnly(Side.SERVER)
	public boolean activateServer(EntityPlayer player)
	{
		checkState();
		player.openGui(PrivateRegions.instance(), 0, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@SideOnly(Side.SERVER)
	private void checkState()
	{
		Region region = findRegion();
		if(region == null || !region.hasModule(RegionModuleRent.class))
		{
			worldObj.func_147480_a(xCoord, yCoord, zCoord, true);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IGui getGui(int id, EntityPlayer player)
	{
		return new GuiRent(getRegion());
	}

	@Override
	public Container getGuiContainer(int id, EntityPlayer player)
	{
		return new ContainerRent();
	}
}
