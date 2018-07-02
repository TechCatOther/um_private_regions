package org.ultramine.mods.privreg.render;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.ultramine.gui.GuiUtils;
import org.ultramine.mods.privreg.ClientUtils;
import org.ultramine.mods.privreg.InitCommon;
import org.ultramine.mods.privreg.KeyBindingUnbindable;
import org.ultramine.mods.privreg.gui.RegionButton;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionChangeResult;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.server.util.BasicTypeFormatter;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class DistanceControlGuiRender
{
	private static final Controls controls = new Controls();
	private static DistanceControlGuiRender instance;

	public static void toggle(Region region, EntityPlayer player, int slot)
	{
		if(instance != null)
		{
			instance.close();
		}
		else
		{
			instance = new DistanceControlGuiRender(region, player, slot);
		}

	}

	public static void acceptMessage(int id)
	{
		if(instance != null)
		{
			switch(RegionChangeResult.getByOrigin(id))
			{
				case ALLOW:
					instance.warnMessage = null;
					break;
				case TOO_SMALL:
					instance.warnMessage = tlt("privreg.gui.main.msg.toosmall");
					break;
				case OVERTACKTS:
					instance.warnMessage = tlt("privreg.gui.main.msg.overtackts");
					break;
				case INTERSECTS:
					instance.warnMessage = tlt("privreg.gui.main.msg.intersects");
					break;
				case OUTOFPARENT:
					instance.warnMessage = tlt("privreg.gui.main.msg.outofparent");
					break;
				case OUTOFWORLD:
					instance.warnMessage = tlt("privreg.gui.main.msg.outofworld");
					break;
			}
		}
	}

	private final Region region;
	private final EntityPlayer player;
	private final int slot;
	private final LocalButton button = new LocalButton(35, 50);

	private boolean guiOpen;
	private int blocks = 1;
	private double lastCharge;
	private double lastCosts;
	private String lastTimeMsg;
	private String warnMessage;


	public DistanceControlGuiRender(Region region, EntityPlayer player, int slot)
	{
		this.region = region;
		this.player = player;
		this.slot = slot;
		FMLCommonHandler.instance().bus().register(this);
		controls.bind();
	}

	private void close()
	{
		controls.unbind();
		FMLCommonHandler.instance().bus().unregister(this);
		instance = null;
	}

	private void openAsGui()
	{
		if (!guiOpen)
		{
			guiOpen = true;
			Minecraft.getMinecraft().displayGuiScreen(new GuiThis());
		}
	}

	public void closeGui()
	{
		if (guiOpen)
		{
			guiOpen = false;
			Minecraft.getMinecraft().displayGuiScreen(null);
			return;
		}
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END)
		{
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.theWorld != null && !mc.skipRenderWorld && !mc.gameSettings.hideGUI && mc.currentScreen == null && !mc.gameSettings.showDebugInfo)
			{
				draw(mc.displayWidth, mc.displayHeight);
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END)
		{
			if(Minecraft.getMinecraft().thePlayer == null)
			{
				close();
				return;
			}

			ItemStack is = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(slot);
			if (is == null || is.getItem() != InitCommon.distanceControl)
			{
				close();
				return;
			}

			if(controls.esc.isPressed()) close();
			if(controls.inv.isPressed()) openAsGui();
			if(controls.vTop.isPressed()) button.press(ForgeDirection.NORTH);
			if(controls.vBottom.isPressed()) button.press(ForgeDirection.SOUTH);
			if(controls.vLeft.isPressed()) button.press(ForgeDirection.WEST);
			if(controls.vRight.isPressed()) button.press(ForgeDirection.EAST);
			if(controls.PgUp.isPressed()) button.press(ForgeDirection.UP);
			if(controls.PgDn.isPressed()) button.press(ForgeDirection.DOWN);

			if(controls.plus.isPressed() || controls.plus1.isPressed()) button.pm = true;
			if(controls.minus.isPressed() || controls.minus1.isPressed()) button.pm = false;

			if(button.pressedCounter > 0) button.pressedCounter--;
		}
	}

	private void draw(int width, int height)
	{
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		GuiUtils.drawGradientRect(0, 25, 120, height, -1072689136, -804253680);
		button.drawInGame();

		int x = region.getShape().getLenX();
		int y = region.getShape().getLenY();
		int z = region.getShape().getLenZ();
		int summ = region.countBlocks();

		if (region.getCharge() != lastCharge || region.getCurrentCost() != lastCosts)
		{
			lastCosts = region.getCurrentCost();
			lastCharge = region.getCharge();
			lastTimeMsg = BasicTypeFormatter.formatTime((long) (lastCharge / lastCosts * 86400000));
		}

		if(!guiOpen)
		{
			GuiUtils.drawCenteredString(tlt("privreg.gui.distcont.line1", Keyboard.getKeyName(controls.inv.getKeyCode())), 60, 105, 0xd4d4d4);
			GuiUtils.drawString(tlt("privreg.gui.distcont.line2"), 2, 115, 0xd4d4d4);
			GuiUtils.drawString(tlt("privreg.gui.distcont.line3"), 2, 125, 0xd4d4d4);
			GuiUtils.drawString(tlt("privreg.gui.distcont.line4"), 2, 135, 0xd4d4d4);
		}

		fr.drawString(tlt("privreg.gui.main.info.charge", (int) region.getCharge(), region.getMaxCharge()), 2, 145, 0xd4d4d4);
		fr.drawString(tlt("privreg.gui.main.info.size", x, z, summ), 2, 155, 0xd4d4d4);
		fr.drawString(tlt("privreg.gui.main.info.cost", (Math.round(region.getCurrentCost() * 100)) / 100F), 2, 165, 0xd4d4d4);
		fr.drawString(tlt("privreg.gui.main.info.exte", region.getTacts(), region.getMaxTacts()), 2, 175, 0xd4d4d4);

		if(region.getCharge() > 0)
		{
			fr.drawString(tlt("privreg.gui.main.info.time", ""), 2, 185, 0xd4d4d4);
			fr.drawString(lastTimeMsg, 2, 195, 0xd4d4d4);
		}
		else
		{
			fr.drawString(tlt("privreg.gui.main.info.dis1"), 2, 185, 0xd4d4d4);
			fr.drawString(tlt("privreg.gui.main.info.dis2"), 2, 195, 0xd4d4d4);
		}

		if (warnMessage != null)
			fr.drawString("\u00a7c" + warnMessage, 2, 205, 0xd4d4d4);
	}

	@SideOnly(Side.CLIENT)
	private static class Controls
	{
		private final KeyBindingUnbindable esc = new KeyBindingUnbindable("esc", 1, "privreg");
		private final KeyBindingUnbindable inv = new KeyBindingUnbindable("inv", Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode(), "privreg");
		private final KeyBindingUnbindable vRight = new KeyBindingUnbindable("vRight", 205, "privreg");
		private final KeyBindingUnbindable vLeft = new KeyBindingUnbindable("vLeft", 203, "privreg");
		private final KeyBindingUnbindable vTop = new KeyBindingUnbindable("vUp", 200, "privreg");
		private final KeyBindingUnbindable vBottom = new KeyBindingUnbindable("vDown", 208, "privreg");
		private final KeyBindingUnbindable PgUp = new KeyBindingUnbindable("PgUp", 201, "privreg");
		private final KeyBindingUnbindable PgDn = new KeyBindingUnbindable("PgDn", 209, "privreg");
		private final KeyBindingUnbindable plus = new KeyBindingUnbindable("plus", 13, "privreg");
		private final KeyBindingUnbindable minus = new KeyBindingUnbindable("minus", 12, "privreg");
		private final KeyBindingUnbindable plus1 = new KeyBindingUnbindable("plus1", 78, "privreg");
		private final KeyBindingUnbindable minus1 = new KeyBindingUnbindable("minus1", 74, "privreg");

		private Controls()
		{
			unbind();
		}

		public void unbind()
		{
			esc.unbind();
			inv.unbind();
			vRight.unbind();
			vLeft.unbind();
			vTop.unbind();
			vBottom.unbind();
			PgUp.unbind();
			PgDn.unbind();
			plus.unbind();
			minus.unbind();
			plus1.unbind();
			minus1.unbind();

			KeyBindingUnbindable.unsafeBind(Minecraft.getMinecraft().gameSettings.keyBindInventory);
		}

		public void bind()
		{
			KeyBindingUnbindable.unsafeUnbind(Minecraft.getMinecraft().gameSettings.keyBindInventory);

			esc.bind();
			inv.bind();
			vRight.bind();
			vLeft.bind();
			vTop.bind();
			vBottom.bind();
			PgUp.bind();
			PgDn.bind();
			plus.bind();
			minus.bind();
			plus1.bind();
			minus1.bind();
		}
	}

	@SideOnly(Side.CLIENT)
	private class LocalButton extends RegionButton
	{
		public LocalButton(int x, int y)
		{
			super(Minecraft.getMinecraft(), x, y);
		}

		public boolean pm = true;
		public int pressedCounter;

		public void press(ForgeDirection dir)
		{
			warnMessage = null;
			RegionManagerClient.getInstance().expandRegion(region, dir, pm ? blocks : -blocks);
			currentState = dir;
			pressedCounter = 5;
			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		}

		private void drawBG()
		{
			mc.renderEngine.bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(x, y, 0, 166, 50, 50);
		}

		public void drawInGame()
		{
			drawBG();
			if (pm) drawTexturedModalRect(x + 36, y + 7, 51, 166, 8, 9);
			if (!pm) drawTexturedModalRect(x + 37, y + 36, 59, 166, 7, 2);
			if (pressedCounter == 0) return;
			switch (currentState)
			{
				case WEST:
					drawTexturedModalRect(x + 11, y + 20, 100, 166, 9, 10);
					break;    // x- +-
				case EAST:
					drawTexturedModalRect(x + 30, y + 20, 110, 166, 9, 10);
					break;    // x+ +-
				case NORTH:
					drawTexturedModalRect(x + 20, y + 11, 78, 166, 10, 9);
					break;    // z- +-
				case SOUTH:
					drawTexturedModalRect(x + 19, y + 29, 88, 166, 12, 9);
					break;    // z+ +-
				case DOWN:
					drawTexturedModalRect(x + 6, y + 35, 66, 166, 6, 6);
					break;    // y- +-
				case UP:
					drawTexturedModalRect(x + 6, y + 8, 72, 166, 6, 7);
					break;    // y+ +-
			}
		}

		@Override
		public void drawButton(int mx, int my) //in GuiScreen only
		{
			drawBG();
			if (plus.mousePressed(mx - x, my - y)) drawTexturedModalRect(x + 36, y + 7, 51, 166, 8, 9);
			if (minus.mousePressed(mx - x, my - y)) drawTexturedModalRect(x + 37, y + 36, 59, 166, 7, 2);
			if (currentState == ForgeDirection.DOWN || bottom.mousePressed(mx - x, my - y))
				drawTexturedModalRect(x + 6, y + 35, 66, 166, 6, 6);
			if (currentState == ForgeDirection.UP || top.mousePressed(mx - x, my - y))
				drawTexturedModalRect(x + 6, y + 8, 72, 166, 6, 7);
			if (currentState == ForgeDirection.NORTH || north.mousePressed(mx - x, my - y))
				drawTexturedModalRect(x + 20, y + 11, 78, 166, 10, 9);
			if (currentState == ForgeDirection.SOUTH || south.mousePressed(mx - x, my - y))
				drawTexturedModalRect(x + 19, y + 29, 88, 166, 12, 9);
			if (currentState == ForgeDirection.WEST || west.mousePressed(mx - x, my - y))
				drawTexturedModalRect(x + 11, y + 20, 100, 166, 9, 10);
			if (currentState == ForgeDirection.EAST || east.mousePressed(mx - x, my - y))
				drawTexturedModalRect(x + 30, y + 20, 110, 166, 9, 10);
		}

		@Override
		public void mouseClicked(Region region, int x, int y, int bt)
		{
			if (bt == 0)
			{
				if (currentState != ForgeDirection.UP && top.mouseClicked(mc, x - this.x, y - this.y))
					currentState = ForgeDirection.UP;
				if (currentState != ForgeDirection.DOWN && bottom.mouseClicked(mc, x - this.x, y - this.y))
					currentState = ForgeDirection.DOWN;
				if (currentState != null && plus.mouseClicked(mc, x - this.x, y - this.y))
					RegionManagerClient.getInstance().expandRegion(region, currentState, 1);
				if (currentState != null && minus.mouseClicked(mc, x - this.x, y - this.y))
					RegionManagerClient.getInstance().expandRegion(region, currentState, -1);
				if (currentState != ForgeDirection.NORTH && north.mouseClicked(mc, x - this.x, y - this.y))
					currentState = ForgeDirection.NORTH;
				if (currentState != ForgeDirection.WEST && west.mouseClicked(mc, x - this.x, y - this.y))
					currentState = ForgeDirection.WEST;
				if (currentState != ForgeDirection.SOUTH && south.mouseClicked(mc, x - this.x, y - this.y))
					currentState = ForgeDirection.SOUTH;
				if (currentState != ForgeDirection.EAST && east.mouseClicked(mc, x - this.x, y - this.y))
					currentState = ForgeDirection.EAST;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private class GuiThis extends GuiScreen
	{
//		private GuiButton enabled;

		public GuiThis()
		{

		}

		@SuppressWarnings("unchecked")
		@Override
		public void initGui()
		{
			buttonList.add(new GuiButton(0, 2, 105, 116, 15, "показать территорию"));
//			buttonList.add(enabled = new GuiButton(1, 2, 122, 116, 15, region.isEnabled() ? "\u00a72выключить" : "\u00a7cвключить"));

			buttonList.add(new GuiButton(3, 2, 25, 15, 15, "+1"));
			buttonList.add(new GuiButton(4, 20, 25, 15, 15, "+5"));
			buttonList.add(new GuiButton(5, 40, 25, 15, 15, "+10"));
			buttonList.add(new GuiButton(6, 60, 25, 15, 15, "+50"));
			buttonList.add(new GuiButton(7, 80, 25, 15, 15, "+100"));
			buttonList.add(new GuiButton(8, 100, 25, 15, 15, "+1000"));
		}

		@Override
		protected void actionPerformed(GuiButton bt)
		{
			switch (bt.id)
			{
				case 0:
					ClientSelectionRenderer.toggleRender(region.getID());
					break;
//				case 1:
//					new PacketButton(region.getID(), region.isEnabled() ? 2 : 1).sendToServer();
//					break;

				case 3:
					blocks = 1;
					break;
				case 4:
					blocks = 5;
					break;
				case 5:
					blocks = 10;
					break;
				case 6:
					blocks = 50;
					break;
				case 7:
					blocks = 100;
					break;
				case 8:
					blocks = 1000;
					break;
			}
		}

		@Override
		public void drawScreen(int par1, int par2, float par3)
		{
			draw(width, height);
//			enabled.displayString = region.isEnabled() ? "\u00a72выключить" : "\u00a7cвключить";
			super.drawScreen(par1, par2, par3);
			button.drawButton(par1, par2);
		}

		@Override
		protected void mouseClicked(int x, int y, int bt)
		{
			super.mouseClicked(x, y, bt);
			button.mouseClicked(region, x, y, bt);
		}

		@Override
		protected void keyTyped(char par1, int par2)
		{
			if(par2 == 1 || par2 == controls.inv.getKeyCode())
				closeGui();
		}

		@Override
		public boolean doesGuiPauseGame()
		{
			return false;
		}
	}
}
