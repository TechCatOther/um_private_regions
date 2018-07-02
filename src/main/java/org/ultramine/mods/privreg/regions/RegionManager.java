package org.ultramine.mods.privreg.regions;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.openhft.koloboke.collect.map.IntObjMap;
import net.openhft.koloboke.collect.map.hash.HashIntObjMaps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.RegionConfig;
import org.ultramine.mods.privreg.data.IRegionDataProvider;
import org.ultramine.mods.privreg.owner.BasicOwner;
import org.ultramine.mods.privreg.packets.PacketRegionExpand;
import org.ultramine.mods.privreg.tiles.TileBlockRegion;
import org.ultramine.network.UMPacket;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.IRegion;
import org.ultramine.regions.IRegionManager;
import org.ultramine.regions.Rectangle;
import org.ultramine.regions.RegionMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SideOnly(Side.SERVER)
public class RegionManager implements IRegionManager
{
	public static final Logger log = LogManager.getLogger();

	private final MinecraftServer server;
	private final int dimension;
	private final IRegionDataProvider dataProvider;
	private final IntObjMap<Region> idToRegion = HashIntObjMaps.newMutableMap();
	private int nextRegionId;

	private final RegionMap regionMap = new RegionMap();

	private RegionTracker tracker = new RegionTracker(this);

	private boolean canRemoveRegionNow = true;
	private final List<Region> regionsToRemove = new ArrayList<Region>();

	public RegionManager(MinecraftServer server, int dimension, IRegionDataProvider dataProvider)
	{
		this.server = server;
		this.dimension = dimension;
		this.dataProvider = dataProvider;
	}

	public void loadRegions()
	{
		dataProvider.init(this);
		List<Region> list = new ArrayList<Region>();
		dataProvider.loadAll(list);
		int maxID = 0;
		for(Region reg : list)
			if(reg.getID() > maxID)
				maxID = reg.getID();
		nextRegionId = maxID + 1;
		for(Region reg : list)
		{
			reg.setWorld(dimension);
			idToRegion.put(reg.getID(), reg);
			regionMap.add(reg);
		}

		for(Region reg : list)
			if(reg.parentWaiting != -1)
				reg.setParent(idToRegion.get(reg.parentWaiting));

		for(Region reg : list)
		{
			if(checkOrRestore(reg))
				reg.onLoad();
			else
				destroyRegion(reg);
		}
	}

	private boolean checkOrRestore(Region reg)
	{
		BlockPos b = reg.getBlock();
		MinecraftServer mcserver = server;
		if (mcserver != null)
		{
			World world = mcserver.worldServerForDimension(reg.getWorld());
			if(world.getBlock(b.x, b.y, b.z) != InitCommon.region)
			{
				log.warn("Loaded region ID:{} for NOT RegionBlock [{}]({}, {}, {}) Trying to restore block", reg.getID(), reg.getWorld(), b.x, b.y, b.z);
				world.setBlockSilently(b.x, b.y, b.z, InitCommon.region, 0, 3);
				TileBlockRegion te = (TileBlockRegion) world.getTileEntity(b.x, b.y, b.z);
				if(te != null)
				{
					te.unsafeSetRegion(reg);
					log.info("RegionBlock successfuly restored");
				}
				else
				{
					log.warn("Failed to restore RegionBlock (TileEntity was NOT created)");
					return false;
				}
			}
		}

		return true;
	}

	private int getNextRegionID()
	{
		return nextRegionId++;
	}

	public @Nonnull Region createRegion(TileBlockRegion te, GameProfile player) throws RegionCreationException
	{
		final int cd = RegionConfig.CheckDistance;
		BlockPos block = BlockPos.fromTileEntity(te);
		Rectangle shape = block.toRect().expandAll(1);
		int world = te.getWorldObj().provider.dimensionId;
		if(world != dimension)
			throw new IllegalArgumentException("Wrong RegionManager for dimension "+world+", used "+dimension);

		Region parent = null;
		if(hasRegionsInRange(shape))
		{
			Region found = getRegion(block);
			if(found != null)
			{
				BlockPos b = found.getBlock();
				if(found.getShape().contains(shape) && !shape.isIntersects(b.toRect().expandAll(1)))
				{
					if(found.hasRight(player, RegionRights.PLACE_SUBREGIONS))
					{
						parent = found;
					}
				}
			}
		}

		if(parent == null)
			shape = shape.setSide(ForgeDirection.UP, 255).setSide(ForgeDirection.DOWN, 0);
		if (parent == null && hasRegionsInRange(shape.expandAll(cd)))
			throw new RegionCreationException();

		Region region = new Region(this, getNextRegionID(), true);
		region.setBlock(block);
		region.setShape(shape);
		region.setWorld(world);
		if (parent != null)
			region.setParent(parent);
		region.onCreate();

		BasicOwner owner = new BasicOwner(player);
		owner.setRight(RegionRights.CREATOR, true);
		region.getOwnerStorage().add(owner);

		idToRegion.put(region.getID(), region);
		dataProvider.createRegion(region);

		regionMap.add(region);
		tracker.onRegionCreate(region);

		return region;
	}

	public Region dangerousCreateRegion(Rectangle shape, BlockPos block, int dimension)
	{
		Region region = new Region(this, getNextRegionID(), true);

		region.setShape(shape);
		region.setWorld(dimension);
		region.setBlock(block);

		region.onCreate();

		idToRegion.put(region.getID(), region);
		dataProvider.createRegion(region);

		regionMap.add(region);
		tracker.onRegionCreate(region);

		return region;
	}

	public void saveRegion(Region region)
	{
		dataProvider.saveRegion(region);
		region.setChanged(false);
	}

	public void saveAllRegion()
	{
		dataProvider.saveAll(idToRegion.values());
	}

	public void onTick(int tick)
	{
		if(tick % 101 == 0)
		{
			canRemoveRegionNow = false;
			for(Region region : idToRegion.values())
				region.onUpdate();
			canRemoveRegionNow = true;
			for(Region region : regionsToRemove)
				idToRegion.remove(region.getID());
		}
	}

	public void unload()
	{
		saveAllRegion();
		for(Region region : idToRegion.values())
			region.onUnload();
		dataProvider.close();
	}

	public void destroyRegion(Region region)
	{
		regionMap.remove(region);
		region.onDestroy();
		if(canRemoveRegionNow)
			idToRegion.remove(region.getID());
		else
			regionsToRemove.add(region);
		dataProvider.destroyRegion(region);
		tracker.onRegionDestroy(region);
	}

	public Region getRegion(int id)
	{
		return idToRegion.get(id);
	}

	@Override
	public Region getRegion(BlockPos point)
	{
		return (Region)regionMap.get(point);
	}

	@Override
	public Region getRegion(int x, int y, int z)
	{
		return getRegion(new BlockPos(x, y, z));
	}

	@Override
	public Set<IRegion> getRegionsInRange(Rectangle range)
	{
		return regionMap.getInRange(range);
	}

	@Override
	public boolean hasRegionsInRange(Rectangle range)
	{
		return regionMap.hasInRange(range);
	}

	public IntObjMap<Region> unsafeGetRegions()
	{
		return idToRegion;
	}

	public RegionChangeResult expandRegion(Region region, ForgeDirection dir, int amount)
	{
		RegionChangeResult res = region.canExpand(dir, amount);
		if(res != RegionChangeResult.ALLOW)
			return res;

		if(amount > 0)
		{
			regionMap.onRegionExpand(region, region.getShape().compress(dir.getOpposite(), region.getShape().getLen(dir)).expand(dir, amount));
			region.doExpand(dir, amount);
		}
		else
		{
			regionMap.remove(region);
			region.doExpand(dir, amount);
			regionMap.add(region);
		}

		tracker.sendToListeners(region, new PacketRegionExpand(region, dir, amount));

		return RegionChangeResult.ALLOW;
	}

	public void sendToListeners(Region region, UMPacket packet)
	{
		tracker.sendToListeners(region, packet);
	}

	RegionTracker getTracker()
	{
		return tracker;
	}
}
