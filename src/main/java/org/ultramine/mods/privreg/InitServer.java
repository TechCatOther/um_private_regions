package org.ultramine.mods.privreg;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.ultramine.mods.privreg.integration.DynmapIntegration;
import org.ultramine.mods.privreg.integration.IC2EventHandler;
import org.ultramine.mods.privreg.integration.OpenComputersEventHandler;
import org.ultramine.mods.privreg.packets.PacketUserFlags;

@SideOnly(Side.SERVER)
public class InitServer extends InitCommon
{
	@Override
	void initSided()
	{
		MinecraftForge.EVENT_BUS.register(new EventHandlerServer());

		try
		{
			Class.forName("ic2.core.IC2");
			MinecraftForge.EVENT_BUS.register(new IC2EventHandler());
		} catch(Throwable t){}

		try
		{
			Class.forName("li.cil.oc.OpenComputers");
			MinecraftForge.EVENT_BUS.register(new OpenComputersEventHandler());
		} catch(Throwable t){}

		try
		{
			Class.forName("org.dynmap.DynmapCommonAPIListener");
			DynmapIntegration.init();
		} catch(Throwable t){}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e)
	{
		EntityPlayerMP player = (EntityPlayerMP)e.player;
		new PacketUserFlags(player).sendTo(player);
	}
}
