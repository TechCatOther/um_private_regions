package org.ultramine.mods.privreg.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.ultramine.gui.ElementButton;
import org.ultramine.gui.GuiContainer;
import org.ultramine.gui.GuiScreenToGui;
import org.ultramine.gui.IGuiElement;
import org.ultramine.mods.privreg.gui.inv.ContainerBlockRegion;
import org.ultramine.mods.privreg.packets.PacketRegionAction;
import org.ultramine.mods.privreg.regions.RegionChangeResult;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.mods.privreg.render.ClientSelectionRenderer;
import org.ultramine.mods.privreg.tiles.TileBlockRegion;
import org.ultramine.server.util.BasicTypeFormatter;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class GuiBlockRegion extends GuiContainer
{
	private static final ResourceLocation BG = new ResourceLocation("privreg:textures/gui/blockregionmain.png");

	public TileBlockRegion block;
	//private EntityPlayer player;

	RegionButton button;

	public String warnMessage = null;

	public GuiBlockRegion(TileBlockRegion te, EntityPlayer entityplayer)
	{
		super(new ContainerBlockRegion(te));
		this.block = te;
		//player = entityplayer;
		setBG(BG);
		setSize(256, 166);
	}

	public void acceptMessage(int id)
	{
		switch(RegionChangeResult.getByOrigin(id))
		{
			case ALLOW:
				warnMessage = null;
				break;
			case TOO_SMALL:
				warnMessage = tlt("privreg.gui.main.msg.toosmall");
				break;
			case OVERTACKTS:
				warnMessage = tlt("privreg.gui.main.msg.overtackts");
				break;
			case INTERSECTS:
				warnMessage = tlt("privreg.gui.main.msg.intersects");
				break;
			case OUTOFPARENT:
				warnMessage = tlt("privreg.gui.main.msg.outofparent");
				break;
			case OUTOFWORLD:
				warnMessage = tlt("privreg.gui.main.msg.outofworld");
				break;
		}
	}

	@Override
	public void update()
	{

	}

	@Override
	public void relayout()
	{
		super.relayout();
		this.controlList.clear();
		if(block.getRegion() == null || !block.getRegion().hasRight(RegionRights.OPEN_BLOCK))
		{
			mc.displayGuiScreen(null);
			return;
		}

		button = new RegionButton(mc, guiLeft + 20, guiTop + 60);
		addElement(new ElementButton(0, 241, 6, 8, 8, "Х"));

		ElementButton modules = new ElementButton(5, 190, 25, 60, 20, tlt("privreg.gui.main.modules"));
		addElement(modules);
		ElementButton owners = new ElementButton(1, 190, 90, 60, 20, tlt("privreg.gui.main.owners"));
		addElement(owners);
		addElement(new ElementButton(2, 190, 115, 60, 20, tlt("privreg.gui.main.show")));
		ElementButton dismantle = new ElementButton(6, 190, 140, 60, 20, "\u00a74"+tlt("privreg.gui.main.dismantle"));
		addElement(dismantle);

		owners.enabled = block.getRegion().hasRight(RegionRights.EDIT_USERS);
		modules.enabled = block.getRegion().hasRight(RegionRights.EDIT_MODULES);
		dismantle.enabled = block.getRegion().hasRight(RegionRights.CREATOR);

//		ElementButtonFixable on = new ElementButtonFixable(3, 200, 65, 50, 20, tlt("privreg.gui.main.on"));
//		ElementButtonFixable off = new ElementButtonFixable(4, 200, 90, 50, 20, tlt("privreg.gui.main.off"));
//		on.linkTo(off);
//
//		if(block.getRegion().isEnabled())
//			on.pressed = true;
//		else
//			off.pressed = true;
//
//		addElement(on);
//		addElement(off);
	}

	@Override
	public void actionPerformed(int id, IGuiElement element, Object... data)
	{
		switch(id)
		{
			case 0:
				mc.displayGuiScreen(null);
				return;
			case 1:
				mc.displayGuiScreen(new GuiScreenToGui(new GuiOwners(this, block.getRegion().getOwnerStorage())));
				return;
			case 2:
				ClientSelectionRenderer.toggleRender(block.getRegion().getID());
				return;
//			case 3:
//				block.getRegion().enable();
//				new PacketButton(block.getRegion().getID(), 1).sendToServer();
//				return;
//			case 4:
//				block.getRegion().disable();
//				new PacketButton(block.getRegion().getID(), 2).sendToServer();
//				return;
			case 5:
				new PacketRegionAction(block.getRegion().getID(), PacketRegionAction.SERVER_OPEN_MODULES).sendToServer();
				return;
			case 6:
				new PacketRegionAction(block.getRegion().getID(), PacketRegionAction.SERVER_DISMANTLE).sendToServer();
				return;
		}
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
	}

	private double lastCharge;
	private double lastCosts;
	private String lastTimeMsg;

	@Override
	protected void drawForeground(int mx, int my)
	{
		button.drawButton(mx, my);

		int x = block.getRegion().getShape().getLenX();
		int y = block.getRegion().getShape().getLenY();
		int z = block.getRegion().getShape().getLenZ();
		int summ = block.getRegion().countBlocks();
		if(block.getRegion().getCharge() != lastCharge || block.getRegion().getCurrentCost() != lastCosts)
		{
			lastCosts = block.getRegion().getCurrentCost();
			lastCharge = block.getRegion().getCharge();
			lastTimeMsg = BasicTypeFormatter.formatTime((long) (lastCharge / lastCosts * 86400000), false, false);
		}

		drawString(tlt("privreg.gui.main.info.charge", (int)block.getRegion().getCharge(), block.getRegion().getMaxCharge()), 20, 25, 0x404040);
		drawString(tlt("privreg.gui.main.info.size", x, z, summ), 20, 35, 0x404040);
		drawString(tlt("privreg.gui.main.info.cost", (Math.round(block.getRegion().getCurrentCost() * 100)) / 100F), 20, 45, 0x404040);

/*		if(!block.getRegion().isEnabled())
		{
			//drawString("\u00a72до отключения: \u00a7r" + "вечность", 20, 115, 0x404040);
			if(block.getRegion().getCharge() != 0)
			{
				drawString("\u00a7cПриват выключен, но потбеляет 20ea.", 20, 115, 0x404040);
				drawString("\u00a72До отключения: \u00a7r" + lastTimeMsg, 20, 125, 0x404040);
			}
			else
			{
				drawString("\u00a7cНедостаточно антиматерии для выкл. привата", 20, 115, 0x404040);
				drawString("\u00a7cПриват исчезнет через неделю", 20, 125, 0x404040);
			}
		}
		else */if(block.getRegion().getCharge() > 0)
		{
			drawString(tlt("privreg.gui.main.info.time", lastTimeMsg), 20, 115, 0x404040);
		}
		else
		{
			drawString(tlt("privreg.gui.main.info.dis1"), 20, 115, 0x404040);
			drawString(tlt("privreg.gui.main.info.dis2"), 20, 125, 0x404040);
		}

		drawString(tlt("privreg.gui.main.info.exte", block.getRegion().getTacts(), block.getRegion().getMaxTacts()), 20, 135, 0x404040);
		if(warnMessage != null)
			drawString("\u00a7c" + warnMessage, 20, 145, 0x404040);


//		if(block.getRegion().isEnabled()) drawString("\u00a72Включено", 200, 55, 0x404040);
//		else drawString("Выключено", 200, 55, 0xFF0000);
	}

	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
	}

	@Override
	public void mouseClicked(int mx, int my, int buttonCode)
	{
		super.mouseClicked(mx, my, buttonCode);
		button.mouseClicked(block.getRegion(), mx, my, buttonCode);
	}
}
