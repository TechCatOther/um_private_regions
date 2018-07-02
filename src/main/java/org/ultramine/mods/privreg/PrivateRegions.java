package org.ultramine.mods.privreg;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.ultramine.mods.privreg.data.DummyDataProvider;
import org.ultramine.mods.privreg.data.NBTFileRegionDataProvider;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManager;
import org.ultramine.mods.privreg.regions.RegionManagerGlobal;

@Mod(modid = "PrivateRegions", name = "UltraMine Private Regions", version = "@version@", acceptableRemoteVersions = "*")
public class PrivateRegions
{
	public static final String ADMIN_PERMISSION = "privreg.admin";

	@Mod.Instance("PrivateRegions")
	private static PrivateRegions instance;

	@SidedProxy(clientSide="org.ultramine.mods.privreg.InitClient", serverSide="org.ultramine.mods.privreg.InitServer")
	public static InitCommon initializer;

	@SideOnly(Side.SERVER)
	private RegionManagerGlobal regMgr;
	@SideOnly(Side.SERVER)
	private ChunkLoaderManager chunkMrg;

	public static PrivateRegions instance()
	{
		return instance;
	}

	@SideOnly(Side.SERVER)
	public RegionManagerGlobal getServerRegionManager()
	{
		return regMgr;
	}

	@SideOnly(Side.SERVER)
	public RegionManager getServerRegionManager(int dim)
	{
		return regMgr.getForWorld(dim);
	}

	@SideOnly(Side.SERVER)
	public Region getServerRegion(int dim, int x, int y, int z)
	{
		return regMgr.getRegion(dim, x, y, z);
	}

	@SideOnly(Side.SERVER)
	public Region getServerRegion(int dim, int id)
	{
		return regMgr.getRegion(dim, id);
	}

	@SideOnly(Side.SERVER)
	public ChunkLoaderManager getChunkLoaderManager()
	{
		return chunkMrg;
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		initializer.initCommon();
		initializer.initSided();
	}

	@Mod.EventHandler
	public void start(FMLServerAboutToStartEvent e)
	{
		if(e.getSide().isServer())
		{
			chunkMrg = new ChunkLoaderManager(e.getServer(), this);
			chunkMrg.register();
			regMgr = new RegionManagerGlobal(e.getServer());
		}
	}

	@Mod.EventHandler
	public void start(FMLServerStartingEvent e)
	{
		if(e.getSide().isServer())
		{
			e.registerCommands(RegionCommands.class);
		}
	}

	@Mod.EventHandler
	public void stop(FMLServerStoppedEvent e)
	{
		if(e.getSide().isServer())
		{
			regMgr = null;
			chunkMrg.unregister();
		}
	}
}
