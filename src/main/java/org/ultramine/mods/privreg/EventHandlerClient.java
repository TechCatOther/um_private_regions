package org.ultramine.mods.privreg;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.ultramine.mods.privreg.modules.RegionModuleGreeting;
import org.ultramine.mods.privreg.modules.RegionModuleBasic;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.render.OverlayMessageRender;
import org.ultramine.regions.BlockPos;

import static org.ultramine.util.I18n.tlt;

@SideOnly(Side.CLIENT)
public class EventHandlerClient
{
	private BlockPos last;

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END)
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if(player != null && (player.posX != 0 || player.posY != 0 || player.posZ != 0))
			{
				BlockPos current = new BlockPos(player.posX, player.posY, player.posZ);

				if(last == null)
				{
					last = current;
					return;
				}
				if (last.equals(current))
					return;

				Region regin1 = RegionManagerClient.getInstance().getRegion(last);
				Region regin2 = RegionManagerClient.getInstance().getRegion(current);

				if(regin1 != regin2)
				{
					if(regin2 != null)
					{
						RegionModuleGreeting module = regin2.getModuleWithClass(RegionModuleGreeting.class);
						String entering = "";
						if(module != null)
							if (regin2.getOwnerStorage().isOwnerClient())
								entering = module.getMsgOwnersEntering();
							else
								entering = module.getMsgGuestEntering();

						OverlayMessageRender.display("\u00a7"+(regin2.getOwnerStorage().isOwnerClient() ? "2" : "c")+tlt("privreg.msg.entering"), "\u00a7c"+entering, 4F);
					}
					else// if(regin1 != null)
					{
						RegionModuleGreeting module = regin1.getModuleWithClass(RegionModuleGreeting.class);
						String leaving = "";
						if (module != null)
							if(regin1.getOwnerStorage().isOwnerClient())
								leaving = module.getMsgOwnersLeaving();
							else
								leaving = module.getMsgGuestLeave();

						OverlayMessageRender.display("\u00a7"+(regin1.getOwnerStorage().isOwnerClient() ? "2" : "c")+tlt("privreg.msg.leave"), "\u00a79"+leaving, 4F);
					}
				}

				last = current;
			} else {
				last = null;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBreakSpeedEvent(PlayerEvent.BreakSpeed e)
	{
		if(!e.entity.worldObj.isRemote || ClientUtils.isAdminClient()) return;

		MovingObjectPosition o = Minecraft.getMinecraft().objectMouseOver;
		if(o != null && o.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			Region region = RegionManagerClient.getInstance().getRegion(o.blockX, o.blockY, o.blockZ);

			if(region != null && region.isActive())
			{
				if(region.hasModule(RegionModuleBasic.class))
					region.getModuleWithClass(RegionModuleBasic.class).onBreakSpeedEvent(e);
			}

		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if(!e.entity.worldObj.isRemote || ClientUtils.isAdminClient()) return;

		Region region = e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR ?
				RegionManagerClient.getInstance().getRegion( MathHelper.floor_double(e.entity.posX), MathHelper.floor_double(e.entity.posY), MathHelper.floor_double(e.entity.posZ)) :
				RegionManagerClient.getInstance().getRegion(e.x, e.y, e.z);
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onPlayerInteractClient(e);
		}
	}
}
