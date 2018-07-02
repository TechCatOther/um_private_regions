package org.ultramine.mods.privreg.event;

import cpw.mods.fml.common.eventhandler.Event;
import org.ultramine.mods.privreg.regions.Region;

public class RegionEvent extends Event
{
	public final Region region;

	public RegionEvent(Region region)
	{
		this.region = region;
	}
}
