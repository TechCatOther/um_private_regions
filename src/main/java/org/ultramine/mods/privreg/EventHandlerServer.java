package org.ultramine.mods.privreg;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.hash.TIntByteHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import org.ultramine.mods.privreg.modules.RegionModuleAdmin;
import org.ultramine.mods.privreg.modules.RegionModuleBasic;
import org.ultramine.mods.privreg.modules.RegionModuleBarrier;
import org.ultramine.mods.privreg.modules.RegionModuleExplosion;
import org.ultramine.mods.privreg.modules.RegionModuleLiquidFlow;
import org.ultramine.mods.privreg.modules.RegionModuleMobDamage;
import org.ultramine.mods.privreg.modules.RegionModuleMobSpawn;
import org.ultramine.mods.privreg.modules.RegionModuleSnowFall;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.server.event.EntityPotionApplyEffectEvent;
import org.ultramine.server.event.EntitySetFireEvent;
import org.ultramine.server.event.HangingEvent;
import org.ultramine.server.event.SetBlockEvent;
import org.ultramine.server.event.WorldEventProxy;
import org.ultramine.server.event.WorldUpdateObject;

import java.util.Iterator;
import java.util.List;

@SideOnly(Side.SERVER)
public class EventHandlerServer
{
	private static Region getRegion(World world, int x, int y, int z)
	{
		return PrivateRegions.instance().getServerRegion(world.provider.dimensionId, x, y, z);
	}

	private static Region getRegion()
	{
		WorldEventProxy proxy = WorldEventProxy.getCurrent();
		WorldUpdateObject obj = proxy.getUpdateObject();
		switch(obj.getType())
		{
			case BLOCK_EVENT:
			case BLOCK_RANDOM:
			case BLOCK_PENDING:
				return getRegion(proxy.getWorld(), obj.getX(), obj.getY(), obj.getZ());
			case TILEE_ENTITY:
				TileEntity te = obj.getTileEntity();
				return getRegion(proxy.getWorld(), te.xCoord, te.yCoord, te.zCoord);
			case PLAYER:
			case ENTITY:
				Entity ent = obj.getEntity();
				return getRegion(proxy.getWorld(), MathHelper.floor_double(ent.posX), MathHelper.floor_double(ent.posY), MathHelper.floor_double(ent.posZ));
			case ENTITY_WEATHER:
				break;
			case WEATHER:
				break;
			case UNKNOWN:
				break;
		}

		return null;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockEvent.BreakEvent e)
	{
		Region region = getRegion(e.world, e.x, e.y, e.z);
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onBlockBreak(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockEvent.PlaceEvent e)
	{
		Region region = getRegion(e.world, e.x, e.y, e.z);
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onBlockPlace(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBlockPlaceMulti(BlockEvent.MultiPlaceEvent e)
	{
		List<BlockSnapshot> snapshots = e.getReplacedBlockSnapshots();
		for(BlockSnapshot bs : snapshots)
		{
			Region region = getRegion(e.world, bs.x, bs.y, bs.z);
			if(region != null && region.isActive())
			{
				if(region.hasModule(RegionModuleBasic.class))
					region.getModuleWithClass(RegionModuleBasic.class).onBlockPlaceMulti(e, bs);
				if(e.isCanceled())
					return;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onBlockChange(SetBlockEvent e)
	{
		Region region = getRegion(e.world, e.x, e.y, e.z);
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onBlockChange(e);
			if(!e.isCanceled() && region.hasModule(RegionModuleBarrier.class))
				region.getModuleWithClass(RegionModuleBarrier.class).onBlockChange(e);
			if(!e.isCanceled() && region.hasModule(RegionModuleLiquidFlow.class))
				region.getModuleWithClass(RegionModuleLiquidFlow.class).onBlockChange(e);
			if(!e.isCanceled() && region.hasModule(RegionModuleSnowFall.class))
				region.getModuleWithClass(RegionModuleSnowFall.class).onBlockChange(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onBlockHarvest(BlockEvent.HarvestDropsEvent e)
	{
		Region region = getRegion(e.world, e.x, e.y, e.z);
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onBlockHarvest(e);
			if(e.dropChance != -1.0f && region.hasModule(RegionModuleAdmin.class))
				region.getModuleWithClass(RegionModuleAdmin.class).onBlockHarvest(e);
			if(e.dropChance != -1.0f && region.hasModule(RegionModuleLiquidFlow.class))
				region.getModuleWithClass(RegionModuleLiquidFlow.class).onBlockHarvest(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onHangingBreak(HangingEvent.HangingBreakEvent e)
	{
		Region region = getRegion(e.entity.worldObj, e.entity.field_146063_b, e.entity.field_146064_c, e.entity.field_146062_d);
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onHangingBreak(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Region region = e.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR ? getRegion() : getRegion(e.world, e.x, e.y, e.z);
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onPlayerInteract(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onExplode(ExplosionEvent.Detonate e)
	{
		boolean removeEntities = false;
		TIntByteMap map = new TIntByteHashMap(16, 0.75F, 0, (byte)0);
		for(Iterator<?> it = e.explosion.affectedBlockPositions.iterator(); it.hasNext();)
		{
			ChunkPosition pos = (ChunkPosition)it.next();
			Region region = getRegion(e.world, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
			if(region != null && region.isActive())
			{
				byte val = map.get(region.getID());
				boolean cancel;
				if(val == 0)
				{
					cancel = region.hasModule(RegionModuleBasic.class) && region.getModuleWithClass(RegionModuleBasic.class).cancelBlockExplosion() ||
							region.hasModule(RegionModuleExplosion.class);
					map.put(region.getID(), (byte)(cancel ? 1 : 2));
				}
				else if(val == 1)
					cancel = true;
				else
					cancel = false;

				if(cancel)
				{
					it.remove();
					removeEntities = true;
				}
			}
		}

		if(removeEntities)
			e.getAffectedEntities().clear();
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLivingAttacked(LivingAttackEvent e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.entity.posX), MathHelper.floor_double(e.entity.posY), MathHelper.floor_double(e.entity.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onLivingAttacked(e);
			if(!e.isCanceled() && region.hasModule(RegionModuleMobDamage.class))
				region.getModuleWithClass(RegionModuleMobDamage.class).onLivingAttacked(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onEntityPotionApplyEffect(EntityPotionApplyEffectEvent e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.entity.posX), MathHelper.floor_double(e.entity.posY), MathHelper.floor_double(e.entity.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onEntityPotionApply(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onEntitySetFire(EntitySetFireEvent e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.entity.posX), MathHelper.floor_double(e.entity.posY), MathHelper.floor_double(e.entity.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onEntitySetFire(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerAttack(AttackEntityEvent e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.target.posX), MathHelper.floor_double(e.target.posY), MathHelper.floor_double(e.target.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onPlayerAttack(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerInteractEntity(EntityInteractEvent e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.target.posX), MathHelper.floor_double(e.target.posY), MathHelper.floor_double(e.target.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onPlayerInteractEntity(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onEntityItemPickup(EntityItemPickupEvent e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.item.posX), MathHelper.floor_double(e.item.posY), MathHelper.floor_double(e.item.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleBasic.class))
				region.getModuleWithClass(RegionModuleBasic.class).onEntityItemPickup(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onMobSpawn(LivingSpawnEvent.CheckSpawn e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.entity.posX), MathHelper.floor_double(e.entity.posY), MathHelper.floor_double(e.entity.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleMobSpawn.class))
				region.getModuleWithClass(RegionModuleMobSpawn.class).onLivingSpawn(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLivingDrops(LivingDropsEvent e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.entity.posX), MathHelper.floor_double(e.entity.posY), MathHelper.floor_double(e.entity.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleAdmin.class))
				region.getModuleWithClass(RegionModuleAdmin.class).onLivingDrops(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerItemToss(ItemTossEvent e)
	{
		Region region = getRegion(e.entity.worldObj, MathHelper.floor_double(e.entity.posX), MathHelper.floor_double(e.entity.posY), MathHelper.floor_double(e.entity.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleAdmin.class))
				region.getModuleWithClass(RegionModuleAdmin.class).onPlayerItemToss(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onCommand(CommandEvent e)
	{
		if(!(e.sender instanceof EntityPlayerMP))
			return;
		EntityPlayerMP player = (EntityPlayerMP)e.sender;
		if(player.playerNetServerHandler == null)
			return;
		Region region = getRegion(player.worldObj, MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
		if(region != null && region.isActive())
		{
			if(region.hasModule(RegionModuleAdmin.class))
				region.getModuleWithClass(RegionModuleAdmin.class).onPlayerCommand(e);
		}
	}
}
