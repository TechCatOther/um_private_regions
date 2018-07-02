package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import org.ultramine.gui.GuiScreenToGui;
import org.ultramine.mods.privreg.gui.GuiBlockRegion;
import org.ultramine.mods.privreg.gui.GuiRent;
import org.ultramine.mods.privreg.render.DistanceControlGuiRender;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketGuiMessage extends UMPacket
{
	private int id;

	public PacketGuiMessage()
	{
	}

	public PacketGuiMessage(int id)
	{
		this.id = id;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeInt(id);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		id = buf.readInt();
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if(gui instanceof GuiScreenToGui)
		{
			GuiScreenToGui gui1 = (GuiScreenToGui) gui;
			if(gui1.gui instanceof GuiBlockRegion)
				((GuiBlockRegion)gui1.gui).acceptMessage(id);
			else if(gui1.gui instanceof GuiRent)
				((GuiRent)gui1.gui).acceptMessage(id);
			else
				DistanceControlGuiRender.acceptMessage(id);
		}
	}
}
