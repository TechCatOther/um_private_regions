package org.ultramine.mods.privreg.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.ultramine.gui.ElementButton;
import org.ultramine.gui.ElementEnergyPanel;
import org.ultramine.gui.ElementTextField;
import org.ultramine.gui.GuiContainer;
import org.ultramine.gui.IGuiElement;
import org.ultramine.mods.privreg.RegionConfig;
import org.ultramine.mods.privreg.gui.inv.ContainerCharger;
import org.ultramine.mods.privreg.packets.PacketChargerAction;
import org.ultramine.mods.privreg.tiles.TileCharger;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class GuiCharger extends GuiContainer
{
	private static final ResourceLocation BG = new ResourceLocation("privreg:textures/gui/amcharger.png");

	private TileCharger.InventoryCharger block;
	private ElementEnergyPanel ep1;
	private ElementEnergyPanel ep2;
	private ElementTextField textbox;

	public GuiCharger(IInventory player, TileCharger.InventoryCharger block)
	{
		super(new ContainerCharger(player, block));
		this.block = block;
		setBG(BG);
		setSize(176, 222);

		addElement(ep1 = new ElementEnergyPanel(80, 14, 112, 500));
		addElement(ep2 = new ElementEnergyPanel(100, 14, 112, 500));

		addElement(textbox = new ElementTextField(-1, 10, 57, 60, 15));
		textbox.setMaxStringLength(9);
		textbox.setText("1");

		addElement(new ElementButton(0, 10, 75, 60, 15, tlt("privreg.gui.charger.action")));
	}

	private int getAmount()
	{
		try
		{
			return Integer.parseInt(textbox.getText());
		} catch (NumberFormatException e)
		{
			return 0;
		}
	}

	@Override
	public void actionPerformed(int id, IGuiElement element, Object... data)
	{
		if(id == 0)
			new PacketChargerAction(getAmount()).form(block.te).sendToServer();
	}

	public void update()
	{
		super.update();
		ep1.setMaxCharge(block.maxCharge >> 1);
		ep2.setMaxCharge(block.maxCharge >> 1);
		ep2.setCharge(ep1.setCharge(block.charge));
	}

	@Override
	protected void drawForeground(int mx, int my)
	{
		super.drawForeground(mx, my);
		mc.fontRenderer.drawString("1 e.a. = " + RegionConfig.moneyPerEA + "$", guiLeft + 10, guiTop + 45, 0x404040);
		mc.fontRenderer.drawString(getAmount() + " e.a. = " + RegionConfig.moneyPerEA * getAmount() + "$", guiLeft + 10, guiTop + 95, 0x404040);

		mc.fontRenderer.drawString(block.charge + "/" + block.maxCharge + " e.a.", guiLeft + 120, guiTop + 40, 0x404040);
	}
}
