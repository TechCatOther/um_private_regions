package org.ultramine.mods.privreg.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.ultramine.gui.ElementButton;
import org.ultramine.gui.ElementEnergyPanel;
import org.ultramine.gui.GuiContainer;
import org.ultramine.gui.IGui;
import org.ultramine.gui.IGuiElement;
import org.ultramine.mods.privreg.gui.inv.ContainerRegionModules;
import org.ultramine.mods.privreg.gui.inv.SlotModule;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.packets.PacketRegionAction;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionRights;

import java.util.List;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class GuiRegionModules extends GuiContainer
{
	private static final ResourceLocation BG = new ResourceLocation("privreg:textures/gui/regionmodules.png");
	private final Region region;
	private ElementEnergyPanel charge;

	public IGui handler;

	public GuiRegionModules(EntityPlayer player, Region region)
	{
		super(new ContainerRegionModules(player.inventory, region));
		this.region = region;
		setBG(BG);
		setSize(176, 222);
	}

	@Override
	public void relayout()
	{
		super.relayout();
		super.controlList.clear();
		addElement(new ElementButton(0, 164, 4, 8, 8, "X"));
		addElement(new ElementButton(1, 132, 4, 30, 8, tlt("privreg.gui.back")));

		charge = new ElementEnergyPanel(7, 15, 112, region.getMaxCharge());
		addElement(charge);

		if(handler != null)
		{
			handler.resizeGui(mc, width, height);
			handler.relayout();
		}
	}

	@Override
	public void actionPerformed(int id, IGuiElement element, Object... data)
	{
		switch (id)
		{
			case 0:
				mc.displayGuiScreen(null);
				return;
			case 1:
				new PacketRegionAction(region.getID(), PacketRegionAction.SERVER_OPEN_MAIN).sendToServer();
		}
	}

	@Override
	protected void drawForeground(int mx, int my)
	{
		mc.fontRenderer.drawString(((int)region.getCharge()) + "/" + region.getMaxCharge() + " e.a.", guiLeft + 29, guiTop + 90, 0x404040);
		super.drawForeground(mx, my);
	}

	@Override
	public void update()
	{
		super.update();
		charge.setMaxCharge(region.getMaxCharge());
		charge.setCharge((int) region.getCharge());
		if(handler != null)
			handler.update();
	}

	@Override
	protected List<String> getItemStackTooltipLines(Slot slot)
	{
		List<String> list = super.getItemStackTooltipLines(slot);
		if (slot instanceof SlotModule)
		{
			RegionModule regionModule = getOrCreateModule(slot.getStack());
			if(regionModule != null)
				list.add(tlt("item.um_privreg_module.desc2", Math.round(regionModule.countCost() * 100) / 100F));
		}
		return list;
	}

	@Override
	protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4)
	{
		if (par1Slot != null)
		{
			par2 = par1Slot.slotNumber;
		}

		if (par1Slot instanceof SlotModule && par3 == 1)
		{
			ItemStack is = par1Slot.getStack();
			if (is != null && region.getOwnerStorage().hasRightClient(RegionRights.EDIT_MODULES))
			{
				RegionModule module = getOrCreateModule(is);
				handler = module.createGuiHandler(this);
				if (handler != null)
				{
					handler.resizeGui(mc, width, height);
					handler.relayout();
				}
				return;
			}
		}

		mc.playerController.windowClick(container.windowId, par2, par3, par4, this.mc.thePlayer);
	}

	@Override
	public void draw(int mx, int my, float par3)
	{
		if(handler != null)
		{
			super.draw(0, 0, 0);
			//GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 100F);
			handler.draw(mx, my, 0);
			//GL11.glPopMatrix();
		} else
			super.draw(mx, my, 0);
	}

	@Override
	public void keyTyped(char c, int code)
	{
		if (handler != null)
			handler.keyTyped(c, code);
		else
			super.keyTyped(c, code);

	}

	@Override
	public void mouseClicked(int mx, int my, int buttonCode)
	{
		if(handler != null)
			handler.mouseClicked(mx, my, buttonCode);
		else
			super.mouseClicked(mx, my, buttonCode);
	}

	private RegionModule getOrCreateModule(ItemStack itemStack)
	{
		RegionModule module = region.getModuleByRegistryId(itemStack.getItemDamage());
		if(module == null)
		{
			module = RegionModule.wrapItemStack(itemStack);
			region.addModule(module);
		}
		return module;
	}
}
