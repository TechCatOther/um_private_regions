package org.ultramine.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class GuiBase implements IGui, IActionListener
{
	protected Minecraft mc = Minecraft.getMinecraft();
	
	public int width;
	public int height;
	
	protected int xSize;
	protected int ySize;
	protected int guiLeft;
	protected int guiTop;
	
	protected List<IGuiElement> controlList = new ArrayList<IGuiElement>();

	@Override
	public final void resizeGui(Minecraft mc, int width, int height)
	{
		this.mc = mc;
		this.width = width;
		this.height = height;
		this.guiLeft = (this.width - this.xSize) >> 1;
		this.guiTop = (this.height - this.ySize) >> 1;
		
		for (int i = 0, s = controlList.size(); i < s; ++i)
		{
			controlList.get(i).init(this, xSize, ySize, guiLeft, guiTop);
		}
	}

	@Override
	public void relayout()
	{
		
	}

	@Override
	public void draw(int mx, int my, float par3)
	{
		for(int i = 0, s = controlList.size(); i < s; ++i)
		{
			controlList.get(i).draw(mc, mx, my);
		}
	}

	@Override
	public void keyTyped(char c, int code)
	{
		if(code == 1)
		{
			this.mc.displayGuiScreen(null);
		}
		else
		{
			for (int i = 0, s = controlList.size(); i < s; ++i)
				controlList.get(i).keyTyped(c, code);
		}
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonCode)
	{
		for (IGuiElement el : new ArrayList<IGuiElement>(controlList))
		{
			el.mouseClicked(mx, my, buttonCode);
		}
	}

	@Override
	public void mouseMovedOrUp(int mx, int my, int buttonCode)
	{
		
	}

	@Override
	public void mouseWheel(int wheel)
	{

	}

	@Override
	public void onGuiClosed()
	{
		
	}

	protected void addElement(IGuiElement element)
	{
		if(mc != null)
			element.init(this, xSize, ySize, guiLeft, guiTop);
		controlList.add(element);
	}
	
	protected void addElements(Collection<IGuiElement> elements)
	{
		controlList.addAll(elements);
		if(mc != null)
			for(IGuiElement el : elements)
				el.init(this, xSize, ySize, guiLeft, guiTop);
	}
	
	protected void removeElement(IGuiElement element)
	{
		controlList.remove(element);
	}
	
	protected void removeElements(Collection<IGuiElement> elements)
	{
		controlList.removeAll(elements);
	}

	@Override
	public void actionPerformed(int id, IGuiElement element, Object... data)
	{
		
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void update()
	{
		for (int i = 0, s = controlList.size(); i < s; ++i)
		{
			controlList.get(i).update();
		}
	}

	protected void setSize(int w, int h)
	{
		xSize = w;
		ySize = h;
		this.guiLeft = (this.width - this.xSize) >> 1;
		this.guiTop = (this.height - this.ySize) >> 1;
	}
	
	public final void drawString(int x, int y, String str)
	{
		drawString(str, x, y, 0x404040);
	}
	
	public final void drawString(String str, int x, int y, int color)
	{
		mc.fontRenderer.drawString(str, guiLeft + x, guiTop + y , color);
	}
}
