package org.ultramine.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiUtils
{
	public static final RenderItem itemRenderer = new RenderItem();
	
	public static void closeGUI()
	{
		Minecraft.getMinecraft().displayGuiScreen(null);
	}
	
	public static void openGUI(IGui gui)
	{
		Minecraft.getMinecraft().displayGuiScreen(new GuiScreenToGui(gui));
	}
	
	private static double zLevel = 0.0F;

	public static void drawHorizontalLine(int par1, int par2, int par3, int par4)
	{
		if (par2 < par1)
		{
			int var5 = par1;
			par1 = par2;
			par2 = var5;
		}

		drawRect(par1, par3, par2 + 1, par3 + 1, par4);
	}

	public static void drawVerticalLine(int par1, int par2, int par3, int par4)
	{
		if (par3 < par2)
		{
			int var5 = par2;
			par2 = par3;
			par3 = var5;
		}

		drawRect(par1, par2 + 1, par1 + 1, par3, par4);
	}

	/**
	 * Draws a solid color rectangle with the specified coordinates and color.
	 */
	public static void drawRect(int par1, int par2, int par3, int par4, int par5)
	{
		int var5;

		if (par1 < par3)
		{
			var5 = par1;
			par1 = par3;
			par3 = var5;
		}

		if (par2 < par4)
		{
			var5 = par2;
			par2 = par4;
			par4 = var5;
		}

		float var10 = (float)(par5 >> 24 & 255) / 255.0F;
		float var6 = (float)(par5 >> 16 & 255) / 255.0F;
		float var7 = (float)(par5 >> 8 & 255) / 255.0F;
		float var8 = (float)(par5 & 255) / 255.0F;
		Tessellator var9 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(var6, var7, var8, var10);
		var9.startDrawingQuads();
		var9.addVertex((double)par1, (double)par4, 0.0D);
		var9.addVertex((double)par3, (double)par4, 0.0D);
		var9.addVertex((double)par3, (double)par2, 0.0D);
		var9.addVertex((double)par1, (double)par2, 0.0D);
		var9.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors.
	 */
	public static void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6)
	{
		float var7 = (float)(par5 >> 24 & 255) / 255.0F;
		float var8 = (float)(par5 >> 16 & 255) / 255.0F;
		float var9 = (float)(par5 >> 8 & 255) / 255.0F;
		float var10 = (float)(par5 & 255) / 255.0F;
		float var11 = (float)(par6 >> 24 & 255) / 255.0F;
		float var12 = (float)(par6 >> 16 & 255) / 255.0F;
		float var13 = (float)(par6 >> 8 & 255) / 255.0F;
		float var14 = (float)(par6 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator var15 = Tessellator.instance;
		var15.startDrawingQuads();
		var15.setColorRGBA_F(var8, var9, var10, var7);
		var15.addVertex((double)par3, (double)par2, zLevel);
		var15.addVertex((double)par1, (double)par2, zLevel);
		var15.setColorRGBA_F(var12, var13, var14, var11);
		var15.addVertex((double)par1, (double)par4, zLevel);
		var15.addVertex((double)par3, (double)par4, zLevel);
		var15.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
	 */
	public static void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6)
	{
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
		var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
		var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
		var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
		var9.draw();
	}
	
	public static void drawTexturedModelRectFromIcon(int par1, int par2, IIcon par3Icon, int par4, int par5)
	{
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMaxV());
		tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMaxV());
		tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMinV());
		tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMinV());
		tessellator.draw();
	}
	
	public static void drawString(String str, int x, int y, int color)
	{
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(str, x, y, color);
	}
	public static void drawCenteredString(FontRenderer fr, String str, int x, int y, int color)
	{
		fr.drawString(str, x - fr.getStringWidth(str) / 2, y, color);
	}
	public static void drawCenteredString(String str, int x, int y, int color)
	{
		drawCenteredString(Minecraft.getMinecraft().fontRenderer, str, x, y, color);
	}
	
	public static void drawDefaultBackground(int width, int height)
	{
		drawGradientRect(0, 0, width, height, -1072689136, -804253680);
	}
	
	public static void drawHoveringText(List<String> lines, int x, int y, FontRenderer font, int width, int height)
	{
		if (!lines.isEmpty())
		{
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int k = 0;

			for(String s : lines)
			{
				int l = font.getStringWidth(s);

				if (l > k)
				{
					k = l;
				}
			}

			int j2 = x + 12;
			int k2 = y - 12;
			int i1 = 8;

			if (lines.size() > 1)
			{
				i1 += 2 + (lines.size() - 1) * 10;
			}

			if (j2 + k > width)
			{
				j2 -= 28 + k;
			}

			if (k2 + i1 + 6 > height)
			{
				k2 = height - i1 - 6;
			}

			zLevel = 300.0F;
			itemRenderer.zLevel = 300.0F;
			int j1 = -267386864;
			drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
			drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
			drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
			drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
			drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
			int k1 = 1347420415;
			int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
			drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
			drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
			drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
			drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

			for (int i2 = 0; i2 < lines.size(); ++i2)
			{
				String s1 = (String)lines.get(i2);
				font.drawStringWithShadow(s1, j2, k2, -1);

				if (i2 == 0)
				{
					k2 += 2;
				}

				k2 += 10;
			}

			zLevel = 0.0F;
			itemRenderer.zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

	public static void drawToolTip(ItemStack is, int x, int y, int width, int height)
	{
		@SuppressWarnings("unchecked")
		List<String> lines = is.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
		drawToolTip(is, lines, x, y, width, height);
	}

	public static void drawToolTip(ItemStack is, List<String> lines, int x, int y, int width, int height)
	{
		for (int k = 0; k < lines.size(); ++k)
		{
			if (k == 0)
			{
				lines.set(k, is.getRarity().rarityColor + (String) lines.get(k));
			}
			else
			{
				lines.set(k, EnumChatFormatting.GRAY + (String) lines.get(k));
			}
		}

		FontRenderer font1 = is.getItem().getFontRenderer(is);
		drawHoveringText(lines, x, y, (font1 == null ? Minecraft.getMinecraft().fontRenderer : font1), width, height);
	}
}
