package org.ultramine.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ElementEnergyPanel implements IGuiElement
{
	protected static final ResourceLocation texture = new ResourceLocation("privreg:textures/gui/icons.png");
	
	private int xRel;
	private int yRel;
	
	private int xPos;
	private int yPos;
	
	private int height;
	private int maxCharge;
	private int charge;
	
	public ElementEnergyPanel(int x, int y, int height, int maxEnergy)
	{
		this.xRel = x;
		this.yRel = y;
		this.height = height;
		this.maxCharge = maxEnergy;
	}
	
	public ElementEnergyPanel(int x, int y, int height, int maxEnergy, int charge)
	{
		this(x, y, height, maxEnergy);
		this.charge = charge;
	}
	
	public int setCharge(int charge)
	{
		if(charge <= this.maxCharge)
		{
			this.charge = charge;
			return 0;
		}
		else
		{
			this.charge = this.maxCharge;
			return charge - this.maxCharge;
		}
	}
	
	public void setMaxCharge(int maxCharge)
	{
		this.maxCharge = maxCharge;
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
		mc.renderEngine.bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		//background
		GuiUtils.drawTexturedModalRect(xPos, yPos, 230, 0, 17, 4);
		int toheight = 4;
		while(true)
		{
			toheight += 13;
			if(toheight < height-4)
			{
				GuiUtils.drawTexturedModalRect(xPos, yPos+height-toheight, 230, 4, 17, 13);
			}
			else
			{
				toheight = (height - 4) - (toheight - 13);
				GuiUtils.drawTexturedModalRect(xPos, yPos+4, 230, 17-toheight, 17, 13);
				break;
			}
		}
		GuiUtils.drawTexturedModalRect(xPos, yPos+height-4, 230, 17, 17, 4);
		
		//charge
		int energyHeight = (int)((float)charge/(float)maxCharge*(height-8));
		if(energyHeight == 0 && charge > 0) energyHeight++;
		toheight = 0;
		while(true)
		{
			toheight += 13;
			if(toheight < energyHeight)
			{
				GuiUtils.drawTexturedModalRect(xPos+4, yPos+height-toheight-4, 247, 0, 9, 13);
			}
			else
			{
				toheight = energyHeight - (toheight - 13);
				GuiUtils.drawTexturedModalRect(xPos+4, yPos+height-energyHeight-4, 247, 13-toheight, 9, 13);
				break;
			}
		}
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
