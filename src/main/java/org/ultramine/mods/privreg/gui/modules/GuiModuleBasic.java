package org.ultramine.mods.privreg.gui.modules;

import org.apache.commons.lang3.StringUtils;
import org.ultramine.gui.ElementLinkedCheckBox;
import org.ultramine.gui.ElementTextField;
import org.ultramine.mods.privreg.ClientUtils;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleBasic;

import static org.ultramine.util.I18n.tlt;

public class GuiModuleBasic extends GuiModuleSettings
{
	private final RegionModuleBasic module;

	private ElementLinkedCheckBox cbItemMode;
	private ElementTextField items;

	private ElementLinkedCheckBox cbBlockMode;
	private ElementTextField blocks;

	public GuiModuleBasic(GuiRegionModules p, RegionModuleBasic module)
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
		module.setDisableItemsMode(cbItemMode.isChecked());
		module.setDisableItems(ClientUtils.commaSplit(items.getText()));
		module.setDisableBlocksMode(cbBlockMode.isChecked());
		module.setDisableBlocks(ClientUtils.commaSplit(blocks.getText()));
	}

	@Override
	protected void addElements()
	{
		ElementLinkedCheckBox cbItemMode2;
		addElement(cbItemMode = new ElementLinkedCheckBox(-1, 10, 20, tlt("privreg.gui.module.basic.items1")));
		addElement(cbItemMode2 = new ElementLinkedCheckBox(-1, 10, 30, tlt("privreg.gui.module.basic.items2")));
		addElement(items = new ElementTextField(-1, 10, 40, 160, 10));

		cbItemMode.linkTo(cbItemMode2);
		cbItemMode.setChecked(module.getDisableItemsMode());
		cbItemMode2.setChecked(!module.getDisableItemsMode());
		items.setMaxStringLength(256);
		items.setText(StringUtils.join(module.getDisableItems(), ','));

		//

		ElementLinkedCheckBox cbBlockMode2;
		addElement(cbBlockMode = new ElementLinkedCheckBox(-1, 10, 50, tlt("privreg.gui.module.basic.blocks1")));
		addElement(cbBlockMode2 = new ElementLinkedCheckBox(-1, 10, 60, tlt("privreg.gui.module.basic.blocks2")));
		addElement(blocks = new ElementTextField(-1, 10, 70, 160, 10));

		cbBlockMode.linkTo(cbBlockMode2);
		cbBlockMode.setChecked(module.getDisableBlocksMode());
		cbBlockMode2.setChecked(!module.getDisableBlocksMode());
		blocks.setMaxStringLength(256);
		blocks.setText(StringUtils.join(module.getDisableBlocks(), ','));
	}
}
