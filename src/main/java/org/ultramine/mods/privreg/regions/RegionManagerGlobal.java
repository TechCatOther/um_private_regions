package org.ultramine.mods.privreg.regions;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.ultramine.mods.privreg.data.IRegionDataProvider;
import org.ultramine.mods.privreg.data.NBTFileRegionDataProvider;
import org.ultramine.regions.BlockPos;

import java.io.File;

@SideOnly(Side.SERVER)
public class RegionManagerGlobal
{
	private final TIntObjectMap<RegionManager> worlds = new TIntObjectHashMap<RegionManager>();
	private final MinecraftServer server;

	public RegionManagerGlobal(MinecraftServer server)
	{
		this.server = server;
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}

	private IRegionDataProvider createDataProvider(int dim)
	{
		return new NBTFileRegionDataProvider(new File(server.getMultiWorld().getDescByID(dim).getDirectory(), "private_regions"));
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e)
	{
		int dim = e.world.provider.dimensionId;
		RegionManager mgr = new RegionManager(server, dim, createDataProvider(dim));
		worlds.put(dim, mgr);
		mgr.loadRegions();
	}

	@SubscribeEvent
	public void onWorldUnoad(WorldEvent.Unload e)
	{
		RegionManager mgr = worlds.remove(e.world.provider.dimensionId);
		if(mgr != null)
			mgr.unload();
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save e)
	{
		RegionManager mgr = worlds.get(e.world.provider.dimensionId);
		if(mgr != null)
			mgr.saveAllRegion();
	}

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END)
			for(RegionManager mgr : worlds.valueCollection())
				mgr.onTick(server.getTickCounter());
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
	{
		EntityPlayerMP player = (EntityPlayerMP)e.player;
		getForWorld(player.dimension).getTracker().onPlayerEnter(player);
	}

	@SubscribeEvent
	public void onPlayerDimChange(PlayerEvent.PlayerChangedDimensionEvent e)
	{
		EntityPlayerMP player = (EntityPlayerMP)e.player;
		RegionManager mgr = getForWorld(e.fromDim);
		if(mgr != null)
			mgr.getTracker().onPlayerLeave(player);
		getForWorld(e.toDim).getTracker().onPlayerEnter(player);
	}

	public RegionManager getForWorld(int dim)
	{
		return worlds.get(dim);
	}

	public Region getRegion(int dim, int id)
	{
		RegionManager mgr = getForWorld(dim);
		return mgr == null ? null : mgr.getRegion(id);
	}

	public Region getRegion(int dim, BlockPos point)
	{
		RegionManager mgr = getForWorld(dim);
		return mgr == null ? null : mgr.getRegion(point);
	}

	public Region getRegion(int dim, int x, int y, int z)
	{
		RegionManager mgr = getForWorld(dim);
		return mgr == null ? null : mgr.getRegion(x, y, z);
	}
}
