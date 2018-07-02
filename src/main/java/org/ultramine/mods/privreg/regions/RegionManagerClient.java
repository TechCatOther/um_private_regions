package org.ultramine.mods.privreg.regions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.packets.PacketRegionExpand;
import org.ultramine.mods.privreg.render.ClientSelectionRenderer;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.IRegion;
import org.ultramine.regions.IRegionManager;
import org.ultramine.regions.Rectangle;
import org.ultramine.regions.RegionMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class RegionManagerClient implements IRegionManager
{
	private static final Logger log = LogManager.getLogger();
	private static RegionManagerClient INSTANCE = new RegionManagerClient();
	public static RegionManagerClient getInstance()
	{
		return INSTANCE;
	}

	private final TIntObjectMap<Region> idmap = new TIntObjectHashMap<Region>();
	private RegionMap map = new RegionMap();

	private final TIntObjectMap<List<Region>> parentWaiting = new TIntObjectHashMap<List<Region>>();

	private RegionManagerClient(){}

	public void addRegion(Region region)
	{
		log.info("Receiving region with ID: {}", region.getID());
		if(idmap.containsKey(region.getID()))
			log.warn("Double receiving region with ID: {}", region.getID());
		region.onLoad();
		idmap.put(region.getID(), region);
		map.add(region);

		if(region.parentWaiting != -1)
		{
			Region parent = idmap.get(region.parentWaiting);
			if(parent != null)
			{
				region.setParent(parent);
			}
			else
			{
				List<Region> list = parentWaiting.get(region.parentWaiting);
				if(list == null)
				{
					list = new ArrayList<Region>();
					parentWaiting.put(region.parentWaiting, list);
				}
				list.add(region);
			}

			List<Region> list = parentWaiting.remove(region.getID());
			if(list != null)
				for(Region reg : list)
					reg.setParent(region);

		}
	}

	public void destroyRegion(Region region)
	{
		log.info("Destroyed region with ID: {}", region.getID());
		region.onDestroy();
		idmap.remove(region.getID());
		map.remove(region);
	}

	public void expandRegion(Region region, ForgeDirection dir, int amount)
	{
		new PacketRegionExpand(region, dir, amount).sendToServer();
	}

	public void receiveExpand(int id, ForgeDirection dir, int amount)
	{
		Region region = getRegion(id);
		if(region == null)
		{
			log.warn("Received PacketRegionExpand for undefined region ID: {}", id);
			return;
		}

		region.doExpand(dir, amount);
	}

	public Region getRegion(int id)
	{
		return idmap.get(id);
	}

	@Override
	public Region getRegion(BlockPos point)
	{
		return (Region)map.get(point);
	}

	@Override
	public Region getRegion(int x, int y, int z)
	{
		return getRegion(new BlockPos(x, y, z));
	}

	public void clearRegions()
	{
		log.info("Regions cleared");
		idmap.clear();
		map = new RegionMap();
		parentWaiting.clear();
	}

	@Override
	public Set<IRegion> getRegionsInRange(Rectangle range)
	{
		return map.getInRange(range);
	}

	@Override
	public boolean hasRegionsInRange(Rectangle range)
	{
		return false;
	}
}
