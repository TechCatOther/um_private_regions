package org.ultramine.mods.privreg.gui.inv;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import org.ultramine.mods.privreg.packets.PacketGuiRentServerTime;

public class ContainerRent extends Container
{
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(Object crafter : this.crafters)
		{
			if(crafter instanceof EntityPlayerMP)
				new PacketGuiRentServerTime().sendTo((EntityPlayerMP)crafter);
		}
	}
	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_)
	{
		return true;
	}
}
