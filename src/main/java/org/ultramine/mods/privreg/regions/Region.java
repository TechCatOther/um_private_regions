package org.ultramine.mods.privreg.regions;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import org.ultramine.mods.privreg.Action;
import org.ultramine.mods.privreg.RegionConfig;
import org.ultramine.mods.privreg.event.RegionCreateEvent;
import org.ultramine.mods.privreg.event.RegionDestroyEvent;
import org.ultramine.mods.privreg.event.RegionLoadEvent;
import org.ultramine.mods.privreg.event.RegionResizeEvent;
import org.ultramine.mods.privreg.event.RegionUnloadEvent;
import org.ultramine.mods.privreg.modifications.RegionModification;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.modules.RegionModuleDiscount;
import org.ultramine.mods.privreg.modules.RegionModuleFree;
import org.ultramine.mods.privreg.modules.RegionModulesStorage;
import org.ultramine.mods.privreg.owner.OwnerRight;
import org.ultramine.mods.privreg.owner.RegionOwnerStorage;
import org.ultramine.mods.privreg.packets.PacketRegionAction;
import org.ultramine.mods.privreg.packets.PacketRegionModule;
import org.ultramine.network.UMPacket;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.IRegion;
import org.ultramine.regions.IRegionManager;
import org.ultramine.regions.Rectangle;

import java.util.ArrayList;
import java.util.List;

public final class Region implements IRegion
{
	private final IRegionManager regMgr;
	private final int id;
	private final boolean server;
	public RegionOwnerStorage ownerStorage = new RegionOwnerStorage(this);
	private RegionModulesStorage modulesStorage = new RegionModulesStorage();
	private final List<RegionModification> modifications = new ArrayList<RegionModification>(2);

	private int dimension;
	private BlockPos blockCoord;
	private Rectangle shape;

//	private boolean enabled = true;
	private long lastPayedTime;

	private double charge;
	private int maxCharge;
	private int tacts;
	private int maxTacts;

	private IRegion parent;
	private List<Region> children = new ArrayList<Region>();
	private int generation;

	// Temporal values

	private double countedCosts;
	private int countedBlocks;
	private double countedDiscount;
	private boolean changed;

	public int parentWaiting = -1;

	public Region(IRegionManager regMgr, int id, boolean server)
	{
		this.regMgr = regMgr;
		this.id = id;
		this.server = server;


	}

	@Override
	public int getID()
	{
		return id;
	}

	public boolean isServer()
	{
		return server;
	}

	@Override
	public Rectangle getShape()
	{
		return shape;
	}

	public void setShape(Rectangle shape)
	{
		this.shape = shape;
	}

	public int getWorld()
	{
		return dimension;
	}

	public void setWorld(int dimension)
	{
		this.dimension = dimension;
	}

	public BlockPos getBlock()
	{
		return blockCoord;
	}

	public void setBlock(BlockPos blockCoord)
	{
		this.blockCoord = blockCoord;
	}

	@Override
	public IRegion getParent()
	{
		return parent;
	}

	public void setParent(Region parent)
	{
		this.parent = parent;
		generation = parent.getGeneration() + 1;
		for(Region reg : children)
			reg.setParent(this); //generation recounting
		parent.addChild(this);
	}

	public boolean hasParent()
	{
		return parent != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IRegion> getChildren()
	{
		return (List)children;
	}

	@Override
	public boolean addChild(IRegion region)
	{
		return !children.contains(region) && children.add((Region) region);
	}

	@Override
	public boolean removeChild(IRegion region)
	{
		return children.remove(region);
	}

	public boolean hasChildren()
	{
		return !children.isEmpty();
	}

	public int getGeneration()
	{
		return generation;
	}

	public int getMaxCharge()
	{
		return maxCharge;
	}

	public void setMaxCharge(int maxCharge)
	{
		this.maxCharge = maxCharge;
	}

	public double getCharge()
	{
		return charge;
	}

	public void setCharge(double charge)
	{
		this.charge = charge;
	}

	public void addCharge(double add)
	{
		this.charge += add;
		if(server && this.charge == add)
			activateModules();
	}

	public int getTacts()
	{
		return tacts;
	}

	public void setTacts(int tacts)
	{
		this.tacts = tacts;
	}

	public int getMaxTacts()
	{
		return maxTacts;
	}

	public void setMaxTacts(int maxTacts)
	{
		this.maxTacts = maxTacts;
	}

	public long getLastPayedTime()
	{
		return lastPayedTime;
	}

	public void setLastPayedTime(long lastPayedTime)
	{
		this.lastPayedTime = lastPayedTime;
	}

	public double getDiscount()
	{
		return countedDiscount;
	}

	public boolean isChanged()
	{
		return changed;
	}

	public void setChanged(boolean hasChanged)
	{
		this.changed = hasChanged;
	}

	public boolean isActive()
	{
		return charge > 0;
	}

	public RegionOwnerStorage getOwnerStorage()
	{
		return ownerStorage;
	}

	public boolean hasRight(GameProfile player, OwnerRight right)
	{
		return ownerStorage.hasRight(player, right);
	}

	@SideOnly(Side.CLIENT)
	public boolean hasRight(OwnerRight right)
	{
		return ownerStorage.hasRightClient(right);
	}

	public void setModulesStorage(RegionModulesStorage modulesStorage)
	{
		if(this.modulesStorage != null)
			for(RegionModule module : this.modulesStorage)
				unregisterModule(module);
		this.modulesStorage = modulesStorage;
		for(RegionModule module : modulesStorage)
			module.onPlaceToRegion(this);
		onModulesChange();
	}

	public void addModule(RegionModule module)
	{
		try
		{
			modulesStorage.addModule(module);
		} catch (Exception e)
		{
			RegionManager.log.warn("Error while adding new module", e);
		}
		module.onPlaceToRegion(this);
		if(server)
		{
			if(isActive())
				module.onRegionActivate();
			PacketRegionModule packet = module.createPacket(Action.ADD);
			if(packet != null)
				sendToListeners(packet);
		}
		onModulesChange();
	}

	public void removeModule(int registryId)
	{
		RegionModule module = modulesStorage.getModuleByRegistryId(registryId);
		if(module == null)
		{
			RegionManager.log.warn("module doesn't exists " + getID() + " " + registryId);
			return;
		}
		if(server)
		{
			PacketRegionModule packet = module.createPacket(Action.REMOVE);
			if(packet != null)
				sendToListeners(packet);
		}
		unregisterModule(module);
		modulesStorage.removeModule(registryId);
		onModulesChange();
	}

	private void unregisterModule(RegionModule module)
	{
		if(server)
			module.onRegionInactivate();
		module.onRemoveFromRegion();
	}

	private void activateModules()
	{
		for(RegionModule module : modulesStorage)
			module.onRegionActivate();
	}

	private void inactivateModules()
	{
		for(RegionModule module : modulesStorage)
			module.onRegionInactivate();
	}

	public boolean hasSameModule(RegionModule module)
	{
		return modulesStorage.hasSameModule(module);
	}

	public boolean hasModuleWithRegistryId(int registryId)
	{
		return modulesStorage.hasModuleWithRegistryId(registryId);
	}

	public boolean hasModule(Class<? extends RegionModule> modulesClass)
	{
		return modulesStorage.hasModule(modulesClass);
	}

	public RegionModule getModuleByRegistryId(int registryId)
	{
		return modulesStorage.getModuleByRegistryId(registryId);
	}

	public <T extends RegionModule> T getModuleWithClass(Class<T> modulesClass)
	{
		return modulesStorage.getModuleWithClass(modulesClass);
	}

	public void clearModuleList()
	{
		for (RegionModule module : modulesStorage)
			unregisterModule(module);
		modulesStorage = new RegionModulesStorage();
		onModulesChange();
	}

	public RegionModulesStorage getModulesStorage()
	{
		return modulesStorage;
	}

	public int getModulesCount()
	{
		return modulesStorage.getModulesCount();
	}

	public void onModulesChange()
	{
		recountCost();
	}

	public List<RegionModification> getModifications()
	{
		return modifications;
	}

	public int countBlocks()
	{
		return countedBlocks;
	}

	private void recountBlocks()
	{
		countedBlocks = shape.getLenX()/* * shape.getLenY()*/ * shape.getLenZ();
		if (!children.isEmpty())
			for (Region child : children)
				countedBlocks -= child.countBlocks();
	}

	private void recountCost()
	{
		countedDiscount = 0.0d;
		if(hasModule(RegionModuleFree.class))
			countedDiscount = 1.0d;
		else if(hasModule(RegionModuleDiscount.class))
			countedDiscount = getModuleWithClass(RegionModuleDiscount.class).getDiscount() / 100d;

		countedCosts = RegionConfig.defaultRegionCost * (1 - countedDiscount);
		if(true/*isEnabled()*/)
			for (RegionModule module : modulesStorage)
				countedCosts += module.countCost();
	}

	public void recountModifications()
	{
		maxCharge = RegionConfig.DefaultBlockMaxCharge;
		maxTacts = RegionConfig.DefaultMaxTacts;
		for(RegionModification mod : modifications)
		{
			maxCharge += mod.getMaxChargeAddition();
			maxTacts += mod.getMaxTacktsAddition();
		}
	}

	public double getCurrentCost()
	{
		return countedCosts;
	}

	RegionChangeResult canExpand(ForgeDirection dir, int amount)
	{
		if(amount < 0)
		{
			Rectangle nshape = getShape().expand(dir, amount);
			Rectangle minShape = blockCoord.toRect().expandAll(1);
			if(!hasParent())
				minShape = minShape.setSide(ForgeDirection.UP, 255).setSide(ForgeDirection.DOWN, 0);
			if(!nshape.contains(minShape))
				return RegionChangeResult.TOO_SMALL;
			for(IRegion child : getChildren())
				if(!nshape.contains(child.getShape()))
					return RegionChangeResult.INTERSECTS;
			return RegionChangeResult.ALLOW;
		}
		if((tacts + amount) > maxTacts)
			return RegionChangeResult.OVERTACKTS;
		Rectangle tocheck = getShape().compress(dir.getOpposite(), getShape().getLen(dir)).expand(dir, amount);
		if(!hasParent())
		{
			int cd = RegionConfig.CheckDistance;
			if(regMgr.hasRegionsInRange(tocheck.expandAll(cd).compress(dir.getOpposite(), cd)))
				return RegionChangeResult.INTERSECTS;

		}
		else
		{
			if(!getParent().getShape().contains(tocheck))
				return RegionChangeResult.OUTOFPARENT;
			for(IRegion reg : getParent().getChildren())
				if(reg.getShape().isIntersects(tocheck))
					return RegionChangeResult.INTERSECTS;
		}

		if(dir == ForgeDirection.DOWN && getShape().getMin().y - amount < 0)
			return RegionChangeResult.OUTOFWORLD;
		if(dir == ForgeDirection.UP && getShape().getMax().y + amount > 255)
			return RegionChangeResult.OUTOFWORLD;

		return RegionChangeResult.ALLOW;
	}

	void doExpand(ForgeDirection dir, int amount)
	{
		Rectangle last = getShape();
		setShape(getShape().expand(dir, amount));
		tacts += amount;
		onAreaChanged(last, dir, amount);
	}

	private void onAreaChanged(Rectangle last, ForgeDirection dir, int amount)
	{
		recountBlocks();
		if(server)
			for(RegionModule module : getModulesStorage())
				module.onAreaChanged(last, dir, amount);
		recountCost();
		if(parent != null)
			parent.onChildAreaChanged(this);
		if(server)
			MinecraftForge.EVENT_BUS.post(new RegionResizeEvent(this, last, dir, amount));
	}

	@Override
	public void onChildAreaChanged(IRegion region)
	{
		recountBlocks();
		recountCost();
		if(parent != null)
			parent.onChildAreaChanged(this);
	}

	public void onCreate()
	{
		lastPayedTime = System.currentTimeMillis();
//		enabled = true;
		maxCharge = RegionConfig.DefaultBlockMaxCharge;
		maxTacts = RegionConfig.DefaultMaxTacts;
		recountBlocks();
		if(server)
			MinecraftForge.EVENT_BUS.post(new RegionCreateEvent(this));
	}

	/** on server, while load, and client, while receive */
	public void onLoad()
	{
		setChanged(false);
		recountBlocks();
		if(server && isActive())
			activateModules();
		recountCost();
		if(server)
			MinecraftForge.EVENT_BUS.post(new RegionLoadEvent(this));
	}

	@SideOnly(Side.SERVER)
	public void onUpdate()//on server
	{
		long time = System.currentTimeMillis();
		if(true/*enabled || charge == 0*/)
		{
			if (lastPayedTime > 0 && !isActive())
				lastPayedTime = -time;
			else if (lastPayedTime <= 0 && isActive())
				lastPayedTime = time;

			if (lastPayedTime < 0 && time + lastPayedTime > RegionConfig.inactiveRegionTimeout)
			{
				World world = MinecraftServer.getServer().worldServerForDimension(getWorld());
				if(!world.isSideSolid(blockCoord.x, blockCoord.y - 1, blockCoord.z, ForgeDirection.UP))
					world.setBlock(blockCoord.x, blockCoord.y - 1, blockCoord.z, Blocks.stone);
				world.setBlock(blockCoord.x, blockCoord.y, blockCoord.z, Blocks.standing_sign);
				TileEntitySign te = (TileEntitySign)world.getTileEntity(blockCoord.x, blockCoord.y, blockCoord.z);
				if(te != null)
				{
					te.signText[0] = "Приват снят";
					te.signText[1] = "по прошествии";
					te.signText[2] = "недели";
					te.signText[3] = "за неуплату";
				}
				return;
			}

			if (!isActive())
				return;
		}

		long elapsed = time - lastPayedTime;
		if(charge > 0 && elapsed > 60000)
		{
			boolean lastActive = isActive();
			double costs = getCurrentCost() * (elapsed / 60000L) / 1440d;
			if(costs > charge)
			{
				charge = 0.0d;
				lastPayedTime = time;
			}
			else
			{
				charge -= costs;
				lastPayedTime = time - (elapsed % 60000L);
			}

			if(lastActive != isActive())
			{
				if(!isActive())
				{
					inactivateModules();
					sendToListeners(new PacketRegionAction(getID(), PacketRegionAction.CLIENT_SET_DISCHARGED));
				}
			}
		}

		if(isActive())
		{
			for(RegionModule module : modulesStorage)
				module.onRegionUpdate();
		}
	}

	public void onDestroy()
	{
		if(server)
		{
			MinecraftForge.EVENT_BUS.post(new RegionDestroyEvent(this));
			inactivateModules();
		}
		if (parent != null)
			parent.removeChild(this);
		setShape(new Rectangle(BlockPos.EMPTY, BlockPos.EMPTY));
		if(hasChildren()) //TODO temp solution
			for(Region child : children)
			{
				child.parent = getParent();
				child.generation = 0;
			}
	}

	public void onUnload()
	{
		if(server)
			MinecraftForge.EVENT_BUS.post(new RegionUnloadEvent(this));
		inactivateModules();
	}

	public void sendToListeners(UMPacket packet)
	{
		if(server)
			((RegionManager)regMgr).sendToListeners(this, packet);
	}
}
