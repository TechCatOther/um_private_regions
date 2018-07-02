package org.ultramine.network;

import java.io.IOException;

import cpw.mods.fml.common.functions.GenericIterableFactory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.management.PlayerManager.PlayerInstance;
import net.minecraft.world.WorldServer;

public abstract class UMPacket
{
	public abstract void write(PacketBuffer buf) throws IOException;
	
	public abstract void read(PacketBuffer buf) throws IOException;
	
	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		throw new UnsupportedOperationException("Client side not implement");
	}
	
	public void processServer(NetHandlerPlayServer net)
	{
		throw new UnsupportedOperationException("Server side not implement");
	}
	
	@SideOnly(Side.CLIENT)
	public Packet toClientPacket()
	{
		return new ClientUMPacketProxy(this);
	}
	
	public Packet toServerPacket()
	{
		return new ServerUMPacketProxy(this);
	}
	
	@SideOnly(Side.CLIENT)
	public void sendToServer()
	{
		Minecraft.getMinecraft().getNetHandler().addToSendQueue(toClientPacket());
	}
	
	public void sendTo(EntityPlayerMP player)
	{
		player.playerNetServerHandler.netManager.scheduleOutboundPacket(toServerPacket());
	}
	
	public void sendTo(NetHandlerPlayServer net)
	{
		net.netManager.scheduleOutboundPacket(toServerPacket());
	}
	
	public void sendTo(NetworkManager net)
	{
		net.scheduleOutboundPacket(toServerPacket());
	}
	
	@SideOnly(Side.CLIENT)
	public void sendTo(NetHandlerPlayClient net)
	{
		net.addToSendQueue(toClientPacket());
	}
	
	public void sendToAllInWorld(WorldServer world)
	{
		Packet packet = toServerPacket();
		
		for(Object player : world.playerEntities)
		{
			((EntityPlayerMP)player).playerNetServerHandler.netManager.scheduleOutboundPacket(packet);
		}
	}
	
	public void sendToAllWatchingChunk(WorldServer world, int cx, int cz)
	{
		Packet packet = toServerPacket();
		PlayerInstance pi = world.getPlayerManager().getOrCreateChunkWatcher(cx, cz, false);
		if(pi != null)
			pi.sendToAllPlayersWatchingChunk(packet);
	}
}
