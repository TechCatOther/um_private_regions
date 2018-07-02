package org.ultramine.mods.privreg.gui;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.util.ResourceLocation;
import org.ultramine.gui.ElementButton;
import org.ultramine.gui.ElementCheckBox;
import org.ultramine.gui.ElementLabel;
import org.ultramine.gui.GuiScreenToGui;
import org.ultramine.gui.GuiStyled;
import org.ultramine.gui.IGui;
import org.ultramine.gui.IGuiElement;
import org.ultramine.mods.privreg.Action;
import org.ultramine.mods.privreg.modules.RegionModuleRent;
import org.ultramine.mods.privreg.owner.OwnerRight;
import org.ultramine.mods.privreg.owner.RegionOwnerStorage;
import org.ultramine.mods.privreg.packets.PacketRegionOwner;
import org.ultramine.mods.privreg.regions.RegionRights;

import static org.ultramine.mods.privreg.owner.RegionOwnerStorage.DEFAULT_OWNER_UUID;
import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class GuiOwners extends GuiStyled
{
	private static final ResourceLocation BG = new ResourceLocation("privreg:textures/gui/blockregionmain.png");
	private static final ResourceLocation BG1 = new ResourceLocation("privreg:textures/gui/ownerrights.png");
	private IGui parent;

	private RegionOwnerStorage storage;
	private int ownersCount;

	private IGui handler;

	public GuiOwners(IGui parent, RegionOwnerStorage storage)
	{
		this.parent = parent;
		this.storage = storage;
		setBG(BG);
		setSize(256, 166);
	}

	@Override
	public void relayout()
	{
		super.relayout();
		this.controlList.clear();
		addElement(new ElementButton(0, 241, 6, 8, 8, "Х"));
		addElement(new ElementButton(1, 209, 6, 30, 8, tlt("privreg.gui.back")));

		int position = 0;
		for(GameProfile profile : storage.getAllNames())
		{
			ElementButton bt1;
			ElementButton bt2;
			if(position < 10)
			{
				bt1 = new ProfileButton(100 + position, 6, 20 + position * 14, 70, 12, profile);
				bt2 = new ProfileButton(500 + position, 78, 20 + position * 14, 10, 12, profile);
			}
			else if(position < 20)
			{
				bt1 = new ProfileButton(100 + position, 92, 20 + (position - 10) * 14, 70, 12, profile);
				bt2= new ProfileButton(500 + position, 164, 20 + (position - 10) * 14, 10, 12, profile);
			}
			else if(position < 30)
			{
				bt1 = new ProfileButton(100 + position, 178, 20 + (position - 20) * 14, 70, 12, profile);
				bt2 = new ProfileButton(500 + position, 250, 20 + (position - 20) * 14, 10, 12, profile);
			}
			else
			{
				bt1 = new ProfileButton(100 + position, 264, 20 + (position - 30) * 14, 70, 12, profile);
				bt2 = new ProfileButton(500 + position, 336, 20 + (position - 30) * 14, 10, 12, profile);
			}

			bt1.enabled = storage.canEditClient(profile);
			bt2.enabled = bt1.enabled && !profile.getId().equals(DEFAULT_OWNER_UUID);
			bt2.displayString = "\u00a7cX";

			addElement(bt1);
			addElement(bt2);
			position++;
		}
		ownersCount = position;

		if(handler != null)
		{
			handler.resizeGui(mc, width, height);
			handler.relayout();
		}
	}

	@Override
	public void onGuiClosed()
	{
		Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C0DPacketCloseWindow(Minecraft.getMinecraft().thePlayer.openContainer.windowId));
	}

	@Override
	public void update()
	{
		super.update();
		if (ownersCount != storage.getAllNames().size())
			relayout();
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
				mc.displayGuiScreen(parent == null ? null : new GuiScreenToGui(parent));
				return;
		}
		if(id >= 100)
		{
			GameProfile profile = ((ProfileButton) element).getProfile();
			if(storage.canEditClient(profile))
			{
				if(id >= 500)
				{
					new PacketRegionOwner(storage.region, storage.getOwner(profile), Action.REMOVE).sendToServer();
				}
				else
				{
					handler = new GuiOwnerRights(this, profile);
					handler.resizeGui(mc, width, height);
					handler.relayout();
				}
			}
		}
	}

	@Override
	public void draw(int mx, int my, float par3)
	{
		if(handler != null)
		{
			super.draw(0, 0, par3);
			handler.draw(mx, my, par3);
		}
		else
		{
			super.draw(mx, my, par3);
		}
	}

	@Override
	public void keyTyped(char c, int code)
	{
		if(handler != null)
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

	@Override
	public void mouseWheel(int wheel)
	{
		if(handler != null)
			handler.mouseWheel(wheel);
	}

	@SideOnly(Side.CLIENT)
	private static class ProfileButton extends ElementButton
	{
		private final GameProfile profile;

		public ProfileButton(int id, int x, int y, int width, int height, GameProfile profile)
		{
			super(id, x, y, width, height, profile.getId().equals(DEFAULT_OWNER_UUID) ?
					tlt("privreg.gui.addowner.guestname") : profile.getId().equals(RegionModuleRent.TENANT_OWNER_UUID) ?
					tlt("privreg.gui.addowner.tenantname") : profile.getName());
			this.profile = profile;
		}

		public GameProfile getProfile()
		{
			return profile;
		}
	}

	@SideOnly(Side.CLIENT)
	private static class OwnerRightCheckBox extends ElementCheckBox
	{
		private RegionOwnerStorage storage;
		private GameProfile profile;
		private OwnerRight linkedRight;

		public OwnerRightCheckBox(int x, int y, RegionOwnerStorage storage, GameProfile profile, OwnerRight right)
		{
			super(-1, x, y, tlt("privreg.right."+right.getKey()));
			this.storage = storage;
			this.profile = profile;
			this.linkedRight = right;
		}

		@Override
		public boolean isChecked()
		{
			return storage.hasRight(profile, linkedRight);
		}

		@Override
		public ElementCheckBox setChecked(boolean ch)
		{
			if(storage.hasRight(profile, RegionRights.ALL_RIGHTS))
				storage.setOwnersRight(profile, RegionRights.ALL_RIGHTS, false);
			storage.setOwnersRight(profile, linkedRight, ch);
			return this;
		}
	}

	@SideOnly(Side.CLIENT)
	private static class GuiOwnerRights extends GuiStyled
	{
		private GuiOwners parent;
		private GameProfile profile;
		private ElementScrollPanel scroll;

		public GuiOwnerRights(GuiOwners p, GameProfile profile)
		{
			this.parent = p;
			this.profile = profile;
			setBG(BG1);
			setSize(180, 130);

			addElement(new ElementLabel(10, 6, profile.getId().equals(DEFAULT_OWNER_UUID) ? tlt("privreg.gui.addowner.rights.title.guest") :
					(tlt("privreg.gui.addowner.rights.title") + " " + profile.getName())));
			addElement(new ElementButton(0, 166, 6, 8, 8, "X"));
			addElement(scroll = new ElementScrollPanel(160, 100, 10, 20, 10));
		}

		private void close()
		{
			parent.handler = null;
			new PacketRegionOwner(parent.storage.region, parent.storage.getOwner(profile), Action.UPDATE).sendToServer();
		}

		@Override
		public void relayout()
		{
			scroll.clearElements();
			String lastGroup = null;
			for(OwnerRight right : parent.storage.getSupportedRights())
			{
				if(lastGroup != null && !lastGroup.equals(right.getGroup()))
					scroll.addElement(new ElementLabel(0, 0, tlt("privreg.right."+right.getGroup())));
				if(lastGroup == null || !lastGroup.equals(right.getGroup()))
					lastGroup = right.getGroup();
				scroll.addElement(new OwnerRightCheckBox(0, 0, parent.storage, profile, right));
				if(right == RegionRights.ALL_RIGHTS)
					scroll.addElement(new ElementEmpty());
			}
			scroll.releyout();
		}

		@Override
		public void actionPerformed(int id, IGuiElement element, Object... data)
		{
			if (id == 0)
				close();
		}

		@Override
		public void keyTyped(char c, int code)
		{
			if (code == 1)
			{
				close();
			}
		}

		@Override
		public void mouseWheel(int wheel)
		{
			scroll.mouseWheel(wheel);
		}
	}
}
