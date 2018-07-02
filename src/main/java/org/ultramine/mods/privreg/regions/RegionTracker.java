package org.ultramine.mods.privreg.regions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.ultramine.mods.privreg.packets.PacketRegionAction;
import org.ultramine.mods.privreg.packets.PacketRegionData;
import org.ultramine.network.UMPacket;

@SideOnly(Side.SERVER)
public class RegionTracker
{
	private final MinecraftServer server = MinecraftServer.getServer();
	private final RegionManager regMgr;

	public RegionTracker(RegionManager regMgr)
	{
		this.regMgr = regMgr;
	}

	public void onPlayerEnter(EntityPlayerMP player)
	{
		int dim = player.dimension;
		for(Region region : regMgr.unsafeGetRegions().values())
			if(region != null)
				new PacketRegionData(region).sendTo(player);
	}

	public void onPlayerLeave(EntityPlayerMP player)
	{
		new PacketRegionAction(PacketRegionAction.CLIENT_CLEAR_REGIONS).sendTo(player);
	}

	public void onRegionCreate(Region region)
	{
		sendToListeners(region, new PacketRegionData(region));
	}

	public void onRegionDestroy(Region region)
	{
		sendToListeners(region, new PacketRegionAction(region.getID(), PacketRegionAction.CLIENT_REMOVE));
	}

	public void sendToListeners(Region region, UMPacket packet)
	{
		packet.sendToAllInWorld(server.worldServerForDimension(region.getWorld()));
	}
}
