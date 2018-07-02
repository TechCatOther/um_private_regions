package org.ultramine.mods.privreg.gui.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.ultramine.gui.ElementLabel;
import org.ultramine.gui.ElementTextField;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleGreeting;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class GuiModuleGreeting extends GuiModuleSettings
{
	private final RegionModuleGreeting module;

	private ElementTextField strEnteringGuests;
	private ElementTextField strLeaveGuests;
	private ElementTextField strEnteringOwners;
	private ElementTextField strLeavingOwners;

	public GuiModuleGreeting(GuiRegionModules p, RegionModuleGreeting module)
	{
		super(p);
		this.module = module;
	}

	@Override
	public RegionModule getModule()
	{
		return module;
	}

	@Override
	protected void onClose()
	{
		module.setMsgGuestEntering(strEnteringGuests.getText());
		module.setMsgGuestLeave(strLeaveGuests.getText());
		module.setMsgOwnersEntering(strEnteringOwners.getText());
		module.setMsgOwnersLeaving(strLeavingOwners.getText());
	}

	@Override
	protected void addElements()
	{
		addElement(new ElementLabel(10, 20, tlt("privreg.gui.module.msg.enter")));
		addElement(strEnteringGuests = new ElementTextField(-1, 10, 30, 160, 12));

		addElement(new ElementLabel(10, 45, tlt("privreg.gui.module.msg.leave")));
		addElement(strLeaveGuests = new ElementTextField(-1, 10, 55, 160, 12));

		addElement(new ElementLabel(10, 70, tlt("privreg.gui.module.msg.enterowner")));
		addElement(strEnteringOwners = new ElementTextField(-1, 10, 80, 160, 12));

		addElement(new ElementLabel(10, 95, tlt("privreg.gui.module.msg.leaveowner")));
		addElement(strLeavingOwners = new ElementTextField(-1, 10, 105, 160, 12));

		strEnteringGuests.setText(module.getMsgGuestEntering());
		strLeaveGuests.setText(module.getMsgGuestLeave());
		strEnteringOwners.setText(module.getMsgOwnersEntering());
		strLeavingOwners.setText(module.getMsgOwnersLeaving());
	}
}
