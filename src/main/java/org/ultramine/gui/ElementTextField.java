package org.ultramine.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElementTextField implements IGuiElement
{
	private final FontRenderer fontRenderer;
	protected final int id;
	private final int xRel;
	private final int yRel;
	private final int width;
	private final int height;
	
	private int xPos;
	private int yPos;
	
	private String text = "";
	private int maxStringLength = 48;
	private int cursorCounter;
	private boolean enableBackgroundDrawing = true;
	
	private boolean canLoseFocus = true;
	private boolean isFocused = false;
	
	private boolean isEnabled = true;
	private int textSelectionStartPoint = 0;
	private int cursorPoint = 0;
	private int textSelectionEndPoint = 0;
	private int enabledColor = 14737632;
	private int disabledColor = 7368816;
	
	private IActionListener actionListener;
	
	private String filter = null;

	public ElementTextField(int id, int x, int y, int w, int h)
	{
		this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
		this.id = id;
		this.xRel = x;
		this.yRel = y;
		this.width = w;
		this.height = h;
	}
	
	@Override
	public void init(IActionListener container, int guiWidth, int guiHeight, int guiLeft, int guiTop)
	{
		this.actionListener = container;
		this.xPos = xRel + guiLeft;
		this.yPos = yRel + guiTop;
	}

	@Override
	public void draw(Minecraft minecraft, int mx, int my)
	{
		drawTextBox();
	}

	@Override
	public void update()
	{
		++this.cursorCounter;
	}
	
	public void setFilterString(String str)
	{
		filter = str;
	}
	public String filerAllowedCharacters(String par0Str)
	{
		if(filter == null) return par0Str;
			
		StringBuilder var1 = new StringBuilder();
		char[] var2 = par0Str.toCharArray();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4)
		{
			char var5 = var2[var4];

			if (filter.indexOf((int)var5) != -1)
			{
				var1.append(var5);
			}
		}

		return var1.toString();
	}
	
	public void setText(String par1Str)
	{
		if (par1Str.length() > this.maxStringLength)
		{
			this.text = par1Str.substring(0, this.maxStringLength);
		}
		else
		{
			this.text = par1Str;
		}
		this.cursorToEnd();
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public int getAsIntegerOr(int deflt)
	{
		try
		{
			if(text.length() != 0)
				return Integer.parseInt(text);
			else
				return deflt;
		}
		catch(NumberFormatException e)
		{
			return deflt;
		}
	}
	
	public double getAsDoubleOr(double deflt)
	{
		try
		{
			if(text.length() != 0)
				return Double.parseDouble(text);
			else
				return deflt;
		}
		catch(NumberFormatException e)
		{
			return deflt;
		}
	}

	public String getSelectedText()
	{
		int var1 = this.cursorPoint < this.textSelectionEndPoint ? this.cursorPoint : this.textSelectionEndPoint;
		int var2 = this.cursorPoint < this.textSelectionEndPoint ? this.textSelectionEndPoint : this.cursorPoint;
		return this.text.substring(var1, var2);
	}

	public void addStr(String str)
	{
		str = filerAllowedCharacters(str);
		String var2 = "";
		int var4 = this.cursorPoint < this.textSelectionEndPoint ? this.cursorPoint : this.textSelectionEndPoint;
		int var5 = this.cursorPoint < this.textSelectionEndPoint ? this.textSelectionEndPoint : this.cursorPoint;
		int var6 = this.maxStringLength - this.text.length() - (var4 - this.textSelectionEndPoint);

		if (this.text.length() > 0)
		{
			var2 = var2 + this.text.substring(0, var4);
		}

		int var8;

		if (var6 < str.length())
		{
			var2 = var2 + str.substring(0, var6);
			var8 = var6;
		}
		else
		{
			var2 = var2 + str;
			var8 = str.length();
		}

		if (this.text.length() > 0 && var5 < this.text.length())
		{
			var2 = var2 + this.text.substring(var5);
		}

		this.text = var2;
		this.moveCursor(var4 - this.textSelectionEndPoint + var8);
		
		if(id >= 0)
		{
			actionListener.actionPerformed(id, this, text);
		}
	}

	public void deleteWordAtCursor(int par1)
	{
		if (this.text.length() != 0)
		{
			if (this.textSelectionEndPoint != this.cursorPoint)
			{
				this.addStr("");
			}
			else
			{
				this.deleteCharAtCursor(this.findSpaseNextCursor(par1) - this.cursorPoint);
			}
		}
	}

	public void deleteCharAtCursor(int par1)
	{
		if (this.text.length() != 0)
		{
			if (this.textSelectionEndPoint != this.cursorPoint)
			{
				this.addStr("");
			}
			else
			{
				boolean var2 = par1 < 0;
				int var3 = var2 ? this.cursorPoint + par1 : this.cursorPoint;
				int var4 = var2 ? this.cursorPoint : this.cursorPoint + par1;
				String var5 = "";

				if (var3 >= 0)
				{
					var5 = this.text.substring(0, var3);
				}

				if (var4 < this.text.length())
				{
					var5 = var5 + this.text.substring(var4);
				}

				this.text = var5;

				if (var2)
				{
					this.moveCursor(par1);
				}
				
				if(id >= 0)
				{
					actionListener.actionPerformed(id, this, text);
				}
			}
		}
	}

	public int findSpaseNextCursor(int par1)
	{
		return findSpace(par1, this.getCursorPoint());
	}

	public int findSpace(int par1, int par2)
	{
		int var3 = par2;
		boolean var4 = par1 < 0;
		int var5 = Math.abs(par1);

		for (int var6 = 0; var6 < var5; ++var6)
		{
			if (var4)
			{
				while (var3 > 0 && this.text.charAt(var3 - 1) == 32)
				{
					--var3;
				}

				while (var3 > 0 && this.text.charAt(var3 - 1) != 32)
				{
					--var3;
				}
			}
			else
			{
				int var7 = this.text.length();
				var3 = this.text.indexOf(32, var3);

				if (var3 == -1)
				{
					var3 = var7;
				}
				else
				{
					while (var3 < var7 && this.text.charAt(var3) == 32)
					{
						++var3;
					}
				}
			}
		}

		return var3;
	}

	public void moveCursor(int par1)
	{
		this.setCursorPos(this.textSelectionEndPoint + par1);
	}

	public void setCursorPos(int par1)
	{
		this.cursorPoint = par1;
		int var2 = this.text.length();

		if (this.cursorPoint < 0)
		{
			this.cursorPoint = 0;
		}

		if (this.cursorPoint > var2)
		{
			this.cursorPoint = var2;
		}

		this.setTextSelection(this.cursorPoint);
	}

	public void cursorToHome()
	{
		this.setCursorPos(0);
	}

	public void cursorToEnd()
	{
		this.setCursorPos(this.text.length());
	}
	
	@Override
	public void keyTyped(char c, int code)
	{
		keyTypedTB(c, code);
	}
	
	public boolean keyTypedTB(char c, int code)
	{
		if (this.isEnabled && this.isFocused)
		{
			switch (c)
			{
				case 1:
					this.cursorToEnd();
					this.setTextSelection(0);
					return true;
				case 3:
					GuiScreen.setClipboardString(this.getSelectedText());
					return true;
				case 22:
					this.addStr(GuiScreen.getClipboardString());
					return true;
				case 24:
					GuiScreen.setClipboardString(this.getSelectedText());
					this.addStr("");
					return true;
				default:
					switch (code)
					{
						case 14:
							if (GuiScreen.isCtrlKeyDown())
							{
								this.deleteWordAtCursor(-1);
							}
							else
							{
								this.deleteCharAtCursor(-1);
							}

							return true;
						case 199:
							if (GuiScreen.isShiftKeyDown())
							{
								this.setTextSelection(0);
							}
							else
							{
								this.cursorToHome();
							}

							return true;
						case 203:
							if (GuiScreen.isShiftKeyDown())
							{
								if (GuiScreen.isCtrlKeyDown())
								{
									this.setTextSelection(this.findSpace(-1, this.getSelectionEndPoint()));
								}
								else
								{
									this.setTextSelection(this.getSelectionEndPoint() - 1);
								}
							}
							else if (GuiScreen.isCtrlKeyDown())
							{
								this.setCursorPos(this.findSpaseNextCursor(-1));
							}
							else
							{
								this.moveCursor(-1);
							}

							return true;
						case 205:
							if (GuiScreen.isShiftKeyDown())
							{
								if (GuiScreen.isCtrlKeyDown())
								{
									this.setTextSelection(this.findSpace(1, this.getSelectionEndPoint()));
								}
								else
								{
									this.setTextSelection(this.getSelectionEndPoint() + 1);
								}
							}
							else if (GuiScreen.isCtrlKeyDown())
							{
								this.setCursorPos(this.findSpaseNextCursor(1));
							}
							else
							{
								this.moveCursor(1);
							}

							return true;
						case 207:
							if (GuiScreen.isShiftKeyDown())
							{
								this.setTextSelection(this.text.length());
							}
							else
							{
								this.cursorToEnd();
							}

							return true;
						case 211: //delele key
							if (GuiScreen.isCtrlKeyDown())
							{
								this.deleteWordAtCursor(1);
							}
							else
							{
								this.deleteCharAtCursor(1);
							}

							return true;
						default:
							if (ChatAllowedCharacters.isAllowedCharacter(c))
							{
								this.addStr(Character.toString(c));
								return true;
							}
							else
							{
								return false;
							}
					}
			}
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		boolean var4 = par1 >= this.xPos && par1 < this.xPos + this.width && par2 >= this.yPos && par2 < this.yPos + this.height;

		if (this.canLoseFocus)
		{
			this.setFocused(this.isEnabled && var4);
		}

		if (this.isFocused && par3 == 0)
		{
			int var5 = par1 - this.xPos;

			if (this.enableBackgroundDrawing)
			{
				var5 -= 4;
			}

			String var6 = this.fontRenderer.trimStringToWidth(this.text.substring(this.textSelectionStartPoint), this.width());
			this.setCursorPos(this.fontRenderer.trimStringToWidth(var6, var5).length() + this.textSelectionStartPoint);
		}
	}
	
	public void drawTextBox()
	{
		if (this.getEnableBackgroundDrawing())
		{
			GuiUtils.drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
			GuiUtils.drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
		}

		int var1 = this.isEnabled ? this.enabledColor : this.disabledColor;
		int var2 = this.cursorPoint - this.textSelectionStartPoint;
		int var3 = this.textSelectionEndPoint - this.textSelectionStartPoint;
		String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.textSelectionStartPoint), this.width());
		boolean var5 = var2 >= 0 && var2 <= var4.length();
		boolean var6 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && var5;
		int var7 = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
		int var8 = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;
		int var9 = var7;

		if (var3 > var4.length())
		{
			var3 = var4.length();
		}

		if (var4.length() > 0)
		{
			String var10 = var5 ? var4.substring(0, var2) : var4;
			var9 = this.fontRenderer.drawStringWithShadow(var10, var7, var8, var1);
		}

		boolean var13 = this.cursorPoint < this.text.length() || this.text.length() >= this.getMaxStrLength();
		int var11 = var9;

		if (!var5)
		{
			var11 = var2 > 0 ? var7 + this.width : var7;
		}
		else if (var13)
		{
			var11 = var9 - 1;
			--var9;
		}

		if (var4.length() > 0 && var5 && var2 < var4.length())
		{
			this.fontRenderer.drawStringWithShadow(var4.substring(var2), var9, var8, var1);
		}

		if (var6)
		{
			if (var13)
			{
				Gui.drawRect(var11, var8 - 1, var11 + 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
			}
			else
			{
				this.fontRenderer.drawStringWithShadow("_", var11, var8, var1);
			}
		}

		if (var3 != var2)
		{
			int var12 = var7 + this.fontRenderer.getStringWidth(var4.substring(0, var3));
			this.drawTextSelection(var11, var8 - 1, var12 - 1, var8 + 1 + this.fontRenderer.FONT_HEIGHT);
		}
	}

	private void drawTextSelection(int par1, int par2, int par3, int par4)
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

		Tessellator var6 = Tessellator.instance;
		GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);
		var6.startDrawingQuads();
		var6.addVertex((double)par1, (double)par4, 0.0D);
		var6.addVertex((double)par3, (double)par4, 0.0D);
		var6.addVertex((double)par3, (double)par2, 0.0D);
		var6.addVertex((double)par1, (double)par2, 0.0D);
		var6.draw();
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void setMaxStringLength(int par1)
	{
		this.maxStringLength = par1;

		if (this.text.length() > par1)
		{
			this.text = this.text.substring(0, par1);
		}
	}

	public int getMaxStrLength()
	{
		return this.maxStringLength;
	}

	public int getCursorPoint()
	{
		return this.cursorPoint;
	}

	/**
	 * get enable drawing background and outline
	 */
	public boolean getEnableBackgroundDrawing()
	{
		return this.enableBackgroundDrawing;
	}

	/**
	 * enable drawing background and outline
	 */
	public void setEnableBackgroundDrawing(boolean par1)
	{
		this.enableBackgroundDrawing = par1;
	}

	/**
	 * setter for the focused field
	 */
	public void setFocused(boolean par1)
	{
		if (par1 && !this.isFocused)
		{
			this.cursorCounter = 0;
		}

		this.isFocused = par1;
	}

	/**
	 * getter for the focused field
	 */
	public boolean getIsFocused()
	{
		return this.isFocused;
	}

	public int getSelectionEndPoint()
	{
		return textSelectionEndPoint;
	}

	public int width()
	{
		return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
	}

	public void setTextSelection(int par1)
	{
		int strlen = this.text.length();

		if (par1 > strlen)
		{
			par1 = strlen;
		}

		if (par1 < 0)
		{
			par1 = 0;
		}

		this.textSelectionEndPoint = par1;

		if (this.fontRenderer != null)
		{
			if (this.textSelectionStartPoint > strlen)
			{
				this.textSelectionStartPoint = strlen;
			}

			int var3 = width();
			String var4 = fontRenderer.trimStringToWidth(text.substring(textSelectionStartPoint), var3);
			int var5 = var4.length() + this.textSelectionStartPoint;

			if (par1 == this.textSelectionStartPoint)
			{
				this.textSelectionStartPoint -= this.fontRenderer.trimStringToWidth(text, var3, true).length();
			}

			if (par1 > var5)
			{
				this.textSelectionStartPoint += par1 - var5;
			}
			else if (par1 <= this.textSelectionStartPoint)
			{
				this.textSelectionStartPoint -= this.textSelectionStartPoint - par1;
			}

			if (this.textSelectionStartPoint < 0)
			{
				this.textSelectionStartPoint = 0;
			}

			if (this.textSelectionStartPoint > strlen)
			{
				this.textSelectionStartPoint = strlen;
			}
		}
	}

	/**
	 * if true the textbox can lose focus by clicking elsewhere on the screen
	 */
	public void setCanLoseFocus(boolean par1)
	{
		this.canLoseFocus = par1;
	}
}
