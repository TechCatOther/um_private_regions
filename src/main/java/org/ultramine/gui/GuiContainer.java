package org.ultramine.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class GuiContainer extends GuiStyled
{
	protected static RenderItem itemRenderer = GuiUtils.itemRenderer;
	
	public Container container;
	
	protected GuiContainer()
	{
		
	}
	
	protected GuiContainer(Container container)
	{
		this.container = container;
	}
	
	@Override
	public void relayout()
	{
		super.relayout();
		if(container != null) mc.thePlayer.openContainer = container;
	}
	
	protected void setContainer(Container container)
	{
		this.container = container;
		if(container != null) mc.thePlayer.openContainer = container;
	}
	
	private Slot slotHover = null;

	@Override
	protected final void drawBackground(int mx, int my)
	{
		super.drawBackground(mx, my);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		slotHover = null;
		for (int i = 0, s = container.inventorySlots.size(); i < s; i++)
		{
			Slot slot = (Slot)container.inventorySlots.get(i);
			drawSlotInventory(slot);

			if (this.isMouseOverSlot(slot, mx, my))
			{
				slotHover = slot;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				int var9 = slot.xDisplayPosition + guiLeft;
				int var10 = slot.yDisplayPosition + guiTop;
				GuiUtils.drawGradientRect(var9, var10, var9 + 16, var10 + 16, -2130706433, -2130706433);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
	}
	
	@Override
	protected void drawForeground(int mx, int my)
	{
		super.drawForeground(mx, my);
		
		InventoryPlayer inv = this.mc.thePlayer.inventory;
		
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		if (inv.getItemStack() != null)
		{
			itemRenderer.zLevel = 300;
			itemRenderer.renderItemIntoGUI(mc.fontRenderer, this.mc.renderEngine, inv.getItemStack(), mx - 8, my - 8);
			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, this.mc.renderEngine, inv.getItemStack(), mx - 8, my - 8);
			itemRenderer.zLevel = 0;
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);

		if (inv.getItemStack() == null && slotHover != null && slotHover.getHasStack())
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			//RenderHelper.disableStandardItemLighting();
			
			ItemStack var22 = slotHover.getStack();
			GuiUtils.drawToolTip(var22, getItemStackTooltipLines(slotHover), mx + 8, my + 8, width, height);
			
			//RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
		
		RenderHelper.disableStandardItemLighting();
	}

	@SuppressWarnings("unchecked")
	protected List<String> getItemStackTooltipLines(Slot slot)
	{
		return slotHover.getStack().getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
	}
	
	private void drawSlotInventory(Slot par1Slot)
	{
		int var2 = par1Slot.xDisplayPosition + guiLeft;
		int var3 = par1Slot.yDisplayPosition + guiTop;
		ItemStack var4 = par1Slot.getStack();

		if (var4 == null)
		{
			IIcon icon = par1Slot.getBackgroundIconIndex();

			if (icon != null)
			{
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_BLEND); // Forge: Blending needs to be enabled for this.
				this.mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
				GuiUtils.drawTexturedModelRectFromIcon(var2, var3, icon, 16, 16);
				GL11.glDisable(GL11.GL_BLEND); // Forge: And clean that up
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		}
		else
		{
			itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, this.mc.renderEngine, var4, var2, var3);
			itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, this.mc.renderEngine, var4, var2, var3);
		}
	}
	
	private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3)
	{
		int var4 = this.guiLeft;
		int var5 = this.guiTop;
		par2 -= var4;
		par3 -= var5;
		return par2 >= par1Slot.xDisplayPosition - 1 && par2 < par1Slot.xDisplayPosition + 16 + 1 && par3 >= par1Slot.yDisplayPosition - 1 && par3 < par1Slot.yDisplayPosition + 16 + 1;
	}
	
	private Slot getSlotAtPosition(int x, int y)
	{
		for (int var3 = 0; var3 < container.inventorySlots.size(); ++var3)
		{
			Slot var4 = (Slot)container.inventorySlots.get(var3);

			if (this.isMouseOverSlot(var4, x, y))
			{
				return var4;
			}
		}

		return null;
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		boolean var4 = par3 == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;

		if (par3 == 0 || par3 == 1 || var4)
		{
			Slot var5 = this.getSlotAtPosition(par1, par2);
			int var6 = this.guiLeft;
			int var7 = this.guiTop;
			boolean var8 = par1 < var6 || par2 < var7 || par1 >= var6 + this.xSize || par2 >= var7 + this.ySize;
			int var9 = -1;

			if (var5 != null)
			{
				var9 = var5.slotNumber;
			}

			if (var8)
			{
				var9 = -999;
			}

			if (this.mc.gameSettings.touchscreen && var8 && this.mc.thePlayer.inventory.getItemStack() == null)
			{
				this.mc.displayGuiScreen((GuiScreen)null);
				return;
			}

			if (var9 != -1)
			{
				if (this.mc.gameSettings.touchscreen)
				{
					if (var5 != null && var5.getHasStack())
					{
						//TODO ????
						
						//this.clickedSlot = var5;
						//this.draggedStack = null;
						//this.isRightMouseClick = par3 == 1;
					}
					else
					{
						//this.clickedSlot = null;
					}
				}
				else if (var4)
				{
					this.handleMouseClick(var5, var9, par3, 3);
				}
				else
				{
					boolean var10 = var9 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
					this.handleMouseClick(var5, var9, par3, var10 ? 1 : 0);
				}
			}
		}
	}
	
	protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4)
	{
		if (par1Slot != null)
		{
			par2 = par1Slot.slotNumber;
		}

		this.mc.playerController.windowClick(container.windowId, par2, par3, par4, this.mc.thePlayer);
	}
	
	@Override
	public void onGuiClosed()
	{
		if (mc.thePlayer != null && container != null)
		{
			container.onContainerClosed(this.mc.thePlayer);
		}
	}
	
	@Override
	public void keyTyped(char c, int code)
	{
		if (code == 1 || code == mc.gameSettings.keyBindInventory.getKeyCode())
		{
			mc.thePlayer.closeScreen();
		}
		else
		for (int i = 0, s = controlList.size(); i < s; ++i)
		{
			controlList.get(i).keyTyped(c, code);
		}
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void update()
	{
		super.update();
		if (!this.mc.thePlayer.isEntityAlive())
		{
			GuiUtils.closeGUI();
		}
	}
}
