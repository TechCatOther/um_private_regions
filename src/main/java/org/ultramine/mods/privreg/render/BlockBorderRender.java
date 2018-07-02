package org.ultramine.mods.privreg.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.ultramine.mods.privreg.modules.RegionModuleBarrier;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;

public class BlockBorderRender implements ISimpleBlockRenderingHandler
{
	private final int rednerID;

	public BlockBorderRender(int rednerID)
	{
		this.rednerID = rednerID;
	}
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		Region region = RegionManagerClient.getInstance().getRegion(x, y, z);
		if(region == null || !region.hasRight(RegionModuleBarrier.RIGHT_PENETRATE_BORDER))
		{
			RegionModuleBarrier border = region == null ? null : region.getModuleWithClass(RegionModuleBarrier.class);
			if(border == null || border.isOpaque())
				return renderer.renderStandardBlock(block, x, y, z);
		}

		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return false;
	}

	@Override
	public int getRenderId()
	{
		return rednerID;
	}
}
