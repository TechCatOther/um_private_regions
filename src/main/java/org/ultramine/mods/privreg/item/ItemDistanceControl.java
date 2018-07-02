package org.ultramine.mods.privreg.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.ultramine.mods.privreg.PrivRegCreativeTab;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.mods.privreg.render.DistanceControlGuiRender;
import org.ultramine.mods.privreg.tiles.TileBlockRegion;
import org.ultramine.server.util.InventoryUtil;

public class ItemDistanceControl extends Item
{
	public ItemDistanceControl()
	{
		setNoRepair();
		setMaxStackSize(1);
		setUnlocalizedName("um_privreg_distancecontrol");
		setTextureName("privreg:distancecontrol");
		setCreativeTab(PrivRegCreativeTab.instance);
	}

	@Override
	public boolean onItemUseFirst(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if(te instanceof TileBlockRegion)
			{
				Region region = ((TileBlockRegion)te).getRegion();
				if(region.hasRight(player.getGameProfile(), RegionRights.OPEN_BLOCK) && region.hasRight(player.getGameProfile(), RegionRights.RESIZE))
				{
					if (is.stackTagCompound == null)
						is.stackTagCompound = new NBTTagCompound();
					is.stackTagCompound.setInteger("region", region.getID());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player)
	{
		if(world.isRemote && is.stackTagCompound != null)
		{
			Region regin = RegionManagerClient.getInstance().getRegion(is.stackTagCompound.getInteger("region"));
			if(regin == null || !regin.hasRight(RegionRights.OPEN_BLOCK) || !regin.hasRight(RegionRights.RESIZE))
				return is;

			int slot = -1;
			for(int i = 0; i < 9; i++)
			{
				ItemStack pis = player.inventory.getStackInSlot(i);
				if(pis != null && InventoryUtil.isStacksEquals(is, pis))
				{
					slot = i;
					break;
				}
			}
			if(slot == -1)
				return is;

			DistanceControlGuiRender.toggle(regin, player, slot);
		}

		return is;
	}
}
