package org.ultramine.mods.privreg.blocks;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.ultramine.mods.privreg.PrivRegCreativeTab;
import org.ultramine.mods.privreg.tiles.TileRentStand;

public class BlockRentStand extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	private IIcon blockIcon1;

	public BlockRentStand()
	{
		super(Material.iron);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(PrivRegCreativeTab.instance);
		setBlockName("um_privreg_rentstand1");
		setBlockTextureName("privreg:rentstand");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r)
	{
		blockIcon = r.registerIcon("privreg:rentstand1");
		blockIcon1 = r.registerIcon("privreg:rentstand");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return side == 1 || side == 0 ? blockIcon : meta == 2 && side == 2 ? blockIcon1 : (meta == 3 && side == 5 ? blockIcon1 : (meta == 0 && side == 3 ? blockIcon1 : (meta == 1 && side == 4 ? blockIcon1 : blockIcon)));
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
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileRentStand();
	}

	@Override
	public void setBlockBoundsForItemRender()
	{
		setBlockBounds(0.0F, 0.0F, 0.4F, 1.0F, 1.0F, 0.6F);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		switch(world.getBlockMetadata(x, y, z))
		{
			case 3: setBlockBounds(0.875F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F); return;
			case 1: setBlockBounds(0.0F, 0.0F, 0.0F, 0.125F, 1.0F, 1.0F); return;
			case 0: setBlockBounds(0.0F, 0.0F, 0.875F, 1.0F, 1.0F, 1.0F); return;
			case 2: setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125F); return;
		}
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is)
	{
		world.setBlockMetadataWithNotify(x, y, z, MathHelper.floor_double((double) ((entity.rotationYaw * 4F) / 360F) + 0.5D) & 3, 3);
		if(!world.isRemote)
		{
			TileRentStand te = (TileRentStand) world.getTileEntity(x, y, z);
			if(te != null)
				te.onBlockPlacedBy(entity);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		TileRentStand te = (TileRentStand) world.getTileEntity(x, y, z);
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
