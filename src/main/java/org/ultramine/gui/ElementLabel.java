package org.ultramine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class ElementLabel implements IGuiElement
{
	private final String text;
	private final int xRel;
	private final int yRel;
	private final int color;
	
	private int xPos;
	private int yPos;
	
	public ElementLabel(int x, int y, String text, int color)
	{
		this.xRel = x;
		this.yRel = y;
		this.text = text;
		this.color = color;
	}
	
	public ElementLabel(int x, int y, String text)
	{
		this(x, y, text, 0x404040);
	}

	@Override
	public void init(IActionListener container, int guiWidth, int guiHeight, int guiLeft, int guiTop)
	{
		xPos = xRel + guiLeft;
		yPos = yRel + guiTop;
	}

	@Override
	public void draw(Minecraft mc, int mx, int my)
	{
		mc.fontRenderer.drawString(text, xPos, yPos, color);
	}

	@Override
	public void keyTyped(char c, int code)
	{
		
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonCode)
	{
		
	}

	@Override
	public void update()
	{
		
	}

}
