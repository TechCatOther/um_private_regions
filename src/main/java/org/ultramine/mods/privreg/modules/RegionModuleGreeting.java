package org.ultramine.mods.privreg.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.modules.GuiModuleGreeting;
import org.ultramine.mods.privreg.gui.modules.GuiModuleSettings;

public class RegionModuleGreeting extends RegionModule
{
	private static final String NBT_GUEST_ENTERING = "msg_entering";
	private static final String NBT_GUEST_LEAVE = "msg_leave";
	private static final String NBT_OWNERS_ENTERING = "msg_entering_owner";
	private static final String NBT_OWNERS_LEAVE = "msg_leave_owner";

	private String msgGuestEntering = "";
	private String msgGuestLeave = "";
	private String msgOwnersEntering = "";
	private String msgOwnersLeaving = "";

	public String getMsgGuestEntering()
	{
		return msgGuestEntering;
	}

	public void setMsgGuestEntering(String msgGuestEntering)
	{
		this.msgGuestEntering = msgGuestEntering;
	}

	public String getMsgGuestLeave()
	{
		return msgGuestLeave;
	}

	public void setMsgGuestLeave(String msgGuestLeave)
	{
		this.msgGuestLeave = msgGuestLeave;
	}

	public String getMsgOwnersEntering()
	{
		return msgOwnersEntering;
	}

	public void setMsgOwnersEntering(String msgOwnersEntering)
	{
		this.msgOwnersEntering = msgOwnersEntering;
	}

	public String getMsgOwnersLeaving()
	{
		return msgOwnersLeaving;
	}

	public void setMsgOwnersLeaving(String msgOwnersLeaving)
	{
		this.msgOwnersLeaving = msgOwnersLeaving;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiModuleSettings createGuiHandler(GuiRegionModules parent)
	{
		return new GuiModuleGreeting(parent, this);
	}

	@Override
	protected void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString(NBT_GUEST_ENTERING, msgGuestEntering);
		nbt.setString(NBT_GUEST_LEAVE, msgGuestLeave);
		nbt.setString(NBT_OWNERS_ENTERING, msgOwnersEntering);
		nbt.setString(NBT_OWNERS_LEAVE, msgOwnersLeaving);
	}

	@Override
	protected void readFromNBT(NBTTagCompound nbt)
	{
		msgGuestEntering = nbt.getString(NBT_GUEST_ENTERING);
		msgGuestLeave = nbt.getString(NBT_GUEST_LEAVE);
		msgOwnersEntering = nbt.getString(NBT_OWNERS_ENTERING);
		msgOwnersLeaving = nbt.getString(NBT_OWNERS_LEAVE);
	}
}
