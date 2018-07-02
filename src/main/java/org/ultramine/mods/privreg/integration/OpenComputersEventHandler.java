package org.ultramine.mods.privreg.integration;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import li.cil.oc.api.event.RobotAttackEntityEvent;
import li.cil.oc.api.event.RobotBreakBlockEvent;
import li.cil.oc.api.event.RobotMoveEvent;
import li.cil.oc.api.event.RobotPlaceBlockEvent;
import li.cil.oc.api.internal.Agent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleBasic;
import org.ultramine.mods.privreg.regions.Region;

public class OpenComputersEventHandler
{
	private static Region getRegion(World world, int x, int y, int z)
	{
		return PrivateRegions.instance().getServerRegion(world.provider.dimensionId, x, y, z);
	}

	private static Region getActiveRegionWithModule(World world, int x, int y, int z, Class<? extends RegionModule> modulesClass)
	{
		Region region = getRegion(world, x, y, z);
		if(region == null || !region.isActive() || !region.hasModule(modulesClass))
			return null;
		return region;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRobotBreakBlockPre(RobotBreakBlockEvent.Pre e)
	{
		Region region = getActiveRegionWithModule(e.world, e.x, e.y, e.z, RegionModuleBasic.class);
		if(region != null)
		{
			GameProfile owner = MinecraftServer.getServer().getConfigurationManager().getDataLoader().internGameProfile(e.agent.ownerUUID(), e.agent.ownerName());
			if(owner == null || !region.hasRight(owner, RegionModuleBasic.RIGHT_BREAK_BLOCKS))
				e.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRobotPlaceBlockPre(RobotPlaceBlockEvent.Pre e)
	{
		Region region = getActiveRegionWithModule(e.world, e.x, e.y, e.z, RegionModuleBasic.class);
		if(region != null)
		{
			GameProfile owner = MinecraftServer.getServer().getConfigurationManager().getDataLoader().internGameProfile(e.agent.ownerUUID(), e.agent.ownerName());
			if(owner == null || !region.hasRight(owner, RegionModuleBasic.RIGHT_PLACE_BLOCKS))
				e.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRobotAttackEntityPre(RobotAttackEntityEvent.Pre e)
	{
		Region region = getActiveRegionWithModule(e.target.worldObj, MathHelper.floor_double(e.target.posX), MathHelper.floor_double(e.target.posY),
				MathHelper.floor_double(e.target.posZ), RegionModuleBasic.class);
		if(region != null)
		{
			GameProfile owner = MinecraftServer.getServer().getConfigurationManager().getDataLoader().internGameProfile(e.agent.ownerUUID(), e.agent.ownerName());
			if(owner == null)
				e.setCanceled(true);
			region.getModuleWithClass(RegionModuleBasic.class).handlePvpEvent(e, owner, e.target);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRobotMoveEventPre(RobotMoveEvent.Pre e)
	{
		Agent a = e.agent;
		Region region = getActiveRegionWithModule(
				a.world(),
				MathHelper.floor_double(a.xPosition()) + e.direction.offsetX,
				MathHelper.floor_double(a.yPosition()) + e.direction.offsetY,
				MathHelper.floor_double(a.zPosition()) + e.direction.offsetZ,
				RegionModuleBasic.class
		);
		if(region != null)
		{
			GameProfile owner = MinecraftServer.getServer().getConfigurationManager().getDataLoader().internGameProfile(e.agent.ownerUUID(), e.agent.ownerName());
			if(owner == null || !region.hasRight(owner, RegionModuleBasic.RIGHT_PLACE_BLOCKS))
				e.setCanceled(true);
		}
	}
}
