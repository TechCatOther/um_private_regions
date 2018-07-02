package org.ultramine.mods.privreg.gui;

import net.minecraft.util.ResourceLocation;
import org.ultramine.gui.ElementButton;
import org.ultramine.gui.ElementLabel;
import org.ultramine.gui.ElementTextField;
import org.ultramine.gui.GuiContainer;
import org.ultramine.gui.GuiStyled;
import org.ultramine.gui.IGuiElement;
import org.ultramine.mods.privreg.gui.inv.ContainerRent;
import org.ultramine.mods.privreg.modules.RegionModuleRent;
import org.ultramine.mods.privreg.modules.RegionModuleRent.RentMode;
import org.ultramine.mods.privreg.packets.PacketRegionRent;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.render.ClientSelectionRenderer;
import org.ultramine.server.util.BasicTypeFormatter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static org.ultramine.util.I18n.tlt;

public class GuiRent extends GuiContainer
{
	private static final ResourceLocation BG = new ResourceLocation("privreg:textures/gui/blockregionmain.png");
	private static final NumberFormat dformat = new DecimalFormat("#0.##");

	private final Region region;
	private RentMode lastMode;

	private ElementButton doAction;
	private ElementTextField tfDays;
	private ElementTextField tfHours;

	private String message = null;
	private long time;

	public GuiRent(Region region)
	{
		super(new ContainerRent());
		this.region = region;
		setBG(BG);
		setSize(256, 166);

		RegionModuleRent module = region == null ? null : region.getModuleWithClass(RegionModuleRent.class);
		lastMode = module != null ? module.getMode() : RentMode.RENT;
	}

	public void acceptMessage(int id)
	{
		switch(id)
		{
			case 0:
				message = "На вашем счету недостаточно средств";
				break;
			case 1:
				message = "Участок уже арендован другим игроком";
				break;
			case 2:
				message = "Превышен максимальный срок аренды";
				break;
		}
	}

	public void acceptServerTime(long time)
	{
		this.time = time;
	}

	@Override
	public void relayout()
	{
		super.relayout();
		controlList.clear();
		addElement(new ElementButton(0, 241, 6, 8, 8, "Х"));
		addElement(new ElementButton(1, 190, 140, 60, 20, tlt("privreg.gui.main.show")));

		RegionModuleRent module = region.getModuleWithClass(RegionModuleRent.class);
		if(module == null)
		{
			mc.displayGuiScreen(null);
			return;
		}
		RentMode mode = module.getMode();

		if(mode == RentMode.RENT)
		{
			addElement(tfDays = new ElementTextField(-1, 20, 95, 60, 10));
			addElement(new ElementLabel(82, 95, "Дней"));

			addElement(tfHours = new ElementTextField(-1, 20, 110, 60, 10));
			addElement(new ElementLabel(82, 110, "Часов"));

			addElement(doAction = new ElementButton(2, 20, 125, 60, 20, "Арендовать"));

			tfDays.setFilterString("0123456789");
			tfDays.setMaxStringLength(8);
			tfDays.setText("1");

			tfHours.setFilterString("0123456789");
			tfHours.setMaxStringLength(8);
			tfHours.setText("0");

			doAction.enabled = (module.isAllowMultiple() || module.getTenantsCount() == 0) && !region.getOwnerStorage().isOwnerClient() || module.hasTenant(mc.getSession().func_148256_e());
		}
		else
		{
			addElement(doAction = new ElementButton(2, 20, 95, 60, 20, "Купить"));
			doAction.enabled = !region.getOwnerStorage().isOwnerClient();
		}

		lastMode = mode;
	}

	@Override
	public void actionPerformed(int id, IGuiElement element, Object... data)
	{
		message = null;
		RegionModuleRent module = region.getModuleWithClass(RegionModuleRent.class);
		if(module == null)
		{
			mc.displayGuiScreen(null);
			return;
		}
		RentMode mode = module.getMode();

		switch(id)
		{
			case 0:
				mc.displayGuiScreen(null);
				return;
			case 1:
				ClientSelectionRenderer.toggleRender(region.getID());
				return;
			case 2:
				if(mode == RentMode.RENT)
				{
					int hours = tfDays.getAsIntegerOr(0)*24 + tfHours.getAsIntegerOr(0);
					if(hours > 0)
						new PacketRegionRent(region.getID(), hours).sendToServer();
				}
				else
				{
					new PacketRegionRent(region.getID(), -1).sendToServer();
				}
				return;
		}
	}

	@Override
	public void update()
	{
		RegionModuleRent module = region.getModuleWithClass(RegionModuleRent.class);
		if(module == null)
		{
			mc.displayGuiScreen(null);
			return;
		}
		if(lastMode != module.getMode())
			relayout();
	}

	@Override
	protected void drawForeground(int mx, int my)
	{
		RegionModuleRent module = region.getModuleWithClass(RegionModuleRent.class);
		if(module == null)
			return;
		RentMode mode = module.getMode();

		if(mode == RentMode.RENT && !module.isAllowMultiple() && module.getTenantsCount() > 0 && !module.hasTenant(mc.getSession().func_148256_e()))
		{
			RegionModuleRent.Tenant tnt = module.getTenants().values().iterator().next();
			long t = tnt.getEndTime() - time;
			drawString("§cУчасток уже сдан в аренду на", 20, 25, 0x404040);
			drawString("§c"+BasicTypeFormatter.formatTime(t, false, false), 20, 35, 0x404040);
			drawString("§2Игроку: §c"+tnt.getProfile().getName(), 20, 45, 0x404040);
		}
		else
		{
			drawString(mode == RentMode.RENT ? "§2Сдается в аренду" : "§2Продается", 20, 25, 0x404040);
			drawString(mode == RentMode.RENT ? "§2По цене §c" + module.getRentalFee() + "$§2/день (реальный, 24 часа)" : "§2По цене §c" + module.getSellPrice(), 20, 35, 0x404040);
			if(mode == RentMode.RENT)
				drawString("§2Максимальный срок аренды: §c"+module.getMaxDays()+ " §2дней", 20, 45, 0x404040);
		}

		drawString(tlt("privreg.gui.main.info.charge", (int)region.getCharge(), region.getMaxCharge()), 30, 55, 0x404040);
		drawString(tlt("privreg.gui.main.info.size", region.getShape().getLenX(), region.getShape().getLenZ(), region.countBlocks()), 30, 65, 0x404040);
		if(module.hasTenant(mc.getSession().func_148256_e()))
		{
			RegionModuleRent.Tenant tnt = module.getTenants().get(mc.getSession().func_148256_e().getId());
			long t = tnt.getEndTime() - time;
			drawString("§2Регион уже арендован вами на §c"+BasicTypeFormatter.formatTime(t, false, false), 20, 75, 0x404040);
		}
		if(mode == RentMode.RENT)
		{
			int hours = tfDays.getAsIntegerOr(0)*24 + tfHours.getAsIntegerOr(0);
			double amount = module.getRentalFee()*hours/24d;
			drawString("§2Стоимость: §c"+dformat.format(amount), 20, 85, 0x404040);
		} else
		{
			drawString("§2Стоимость: §c"+dformat.format(module.getRentalFee()), 20, 85, 0x404040);
		}

		if(message != null)
			drawString("\u00a7c" + message, 20, 145, 0x404040);
	}
}
