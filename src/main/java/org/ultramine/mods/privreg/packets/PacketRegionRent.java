package org.ultramine.mods.privreg.packets;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.modules.RegionModuleRent;
import org.ultramine.mods.privreg.modules.RegionModuleRent.RentMode;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketRegionRent extends UMPacket
{
	private static final Logger log = LogManager.getLogger();

	private int id;
	private int hours;

	public PacketRegionRent(){}
	public PacketRegionRent(int id, int hours)
	{
		this.id = id;
		this.hours = hours;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeInt(id);
		buf.writeInt(hours);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		id = buf.readInt();
		hours = buf.readInt();
	}

	public void processServer(NetHandlerPlayServer net)
	{
		Region region = PrivateRegions.instance().getServerRegion(net.playerEntity.dimension, id);
		if(region == null)
		{
			log.warn("Received PacketRegionRent for undefined region ID: {}", id);
			return;
		}
		RegionModuleRent module = region.getModuleWithClass(RegionModuleRent.class);
		if(module == null)
		{
			log.warn("Received PacketRegionRent for region without RegionModuleRent module. ID: {}", id);
			return;
		}
		if(module.getMode() == RentMode.SELL && hours != -1)
		{
			log.warn("Received PacketRegionRent with hours != -1 for region with RentMode.SELL. ID: {}", id);
			return;
		}

		module.doRentOrSell(net.playerEntity, hours);
	}
}
