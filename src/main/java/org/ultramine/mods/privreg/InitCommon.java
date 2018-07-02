package org.ultramine.mods.privreg;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import org.ultramine.mods.privreg.blocks.BlockBarrier;
import org.ultramine.mods.privreg.blocks.BlockCharger;
import org.ultramine.mods.privreg.blocks.BlockRegion;
import org.ultramine.mods.privreg.blocks.BlockRentStand;
import org.ultramine.mods.privreg.modules.RegionModuleAdmin;
import org.ultramine.mods.privreg.modules.RegionModuleExplosion;
import org.ultramine.mods.privreg.modules.RegionModuleGreeting;
import org.ultramine.mods.privreg.item.ItemAntimatter;
import org.ultramine.mods.privreg.item.ItemBiometricCard;
import org.ultramine.mods.privreg.item.ItemDistanceControl;
import org.ultramine.mods.privreg.item.ItemRegionModification;
import org.ultramine.mods.privreg.item.ItemRegionModule;
import org.ultramine.mods.privreg.modifications.RegionModificationCharge;
import org.ultramine.mods.privreg.modifications.RegionModificationTacts;
import org.ultramine.mods.privreg.modifications.RegionModificationsRegistry;
import org.ultramine.mods.privreg.modules.RegionModuleBasic;
import org.ultramine.mods.privreg.modules.RegionModuleBarrier;
import org.ultramine.mods.privreg.modules.RegionModuleChunkLoader;
import org.ultramine.mods.privreg.modules.RegionModuleDiscount;
import org.ultramine.mods.privreg.modules.RegionModuleFree;
import org.ultramine.mods.privreg.modules.RegionModuleLiquidFlow;
import org.ultramine.mods.privreg.modules.RegionModuleMobDamage;
import org.ultramine.mods.privreg.modules.RegionModuleMobSpawn;
import org.ultramine.mods.privreg.modules.RegionModuleRent;
import org.ultramine.mods.privreg.modules.RegionModuleSnowFall;
import org.ultramine.mods.privreg.modules.RegionModulesRegistry;
import org.ultramine.mods.privreg.packets.PacketChargerAction;
import org.ultramine.mods.privreg.packets.PacketGuiMessage;
import org.ultramine.mods.privreg.packets.PacketGuiRentServerTime;
import org.ultramine.mods.privreg.packets.PacketIntWindowProperty;
import org.ultramine.mods.privreg.packets.PacketRegionAction;
import org.ultramine.mods.privreg.packets.PacketRegionData;
import org.ultramine.mods.privreg.packets.PacketRegionExpand;
import org.ultramine.mods.privreg.packets.PacketRegionModule;
import org.ultramine.mods.privreg.packets.PacketRegionOwner;
import org.ultramine.mods.privreg.packets.PacketRegionRent;
import org.ultramine.mods.privreg.packets.PacketTEBlockRegion;
import org.ultramine.mods.privreg.packets.PacketTitle;
import org.ultramine.mods.privreg.packets.PacketUserFlags;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.mods.privreg.tiles.TileBlockRegion;
import org.ultramine.mods.privreg.tiles.TileCharger;
import org.ultramine.mods.privreg.tiles.TileBarrier;
import org.ultramine.mods.privreg.tiles.TileRentStand;
import org.ultramine.network.UMNetworkRegistry;
import org.ultramine.util.GuiHandler;

public abstract class InitCommon
{
	public static final BlockRegion region = new BlockRegion();
	public static final BlockBarrier barrier = new BlockBarrier();

	public static final ItemBiometricCard biocard = new ItemBiometricCard();
	public static final ItemRegionModule module = new ItemRegionModule();
	public static final ItemAntimatter antimatter = new ItemAntimatter();
	public static final ItemDistanceControl distanceControl = new ItemDistanceControl();

	void initCommon()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(PrivateRegions.instance(), new GuiHandler());

		GameRegistry.registerBlock(region, "region");
		GameRegistry.registerBlock(barrier, "barrier");
		GameRegistry.registerBlock(new BlockCharger(), "amcharger");
		GameRegistry.registerBlock(new BlockRentStand(), "rentstand");

		GameRegistry.registerItem(biocard, "biocard");
		GameRegistry.registerItem(module, "module");
		GameRegistry.registerItem(antimatter, "antimatter");
		GameRegistry.registerItem(distanceControl, "distanceControl");
		GameRegistry.registerItem(new ItemRegionModification(RegionModificationsRegistry.instance().register(RegionModificationCharge.class, 0, "charge")), "mod_charge");
		GameRegistry.registerItem(new ItemRegionModification(RegionModificationsRegistry.instance().register(RegionModificationTacts.class, 1, "tacts")), "mod_tackts");

		GameRegistry.registerTileEntity(TileBlockRegion.class, "um_pregion");
		GameRegistry.registerTileEntity(TileBarrier.class, "um_barrier");
		GameRegistry.registerTileEntity(TileCharger.class, "um_amcharger");
		GameRegistry.registerTileEntity(TileRentStand.class, "um_rentstand");

		UMNetworkRegistry.registerPacket(PacketRegionData.class);
		UMNetworkRegistry.registerPacket(PacketRegionAction.class);
		UMNetworkRegistry.registerPacket(PacketTEBlockRegion.class);
		UMNetworkRegistry.registerPacket(PacketRegionExpand.class);
		UMNetworkRegistry.registerPacket(PacketGuiMessage.class);
		UMNetworkRegistry.registerPacket(PacketRegionOwner.class);
		UMNetworkRegistry.registerPacket(PacketUserFlags.class);
		UMNetworkRegistry.registerPacket(PacketRegionModule.class);
		UMNetworkRegistry.registerPacket(PacketIntWindowProperty.class);
		UMNetworkRegistry.registerPacket(PacketChargerAction.class);
		UMNetworkRegistry.registerPacket(PacketTitle.class);
		UMNetworkRegistry.registerPacket(PacketRegionRent.class);
		UMNetworkRegistry.registerPacket(PacketGuiRentServerTime.class);

		RegionRights.ALL_RIGHTS.getID(); // doing register (static init)

		RegionModulesRegistry.instance().register(RegionModuleBasic.class, 			0,  15, "basic");
		RegionModulesRegistry.instance().register(RegionModuleMobDamage.class,		1,   8, "mobdamage");
		RegionModulesRegistry.instance().register(RegionModuleExplosion.class,		2,   5, "explosion");
		RegionModulesRegistry.instance().register(RegionModuleMobSpawn.class,		3,   8, "mobspawn");
		RegionModulesRegistry.instance().register(RegionModuleLiquidFlow.class,		4,   5, "liquidflow");
		RegionModulesRegistry.instance().register(RegionModuleSnowFall.class,		5,   4, "snowfall");
		RegionModulesRegistry.instance().register(RegionModuleGreeting.class,		6,   0, "greeting");
		RegionModulesRegistry.instance().register(RegionModuleBarrier.class,		7,   5, "barrier");
		RegionModulesRegistry.instance().register(RegionModuleChunkLoader.class,	8,  10, "chunkloader");
		RegionModulesRegistry.instance().register(RegionModuleFree.class,			9,   0, "free");
		RegionModulesRegistry.instance().register(RegionModuleDiscount.class,		10,  0, "discount");
		RegionModulesRegistry.instance().register(RegionModuleAdmin.class, 			11,  0, "admin");
		RegionModulesRegistry.instance().register(RegionModuleRent.class, 			12,  0, "rent");

		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	abstract void initSided();
}
