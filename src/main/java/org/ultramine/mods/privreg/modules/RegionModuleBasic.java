package org.ultramine.mods.privreg.modules;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.ultramine.mods.privreg.gui.GuiRegionModules;
import org.ultramine.mods.privreg.gui.modules.GuiModuleBasic;
import org.ultramine.mods.privreg.gui.modules.GuiModuleSettings;
import org.ultramine.mods.privreg.owner.OwnerRight;
import org.ultramine.mods.privreg.owner.RightRegistry;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.regions.BlockPos;
import org.ultramine.server.event.EntityPotionApplyEffectEvent;
import org.ultramine.server.event.EntitySetFireEvent;
import org.ultramine.server.event.HangingEvent;
import org.ultramine.server.event.SetBlockEvent;
import org.ultramine.server.event.WorldEventProxy;
import org.ultramine.server.event.WorldUpdateObject;
import org.ultramine.server.event.WorldUpdateObjectType;
import org.ultramine.server.util.BasicTypeParser;

import static net.minecraft.util.EnumChatFormatting.RED;
import static org.ultramine.util.I18n.tlt;

public class RegionModuleBasic extends RegionModule
{
	public static final OwnerRight RIGHT_BASIC = RightRegistry.register("modbasic", "basic");
	public static final OwnerRight RIGHT_USE_ITEMS = RightRegistry.register("modbasic", "use_items");
	public static final OwnerRight RIGHT_USE_BLOCKS = RightRegistry.register("modbasic", "use_blocks");
	public static final OwnerRight RIGHT_INTERACT_MOBS = RightRegistry.register("modbasic", "interact_mobs");
	public static final OwnerRight RIGHT_PLACE_BLOCKS = RightRegistry.register("modbasic", "place_blocks");
	public static final OwnerRight RIGHT_BREAK_BLOCKS = RightRegistry.register("modbasic", "break_blocks");
	public static final OwnerRight RIGHT_PICKUP_ITEMS = RightRegistry.register("modbasic", "pickup_items");
	public static final OwnerRight RIGHT_ATTACK_OWNERS = RightRegistry.register("modbasic", "attack_owners");
	public static final OwnerRight RIGHT_ATTACK_GUESTS = RightRegistry.register("modbasic", "attack_guests");
	public static final OwnerRight RIGHT_ATTACK_ANIMALS = RightRegistry.register("modbasic", "attack_animals");
	public static final OwnerRight RIGHT_ATTACK_MONSTERS = RightRegistry.register("modbasic", "attack_monsters");
	public static final OwnerRight RIGHT_ATTACK_OTHER_ENTITIES = RightRegistry.register("modbasic", "attack_other_entities");

	private boolean disableItemsMode;
	private String[] disableItemsRaw = new String[0];
	private TIntSet disableItems = new TIntHashSet();
	private boolean disableBlocksMode;
	private String[] disableBlocksRaw = new String[0];
	private TIntSet disableBlocks = new TIntHashSet();

	public boolean getDisableItemsMode()
	{
		return disableItemsMode;
	}

	public void setDisableItemsMode(boolean disableItemsMode)
	{
		this.disableItemsMode = disableItemsMode;
	}

	public boolean getDisableBlocksMode()
	{
		return disableBlocksMode;
	}

	public void setDisableBlocksMode(boolean disableBlocksMode)
	{
		this.disableBlocksMode = disableBlocksMode;
	}

	public String[] getDisableItems()
	{
		return disableItemsRaw;
	}

	public void setDisableItems(String[] disableItemsRaw)
	{
		this.disableItemsRaw = disableItemsRaw;
		disableItems.clear();
		for(String str : disableItemsRaw)
		{
			try
			{
				ItemStack is = BasicTypeParser.parseStackType(str);
				disableItems.add(Item.getIdFromItem(is.getItem()) | (is.getItemDamage() << 16));
			}
			catch(Exception e){}
		}
	}

	public String[] getDisableBlocks()
	{
		return disableBlocksRaw;
	}

	public void setDisableBlocks(String[] disableBlocksRaw)
	{
		this.disableBlocksRaw = disableBlocksRaw;
		disableBlocks.clear();
		for(String str : disableBlocksRaw)
		{
			try
			{
				ItemStack is = BasicTypeParser.parseStackType(str);
				disableBlocks.add(Item.getIdFromItem(is.getItem()) | (is.getItemDamage() << 12));
			}
			catch (Exception e){}
		}
	}

	@Override
	public void onPlaceToRegion(Region region)
	{
		super.onPlaceToRegion(region);
		this.region.getOwnerStorage().registerRight(RIGHT_BASIC, true);
		this.region.getOwnerStorage().registerRight(RIGHT_USE_ITEMS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_USE_BLOCKS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_INTERACT_MOBS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_PLACE_BLOCKS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_BREAK_BLOCKS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_PICKUP_ITEMS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_OWNERS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_GUESTS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_ANIMALS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_MONSTERS, true);
		this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_OTHER_ENTITIES, true);

	}

	@Override
	public void onRemoveFromRegion()
	{
		if(this.region != null)
		{
			this.region.getOwnerStorage().registerRight(RIGHT_BASIC, false);
			this.region.getOwnerStorage().registerRight(RIGHT_USE_ITEMS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_USE_BLOCKS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_INTERACT_MOBS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_PLACE_BLOCKS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_BREAK_BLOCKS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_PICKUP_ITEMS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_OWNERS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_GUESTS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_ANIMALS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_MONSTERS, false);
			this.region.getOwnerStorage().registerRight(RIGHT_ATTACK_OTHER_ENTITIES, false);
		}
		super.onRemoveFromRegion();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiModuleSettings createGuiHandler(GuiRegionModules parent)
	{
		return new GuiModuleBasic(parent, this);
	}

	@Override
	protected void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("im", disableItemsMode);
		writeStrings(nbt, "il", disableItemsRaw);
		nbt.setBoolean("bm", disableBlocksMode);
		writeStrings(nbt, "bl", disableBlocksRaw);
	}

	@Override
	protected void readFromNBT(NBTTagCompound nbt)
	{
		disableItemsMode = nbt.getBoolean("im");
		setDisableItems(readStrings(nbt, "il"));
		disableBlocksMode = nbt.getBoolean("bm");
		setDisableBlocks(readStrings(nbt, "bl"));
	}

	private static void writeStrings(NBTTagCompound nbt, String name, String[] data)
	{
		NBTTagList list = new NBTTagList();
		for(String str : data)
			list.appendTag(new NBTTagString(str));
		nbt.setTag(name, list);
	}

	private static String[] readStrings(NBTTagCompound nbt, String name)
	{
		NBTTagList list = nbt.getTagList(name, 8);
		String[] data = new String[list.tagCount()];
		for(int i = 0; i < list.tagCount(); i++)
			data[i] = list.getStringTagAt(i);
		return data;
	}

	@SideOnly(Side.SERVER)
	public void onBlockBreak(BlockEvent.BreakEvent e)
	{
		if(prohibitDefault(RIGHT_BREAK_BLOCKS, false))
			e.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	public void onBlockPlace(BlockEvent.PlaceEvent e)
	{
		if(prohibitDefault(RIGHT_PLACE_BLOCKS, false))
			e.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	public void onBlockPlaceMulti(BlockEvent.MultiPlaceEvent e, BlockSnapshot current)
	{
		if(prohibitDefault(RIGHT_PLACE_BLOCKS, false))
			e.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	public void onBlockChange(SetBlockEvent e)
	{
		if(e.world.getBlock(e.x, e.y, e.z) == Blocks.farmland && e.newBlock == Blocks.dirt && prohibitDefault(RIGHT_BREAK_BLOCKS, false) ||
				prohibitRaw(WorldEventProxy.getCurrent()))
			e.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	public void onBlockHarvest(BlockEvent.HarvestDropsEvent e)
	{
		if(prohibitRaw(WorldEventProxy.getCurrent()))
			e.dropChance = -1.0f;
	}

	@SideOnly(Side.SERVER)
	public void onHangingBreak(HangingEvent.HangingBreakEvent e)
	{
		if(prohibitDefault(RIGHT_BREAK_BLOCKS, false))
			e.setCanceled(true);
	}

	private boolean isItemWhitelisted(ItemStack is)
	{
		int id = Item.getIdFromItem(is.getItem());
		boolean contains = disableItems.contains(id | (is.getItemDamage() << 16));
		if(!contains)
			contains = disableItems.contains(id | (OreDictionary.WILDCARD_VALUE << 16));
		return disableItemsMode != contains;
	}

	private boolean isBlockWhitelisted(Block block, int meta)
	{
		int id = Block.getIdFromBlock(block);
		boolean contains = disableBlocks.contains(id | (meta << 12));
		if(!contains)
			contains = disableBlocks.contains(id | (OreDictionary.WILDCARD_VALUE << 12));
		return disableBlocksMode != contains;
	}

	private void handleInteractCommon(PlayerInteractEvent e)
	{
		EntityPlayer player = e.entityPlayer;
		ItemStack is = player.inventory.getCurrentItem();
		if(is != null)
		{
			boolean isBlock = Block.getBlockFromItem(is.getItem()) != Blocks.air && e.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR;
			if((isBlock || !isItemWhitelisted(is)) && !region.hasRight(player.getGameProfile(), isBlock ? RIGHT_PLACE_BLOCKS : RIGHT_USE_ITEMS))
				e.useItem = Event.Result.DENY;
		}
		if(e.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR && !region.hasRight(player.getGameProfile(), RIGHT_USE_BLOCKS) &&
				!isBlockWhitelisted(e.world.getBlock(e.x, e.y, e.z), e.world.getBlockMetadata(e.x, e.y, e.z)))
		{
			e.useBlock = Event.Result.DENY;
			if(e.useItem != Event.Result.DENY)
				e.useItem = Event.Result.ALLOW;
		}

		if(e.useItem == Event.Result.DENY && e.useBlock == Event.Result.DENY)
			e.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if(isDirectPlayerAction())
		{
			handleInteractCommon(e);
		}
		else
		{
			if(prohibitDefault())
				e.setCanceled(true);
		}
	}

	@SideOnly(Side.SERVER)
	public boolean cancelBlockExplosion()
	{
		return prohibitDefault(RIGHT_BASIC, false) || prohibitDefault(RIGHT_BREAK_BLOCKS, false);
	}

	@SideOnly(Side.SERVER)
	public void onLivingAttacked(LivingAttackEvent e)
	{
		if(e.source instanceof EntityDamageSource || e.source.isExplosion() || e.source.isProjectile())
			handlePvpEvent(e, e.entity, false);
	}

	@SideOnly(Side.SERVER)
	public void onEntityPotionApply(EntityPotionApplyEffectEvent e)
	{
		handlePvpEvent(e, e.entity, true);
	}

	@SideOnly(Side.SERVER)
	public void onEntitySetFire(EntitySetFireEvent e)
	{
		handlePvpEvent(e, e.entity, true);
	}

	@SideOnly(Side.SERVER)
	public void onPlayerAttack(AttackEntityEvent e)
	{
		if(!e.entityPlayer.isEntityPlayerMP())
			return;
		EntityPlayerMP player = (EntityPlayerMP)e.entityPlayer;
		if(player.playerNetServerHandler == null)
			return;
		GameProfile profile = e.entityPlayer.getGameProfile();
		if(e.target.isEntityPlayerMP())
		{
			if(!region.hasRight(profile, region.hasRight(((EntityPlayerMP)e.target).getGameProfile(), RIGHT_BASIC) ? RIGHT_ATTACK_OWNERS : RIGHT_ATTACK_OWNERS))
			{
				e.setCanceled(true);
				player.addChatMessage(new ChatComponentTranslation("privreg.msg.pvp"));
			}

		}
		else if(e.target.isEntityAnimal())
		{
			if(!region.hasRight(profile, RIGHT_ATTACK_ANIMALS))
			{
				e.setCanceled(true);
				player.addChatMessage(new ChatComponentTranslation("privreg.msg.animals"));
			}

		}
		else if(e.target.isEntityMonster())
		{
			if(!region.hasRight(profile, RIGHT_ATTACK_MONSTERS))
			{
				e.setCanceled(true);
				player.addChatMessage(new ChatComponentTranslation("privreg.msg.monsters"));
			}
		}
		else
		{
			if(!region.hasRight(profile, RIGHT_ATTACK_OTHER_ENTITIES))
			{
				e.setCanceled(true);
				player.addChatMessage(new ChatComponentTranslation("privreg.msg.other_entities"));
			}
		}
	}

	@SideOnly(Side.SERVER)
	public void onPlayerInteractEntity(EntityInteractEvent e)
	{
		if(prohibitDefault(RIGHT_INTERACT_MOBS, false))
			e.setCanceled(true);
	}

	@SideOnly(Side.SERVER)
	public void handlePvpEvent(Event e, Entity target, boolean prohibitIfNoEntityOwner)
	{
		if(target.isEntityPlayerMP())
		{
			if(prohibitDefault(region.hasRight(((EntityPlayerMP)target).getGameProfile(), RIGHT_BASIC) ? RIGHT_ATTACK_OWNERS : RIGHT_ATTACK_OWNERS, prohibitIfNoEntityOwner))
			{
				e.setCanceled(true);
				message("privreg.msg.pvp");
			}

		}
		else if(target.isEntityAnimal())
		{
			if(prohibitDefault(RIGHT_ATTACK_ANIMALS, prohibitIfNoEntityOwner))
			{
				e.setCanceled(true);
				message("privreg.msg.animals");
			}

		}
		else if(target.isEntityMonster())
		{
			if(prohibitDefault(RIGHT_ATTACK_MONSTERS, prohibitIfNoEntityOwner))
			{
				e.setCanceled(true);
				message("privreg.msg.monsters");
			}
		}
	}

	@SideOnly(Side.SERVER)
	public void handlePvpEvent(Event e, GameProfile attacker, Entity target)
	{
		EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(attacker.getName());
		if(target.isEntityPlayerMP())
		{
			if(!region.hasRight(attacker, region.hasRight(((EntityPlayerMP)target).getGameProfile(), RIGHT_BASIC) ? RIGHT_ATTACK_OWNERS : RIGHT_ATTACK_OWNERS))
			{
				e.setCanceled(true);
				if(player != null)
					player.addChatMessage(new ChatComponentTranslation("privreg.msg.pvp"));
			}

		}
		else if(target.isEntityAnimal())
		{
			if(!region.hasRight(attacker, RIGHT_ATTACK_ANIMALS))
			{
				e.setCanceled(true);
				if(player != null)
					player.addChatMessage(new ChatComponentTranslation("privreg.msg.animals"));
			}

		}
		else if(target.isEntityMonster())
		{
			if(!region.hasRight(attacker, RIGHT_ATTACK_MONSTERS))
			{
				e.setCanceled(true);
				if(player != null)
					player.addChatMessage(new ChatComponentTranslation("privreg.msg.monsters"));
			}
		}
	}

	@SideOnly(Side.SERVER)
	public void onEntityItemPickup(EntityItemPickupEvent e)
	{
		GameProfile owner = e.item.getObjectOwner();
		if(owner != null && e.entityPlayer.getGameProfile().getId().equals(owner.getId()))
			return;
		if(!region.hasRight(e.entityPlayer.getGameProfile(), RIGHT_PICKUP_ITEMS))
			e.setCanceled(true);
	}

	private long lastMsgTime = 0;

	@SideOnly(Side.CLIENT)
	public void onBreakSpeedEvent(PlayerEvent.BreakSpeed e)
	{
		if(!region.hasRight(RIGHT_BREAK_BLOCKS))
		{
			e.setCanceled(true);
			long time = System.currentTimeMillis();

			if (time - lastMsgTime > 1000)
			{
				cmessage("privreg.msg.break");
				lastMsgTime = time;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void onPlayerInteractClient(PlayerInteractEvent e)
	{
		boolean canBlock = e.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR;
		ItemStack is = e.entityPlayer.inventory.getCurrentItem();
		boolean isBlock = is != null && Block.getBlockFromItem(is.getItem()) != Blocks.air && canBlock;
		handleInteractCommon(e);
		if(e.useItem == Event.Result.DENY && e.useBlock == Event.Result.DENY && canBlock)
			cmessage("privreg.msg.interact");
		else if(e.useItem == Event.Result.DENY)
			cmessage(isBlock ? "privreg.msg.place" : "privreg.msg.interact.items");
		else if(e.useBlock == Event.Result.DENY && canBlock)
			cmessage("privreg.msg.interact.blocks");
	}


	@SideOnly(Side.CLIENT)
	private static void cmessage(String msg)
	{
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation(msg).setChatStyle(new ChatStyle().setColor(RED)));
	}

	@SideOnly(Side.SERVER)
	private boolean isDirectPlayerAction()
	{
		WorldEventProxy proxy = WorldEventProxy.getCurrent();
		if(proxy == null)
			return false;
		return proxy.getUpdateObject().getType() == WorldUpdateObjectType.PLAYER;
	}

	@SideOnly(Side.SERVER)
	private boolean prohibitDefault()
	{
		return prohibitDefault(RIGHT_BASIC, true);
	}

	@SideOnly(Side.SERVER)
	private boolean prohibitDefault(OwnerRight right, boolean prohibitIfNoEntityOwner)
	{
		WorldEventProxy proxy = WorldEventProxy.getCurrent();
		if(proxy == null)
			return false;
		WorldUpdateObject obj = proxy.getUpdateObject();
		switch(obj.getType())
		{
			case BLOCK_EVENT:
			case BLOCK_RANDOM:
			case BLOCK_PENDING:
				if(!region.getShape().contains(new BlockPos(obj.getX(), obj.getY(), obj.getZ())))
					return true;
				break;
			case TILEE_ENTITY:
				if(!region.getShape().contains(BlockPos.fromTileEntity(obj.getTileEntity())))
					return true;
				break;
			case PLAYER:
				if(!region.hasRight(((EntityPlayerMP) obj.getEntity()).getGameProfile(), right))
				{
					if(obj.isInteracting())
					{
						ItemStack is = obj.getInteractStack();
						if(is != null && is.getItem() == Items.dye && is.getItemDamage() == 15)
							return false;
					}

					return true;
				}
				break;
			case ENTITY:
				GameProfile profile1 = getEntityOwner(obj.getEntity());
				GameProfile profile2 = getEntityTarget(obj.getEntity());
				if(profile1 == null && profile2 == null)
					return prohibitIfNoEntityOwner;
				if((profile1 != null && !region.hasRight(profile1, right)) || (profile2 != null && !region.hasRight(profile2, right)))
					return true;
				break;
			case ENTITY_WEATHER:
				break;
			case WEATHER:
				break;
			case UNKNOWN:
				break;
		}

		return false;
	}

	@SideOnly(Side.SERVER)
	private void message(String msg)
	{
		WorldEventProxy proxy = WorldEventProxy.getCurrent();
		if(proxy == null)
			return;
		WorldUpdateObject obj = proxy.getUpdateObject();
		EntityPlayerMP player = null;
		switch(obj.getType())
		{
			case BLOCK_EVENT:
			case BLOCK_RANDOM:
			case BLOCK_PENDING:
				break;
			case TILEE_ENTITY:
				GameProfile profile = obj.getTileEntity().getObjectOwner();
				player = profile == null ? null : MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(profile.getName());
				break;
			case PLAYER:
				player = (EntityPlayerMP) obj.getEntity();
				break;
			case ENTITY:
				GameProfile profile1 = getEntityOwner(obj.getEntity());
				player = profile1 == null ? null : MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(profile1.getName());
			case ENTITY_WEATHER:
				break;
			case WEATHER:
				break;
			case UNKNOWN:
				break;
		}

		if(player != null)
			player.addChatMessage(new ChatComponentTranslation(msg));
	}

	@SideOnly(Side.SERVER)
	private boolean prohibitRaw(WorldEventProxy proxy)
	{
		if(proxy == null)
			return false;
		WorldUpdateObject obj = proxy.getUpdateObject();
		switch(obj.getType())
		{
			case BLOCK_EVENT:
			{
				Block block = proxy.getWorld().getBlock(obj.getX(), obj.getY(), obj.getZ());
				if(!region.getShape().contains(new BlockPos(obj.getX(), obj.getY(), obj.getZ())) && (block == Blocks.piston || block == Blocks.sticky_piston ||
						block == Blocks.piston_extension || block == Blocks.piston_head))
					return true;
				break;
			}
			case BLOCK_RANDOM:
			case BLOCK_PENDING:
			{
				if(!region.getShape().contains(new BlockPos(obj.getX(), obj.getY(), obj.getZ())))
				{
					Block block = proxy.getWorld().getBlock(obj.getX(), obj.getY(), obj.getZ());
					if(block == Blocks.sapling || block.isWood(proxy.getWorld(), obj.getX(), obj.getY(), obj.getZ()) || block == Blocks.air || block == Blocks.fire)
						return true;
					else if(FluidRegistry.lookupFluidForBlock(block) != null || FluidRegistry.lookupFluidForBlock(Block.getBlockById(Block.getIdFromBlock(block) + 1)) != null)
						return true;
					TileEntity te = proxy.getWorld().getTileEntity(obj.getX(), obj.getY(), obj.getZ());
					if(te instanceof TileEntityDispenser)
					{
						TileEntityDispenser ted = (TileEntityDispenser)te;
						int slot = ted.func_146017_i();
						if(slot >= 0 && isProhibitRawItemUse(ted.getStackInSlot(slot)))
							return true;
					}
				}
				break;
			}
			case TILEE_ENTITY:
				TileEntity te = obj.getTileEntity();
				if(!region.getShape().contains(new BlockPos(te.xCoord, te.yCoord, te.zCoord)))
				{
					String name = te.getClass().getName();
					if
					(
							name.equals("com.rwtema.extrautils.tileentity.enderquarry.TileEntityEnderQuarry") ||
							name.equals("ic2.core.block.machine.tileentity.TileEntityTerra")
					)
						return true;
				}
				break;
			case PLAYER:
				if(obj.isInteracting())
				{
					if(!region.getShape().contains(new BlockPos(obj.getInteractX(), obj.getInteractY(), obj.getInteractZ())) && isProhibitRawItemUse(obj.getInteractStack()))
						return true;
				}
				else
				{
					EntityPlayerMP player = (EntityPlayerMP) obj.getEntity();
					if(isProhibitRawItemUse(player.inventory.getCurrentItem()) && !region.hasRight(player.getGameProfile(), RIGHT_BREAK_BLOCKS))
						return true;
				}
				break;
			case ENTITY:
			{
				Entity entity = obj.getEntity();
				if(entity.getClass() == EntityWither.class || entity.getClass() == EntityDragon.class)
					return true;
				break;
			}
			case ENTITY_WEATHER:
				break;
			case WEATHER:
				break;
			case UNKNOWN:
				break;
		}

		return false;
	}

	private boolean isProhibitRawItemUse(ItemStack is)
	{
		if(is == null)
			return false;
		if(is.getItem() == Items.dye && is.getItemDamage() == 15)
			return true;
		else if(Item.itemRegistry.getNameForObject(is.getItem()).equals("appliedenergistics2:item.ToolMassCannon"))
			return true;

		return false;
	}

	@SideOnly(Side.SERVER)
	private static GameProfile getEntityOwner(Entity entity)
	{
		if(entity.isEntityPlayerMP() && ((EntityPlayerMP)entity).playerNetServerHandler != null)
			return ((EntityPlayerMP)entity).getGameProfile();
		else
			return entity.getObjectOwner();
	}

	@SideOnly(Side.SERVER)
	private static GameProfile getEntityTarget(Entity entity)
	{
		if(entity.isEntityLiving())
		{
			EntityLivingBase to = ((EntityLiving)entity).getAttackTarget();
			if(to != null && to.isEntityPlayerMP())
			{
				return ((EntityPlayerMP)to).getGameProfile();
			}
			else
			{
				to = ((EntityLiving)entity).getAITarget();
				if(to != null && to.isEntityPlayerMP())
				{
					return ((EntityPlayerMP)to).getGameProfile();
				}
			}
		}
		return null;
	}
}
