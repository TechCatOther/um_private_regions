package org.ultramine.mods.privreg.packets;

import net.minecraft.network.PacketBuffer;
import org.ultramine.network.TEPacket;

import java.io.IOException;

public class PacketTEBlockRegion extends TEPacket
{
	private int id;

	public PacketTEBlockRegion(){}
	public PacketTEBlockRegion(int id)
	{
		this.id = id;
	}

	public int getRegionID()
	{
		return id;
	}

	@Override
	public void writePacketData(PacketBuffer buf) throws IOException
	{
		buf.writeInt(id);
	}

	@Override
	public void readPacketData(PacketBuffer buf) throws IOException
	{
		id = buf.readInt();
	}
}
