package org.ultramine.mods.privreg.integration;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.event.RegionCreateEvent;
import org.ultramine.mods.privreg.event.RegionDestroyEvent;
import org.ultramine.mods.privreg.event.RegionLoadEvent;
import org.ultramine.mods.privreg.event.RegionResizeEvent;
import org.ultramine.mods.privreg.event.RegionUnloadEvent;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.regions.BlockPos;

@SideOnly(Side.SERVER)
public class DynmapIntegration extends DynmapCommonAPIListener
{
	public static void init()
	{
		DynmapIntegration instance = new DynmapIntegration();
		DynmapCommonAPIListener.register(instance);
	}

	private MarkerAPI api;
	private MarkerSet markers;

	@Override
	public void apiEnabled(DynmapCommonAPI capi)
	{
		if(api != null)
			return;
		this.api = capi.getMarkerAPI();
		this.markers = api.createMarkerSet("privreg", "Private Regions", null, false);
		this.markers.setHideByDefault(true);

		for(Region region : PrivateRegions.instance().getServerRegionManager(0).unsafeGetRegions().values())
		{
			if(region != null)
				loadRegion(region);
		}

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void loadRegion(Region region)
	{
		if(markers == null)
			return;
		if(region.getWorld() != 0)
			return;
		BlockPos m = region.getShape().getMin();
		BlockPos x = region.getShape().getMax();
		int color;
		switch(region.getGeneration() % 6)
		{
			case 0: color = 0xCC4C4C; break;
			case 1: color = 0x4CCC4C; break;
			case 2: color = 0x4C4CCC; break;
			case 3: color = 0xCCCC4C; break;
			case 4: color = 0x4C4CCC; break;
			default: color = 0xCC4CCC; break;
		}
		AreaMarker marker = markers.createAreaMarker(getMarkerID(region), "", false, "world", new double[]{m.x, x.x+1}, new double[]{m.z, x.z+1}, false);
		marker.setLineStyle(1, 0.8, color);
		marker.setFillStyle(0.16, color);
		marker.setRangeY(region.getBlock().y, region.getBlock().y);
	}

	private void unloadRegion(Region region)
	{
		if(markers == null)
			return;
		AreaMarker marker = markers.findAreaMarker(getMarkerID(region));
		if(marker != null)
			marker.deleteMarker();
	}

	private void updateRegion(Region region)
	{
		if(markers == null)
			return;
		AreaMarker marker = markers.findAreaMarker(getMarkerID(region));
		if(marker != null)
		{
			BlockPos m = region.getShape().getMin();
			BlockPos x = region.getShape().getMax();
			marker.setCornerLocations(new double[]{m.x, x.x+1}, new double[]{m.z, x.z+1});
		}
	}

	private String getMarkerID(Region region)
	{
		return "privreg_"+region.getWorld()+"_"+region.getID();
	}

	@SubscribeEvent
	public void onRegionCreate(RegionCreateEvent e)
	{
		loadRegion(e.region);
	}

	@SubscribeEvent
	public void onRegionLoad(RegionLoadEvent e)
	{
		loadRegion(e.region);
	}

	@SubscribeEvent
	public void onRegionDestroy(RegionDestroyEvent e)
	{
		unloadRegion(e.region);
	}

	@SubscribeEvent
	public void onRegionUnload(RegionUnloadEvent e)
	{
		unloadRegion(e.region);
	}

	@SubscribeEvent
	public void onRegionResize(RegionResizeEvent e)
	{
		updateRegion(e.region);
	}
}
