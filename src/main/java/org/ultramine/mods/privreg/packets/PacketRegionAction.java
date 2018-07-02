package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.mods.privreg.render.ClientSelectionRenderer;
import org.ultramine.mods.privreg.tiles.TileBlockRegion;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketRegionAction extends UMPacket
{
	private static final Logger log = LogManager.getLogger();
	public static final int CLIENT_REMOVE = 0;
	public static final int CLIENT_RENDER = 3;
	public static final int CLIENT_SET_CHARGED = 4;
	public static final int CLIENT_SET_DISCHARGED = 5;

	public static final int CLIENT_CLEAR_REGIONS = -1;
	public static final int CLIENT_CLEAR_RENDER = -2;

	public static final int SERVER_OPEN_MODULES = 3;
	public static final int SERVER_OPEN_MAIN = 4;
	public static final int SERVER_DISMANTLE = 5;

	private int id;
	private int action;

	public PacketRegionAction(){}
	public PacketRegionAction(int id, int action)
	{
		this.id = id;
		this.action = action;
	}

	public PacketRegionAction(int action)
	{
		if(action >= 0)
			throw new IllegalArgumentException();
		this.id = action;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeInt(id);
		buf.writeByte(action);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		id = buf.readInt();
		action = buf.readUnsignedByte();
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		if(id < 0)
		{
			switch(id)
			{
				case CLIENT_CLEAR_REGIONS:
					RegionManagerClient.getInstance().clearRegions();
					break;
				case CLIENT_CLEAR_RENDER:
					ClientSelectionRenderer.clear();
					break;
				default:
					throw new RuntimeException("Unknown action: "+id);
			}
			return;
		}
		Region region = RegionManagerClient.getInstance().getRegion(id);
		if(region == null)
		{
			log.warn("Received PacketRegionAction for undefined region ID: {}", id);
			return;
		}
		switch(action)
		{
			case CLIENT_REMOVE:
				RegionManagerClient.getInstance().destroyRegion(region);
				break;
			case CLIENT_RENDER:
				ClientSelectionRenderer.toggleRender(id);
				break;
			case CLIENT_SET_CHARGED:
				region.setCharge(1);
				break;
			case CLIENT_SET_DISCHARGED:
				region.setCharge(0);
				break;
			default:
				throw new RuntimeException("Unknown action: "+action);
		}
	}

	public void processServer(NetHandlerPlayServer net)
	{
		Region region = PrivateRegions.instance().getServerRegion(net.playerEntity.dimension, id);
		if(region == null)
		{
			log.warn("Received PacketRegionAction for undefined region ID: {}", id);
			return;
		}
		EntityPlayerMP player = net.playerEntity;
		switch(action)
		{
			case SERVER_OPEN_MODULES:
				player.openGui(PrivateRegions.instance(), TileBlockRegion.GUI_MODULES_ID, player.worldObj, region.getBlock().x, region.getBlock().y, region.getBlock().z);
				return;
			case SERVER_OPEN_MAIN:
				player.openGui(PrivateRegions.instance(), TileBlockRegion.GUI_MAIN_ID, player.worldObj, region.getBlock().x, region.getBlock().y, region.getBlock().z);
				return;
			case SERVER_DISMANTLE:
				if(region.hasRight(player.getGameProfile(), RegionRights.CREATOR))
					MinecraftServer.getServer().worldServerForDimension(region.getWorld()).func_147480_a(region.getBlock().x, region.getBlock().y, region.getBlock().z, true);
				return;
			default:
				throw new RuntimeException("Unknown action: "+action);
		}
	}
}
