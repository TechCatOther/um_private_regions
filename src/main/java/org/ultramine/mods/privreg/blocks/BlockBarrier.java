package org.ultramine.mods.privreg.blocks;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.modules.RegionModuleBarrier;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.tiles.TileBarrier;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.IRegionManager;
import org.ultramine.server.event.WorldEventProxy;
import org.ultramine.server.event.WorldUpdateObjectType;

import java.util.List;
import java.util.Random;

public class BlockBarrier extends Block implements ITileEntityProvider
{
	private static int rednerID = RenderingRegistry.getNextAvailableRenderId();

	public BlockBarrier()
	{
		super(Material.glass);
		setTickRandomly(true);
		setBlockUnbreakable();
		setResistance(6000000F);
		setStepSound(Block.soundTypeGlass);
		setBlockName("um_privreg_barrier");
		setBlockTextureName("minecraft:stone");
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
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side)
	{
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return super.shouldSideBeRendered(world, x, y, z, side) && world.getBlock(x, y, z) != this;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity)
	{
		IRegionManager mgr = world.isRemote ? RegionManagerClient.getInstance() : PrivateRegions.instance().getServerRegionManager(world.provider.dimensionId);
		Region region = mgr.getRegion(new BlockPos(x, y, z));
		RegionModuleBarrier border = region == null ? null : region.getModuleWithClass(RegionModuleBarrier.class);
		if(border == null)
		{
			if(!world.isRemote)
				breakAt(world, x, y, z);
		}
		else if(world.isRemote)
		{
			if(!(entity instanceof EntityPlayerSP) || !region.hasRight(RegionModuleBarrier.RIGHT_PENETRATE_BORDER))
				addAABB(x, y, z, aabb, list);
		}
		else
		{
			GameProfile profile;
			if(entity == null)
			{
				WorldEventProxy wep = WorldEventProxy.getCurrent();
				if(wep != null && wep.getUpdateObject().getType() == WorldUpdateObjectType.ENTITY)
					profile = wep.getUpdateObject().getEntity().getObjectOwner();
				else
					profile = null;
			}
			else if(entity.isEntityPlayerMP())
				profile = ((EntityPlayerMP)entity).getGameProfile();
			else
				profile = entity.getObjectOwner();
			if(profile == null || !region.hasRight(profile, RegionModuleBarrier.RIGHT_PENETRATE_BORDER))
				addAABB(x, y, z, aabb, list);
		}
	}

	@SuppressWarnings("unchecked")
	private void addAABB(int x, int y, int z, AxisAlignedBB aabb1, List list)
	{
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1);
		if(aabb1.intersectsWith(aabb))
			list.add(aabb);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		if(world.isRemote)
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		IRegionManager mgr = PrivateRegions.instance().getServerRegionManager(world.provider.dimensionId);
		Region region = mgr.getRegion(new BlockPos(x, y, z));
		RegionModuleBarrier border = region == null ? null : region.getModuleWithClass(RegionModuleBarrier.class);
		if(border != null)
		{
			Entity entity = null;
			WorldEventProxy wep = WorldEventProxy.getCurrent();
			if(wep != null && wep.getUpdateObject().getType() == WorldUpdateObjectType.ENTITY)
				entity = wep.getUpdateObject().getEntity();

			GameProfile profile;

			if(entity == null)
				profile = null;
			else if(entity.isEntityPlayerMP())
				profile = ((EntityPlayerMP)entity).getGameProfile();
			else
				profile = entity.getObjectOwner();
			if(profile == null || !region.hasRight(profile, RegionModuleBarrier.RIGHT_PENETRATE_BORDER))
				return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}

		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		if(meta == 1)
			return new TileBarrier();
		return null;
	}

	@Override
	public boolean hasTileEntity(int metadata)
	{
		return metadata == 1;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		Region region = PrivateRegions.instance().getServerRegion(world.provider.dimensionId, x, y, z);
		RegionModuleBarrier border = region == null ? null : region.getModuleWithClass(RegionModuleBarrier.class);
		if(border == null)
			breakAt(world, x, y, z);
	}

	public void placeAt(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		if(block.isAir(world, x, y, z))
		{
			world.setBlockSilently(x, y, z, this, 0, 3);
		}
		else
		{
			int meta = world.getBlockMetadata(x, y, z);
			if(!block.isOpaqueCube() && !block.hasTileEntity(meta) && block != this)
			{
				world.setBlockSilently(x, y, z, this, 1, 3);
				TileBarrier tile = (TileBarrier)world.getTileEntity(x, y, z);
				tile.setTypes(block, meta);
			}
		}
	}

	public void breakAt(World world, int x, int y, int z)
	{
		if(world.getBlock(x, y, z) == this)
		{
			TileBarrier tile = (TileBarrier)world.getTileEntity(x, y, z);
			if(tile != null)
				tile.replace();
			else
				world.setBlockSilently(x, y, z, Blocks.air, 0, 3);
		}
	}
}
