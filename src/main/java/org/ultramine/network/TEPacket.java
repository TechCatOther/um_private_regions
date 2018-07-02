package org.ultramine.network;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

public abstract class TEPacket extends UMPacket
{
	public int x;
	public int y;
	public int z;
	
	protected TEPacket(){}
	
	public TEPacket form(TileEntity te)
	{
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
		return this;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		writePacketData(buf);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		readPacketData(buf);
	}
	
	public abstract void writePacketData(PacketBuffer data) throws IOException;
	
	public abstract void readPacketData(PacketBuffer data) throws IOException;
	
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void processClient(NetHandlerPlayClient net)
	{
		TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
		if(te instanceof ITEPacketHandler)
			((ITEPacketHandler)te).handlePacketClient(this);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void processServer(NetHandlerPlayServer net)
	{
		TileEntity te = net.playerEntity.worldObj.getTileEntity(x, y, z);
		if(te instanceof ITEPacketHandler)
			((ITEPacketHandler)te).handlePacketServer(this, net.playerEntity);
	}
}
