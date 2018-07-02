package org.ultramine.util;

import org.ultramine.gui.GuiScreenToGui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		
		if(te instanceof IHasGui)
		{
			return ((IHasGui)te).getGuiContainer(ID, (EntityPlayer)player);
		}
		else
		{
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
		
		if(te instanceof IHasGui)
		{
			return new GuiScreenToGui(((IHasGui)te).getGui(ID, (EntityPlayer)player));
		}
		else
		{
			return null;
		}
	}
}
