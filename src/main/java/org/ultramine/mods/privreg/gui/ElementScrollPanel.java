package org.ultramine.mods.privreg.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import org.ultramine.gui.GuiUtils;
import org.ultramine.gui.IActionListener;
import org.ultramine.gui.IGuiElement;

import java.util.ArrayList;
import java.util.List;

public class ElementScrollPanel implements IGuiElement
{
	private final List<IGuiElement> elements = new ArrayList<IGuiElement>();
	private final int width;
	private final int height;
	private final int posX;
	private final int posY;
	private final int elementHeight;
	private final int limit;

	private IActionListener container;
	private int guiLeft;
	private int guiTop;

	private int scroll;
	private int maxScroll;

	public ElementScrollPanel(int width, int height, int posX, int posY, int elementHeight)
	{
		this.width = width;
		this.height = height;
		this.posX = posX;
		this.posY = posY;
		this.elementHeight = elementHeight;

		this.limit = height/elementHeight;
	}

	public void addElement(IGuiElement el)
	{
		elements.add(el);
		maxScroll = elements.size() - limit;
	}

	public void clearElements()
	{
		elements.clear();
		scroll = 0;
	}

	public void releyout()
	{
		for(int i = scroll; i < Math.min(scroll+limit, elements.size()); i++)
			elements.get(i).init(container, width, height, guiLeft+posX, guiTop+posY + (i-scroll)*elementHeight);
	}

	@Override
	public void init(IActionListener container, int guiWidth, int guiHeight, int guiLeft, int guiTop)
	{
		this.container = container;
		this.guiLeft = guiLeft;
		this.guiTop = guiTop;
		releyout();
	}

	@Override
	public void draw(Minecraft mc, int mx, int my)
	{
		int size = elements.size();
		if(size > limit)
		{
			int barOffset = MathHelper.ceiling_double_int(height * limit / (double) size);
			int secOffset = MathHelper.ceiling_double_int(height * scroll / (double) size);
			GuiUtils.drawRect(guiLeft+posX+width - 4, guiTop+posY, guiLeft+posX+width, guiTop+posY + height, 0xFFB0B0B0);
			GuiUtils.drawRect(guiLeft+posX+width - 4, guiTop+posY + secOffset, guiLeft+posX+width, guiTop+posY + secOffset + barOffset, 0xFF101010);
		}

		for(int i = scroll; i < Math.min(scroll+limit, elements.size()); i++)
			elements.get(i).draw(mc, mx, my);
	}

	public void mouseWheel(int wheel)
	{
		if(wheel != 0)
		{
			if(wheel < 0) scroll++;
			if(wheel > 0) scroll--;
			if(scroll > maxScroll) scroll = maxScroll;
			if(scroll < 0) scroll = 0;
			releyout();
		}
	}

	@Override
	public void keyTyped(char c, int code)
	{
		for(int i = scroll; i < Math.min(scroll+limit, elements.size()); i++)
			elements.get(i).keyTyped(c, code);
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonCode)
	{
		for(int i = scroll; i < Math.min(scroll+limit, elements.size()); i++)
			elements.get(i).mouseClicked(mx, my, buttonCode);
	}

	@Override
	public void update()
	{
		for(int i = scroll; i < Math.min(scroll+limit, elements.size()); i++)
			elements.get(i).update();
	}
}
