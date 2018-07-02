package org.ultramine.regions;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.IOException;

public class Rectangle
{
	private final BlockPos min;
	private final BlockPos max;

	public Rectangle(BlockPos min, BlockPos max)
	{
		this.min = min;
		this.max = max;
	}

	public Rectangle(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		this(new BlockPos(x1, y1, z1), new BlockPos(x2, y2, z2));
	}

	public BlockPos getMin()
	{
		return min;
	}

	public BlockPos getMax()
	{
		return max;
	}

	public int getLenX()
	{
		return max.x - min.x + 1;
	}

	public int getLenY()
	{
		return max.y - min.y + 1;
	}

	public int getLenZ()
	{
		return max.z - min.z + 1;
	}

	public int getLen(ForgeDirection dir)
	{
		switch(dir)
		{
			case DOWN:	return getLenY();
			case NORTH:	return getLenZ();
			case WEST:	return getLenX();
			case UP:	return getLenY();
			case SOUTH:	return getLenZ();
			case EAST:	return getLenX();
			case UNKNOWN: throw new IllegalArgumentException("UNKNOWN direction");
		}

		return 0;
	}

	public Rectangle setSide(ForgeDirection dir, int amount)
	{
		switch(dir)
		{
			case DOWN:	return new Rectangle(new BlockPos(min.x, amount, min.z), max);
			case NORTH:	return new Rectangle(new BlockPos(min.x, min.y, amount), max);
			case WEST:	return new Rectangle(new BlockPos(amount, min.y, min.z), max);
			case UP:	return new Rectangle(min, new BlockPos(max.x, amount, max.z));
			case SOUTH:	return new Rectangle(min, new BlockPos(max.x, max.y, amount));
			case EAST:	return new Rectangle(min, new BlockPos(amount, max.y, max.z));
			case UNKNOWN: throw new IllegalArgumentException("UNKNOWN direction");
		}

		return this;
	}

	public Rectangle expand(ForgeDirection dir, int amount)
	{
		switch(dir)
		{
			case DOWN:	return new Rectangle(new BlockPos(min.x, min.y - amount, min.z), max);
			case NORTH:	return new Rectangle(new BlockPos(min.x, min.y, min.z - amount), max);
			case WEST:	return new Rectangle(new BlockPos(min.x - amount, min.y, min.z), max);
			case UP:	return new Rectangle(min, new BlockPos(max.x, max.y + amount, max.z));
			case SOUTH:	return new Rectangle(min, new BlockPos(max.x, max.y, max.z + amount));
			case EAST:	return new Rectangle(min, new BlockPos(max.x + amount, max.y, max.z));
			case UNKNOWN: throw new IllegalArgumentException("UNKNOWN direction");
		}

		return this;
	}

	public Rectangle compress(ForgeDirection dir, int amount)
	{
		return expand(dir, -amount);
	}

	public Rectangle expandAll(int amount)
	{
		return new Rectangle(new BlockPos(min.x-amount, min.y-amount, min.z-amount), new BlockPos(max.x+amount, max.y+amount, max.z+amount));
	}

	public boolean isIntersects(Rectangle other)
	{
		return RegionUtil.isIntersects(min, max, other.min, other.max);
	}

	public boolean contains(BlockPos point)
	{
		return point.isInAABB(min, max);
	}

	public boolean contains(Rectangle rect)
	{
		return RegionUtil.isBoxInBox(rect.min, rect.max, min, max);
	}

	public static Rectangle read(ByteBuf buf) throws IOException
	{
		return new Rectangle(BlockPos.read(buf), BlockPos.read(buf));
	}

	public void write(ByteBuf buf) throws IOException
	{
		min.write(buf);
		max.write(buf);
	}

	public NBTTagCompound toNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("m", min.toNBT());
		nbt.setTag("x", max.toNBT());
		return nbt;
	}

	public static Rectangle fromNBT(NBTTagCompound nbt)
	{
		return new Rectangle(BlockPos.fromNBT(nbt.getCompoundTag("m")), BlockPos.fromNBT(nbt.getCompoundTag("x")));
	}

	public String toString()
	{
		return "Rectangle{min("+min.x + ", "+min.y+", "+min.z+"), max("+max.x + ", "+max.y+", "+max.z+")}";
	}

	public boolean equals(Object o)
	{
		if(!(o instanceof Rectangle))
			return false;
		Rectangle rect = (Rectangle)o;
		return min.equals(rect.min) && max.equals(rect.max);
	}
}
