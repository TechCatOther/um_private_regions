package org.ultramine.mods.privreg.item;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.ultramine.mods.privreg.Action;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.PrivRegCreativeTab;
import org.ultramine.mods.privreg.packets.PacketRegionOwner;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.mods.privreg.tiles.TileBlockRegion;

import java.util.List;
import java.util.UUID;

import static org.ultramine.util.I18n.tlt;

public class ItemBiometricCard extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon itemIcon1;

	public ItemBiometricCard()
	{
		setNoRepair();
		setMaxDamage(0);
		setMaxStackSize(1);
		setTextureName("privreg:biocard");
		setUnlocalizedName("um_privreg_biocard");
		setCreativeTab(PrivRegCreativeTab.instance);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister r)
	{
		itemIcon = r.registerIcon("privreg:biocard");
		itemIcon1 = r.registerIcon("privreg:biocard_full");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int data)
	{
		return data == 0 ? itemIcon : itemIcon1;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean b)
	{
		list.add(tlt("item.um_privreg_biocard.desc1"));
		list.add(tlt("item.um_privreg_biocard.desc2"));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
	{
		if(p.isSneaking())
		{
			encode(is, p);
			p.swingItem();
			return is;
		}

		return is;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer par2EntityPlayer, EntityLivingBase target)
	{
		if(target instanceof EntityPlayer && !par2EntityPlayer.isSneaking())
		{
			if(par2EntityPlayer.capabilities.isCreativeMode)
				is = par2EntityPlayer.getCurrentEquippedItem();
			encode(is, (EntityPlayer) target);
			par2EntityPlayer.swingItem();
			return true;
		}
		return false;
	}

	@Override
	public boolean onItemUseFirst(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float ox, float oy, float oz)
	{
		if(world.isRemote)
			return false;
		if(world.getBlock(x, y, z) == InitCommon.region)
		{
			TileBlockRegion te = (TileBlockRegion)world.getTileEntity(x, y, z);
			if(te != null && te.getRegion() != null && te.getRegion().hasRight(player.getGameProfile(), RegionRights.EDIT_USERS))
			{
				GameProfile profile = getProfile(is);
				if(profile != null && te.getRegion().getOwnerStorage().add(profile))
				{
					te.getRegion().sendToListeners(new PacketRegionOwner(te.getRegion(), te.getRegion().getOwnerStorage().getOwner(profile), Action.ADD));
					is.stackSize--;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getItemStackDisplayName(ItemStack is)
	{
		GameProfile profile = this.getProfile(is);
		return profile != null ? super.getItemStackDisplayName(is) + " - " + profile.getName() : super.getItemStackDisplayName(is);
	}

	public void encode(ItemStack is, EntityPlayer p)
	{
		GameProfile profile = getProfile(is);

		if(profile != null && profile.equals(p.getGameProfile()))
			setProfile(is, null);
		else
			setProfile(is, p.getGameProfile());
	}

	public void setProfile(ItemStack is, GameProfile profile)
	{
		if(profile == null)
		{
			if(is.hasTagCompound())
				is.getTagCompound().removeTag("profile");
			is.setItemDamage(0);
			return;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("n", profile.getName());
		nbt.setLong("m", profile.getId().getMostSignificantBits());
		nbt.setLong("l", profile.getId().getLeastSignificantBits());
		is.setTagInfo("profile", nbt);
		is.setItemDamage(1);
	}

	public GameProfile getProfile(ItemStack is)
	{
		if(!is.hasTagCompound())
			return null;
		if(!is.getTagCompound().hasKey("profile"))
			return null;
		NBTTagCompound nbt = is.getTagCompound().getCompoundTag("profile");
		return new GameProfile(new UUID(nbt.getLong("m"), nbt.getLong("l")), nbt.getString("n"));
	}
}
