package org.ultramine.mods.privreg.gui.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.ultramine.gui.ElementButton;
import org.ultramine.gui.ElementLabel;
import org.ultramine.gui.GuiStyled;
import org.ultramine.gui.IGuiElement;
import org.ultramine.mods.privreg.Action;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.packets.PacketRegionModule;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public abstract class GuiModuleSettings extends GuiStyled
{
	private static final ResourceLocation BG = new ResourceLocation("privreg:textures/gui/ownerrights.png");

	private final GuiRegionModules parent;

	GuiModuleSettings(GuiRegionModules p)
	{
		this.parent = p;

		setBG(BG);
		setSize(180, 130);
		//setPadding(19, 9);
	}

	public final void close()
	{
		onClose();
		getParent().handler = null;
		PacketRegionModule packet = getModule().createPacket(Action.UPDATE);
		if (packet != null)
			packet.sendToServer();
	}


	@Override
	public void relayout()
	{
		controlList.clear();
		addElement(new ElementButton(0, 166, 6, 8, 8, "X"));
		addElement(new ElementLabel(10, 6, tlt("privreg.gui.module.settings") + " " + getModule().getClass().getSimpleName()));
		addElements();
	}


	@Override
	public void actionPerformed(int id, IGuiElement element, Object... data)
	{
		if (id == 0) close();
	}

	@Override
	public void keyTyped(char c, int code)
	{
		if (code == 1)
		{
			close();
		} else
		{
			super.keyTyped(c, code);
		}
	}


	public GuiRegionModules getParent()
	{
		return parent;
	}

	public abstract RegionModule getModule();

	protected abstract void onClose();

	protected abstract void addElements();
}
