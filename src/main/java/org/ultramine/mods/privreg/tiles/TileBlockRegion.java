package org.ultramine.mods.privreg.tiles;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.gui.IGui;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.inv.ContainerBlockRegion;
import org.ultramine.mods.privreg.gui.GuiBlockRegion;
import org.ultramine.mods.privreg.gui.inv.ContainerRegionModules;
import org.ultramine.mods.privreg.modifications.RegionModification;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.packets.PacketRegionAction;
import org.ultramine.mods.privreg.packets.PacketTEBlockRegion;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionCreationException;
import org.ultramine.mods.privreg.regions.RegionManager;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.network.ITEPacketHandler;
import org.ultramine.server.util.InventoryUtil;
import org.ultramine.util.IHasGui;

public class TileBlockRegion extends TileEntity implements ITEPacketHandler<PacketTEBlockRegion>, IHasGui
{
	private static final Logger log = LogManager.getLogger();
	private static final boolean isClient = FMLCommonHandler.instance().getSide().isClient();
	public static final int GUI_MAIN_ID			= 0;
	public static final int GUI_MODULES_ID		= 1;

	private Region region;
	private boolean remove;

	@SideOnly(Side.CLIENT)
	private int id;

	public TileBlockRegion()
	{
		if(isClient)
			id = -1;
	}

	private RegionManager getRegionManager()
	{
		return PrivateRegions.instance().getServerRegionManager(worldObj.provider.dimensionId);
	}

	public Region getRegion()
	{
		return region;
	}

	public void unsafeSetRegion(Region region)
	{
		this.region = region;
		this.remove = false;
	}

	@SideOnly(Side.SERVER)
	public void onBlockPlacedBy(EntityLivingBase entity) //Server side only
	{
		if(!(entity instanceof EntityPlayerMP))
		{
			remove = true;
			return;
		}

		try {
			region = getRegionManager().createRegion(this, ((EntityPlayerMP) entity).getGameProfile());
			new PacketRegionAction(region.getID(), PacketRegionAction.CLIENT_RENDER).sendTo((EntityPlayerMP)entity);
		} catch(RegionCreationException e) {
			remove = true;
			((EntityPlayerMP) entity).addChatComponentMessage(new ChatComponentTranslation("privreg.msg.createfail").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
		}
	}

	@SideOnly(Side.SERVER)
	public void onBlockBreak() //Server side only
	{
		if(region != null)
		{
			for(RegionModule mod : region.getModulesStorage())
				InventoryUtil.dropItem(worldObj, xCoord, yCoord, zCoord, mod.toItemStack());
			for(RegionModification mod : region.getModifications())
				InventoryUtil.dropItem(worldObj, xCoord, yCoord, zCoord, mod.toItemStack());

			getRegionManager().destroyRegion(region);
		}
		region = null;
	}

	@SideOnly(Side.CLIENT)
	public boolean activateClient(EntityPlayer player)
	{
		return true;
	}

	@SideOnly(Side.SERVER)
	public boolean activateServer(EntityPlayer player)
	{
		if(region != null && region.hasRight(player.getGameProfile(), RegionRights.OPEN_BLOCK))
		{
			player.openGui(PrivateRegions.instance(), GUI_MAIN_ID, worldObj, xCoord, yCoord, zCoord);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IGui getGui(int id, EntityPlayer player)
	{
		switch (id)
		{
			case GUI_MAIN_ID:		return new GuiBlockRegion(this, player);
			case GUI_MODULES_ID:	return new GuiRegionModules(player, region);
		}
		return null;
	}

	@Override
	public Container getGuiContainer(int id, EntityPlayer player)
	{
		switch (id)
		{
			case GUI_MAIN_ID:		return new ContainerBlockRegion(this);
			case GUI_MODULES_ID:	return new ContainerRegionModules(player.inventory, region);
		}
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if(!remove)
		{
			int id = nbt.getInteger("rid");
			region = PrivateRegions.instance().getServerRegion(nbt.getInteger("dim"), id);
			if(region == null)
			{
				log.warn("Region with ID:{} was not found for block({}, {}, {}). Removing block", id, xCoord, yCoord, zCoord);
				remove = true;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if(region != null && !remove)
		{
			nbt.setInteger("rid", region.getID());
			nbt.setInteger("dim", worldObj.provider.dimensionId);
		}
	}

	@Override
	public void updateEntity()
	{
		if(worldObj.isRemote)
		{
			if(id != -1)
			{
				region = RegionManagerClient.getInstance().getRegion(id);
				if(region != null)
					id = -1;
			}
		}
		else
		{
			if(region == null && !remove)
			{
				log.warn("null region object at block({}, {}, {}) while updating TileEntity. Removing block", xCoord, yCoord, zCoord);
				remove = true;
			}

			if(remove)
			{
				worldObj.func_147480_a(xCoord, yCoord, zCoord, true);
			}
		}
	}

	@Override
	public Packet getDescriptionPacket()
	{
		if(region == null)
		{
			if(!remove)
				log.warn("null region object at block({}, {}, {}) on getDescriptionPacket", xCoord, yCoord, zCoord);
			return null;
		}
		else
		{
			return new PacketTEBlockRegion(region.getID()).form(this).toServerPacket();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handlePacketClient(PacketTEBlockRegion pkt)
	{
		this.id = pkt.getRegionID();
	}

	@Override
	public void handlePacketServer(PacketTEBlockRegion pkt, EntityPlayerMP player)
	{

	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getBoundingBox(xCoord - 2, yCoord, zCoord - 2, xCoord + 2, yCoord, zCoord + 2);
	}
}
