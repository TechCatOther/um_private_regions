package org.ultramine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public interface IGui
{
	void resizeGui(Minecraft minecraft, int par2, int par3);
	
	void relayout();

	void draw(int mx, int my, float par3);
	
	void keyTyped(char c, int code);
	
	void mouseClicked(int mx, int my, int buttonCode);
	
	void mouseMovedOrUp(int mx, int my, int buttonCode);

	void mouseWheel(int wheel);
	
	void onGuiClosed();
	
	boolean doesGuiPauseGame();
	
	void update();
}
