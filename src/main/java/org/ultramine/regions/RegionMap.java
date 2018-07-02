package org.ultramine.regions;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.ultramine.server.chunk.ChunkHash;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegionMap
{
	private static final int SF = 7;

	private final Set<IRegion> set = new HashSet<IRegion>();
	private final TIntObjectMap<List<IRegion>> map = new TIntObjectHashMap<List<IRegion>>();

	public void add(IRegion region)
	{
		if(!set.add(region))
			return;
		Rectangle shape = region.getShape();
		for(int x = shape.getMin().x >> SF, sx = shape.getMax().x >> SF; x <= sx; x++)
		{
			for(int z = shape.getMin().z >> SF, sz = shape.getMax().z >> SF; z <= sz; z++)
			{
				int key = ChunkHash.chunkToKey(x, z);
				List<IRegion> list = map.get(key);
				if(list == null)
				{
					list = new ArrayList<IRegion>(2);
					map.put(key, list);
				}
				list.add(region);
			}
		}
	}

	public void remove(IRegion region)
	{
		if(!set.remove(region))
			return;
		Rectangle shape = region.getShape();
		for(int x = shape.getMin().x >> SF, sx = shape.getMax().x >> SF; x <= sx; x++)
		{
			for(int z = shape.getMin().z >> SF, sz = shape.getMax().z >> SF; z <= sz; z++)
			{
				int key = ChunkHash.chunkToKey(x, z);
				List<IRegion> list = map.get(key);
				if(list != null)
				{
					list.remove(region);
					if(list.isEmpty())
						map.remove(key);
				}
			}
		}
	}

	public void onRegionExpand(IRegion region, Rectangle expandedPart)
	{
		if(!set.contains(region))
			return;
		for(int x = expandedPart.getMin().x >> SF, sx = expandedPart.getMax().x >> SF; x <= sx; x++)
		{
			for(int z = expandedPart.getMin().z >> SF, sz = expandedPart.getMax().z >> SF; z <= sz; z++)
			{
				int key = ChunkHash.chunkToKey(x, z);
				List<IRegion> list = map.get(key);
				if(list == null)
				{
					list = new ArrayList<IRegion>(2);
					map.put(key, list);
				}
				if(!list.contains(region))
					list.add(region);
			}
		}
	}

	public IRegion get(BlockPos point)
	{
		return findInHierarchy(map.get(ChunkHash.chunkToKey(point.x >> SF, point.z >> SF)), point);
	}

	private IRegion findInHierarchy(List<IRegion> list, BlockPos point)
	{
		if(list == null)
			return null;
		for(IRegion region : list)
		{
			if(region.getShape().contains(point))
			{
				IRegion region1 = findInHierarchy(region.getChildren(), point);
				return region1 != null ? region1 : region;
			}
		}
		return null;
	}

	public Set<IRegion> getInRange(Rectangle rect)
	{
		return getInRange(rect.getMin(), rect.getMax());
	}

	public Set<IRegion> getInRange(BlockPos min, BlockPos max)
	{
		Set<IRegion> ret = new HashSet<IRegion>();
		for(int x = min.x >> SF, sx = max.x >> SF; x <= sx; x++)
		{
			for(int z = min.z >> SF, sz = max.z >> SF; z <= sz; z++)
			{
				int key = ChunkHash.chunkToKey(x, z);
				List<IRegion> list = map.get(key);
				if(list != null)
				{
					for(IRegion region : list)
						if(RegionUtil.isIntersects(min, max, region.getShape().getMin(), region.getShape().getMax()))
							ret.add(region);
				}
			}
		}

		return ret;
	}

	public boolean hasInRange(Rectangle rect)
	{
		return hasInRange(rect.getMin(), rect.getMax());
	}

	public boolean hasInRange(BlockPos min, BlockPos max)
	{
		for(int x = min.x >> SF, sx = max.x >> SF; x <= sx; x++)
		{
			for(int z = min.z >> SF, sz = max.z >> SF; z <= sz; z++)
			{
				int key = ChunkHash.chunkToKey(x, z);
				List<IRegion> list = map.get(key);
				if(list != null)
				{
					for(IRegion region : list)
						if(RegionUtil.isIntersects(min, max, region.getShape().getMin(), region.getShape().getMax()))
							return true;
				}
			}
		}

		return false;
	}
}
