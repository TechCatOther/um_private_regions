package org.ultramine.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ElementButton implements IGuiElement
{
	protected static final ResourceLocation texture = new ResourceLocation("textures/gui/widgets.png");
	
	protected final int id;
	protected final int width;
	protected final int height;
	protected final int xRel;
	protected final int yRel;
	
	public int xPos;
	public int yPos;
	
	public String displayString;
	public boolean enabled = true;
	public boolean drawButton = true;
	
	protected IActionListener actionListener;
	
	protected int texY = 46;
	public int texW = 20;
	
	public ElementButton(int id, int x, int y, int width, int height, String str)
	{
		this.id = id;
		this.xRel = x;
		this.yRel = y;
		this.width = width;
		this.height = height;
		this.displayString = str;
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
		if (this.drawButton)
		{
			minecraft.renderEngine.bindTexture(texture);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean var5 = mx >= xPos && my >= yPos && mx < xPos + width && my < yPos + height;
			int var6 = getHoverState(var5);
			GuiUtils.drawTexturedModalRect(xPos, yPos, 0, texY + var6 * texW, width / 2, height/2);
			GuiUtils.drawTexturedModalRect(xPos + width / 2, yPos, 200 - width / 2, texY + var6 * texW, width / 2, height/2);
			GuiUtils.drawTexturedModalRect(xPos, yPos + height/2, 0, texY+texW-height/2 + var6 * texW, width / 2, height/2);
			GuiUtils.drawTexturedModalRect(xPos + width / 2, yPos + height/2, 200 - width / 2, texY+texW-height/2 + var6 * texW, width / 2, height/2);
			mouseDragged(minecraft, mx, my);
			int var7;
			
			switch(var6)
			{
			case 0: var7 = -6250336; break;
			case 2: var7 = 16777120; break;
			default: var7 = 14737632; break;
			}

			GuiUtils.drawCenteredString(minecraft.fontRenderer, displayString, xPos + width / 2, yPos + (height - 10) / 2, var7);
		}
	}

	@Override
	public void keyTyped(char c, int code)
	{
		
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonCode)
	{
		if(buttonCode == 0 && mousePressed(mx, my) && canPress())
		{
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
			buttonActivate();
			if(id >= 0) actionListener.actionPerformed(id, this);
		}
	}
	
	protected boolean canPress(){return true;}
	protected void buttonActivate(){}
	
	protected int getHoverState(boolean par1)
	{
		if (!enabled)
		{
			return 0;
		}
		else if (par1)
		{
			return 2;
		}

		return 1;
	}
	
	public boolean mousePressed(int par2, int par3)
	{
		return enabled && drawButton && par2 >= xPos && par3 >= yPos && par2 < xPos + width && par3 < yPos + height;
	}
	
	protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {}

	@Override
	public void update()
	{
		
	}
}
