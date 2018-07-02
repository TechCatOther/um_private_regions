package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import org.ultramine.server.event.SetBlockEvent;
import org.ultramine.server.event.WorldUpdateObjectType;

public class RegionModuleSnowFall extends RegionModule
{
	@SideOnly(Side.SERVER)
	public void onBlockChange(SetBlockEvent e)
	{
		if(e.initiator.getType() == WorldUpdateObjectType.WEATHER && e.newBlock == Blocks.snow || e.newBlock == Blocks.snow_layer)
			e.setCanceled(true);
	}
}
