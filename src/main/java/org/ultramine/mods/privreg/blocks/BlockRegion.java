package org.ultramine.mods.privreg.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.ultramine.mods.privreg.PrivRegCreativeTab;
import org.ultramine.mods.privreg.tiles.TileBlockRegion;

public class BlockRegion extends BlockContainer
{
	private static int rednerID = RenderingRegistry.getNextAvailableRenderId();

	public BlockRegion()
	{
		super(Material.iron);
		setBlockUnbreakable();
		setResistance(6000000F);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(PrivRegCreativeTab.instance);
		setBlockName("um_privreg_pregion");
		setBlockTextureName("privreg:pregion");
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
		return rednerID;
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileBlockRegion();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is)
	{
		world.setBlockMetadataWithNotify(x, y, z, MathHelper.floor_double((double) ((entity.rotationYaw * 4F) / 360F) + 0.5D) & 3, 3);
		if(!world.isRemote)
		{
			TileBlockRegion te = (TileBlockRegion) world.getTileEntity(x, y, z);
			if(te != null)
				te.onBlockPlacedBy(entity);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int data)
	{
		if(!world.isRemote)
		{
			TileBlockRegion te = (TileBlockRegion)world.getTileEntity(x, y, z);
			if(te != null)
				te.onBlockBreak();
		}
		super.breakBlock(world, x, y, z, block, data);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		return false;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		TileBlockRegion te = (TileBlockRegion) world.getTileEntity(x, y, z);
		if(te != null)
		{
			if(world.isRemote)
			{
				return te.activateClient(player);
			}
			else/**/ if(FMLCommonHandler.instance().getSide().isServer())
			{
				return te.activateServer(player);
			}
		}

		return true;
	}
}
