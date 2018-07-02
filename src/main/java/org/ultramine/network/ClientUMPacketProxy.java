package org.ultramine.network;

import java.io.IOException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class ClientUMPacketProxy extends Packet
{
	private UMPacket packet;
	
	public ClientUMPacketProxy(){}
	public ClientUMPacketProxy(UMPacket packet)
	{
		this.packet = packet;
	}
	
	@Override
	public void writePacketData(PacketBuffer buf) throws IOException
	{
		buf.writeByte((byte)UMNetworkRegistry.getPacketID(packet));
		packet.write(buf);
	}
	
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException
	{
		int id = buf.readUnsignedByte();
		packet = UMNetworkRegistry.makePacketServer(id);
		packet.read(buf);
	}

	@Override
	public void processPacket(INetHandler net)
	{
		packet.processServer((NetHandlerPlayServer)net);
	}
}
