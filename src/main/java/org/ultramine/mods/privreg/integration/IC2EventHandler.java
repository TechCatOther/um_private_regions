package org.ultramine.mods.privreg.integration;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.event.ExplosionEvent;
import ic2.api.event.LaserEvent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.modules.RegionModuleBasic;
import org.ultramine.mods.privreg.modules.RegionModuleExplosion;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.IRegion;
import org.ultramine.regions.Rectangle;

import java.util.Set;

@SideOnly(Side.SERVER)
public class IC2EventHandler
{
	private static Region getRegion(World world, int x, int y, int z)
	{
		return PrivateRegions.instance().getServerRegion(world.provider.dimensionId, x, y, z);
	}

	private void handleExplosion(Event e, World world, double x, double y, double z, double r)
	{
		Set<IRegion> regions = PrivateRegions.instance().getServerRegionManager(world.provider.dimensionId).getRegionsInRange(
				new Rectangle(new BlockPos(x-r, y-r, z-r), new BlockPos(x+r, y+r, z+r)));
		if(!regions.isEmpty())
		{
			for(IRegion region2 : regions)
			{
				Region region = (Region)region2;
				if(region.hasModule(RegionModuleBasic.class) && region.getModuleWithClass(RegionModuleBasic.class).cancelBlockExplosion() ||
					region.hasModule(RegionModuleExplosion.class))
				{
					e.setCanceled(true);
					break;
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onExplosionEvent(ExplosionEvent e)
	{
		handleExplosion(e, e.world, e.x, e.y, e.z, Math.max(e.rangeLimit, e.radiationRange));
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLaserHitsBlockEvent(LaserEvent.LaserHitsBlockEvent e)
	{
		Region region = getRegion(e.world, e.x, e.y, e.z);
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
			{
				GameProfile owner = e.lasershot.getObjectOwner();
				if(owner == null || !region.hasRight(owner, RegionModuleBasic.RIGHT_BREAK_BLOCKS))
					e.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLaserHitsEntityEvent(LaserEvent.LaserHitsEntityEvent e)
	{
		Region region = getRegion(e.world, MathHelper.floor_double(e.hitentity.posX), MathHelper.floor_double(e.hitentity.posY), MathHelper.floor_double(e.hitentity.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).handlePvpEvent(e, e.hitentity, true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLaserExplodesEvent(LaserEvent.LaserExplodesEvent e)
	{
		handleExplosion(e, e.world, e.lasershot.posX, e.lasershot.posY, e.lasershot.posZ, e.explosionpower / 0.4d);
	}
}
