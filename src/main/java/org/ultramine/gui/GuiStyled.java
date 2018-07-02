package org.ultramine.gui;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiStyled extends GuiBase
{
	protected ResourceLocation background;
	
	@Override
	public void draw(int mx, int my, float par3)
	{
		drawBackground(mx, my);
		super.draw(mx, my, par3);
		drawForeground(mx, my);
	}
	
	protected void drawBackground(int mx, int my)
	{
		drawBG(mx, my);
	}
	
	protected void drawBG(int mx, int my)
	{
		GuiUtils.drawDefaultBackground(width, height);
		if(background != null)
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			mc.renderEngine.bindTexture(background);
			GuiUtils.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}
	
	protected void drawForeground(int mx, int my){}
	
	protected void setBG(ResourceLocation str){background = str;}
}
