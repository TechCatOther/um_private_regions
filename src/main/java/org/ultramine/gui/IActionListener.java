package org.ultramine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IActionListener
{
	void actionPerformed(int id, IGuiElement element, Object... data);
}
