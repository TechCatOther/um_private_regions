package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidRegistry;
import org.ultramine.server.event.SetBlockEvent;
import org.ultramine.server.event.WorldEventProxy;
import org.ultramine.server.event.WorldUpdateObject;
import org.ultramine.server.event.WorldUpdateObjectType;

public class RegionModuleLiquidFlow extends RegionModule
{
	@SideOnly(Side.SERVER)
	public void onBlockChange(SetBlockEvent e)
	{
		if(prohibitRaw())
			e.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	public void onBlockHarvest(BlockEvent.HarvestDropsEvent e)
	{
		if(prohibitRaw())
			e.dropChance = -1.0f;
	}

	@SideOnly(Side.SERVER)
	private boolean prohibitRaw()
	{
		WorldEventProxy proxy = WorldEventProxy.getCurrent();
		if(proxy == null)
			return false;
		WorldUpdateObject obj = proxy.getUpdateObject();
		if(obj.getType() == WorldUpdateObjectType.BLOCK_PENDING || obj.getType() == WorldUpdateObjectType.BLOCK_RANDOM)
		{
			Block block = proxy.getWorld().getBlock(obj.getX(), obj.getY(), obj.getZ());
			if(FluidRegistry.lookupFluidForBlock(block) != null || FluidRegistry.lookupFluidForBlock(Block.getBlockById(Block.getIdFromBlock(block) + 1)) != null)
				return true;
		}

		return false;
	}
}
