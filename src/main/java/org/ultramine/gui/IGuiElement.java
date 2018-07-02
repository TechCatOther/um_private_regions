package org.ultramine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public interface IGuiElement
{
	void init(IActionListener container, int guiWidth, int guiHeight, int guiLeft, int guiTop);

	void draw(Minecraft mc, int mx, int my);
	
	void keyTyped(char c, int code);
	
	void mouseClicked(int mx, int my, int buttonCode);
	
	void update();
}
