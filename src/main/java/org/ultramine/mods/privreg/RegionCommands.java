package org.ultramine.mods.privreg;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import org.ultramine.commands.Command;
import org.ultramine.commands.CommandContext;
import org.ultramine.mods.privreg.packets.PacketRegionAction;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.IRegion;
import org.ultramine.regions.Rectangle;

import java.util.Set;

@SideOnly(Side.SERVER)
public class RegionCommands
{
	@Command(
			name = "region",
			group = "privreg",
			permissions = {"command.privreg.region"},
			syntax = {
					"[clear show id block]",
					"[find] <%radius>"
			}
	)
	public static void region(CommandContext ctx)
	{
		EntityPlayerMP player = ctx.getSenderAsPlayer();
		if(ctx.getAction().equals("find"))
		{
			int radius = ctx.get("radius").asInt(1);
			Set<IRegion> regions =  PrivateRegions.instance().getServerRegionManager(player.worldObj.provider.dimensionId)
					.getRegionsInRange(new BlockPos(player.posX, player.posY, player.posZ).toRect().expandAll(radius));
			if(regions.isEmpty())
			{
				ctx.sendMessage("Регионы в радиусе не обнаружены");
			}
			else
			{
				ctx.sendMessage("Обнаружено %s регионов", regions.size());
				for(IRegion reg : regions)
				{
					new PacketRegionAction(reg.getID(), PacketRegionAction.CLIENT_RENDER).sendTo(player);
				}
			}
		}
		else if(ctx.getAction().equals("clear"))
		{
			new PacketRegionAction(PacketRegionAction.CLIENT_CLEAR_RENDER).sendTo(player);
		}
		else if(ctx.getAction().equals("show"))
		{
			Region region = PrivateRegions.instance().getServerRegionManager(player.worldObj.provider.dimensionId).getRegion(new BlockPos(player.posX, player.posY, player.posZ));
			ctx.check(region != null, "Вы находитесь вне региона");
			new PacketRegionAction(region.getID(), PacketRegionAction.CLIENT_RENDER).sendTo(player);
		}
		else if(ctx.getAction().equals("id"))
		{
			Region region = PrivateRegions.instance().getServerRegionManager(player.worldObj.provider.dimensionId).getRegion(new BlockPos(player.posX, player.posY, player.posZ));
			ctx.check(region != null, "Вы находитесь вне региона");
			ctx.sendMessage("ID: %s", region.getID());
		}
		else if(ctx.getAction().equals("block"))
		{
			Region region = PrivateRegions.instance().getServerRegionManager(player.worldObj.provider.dimensionId).getRegion(new BlockPos(player.posX, player.posY, player.posZ));
			ctx.check(region != null, "Вы находитесь вне региона");
			ctx.sendMessage("Block coords: (%s, %s, %s)", region.getBlock().x, region.getBlock().y, region.getBlock().z);
		}
	}
}
