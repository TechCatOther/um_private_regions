package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.ultramine.mods.privreg.modules.RegionModulesStorage;
import org.ultramine.mods.privreg.owner.RegionOwnerStorage;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.network.UMPacket;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.Rectangle;

import java.io.IOException;

public class PacketRegionData extends UMPacket
{
	private int id;
	private BlockPos blockCoord;
	private Rectangle shape;
	private int parentID;
	private double charge;
	private RegionOwnerStorage ros; //TODO
	private NBTTagCompound modules;

	public PacketRegionData(){}
	public PacketRegionData(Region region)
	{
		this.id = region.getID();
		this.blockCoord = region.getBlock();
		this.shape = region.getShape();
		this.parentID = region.hasParent() ? region.getParent().getID() : -1;
		this.charge = region.getCharge();
		ros = region.getOwnerStorage();
		this.modules = region.getModulesStorage().toNBT();
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeInt(id);
		blockCoord.write(buf);
		shape.write(buf);
		buf.writeInt(parentID);
		buf.writeDouble(charge);
		ros.write(buf);
		buf.writeNBTTagCompoundToBuffer(modules);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		id = buf.readInt();
		blockCoord = BlockPos.read(buf);
		shape = Rectangle.read(buf);
		parentID = buf.readInt();
		charge = buf.readDouble();
		ros = new RegionOwnerStorage(null);
		ros.read(buf);
		modules = buf.readNBTTagCompoundFromBuffer();
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		Region region = new Region(RegionManagerClient.getInstance(), id, false);
		region.setBlock(blockCoord);
		region.setShape(shape);
		region.parentWaiting = parentID;
		region.setCharge(charge);
		ros.region = region;
		region.ownerStorage = ros;
		region.setModulesStorage(RegionModulesStorage.parseNBT(modules));

		RegionManagerClient.getInstance().addRegion(region);
	}
}
