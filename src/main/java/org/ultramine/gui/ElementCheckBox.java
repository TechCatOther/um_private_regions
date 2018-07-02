package org.ultramine.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ElementCheckBox implements IGuiElement
{
	protected static final ResourceLocation texture = new ResourceLocation("privreg:textures/gui/icons.png");
	
	protected final int id;
	protected final int xRel;
	protected final int yRel;
	
	public int xPos;
	public int yPos;
	
	private IActionListener actionListener;
	
	public boolean enabled = true;
	protected boolean isChecked = false;
	
	private String annotation;
	
	public ElementCheckBox(int id, int x, int y)
	{
		this.id = id;
		this.xRel = x;
		this.yRel = y;
	}
	
	public ElementCheckBox(int id, int x, int y, String str)
	{
		this(id, x, y);
		annotation = str;
	}
	
	@Override
	public void init(IActionListener container, int guiWidth, int guiHeight, int guiLeft, int guiTop) 
	{
		actionListener = container;
		xPos = xRel + guiLeft;
		yPos = yRel + guiTop;
	}

	@Override
	public void draw(Minecraft mc, int mx, int my)
	{
		mc.renderEngine.bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(isChecked())
			GuiUtils.drawTexturedModalRect(xPos, yPos, 16, 0, 8, 9);
		else
			GuiUtils.drawTexturedModalRect(xPos, yPos, 8, 0, 8, 9);
		if(annotation != null) mc.fontRenderer.drawString(annotation, xPos + 10, yPos, 0x000000);//0x404040);
	}

	@Override
	public void keyTyped(char c, int code)
	{
		
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonCode)
	{
		if(buttonCode == 0 && mousePressed(mx, my))
		{
			Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));

			setChecked(!isChecked());
			if(id >= 0)
			{
				actionListener.actionPerformed(id, this, isChecked());
			}
			stateChanged();
		}
	}

	@Override
	public void update()
	{
		
	}
	
	public boolean mousePressed(int par2, int par3)
	{
		return this.enabled && par2 >= this.xPos && par3 >= this.yPos && par2 < this.xPos + 8 && par3 < this.yPos + 9;
	}
	
	public ElementCheckBox setChecked(boolean ch)
	{
		isChecked = ch;
		return this;
	}

	public boolean isChecked()
	{
		return isChecked;
	}
	
	protected void stateChanged()
	{
		
	}
}
