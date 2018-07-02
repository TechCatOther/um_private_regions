package org.ultramine.mods.privreg.event;

import net.minecraftforge.common.util.ForgeDirection;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.regions.Rectangle;

public class RegionResizeEvent extends RegionEvent
{
	public final Rectangle lastShape;
	public final ForgeDirection dir;
	public final int amount;

	public RegionResizeEvent(Region region, Rectangle lastShape, ForgeDirection dir, int amount)
	{
		super(region);
		this.lastShape = lastShape;
		this.dir = dir;
		this.amount = amount;
	}
}
