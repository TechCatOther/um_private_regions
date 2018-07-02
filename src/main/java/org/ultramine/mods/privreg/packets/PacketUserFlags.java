package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import org.ultramine.mods.privreg.ClientUtils;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketUserFlags extends UMPacket
{
	private int flags;

	public PacketUserFlags(){}
	public PacketUserFlags(EntityPlayerMP player)
	{
		flags = player.hasPermission(PrivateRegions.ADMIN_PERMISSION) ? 1 : 0;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeByte(flags);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		flags = buf.readByte();
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		ClientUtils.setAdmin(flags == 1);
	}
}
