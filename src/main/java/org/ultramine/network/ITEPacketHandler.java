package org.ultramine.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;

public interface ITEPacketHandler<T extends TEPacket>
{
	@SideOnly(Side.CLIENT)
	void handlePacketClient(T pkt);
	
	void handlePacketServer(T pkt, EntityPlayerMP player);
}
