package org.ultramine.mods.privreg.event;

import org.ultramine.mods.privreg.event.RegionEvent;
import org.ultramine.mods.privreg.regions.Region;

public class RegionLoadEvent extends RegionEvent
{
	public RegionLoadEvent(Region region)
	{
		super(region);
	}
}
