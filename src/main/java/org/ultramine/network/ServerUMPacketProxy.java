package org.ultramine.network;

import java.io.IOException;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class ServerUMPacketProxy extends Packet
{
	private UMPacket packet;
	
	public ServerUMPacketProxy(){}
	public ServerUMPacketProxy(UMPacket packet)
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
		packet = UMNetworkRegistry.makePacketClient(id);
		packet.read(buf);
	}

	@Override
	public void processPacket(INetHandler net)
	{
		packet.processClient((NetHandlerPlayClient)net);
	}
}
