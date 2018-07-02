package org.ultramine.gui;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public final class GuiScreenToGui extends GuiScreen
{
	private int eventButton;
	private long lastMouseEvent;
	private int field_146298_h;

	public final IGui gui;
	
	public GuiScreenToGui(IGui igui)
	{
		gui = igui;
	}
	
	@Override
	public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3)
	{
		gui.resizeGui(par1Minecraft, par2, par3);
		super.setWorldAndResolution(par1Minecraft, par2, par3);
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		gui.relayout();
	}

	@Override
	public void drawScreen(int mx, int my, float par3)
	{
		gui.draw(mx, my, par3);
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		gui.keyTyped(c, code);
	}

	@Override
	protected void mouseClicked(int mx, int my, int buttonCode)
	{
		gui.mouseClicked(mx, my, buttonCode);
	}

	@Override
	protected void mouseMovedOrUp(int mx, int my, int buttonCode)
	{
		gui.mouseMovedOrUp(mx, my, buttonCode);
	}

	@Override
	public void updateScreen()
	{
		gui.update();
	}

	@Override
	public void onGuiClosed()
	{
		gui.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return gui.doesGuiPauseGame();
	}

	@Override
	public void handleMouseInput()
	{
		int wheel = Mouse.getEventDWheel();
		if(wheel != 0)
			handleMouseWheel(wheel);
		int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int k = Mouse.getEventButton();

		if (Mouse.getEventButtonState())
		{
			if (this.mc.gameSettings.touchscreen && this.field_146298_h++ > 0)
			{
				return;
			}

			this.eventButton = k;
			this.lastMouseEvent = Minecraft.getSystemTime();
			this.mouseClicked(i, j, this.eventButton);
		}
		else if (k != -1)
		{
			if (this.mc.gameSettings.touchscreen && --this.field_146298_h > 0)
			{
				return;
			}

			this.eventButton = -1;
			this.mouseMovedOrUp(i, j, k);
		}
		else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
		{
			long l = Minecraft.getSystemTime() - this.lastMouseEvent;
			this.mouseClickMove(i, j, this.eventButton, l);
		}
	}

	private void handleMouseWheel(int wheel)
	{
		gui.mouseWheel(wheel);
	}
}
