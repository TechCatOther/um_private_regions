package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionChangeResult;
import org.ultramine.mods.privreg.regions.RegionManager;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketRegionExpand extends UMPacket
{
	private static final Logger log = LogManager.getLogger();

	private int id;
	private ForgeDirection dir;
	private int amount;

	public PacketRegionExpand()
	{
	}

	public PacketRegionExpand(Region region, ForgeDirection dir, int amount)
	{
		this.id = region.getID();
		this.dir = dir;
		this.amount = amount;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeInt(id);
		buf.writeByte(dir.ordinal());
		buf.writeInt(amount);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		id = buf.readInt();
		dir = ForgeDirection.getOrientation(buf.readByte());
		amount = buf.readInt();
	}

	public void processServer(NetHandlerPlayServer net)
	{
		RegionManager mgr = PrivateRegions.instance().getServerRegionManager(net.playerEntity.dimension);
		Region region = mgr == null ? null : mgr.getRegion(id);
		if(region == null)
		{
			log.warn("Received PacketRegionExpand for undefined region ID: {}", id);
			return;
		}

		if(region.hasRight(net.playerEntity.getGameProfile(), RegionRights.RESIZE))
		{
			RegionChangeResult res = mgr.expandRegion(region, dir, amount);
			new PacketGuiMessage(res.ordinal()).sendTo(net);
		}
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		RegionManagerClient.getInstance().receiveExpand(id, dir, amount);
	}
}
