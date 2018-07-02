package org.ultramine.mods.privreg.packets;

import net.minecraft.network.PacketBuffer;
import org.ultramine.network.TEPacket;

import java.io.IOException;

public class PacketChargerAction extends TEPacket
{
	public int amount;

	public PacketChargerAction()
	{
	}

	public PacketChargerAction(int charge)
	{
		this.amount = charge;
	}

	@Override
	public void readPacketData(PacketBuffer data) throws IOException
	{
		amount = data.readInt();
	}

	@Override
	public void writePacketData(PacketBuffer data) throws IOException
	{
		data.writeInt(amount);
	}

}
