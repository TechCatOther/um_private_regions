package org.ultramine.mods.privreg.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.ultramine.mods.privreg.PrivRegCreativeTab;
import org.ultramine.mods.privreg.tiles.TileCharger;

public class BlockCharger extends BlockContainer
{
	public BlockCharger()
	{
		super(Material.iron);
		setBlockUnbreakable();
		setResistance(6000000F);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(PrivRegCreativeTab.instance);
		setBlockName("um_privreg_charger");
		setBlockTextureName("privreg:amcharger");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileCharger();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		TileCharger te = (TileCharger) world.getTileEntity(x, y, z);
		if(te != null)
		{
			if(world.isRemote)
				return true;
			else
				return te.activateServer(player);
		}

		return false;
	}
}
