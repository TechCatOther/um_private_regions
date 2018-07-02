package org.ultramine.mods.privreg.tiles;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileBarrier extends TileEntity
{
	private Block block = Blocks.air;
	private int meta;

	public void setTypes(Block block, int meta)
	{
		this.block = block;
		this.meta = meta;
	}

	public void replace()
	{
		if(block != null && block != Blocks.air)
			worldObj.setBlockSilently(xCoord, yCoord, zCoord, block, meta, 3);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setShort("b", (short) Block.getIdFromBlock(block));
		nbt.setByte("m", (byte)meta);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		block = Block.getBlockById(nbt.getShort("b"));
		meta = nbt.getByte("m");
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}
}
