package org.ultramine.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class SPacketNetworkList extends Packet
{
	private byte[] bytes;
	@SideOnly(Side.CLIENT)
	private String[] readed;
	
	public SPacketNetworkList(){}
	public SPacketNetworkList(byte[] bytes)
	{
		this.bytes = bytes;
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException
	{
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}
	
	@Override
	public void readPacketData(PacketBuffer buf) throws IOException
	{
		int size = buf.readInt();
		bytes = new byte[size];
		buf.readBytes(bytes);
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
		int count = data.read();
		readed = new String[count];
		for(int i = 0; i < count; i++)
			readed[i] = data.readUTF();
		
		//Если не обработать при чтении, то следующие пакеты будут прочитаны только после обработки этого.
		UMNetworkRegistry.acceptServerList(readed);
	}

	@Override
	public void processPacket(INetHandler net)
	{
		System.out.println("Client handled");
	}
}
