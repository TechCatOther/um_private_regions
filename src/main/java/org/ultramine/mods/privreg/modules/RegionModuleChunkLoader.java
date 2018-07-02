package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import org.ultramine.mods.privreg.ChunkLoaderManager;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.regions.Rectangle;
import org.ultramine.server.chunk.ChunkHash;

import java.util.List;

import static org.ultramine.util.I18n.tlt;

public class RegionModuleChunkLoader extends RegionModule implements ChunkLoaderManager.IChunkLoader
{
	@Override
	@SideOnly(Side.SERVER)
	public void onRegionActivate()
	{
		PrivateRegions.instance().getChunkLoaderManager().addChunkLoader(this);
	}

	@Override
	@SideOnly(Side.SERVER)
	public void onRegionInactivate()
	{
		PrivateRegions.instance().getChunkLoaderManager().removeChunkLoader(this);
	}

	@Override
	@SideOnly(Side.SERVER)
	public void onAreaChanged(Rectangle last, ForgeDirection dir, int amount)
	{
		if(region.isServer() && countChunks(region.getShape()) != countChunks(last))
			PrivateRegions.instance().getChunkLoaderManager().updateChunkLoader(this);
	}

	@Override
	protected double countRawCost()
	{
		return countChunks(region.getShape()) * registryItem.getEnergyCost();
	}

	@Override
	protected void addEnergyCostDescLine(List<String> desc)
	{
		if(registryItem.getEnergyCost() > 0F)
			desc.add(tlt("item.um_privreg_module.chunkloader.desc1", registryItem.getEnergyCost()));
	}

	private int countChunks(Rectangle shape)
	{
		int cxmin = shape.getMin().x >> 4;
		int czmin = shape.getMin().z >> 4;
		int cxmax = shape.getMax().x >> 4;
		int czmax = shape.getMax().z >> 4;
		return (cxmax-cxmin+1)*(czmax-czmin+1);
	}

	@Override
	public int getDimenson()
	{
		return region.getWorld();
	}

	@Override
	public TIntList getChunks()
	{
		Rectangle shape = region.getShape();
		int cxmin = shape.getMin().x >> 4;
		int czmin = shape.getMin().z >> 4;
		int cxmax = shape.getMax().x >> 4;
		int czmax = shape.getMax().z >> 4;
		TIntList list = new TIntArrayList((cxmax-cxmin+1)*(czmax-czmin+1));
		for(int cx = cxmin; cx <= cxmax; cx++)
			for(int cz = czmin; cz <= czmax; cz++)
				list.add(ChunkHash.chunkToKey(cx, cz));
		return list;
	}

	@Override
	protected void writeToNBT(NBTTagCompound nbt)
	{

	}

	@Override
	protected void readFromNBT(NBTTagCompound nbt)
	{

	}
}
