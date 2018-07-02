package org.ultramine.mods.privreg.event;

import org.ultramine.mods.privreg.regions.Region;

public class RegionUnloadEvent extends RegionEvent
{
	public RegionUnloadEvent(Region region)
	{
		super(region);
	}
}
