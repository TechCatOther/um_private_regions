package org.ultramine.mods.privreg.gui.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.ultramine.gui.ElementCheckBox;
import org.ultramine.gui.ElementLinkedCheckBox;
import org.ultramine.gui.ElementTextField;
import org.ultramine.mods.privreg.ClientUtils;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleAdmin;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class GuiModuleAdmin extends GuiModuleSettings
{
	private final RegionModuleAdmin module;

	private ElementCheckBox cbBlockDrops;
	private ElementCheckBox cbMobDrops;
	private ElementCheckBox cbPlayerDrops;

	private ElementLinkedCheckBox cbCommandMode;
	private ElementTextField commands;

	public GuiModuleAdmin(GuiRegionModules p, RegionModuleAdmin module)
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
		module.setDisableBlockDrops(cbBlockDrops.isChecked());
		module.setDisableMobDrops(cbMobDrops.isChecked());
		module.setDisablePlayerDrops(cbPlayerDrops.isChecked());

		module.setDisableCommandsMode(cbCommandMode.isChecked());
		module.setDisableCommands(ClientUtils.commaSplit(commands.getText()));
	}

	@Override
	protected void addElements()
	{
		addElement(cbBlockDrops = new ElementCheckBox(-1, 10, 20, tlt("privreg.gui.module.admin.disblockdrops")));
		addElement(cbMobDrops = new ElementCheckBox(-1, 10, 30, tlt("privreg.gui.module.admin.dismobdrops")));
		addElement(cbPlayerDrops = new ElementCheckBox(-1, 10, 40, tlt("privreg.gui.module.admin.displayerdrops")));

		ElementLinkedCheckBox cbCommandMode2;
		addElement(cbCommandMode = new ElementLinkedCheckBox(-1, 10, 50, tlt("privreg.gui.module.admin.cmd1")));
		addElement(cbCommandMode2 = new ElementLinkedCheckBox(-1, 10, 60, tlt("privreg.gui.module.admin.cmd2")));
		addElement(commands = new ElementTextField(-1, 10, 70, 160, 10));

		cbBlockDrops.setChecked(module.isDisableBlockDrops());
		cbMobDrops.setChecked(module.isDisableMobDrops());
		cbPlayerDrops.setChecked(module.isDisablePlayerDrops());

		cbCommandMode.linkTo(cbCommandMode2);
		cbCommandMode.setChecked(module.getDisableCommandsMode());
		cbCommandMode2.setChecked(!module.getDisableCommandsMode());
		commands.setText(StringUtils.join(module.getDisableCommands(), ','));
	}
}
