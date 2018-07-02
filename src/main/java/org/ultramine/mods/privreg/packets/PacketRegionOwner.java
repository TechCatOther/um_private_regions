package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.iterator.TIntIterator;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.Action;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.owner.BasicOwner;
import org.ultramine.mods.privreg.owner.RightRegistry;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.network.UMPacket;
import org.ultramine.server.util.InventoryUtil;

import java.io.IOException;

public class PacketRegionOwner extends UMPacket
{
	private static final Logger log = LogManager.getLogger();

	private int id;
	private BasicOwner owner;
	private Action action;

	public PacketRegionOwner(){}
	public PacketRegionOwner(Region region, BasicOwner owner, Action action)
	{
		this.id = region.getID();
		this.owner = owner;
		this.action = action;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeInt(id);
		owner.write(buf);
		buf.writeByte(action.ordinal());
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		id = buf.readInt();
		owner = BasicOwner.read(buf);
		action = Action.getAction(buf.readByte());
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		Region region = RegionManagerClient.getInstance().getRegion(id);
		if(region == null)
			return;
		boolean changed = false;
		switch(action)
		{
			case ADD:
				changed = region.getOwnerStorage().add(owner);
				break;
			case REMOVE:
				changed = region.getOwnerStorage().remove(owner.getProfile());
				break;
			case UPDATE:
				changed = region.getOwnerStorage().update(owner);
				break;
		}
		if(changed)
			region.setChanged(true);
	}

	public void processServer(NetHandlerPlayServer net)
	{
		Region region = PrivateRegions.instance().getServerRegion(net.playerEntity.dimension, id);
		if(region == null)
		{
			log.warn("Received PacketRegionOwner for undefined region ID: {}", id);
			return;
		}
		if(!region.hasRight(net.playerEntity.getGameProfile(), RegionRights.EDIT_USERS))
		{
			log.warn("Received PacketRegionOwner from user without edit rights: {}", net.playerEntity.getGameProfile());
			return;
		}
		boolean changed = false;
		switch(action)
		{
			case ADD:
				//changed = region.getOwnerStorage().add(owner);
				throw new RuntimeException();
			case REMOVE:
				if(region.getOwnerStorage().remove(owner.getProfile()))
				{
					changed = true;
					ItemStack card = new ItemStack(InitCommon.biocard);
					InitCommon.biocard.setProfile(card, owner.getProfile());
					InventoryUtil.addItem(net.playerEntity, card);
				}
				break;
			case UPDATE:
				for(TIntIterator it = owner.getRights().iterator(); it.hasNext();)
				{
					int id = it.next();
					if(RightRegistry.getRightByID(id) == null)
						throw new RuntimeException("Unknown owner right id: "+id);
				}
				changed = region.getOwnerStorage().update(owner);
				break;
		}
		if(changed)
		{
			region.setChanged(true);
			region.sendToListeners(this);
		}
	}
}
