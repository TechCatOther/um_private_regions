package org.ultramine.mods.privreg.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class RegionButton extends Gui
{
	protected static final ResourceLocation buttonTextures = new ResourceLocation("privreg:textures/gui/blockregionmain.png");
	protected Minecraft mc;

	protected int x;
	protected int y;

	protected ForgeDirection currentState;

	protected ButtonPolygon top;
	protected ButtonPolygon bottom;
	protected ButtonPolygon plus;
	protected ButtonPolygon minus;
	protected ButtonPolygon north;
	protected ButtonPolygon west;
	protected ButtonPolygon south;
	protected ButtonPolygon east;

	public RegionButton(Minecraft mc, int x, int y)
	{
		this.mc = mc;
		this.x = x;
		this.y = y;

		List<Point> pt = new ArrayList<Point>();
		pt.add(new Point(0, 24));
		pt.add(new Point(9, 24));
		pt.add(new Point(9, 19));
		pt.add(new Point(13, 13));
		pt.add(new Point(20, 8));
		pt.add(new Point(24, 8));
		pt.add(new Point(24, 0));
		pt.add(new Point(16, 1));
		pt.add(new Point(8, 6));
		pt.add(new Point(2, 14));
		top = new ButtonPolygon(pt);

		pt = new ArrayList<Point>();
		pt.add(new Point(0, 24));
		pt.add(new Point(0, 30));
		pt.add(new Point(6, 42));
		pt.add(new Point(15, 48));
		pt.add(new Point(24, 49));
		pt.add(new Point(24, 39));
		pt.add(new Point(17, 37));
		pt.add(new Point(11, 31));
		pt.add(new Point(9, 24));
		bottom = new ButtonPolygon(pt);

		pt = new ArrayList<Point>();
		pt.add(new Point(39, 24));
		pt.add(new Point(48, 25));
		pt.add(new Point(48, 17));
		pt.add(new Point(45, 10));
		pt.add(new Point(39, 4));
		pt.add(new Point(30, 1));
		pt.add(new Point(24, 0));
		pt.add(new Point(24, 8));
		pt.add(new Point(33, 11));
		pt.add(new Point(39, 19));
		plus = new ButtonPolygon(pt);

		pt = new ArrayList<Point>();
		pt.add(new Point(24, 39));
		pt.add(new Point(24, 49));
		pt.add(new Point(34, 48));
		pt.add(new Point(42, 42));
		pt.add(new Point(47, 35));
		pt.add(new Point(49, 24));
		pt.add(new Point(39, 24));
		pt.add(new Point(36, 32));
		pt.add(new Point(29, 37));
		minus = new ButtonPolygon(pt);

		pt = new ArrayList<Point>();
		pt.add(new Point(21, 22));
		pt.add(new Point(25, 20));
		pt.add(new Point(28, 21));
		pt.add(new Point(35, 14));
		pt.add(new Point(29, 9));
		pt.add(new Point(20, 9));
		pt.add(new Point(13, 13));
		north = new ButtonPolygon(pt);

		pt = new ArrayList<Point>();
		pt.add(new Point(13, 13));
		pt.add(new Point(10, 18));
		pt.add(new Point(10, 27));
		pt.add(new Point(14, 35));
		pt.add(new Point(21, 28));
		pt.add(new Point(20, 25));
		pt.add(new Point(21, 21));
		west = new ButtonPolygon(pt);

		pt = new ArrayList<Point>();
		pt.add(new Point(14, 34));
		pt.add(new Point(21, 38));
		pt.add(new Point(29, 38));
		pt.add(new Point(34, 34));
		pt.add(new Point(27, 28));
		pt.add(new Point(25, 29));
		pt.add(new Point(21, 27));
		south = new ButtonPolygon(pt);

		pt = new ArrayList<Point>();
		pt.add(new Point(28, 29));
		pt.add(new Point(34, 34));
		pt.add(new Point(38, 28));
		pt.add(new Point(38, 21));
		pt.add(new Point(35, 14));
		pt.add(new Point(28, 21));
		pt.add(new Point(29, 25));
		pt.add(new Point(28, 28));
		east = new ButtonPolygon(pt);
	}

	public void drawButton(int mx, int my)
	{
		mc.renderEngine.bindTexture(buttonTextures);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(x, y, 0, 166, 50, 50);
		if(plus.mousePressed(mx - x, my - y)) drawTexturedModalRect(x + 36, y + 7, 51, 166, 8, 9);
		if(minus.mousePressed(mx - x, my - y)) drawTexturedModalRect(x + 37, y + 36, 59, 166, 7, 2);
		if(currentState == ForgeDirection.DOWN || bottom.mousePressed(mx - x, my - y))
			drawTexturedModalRect(x + 6, y + 35, 66, 166, 6, 6);
		if(currentState == ForgeDirection.UP || top.mousePressed(mx - x, my - y))
			drawTexturedModalRect(x + 6, y + 8, 72, 166, 6, 7);
		if(currentState == ForgeDirection.NORTH || north.mousePressed(mx - x, my - y))
			drawTexturedModalRect(x + 20, y + 11, 78, 166, 10, 9);
		if(currentState == ForgeDirection.SOUTH || south.mousePressed(mx - x, my - y))
			drawTexturedModalRect(x + 19, y + 29, 88, 166, 12, 9);
		if(currentState == ForgeDirection.WEST || west.mousePressed(mx - x, my - y))
			drawTexturedModalRect(x + 11, y + 20, 100, 166, 9, 10);
		if(currentState == ForgeDirection.EAST || east.mousePressed(mx - x, my - y))
			drawTexturedModalRect(x + 30, y + 20, 110, 166, 9, 10);
	}

	public void mouseClicked(Region region, int x, int y, int bt)
	{
		if(bt == 0)
		{
			if(currentState != ForgeDirection.UP && top.mouseClicked(mc, x - this.x, y - this.y))
				currentState = ForgeDirection.UP;
			if(currentState != ForgeDirection.DOWN && bottom.mouseClicked(mc, x - this.x, y - this.y))
				currentState = ForgeDirection.DOWN;
			if(currentState != ForgeDirection.NORTH && north.mouseClicked(mc, x - this.x, y - this.y))
				currentState = ForgeDirection.NORTH;
			if(currentState != ForgeDirection.WEST && west.mouseClicked(mc, x - this.x, y - this.y))
				currentState = ForgeDirection.WEST;
			if(currentState != ForgeDirection.SOUTH && south.mouseClicked(mc, x - this.x, y - this.y))
				currentState = ForgeDirection.SOUTH;
			if(currentState != ForgeDirection.EAST && east.mouseClicked(mc, x - this.x, y - this.y))
				currentState = ForgeDirection.EAST;

			if(currentState != null && plus.mouseClicked(mc, x - this.x, y - this.y))
				RegionManagerClient.getInstance().expandRegion(region, currentState, 1);
			if(currentState != null && minus.mouseClicked(mc, x - this.x, y - this.y))
				RegionManagerClient.getInstance().expandRegion(region, currentState, -1);
		}
	}
}
