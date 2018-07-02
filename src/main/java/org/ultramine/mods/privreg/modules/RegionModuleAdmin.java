package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.modules.GuiModuleAdmin;
import org.ultramine.mods.privreg.gui.modules.GuiModuleSettings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RegionModuleAdmin extends RegionModule
{
	private static final String NBT_BLOCK_DROPS = "bd";
	private static final String NBT_MOB_DROPS = "md";
	private static final String NBT_PLAYER_DROPS = "pd";

	private static final String NBT_COMMANDS_MODE = "cm";
	private static final String NBT_COMMANDS = "cs";

	private boolean disableBlockDrops;
	private boolean disableMobDrops;
	private boolean disablePlayerDrops;

	private boolean disableCommandsMode = true;
	private Set<String> disableCommands = new HashSet<String>();

	public boolean isDisableBlockDrops()
	{
		return disableBlockDrops;
	}

	public void setDisableBlockDrops(boolean disableBlockDrops)
	{
		this.disableBlockDrops = disableBlockDrops;
	}

	public boolean isDisableMobDrops()
	{
		return disableMobDrops;
	}

	public void setDisableMobDrops(boolean disableMobDrops)
	{
		this.disableMobDrops = disableMobDrops;
	}

	public boolean isDisablePlayerDrops()
	{
		return disablePlayerDrops;
	}

	public void setDisablePlayerDrops(boolean disablePlayerDrops)
	{
		this.disablePlayerDrops = disablePlayerDrops;
	}

	public boolean getDisableCommandsMode()
	{
		return disableCommandsMode;
	}

	public void setDisableCommandsMode(boolean disableCommandsMode)
	{
		this.disableCommandsMode = disableCommandsMode;
	}

	public Set<String> getDisableCommands()
	{
		return disableCommands;
	}

	public void setDisableCommands(String[] disableCommands)
	{
		this.disableCommands.clear();
		Collections.addAll(this.disableCommands, disableCommands);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiModuleSettings createGuiHandler(GuiRegionModules parent)
	{
		return new GuiModuleAdmin(parent, this);
	}

	@Override
	protected void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean(NBT_BLOCK_DROPS, disableBlockDrops);
		nbt.setBoolean(NBT_MOB_DROPS, disableMobDrops);
		nbt.setBoolean(NBT_PLAYER_DROPS, disablePlayerDrops);

		nbt.setBoolean(NBT_COMMANDS_MODE, disableCommandsMode);
		NBTTagList list = new NBTTagList();
		for(String str : disableCommands)
			list.appendTag(new NBTTagString(str));
		nbt.setTag(NBT_COMMANDS, list);
	}

	@Override
	protected void readFromNBT(NBTTagCompound nbt)
	{
		disableCommands.clear();
		disableBlockDrops = nbt.getBoolean(NBT_BLOCK_DROPS);
		disableMobDrops = nbt.getBoolean(NBT_MOB_DROPS);
		disablePlayerDrops = nbt.getBoolean(NBT_PLAYER_DROPS);

		if(nbt.hasKey(NBT_COMMANDS_MODE))
			disableCommandsMode = nbt.getBoolean(NBT_COMMANDS_MODE);
		NBTTagList list = nbt.getTagList(NBT_COMMANDS, 8);
		for(int i = 0; i < list.tagCount(); i++)
			disableCommands.add(list.getStringTagAt(i));
	}

	@SideOnly(Side.SERVER)
	public void onBlockHarvest(BlockEvent.HarvestDropsEvent e)
	{
		if(disableBlockDrops)
			e.dropChance = -1.0f;
	}

	@SideOnly(Side.SERVER)
	public void onLivingDrops(LivingDropsEvent e)
	{
		if(e instanceof PlayerDropsEvent)
		{
			if(disablePlayerDrops)
				e.setCanceled(true);
		}
		else
		{
			if(disableMobDrops)
				e.setCanceled(true);
		}
	}

	@SideOnly(Side.SERVER)
	public void onPlayerItemToss(ItemTossEvent e)
	{
		if(disablePlayerDrops)
			e.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	public void onPlayerCommand(CommandEvent e)
	{
		EntityPlayerMP player = (EntityPlayerMP)e.sender;
		if(player.hasPermission(PrivateRegions.ADMIN_PERMISSION))
			return;
		if(disableCommandsMode == disableCommands.contains(e.command.getCommandName()))
		{
			e.setCanceled(true);
			e.exception = new CommandException("privreg.msg.command");
		}
	}
}
