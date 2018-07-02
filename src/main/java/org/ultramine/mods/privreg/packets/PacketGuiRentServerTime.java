package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import org.ultramine.gui.GuiScreenToGui;
import org.ultramine.mods.privreg.gui.GuiRent;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketGuiRentServerTime extends UMPacket
{
	private long time;

	public PacketGuiRentServerTime(){}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeLong(System.currentTimeMillis());
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		time = buf.readLong();
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if(gui instanceof GuiScreenToGui)
		{
			GuiScreenToGui gui1 = (GuiScreenToGui) gui;
			if(gui1.gui instanceof GuiRent)
				((GuiRent) gui1.gui).acceptServerTime(time);
		}
	}
}
