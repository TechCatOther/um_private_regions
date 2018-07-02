package org.ultramine.mods.privreg.gui.inv;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import org.ultramine.mods.privreg.packets.PacketIntWindowProperty;
import org.ultramine.mods.privreg.tiles.TileBlockRegion;

public class ContainerBlockRegion extends Container
{
	private final TileBlockRegion te;

	public ContainerBlockRegion(TileBlockRegion te)
	{//174, 143
		this.te = te;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int par2)
	{
		if(te.getRegion() == null)
			return;
		switch (id)
		{
			case 0:
				te.getRegion().setCharge(Float.intBitsToFloat(par2));
				break;
			case 1:
				te.getRegion().setMaxCharge(par2);
				break;
			case 2:
				te.getRegion().setTacts(par2);
				break;
			case 3:
				te.getRegion().setMaxTacts(par2);
				break;
		}
	}

	private int lastBlockCharge = -1;
	private int lastBlockMaxCharge = -1;
	private int lastTacts = -1;
	private int lastMaxTacts = -1;

	@Override
	public void detectAndSendChanges()
	{
		//super.detectAndSendChanges();
		if(te.getRegion() == null)
			return;

		if(crafters.size() > 0)
		{
			ICrafting player = (ICrafting) this.crafters.get(0);
			int val = Float.floatToRawIntBits((float)te.getRegion().getCharge());
			if (lastBlockCharge != val)
			{
				new PacketIntWindowProperty(this, 0, val).sendTo(player);
				lastBlockCharge = val;
			}
			val = te.getRegion().getMaxCharge();
			if (lastBlockMaxCharge != val)
			{
				new PacketIntWindowProperty(this, 1, val).sendTo(player);
				lastBlockMaxCharge = val;
			}
			val = te.getRegion().getTacts();
			if (lastTacts != val)
			{
				player.sendProgressBarUpdate(this, 2, val);
				lastTacts = val;
			}
			val = te.getRegion().getMaxTacts();
			if (lastMaxTacts != val)
			{
				player.sendProgressBarUpdate(this, 3, val);
				lastMaxTacts = val;
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return te.getRegion() != null;
	}
}
