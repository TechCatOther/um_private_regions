package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.PacketBuffer;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketIntWindowProperty extends UMPacket
{
	private int windowID;
	private int id;
	private int val;

	public PacketIntWindowProperty(){}
	public PacketIntWindowProperty(Container cont, int id, int val)
	{
		this.windowID = cont.windowId;
		this.id = id;
		this.val = val;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeByte(windowID);
		buf.writeByte(id);
		buf.writeInt(val);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		windowID = buf.readUnsignedByte();
		id = buf.readUnsignedByte();
		val = buf.readInt();
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		EntityClientPlayerMP entityclientplayermp = Minecraft.getMinecraft().thePlayer;
		if(entityclientplayermp.openContainer != null && entityclientplayermp.openContainer.windowId == windowID)
			entityclientplayermp.openContainer.updateProgressBar(id, val);
	}

	public void sendTo(ICrafting player)
	{
		if(player instanceof EntityPlayerMP)
			sendTo((EntityPlayerMP)player);
	}
}
