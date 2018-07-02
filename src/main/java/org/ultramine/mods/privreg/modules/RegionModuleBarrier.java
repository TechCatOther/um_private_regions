package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.WorldEvent;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.modules.GuiModuleBarrier;
import org.ultramine.mods.privreg.gui.modules.GuiModuleSettings;
import org.ultramine.mods.privreg.owner.OwnerRight;
import org.ultramine.mods.privreg.owner.RightRegistry;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.Rectangle;
import org.ultramine.server.chunk.ChunkHash;
import org.ultramine.server.chunk.IChunkLoadCallback;
import org.ultramine.server.event.SetBlockEvent;

import java.util.List;

import static org.ultramine.util.I18n.tlt;

public class RegionModuleBarrier extends RegionModule
{
	public static final OwnerRight RIGHT_PENETRATE_BORDER = RightRegistry.register("barrier", "penetrate");

	private boolean opaque;
	private SetBorderTask task;

	public boolean isOpaque()
	{
		return opaque;
	}

	public void setOpaque(boolean opaque)
	{
		this.opaque = opaque;
	}

	@Override
	public void onPlaceToRegion(Region region)
	{
		super.onPlaceToRegion(region);
		this.region.getOwnerStorage().registerRight(RIGHT_PENETRATE_BORDER, true);
	}

	@Override
	public void onRemoveFromRegion()
	{
		if (this.region != null)
			this.region.getOwnerStorage().registerRight(RIGHT_PENETRATE_BORDER, false);
		super.onRemoveFromRegion();
	}

	@Override
	@SideOnly(Side.SERVER)
	public void onRegionActivate()
	{
		set(region.getShape(), true);
	}

	@Override
	@SideOnly(Side.SERVER)
	public void onRegionInactivate()
	{
		set(region.getShape(), false);
	}

	@Override
	@SideOnly(Side.SERVER)
	public void onAreaChanged(Rectangle last, ForgeDirection dir, int amount)
	{
		set(last, false);
		set(region.getShape(), true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiModuleSettings createGuiHandler(GuiRegionModules parent)
	{
		return new GuiModuleBarrier(parent, this);
	}

	@Override
	protected void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("o", opaque);
	}

	@Override
	protected void readFromNBT(NBTTagCompound nbt)
	{
		opaque = nbt.getBoolean("o");
	}

	@Override
	protected double countRawCost()
	{
		Rectangle shape = region.getShape();
		return (shape.getLenX() * shape.getLenZ() * 2 +
				shape.getLenX() * shape.getLenY() * 2 +
				shape.getLenZ() * shape.getLenY() * 2) * registryItem.getEnergyCostPerBlock();
	}

	@Override
	protected void addEnergyCostDescLine(List<String> desc)
	{
		if(registryItem.getEnergyCost() > 0F)
			desc.add(tlt("item.um_privreg_module.barrier.desc1", registryItem.getEnergyCost()));
	}

	private void set(Rectangle shape, boolean set)
	{
		if(task == null)
			task = new SetBorderTask(MinecraftServer.getServer().getMultiWorld().getWorldByID(region.getWorld()));
		BlockPos min = shape.getMin();
		BlockPos max = shape.getMax();

		for(int x = min.x; x <= max.x; x++)
		{
			for(int y = min.y; y <= max.y; y++)
			{
				task.set(x, y, min.z, set);
				task.set(x, y, max.z, set);
			}
		}

		for(int z = min.z; z <= max.z; z++)
		{
			for(int y = min.y; y <= max.y; y++)
			{
				task.set(min.x, y, z, set);
				task.set(max.x, y, z, set);
			}
		}

		for(int x = min.x; x <= max.x; x++)
		{
			for(int z = min.z; z <= max.z; z++)
			{
				task.set(x, min.y, z, set);
				task.set(x, max.y, z, set);
			}
		}

		task.commit();
	}

	public void onBlockChange(SetBlockEvent e)
	{
		Rectangle shape = region.getShape();
		BlockPos min = shape.getMin();
		BlockPos max = shape.getMax();
		if(e.newBlock == Blocks.air)
		{
			if(e.x == min.x || e.x == max.x || e.y == min.y || e.y == max.y || e.z == min.z || e.z == max.z)
			{
				e.setCanceled(true);
				e.world.setBlockSilently(e.x, e.y, e.z, InitCommon.barrier, 0, 3);
			}
		}
	}

	public class SetBorderTask
	{
		private final WorldServer world;
		private final TIntObjectMap<TLongList> map = new TIntObjectHashMap<TLongList>();
		private TIntObjectIterator<TLongList> iterator;

		public SetBorderTask(WorldServer world)
		{
			this.world = world;
		}

		public void set(int x, int y, int z, boolean set)
		{
			int key = ChunkHash.chunkToKey(x >> 4, z >> 4);
			TLongList list = map.get(key);
			if(list == null)
			{
				list = new TLongArrayList(512);
				map.put(key, list);
			}
			long bkey = ChunkHash.blockCoordToHash(x, y, z);
			if(set)
				list.add(bkey);
			else
				list.add(-bkey);
		}

		public void commit()
		{
			iterator = map.iterator();
			FMLCommonHandler.instance().bus().register(this);
		}

		@SubscribeEvent
		public void onTick(TickEvent.ServerTickEvent e)
		{
			if(e.phase == TickEvent.Phase.END)
				tick();
		}

		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload e)
		{
			if(world == e.world)
			{
				FMLCommonHandler.instance().bus().unregister(this);
				task = null;
			}
		}

		private void tick()
		{
			for(int i = 0; i < 16 && iterator.hasNext(); i++)
			{
				iterator.advance();
				int key = iterator.key();
				final TLongList blocks = iterator.value();
				if(world.theChunkProviderServer.chunkExists(ChunkHash.keyToX(key), ChunkHash.keyToZ(key)))
				{
					processBlocks(blocks);
				}
				else
				{
					world.theChunkProviderServer.loadAsync(ChunkHash.keyToX(key), ChunkHash.keyToZ(key), new IChunkLoadCallback()
					{
						@Override
						public void onChunkLoaded(Chunk chunk)
						{
							processBlocks(blocks);
						}
					});
				}
			}

			if(!iterator.hasNext())
			{
				FMLCommonHandler.instance().bus().unregister(this);
				task = null;
			}
		}

		private void processBlocks(TLongList blocks)
		{
			TLongSet sett = new TLongHashSet((int)(blocks.size()*1.5+1), 0.75F);
			for(int i = blocks.size() - 1; i >= 0; i--)
			{
				long block = blocks.get(i);
				boolean set = block > 0;
				if(!set)
					block = -block;
				if(!sett.add(block))
					continue;
				if(set)
					InitCommon.barrier.placeAt(world, ChunkHash.blockKeyToX(block), ChunkHash.blockKeyToY(block), ChunkHash.blockKeyToZ(block));
				else
					InitCommon.barrier.breakAt(world, ChunkHash.blockKeyToX(block), ChunkHash.blockKeyToY(block), ChunkHash.blockKeyToZ(block));
			}
		}
	}
}
