package org.ultramine.regions;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.util.ForgeDirection;
import org.ultramine.server.chunk.ChunkHash;

import java.io.IOException;

public final class BlockPos
{
	public static final BlockPos EMPTY = new BlockPos();

	public final int x;
	public final int y;
	public final int z;

	public BlockPos(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockPos()
	{
		this(0, 0, 0);
	}

	public BlockPos(double x, double y, double z)
	{
		this.x = MathHelper.floor_double(x);
		this.y = MathHelper.floor_double(y);
		this.z = MathHelper.floor_double(z);
	}

	public BlockPos step(ForgeDirection d)
	{
		return new BlockPos(x + d.offsetX, y + d.offsetY, z + d.offsetZ);
	}

	public BlockPos stepBack(ForgeDirection d)
	{
		return new BlockPos(x - d.offsetX, y - d.offsetY, z - d.offsetZ);
	}

	public Rectangle toRect()
	{
		return new Rectangle(this, this);
	}

	public boolean isInAABB(BlockPos min, BlockPos max)
	{
		return x >= min.x && x <= max.x && y >= min.y && y <= max.y && z >= min.z && z <= max.z;
	}

	public static BlockPos fromChunkPosition(ChunkPosition block)
	{
		return new BlockPos(block.chunkPosX, block.chunkPosY, block.chunkPosZ);
	}

	public static BlockPos fromTileEntity(TileEntity block)
	{
		return new BlockPos(block.xCoord, block.yCoord, block.zCoord);
	}

	public static BlockPos read(ByteBuf buf) throws IOException
	{
		return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	public void write(ByteBuf buf) throws IOException
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static BlockPos fromNBT(NBTTagCompound nbt)
	{
		return new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
	}

	public NBTTagCompound toNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
		return nbt;
	}

	@Override
	public String toString()
	{
		return "BlockPos(" + x + ", " + y + ", " + z + ")";
	}

	@Override
	public int hashCode()
	{
		return Long.hashCode(ChunkHash.blockCoordToHash(x, y, z));
	}

	public boolean equals(BlockPos v)
	{
		return x == v.x && y == v.y && z == v.z;
	}

	@Override
	public boolean equals(Object o)
	{
		return o != null && o.getClass() == BlockPos.class && equals((BlockPos)o);
	}
}
