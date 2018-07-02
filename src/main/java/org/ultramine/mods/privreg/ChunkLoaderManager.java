package org.ultramine.mods.privreg;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.ultramine.server.chunk.ChunkHash;
import org.ultramine.server.world.MultiWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@SideOnly(Side.SERVER)
public class ChunkLoaderManager
{
	private TLongObjectMap<List<IChunkLoader>> forcedChunksByChunk = new TLongObjectHashMap<List<IChunkLoader>>();
	private HashMap<IChunkLoader, TIntSet> forcedChunksByLoader = new HashMap<IChunkLoader, TIntSet>();
	private TLongIntMap timedUnloadQueue = new TLongIntHashMap();
	private final TicketManager ticketMgr;

	public ChunkLoaderManager(MinecraftServer server, Object mod)
	{
		ticketMgr = new TicketManager(server, mod);
		ForgeChunkManager.setForcedChunkLoadingCallback(mod, new DummyLoadingCallback());
	}

	public void addChunkLoader(IChunkLoader loader)
	{
		forcedChunksByLoader.put(loader, new TIntHashSet());
		forceChunks(loader, loader.getChunks());
	}

	private void forceChunks(IChunkLoader loader, TIntCollection chunks)
	{
		int dim = loader.getDimenson();
		for(TIntIterator it = chunks.iterator(); it.hasNext();)
		{
			int key = it.next();
			long dimCoord = ChunkHash.worldChunkToKey(dim, ChunkHash.keyToX(key), ChunkHash.keyToZ(key));
			List<IChunkLoader> loaders = forcedChunksByChunk.get(dimCoord);
			if(loaders == null)
				forcedChunksByChunk.put(dimCoord, loaders = new LinkedList<IChunkLoader>());
			if(loaders.isEmpty())
			{
				timedUnloadQueue.remove(dimCoord);
				ticketMgr.addChunk(dim, ChunkHash.keyToX(key), ChunkHash.keyToZ(key));
			}

			if(!loaders.contains(loader))
				loaders.add(loader);
		}

		forcedChunksByLoader.get(loader).addAll(chunks);
	}

	public void removeChunkLoader(IChunkLoader loader)
	{
		TIntSet chunks = forcedChunksByLoader.remove(loader);
		if(chunks == null)
			return;
		unforceChunks(loader, chunks, true);
	}

	private void unforceChunks(IChunkLoader loader, TIntSet chunks, boolean remLoader)
	{
		int dim = loader.getDimenson();
		for(TIntIterator it = chunks.iterator(); it.hasNext();)
		{
			int key = it.next();
			long dimCoord = ChunkHash.worldChunkToKey(dim, ChunkHash.keyToX(key), ChunkHash.keyToZ(key));
			List<IChunkLoader> loaders = forcedChunksByChunk.get(dimCoord);
			if(loaders == null || !loaders.remove(loader))
				continue;

			if(loaders.isEmpty())
			{
				forcedChunksByChunk.remove(dimCoord);
				timedUnloadQueue.put(dimCoord, 100);
			}
		}

		if(!remLoader)
			forcedChunksByLoader.get(loader).removeAll(chunks);
	}

	public void updateChunkLoader(IChunkLoader loader)
	{
		TIntSet loaderChunks = forcedChunksByLoader.get(loader);
		if(loaderChunks == null)
		{
			addChunkLoader(loader);
			return;
		}
		TIntSet oldChunks = new TIntHashSet(loaderChunks);
		TIntSet newChunks = new TIntHashSet();
		for(TIntIterator it = loader.getChunks().iterator(); it.hasNext();)
		{
			int key = it.next();
			if(!oldChunks.remove(key))
				newChunks.add(key);
		}

		int dim = loader.getDimenson();
		if(!oldChunks.isEmpty())
			unforceChunks(loader, oldChunks, false);
		if(!newChunks.isEmpty())
			forceChunks(loader, newChunks);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent e)
	{
		if(e.phase != TickEvent.Phase.END)
			return;
		for(TLongIntIterator it = timedUnloadQueue.iterator(); it.hasNext();)
		{
			it.advance();
			long dimkey = it.key();
			int ticks = it.value();
			if(ticks == 0)
			{
				int dim = (int)(dimkey >> 32);
				int key = (int)(dimkey & 0xFFFFFFFF);
				ticketMgr.removeChunk(dim, ChunkHash.keyToX(key), ChunkHash.keyToZ(key));
				it.remove();
			}
			else
			{
				it.setValue(ticks - 1);
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload e)
	{
		ticketMgr.unloadDimension(e.world.provider.dimensionId);
	}

	public void register()
	{
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void unregister()
	{
		FMLCommonHandler.instance().bus().unregister(this);
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	public interface IChunkLoader
	{
		int getDimenson();

		TIntList getChunks();
	}

	@SideOnly(Side.SERVER)
	private class DummyLoadingCallback implements ForgeChunkManager.OrderedLoadingCallback
	{
		@Override
		public List<Ticket> ticketsLoaded(List<Ticket> list, World world, int i)
		{
			return new ArrayList<Ticket>();
		}

		@Override
		public void ticketsLoaded(List<Ticket> list, World world)
		{

		}
	}

	@SideOnly(Side.SERVER)
	private class TicketManager
	{
		private final TIntObjectMap<List<Ticket>> ticketsWithSpace = new TIntObjectHashMap<List<Ticket>>();
		private final TLongObjectMap<Ticket> heldChunks = new TLongObjectHashMap<Ticket>();
		private final MultiWorld mw;
		private final Object mod;

		public TicketManager(MinecraftServer server, Object mod)
		{
			mw = server.getMultiWorld();
			this.mod = mod;
		}

		public void addChunk(int dim, int cx, int cz)
		{
			long key = ChunkHash.worldChunkToKey(dim, cx, cz);
			if(heldChunks.containsKey(key))
				return;

			List<Ticket> freeTickets = ticketsWithSpace.get(dim);
			if(freeTickets == null)
				ticketsWithSpace.put(dim, freeTickets = new ArrayList<Ticket>());

			Ticket ticket;
			if(freeTickets.isEmpty())
				freeTickets.add(ticket = createTicket(dim));
			else
				ticket = freeTickets.get(freeTickets.size()-1);

			if(ticket == null)
				return; // Forge limit exceeded

			ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(cx, cz));
			heldChunks.put(key, ticket);
			if(ticket.getChunkList().size() == ticket.getChunkListDepth() && !freeTickets.isEmpty())
				freeTickets.remove(freeTickets.size() - 1);
		}

		private Ticket createTicket(int dim)
		{
			return ForgeChunkManager.requestTicket(mod, mw.getDescByID(dim).getOrLoadWorld(), ForgeChunkManager.Type.NORMAL);
		}

		public void removeChunk(int dim, int cx, int cz)
		{
			long key = ChunkHash.worldChunkToKey(dim, cx, cz);
			Ticket ticket = heldChunks.remove(key);
			if(ticket == null)
				return;

			ForgeChunkManager.unforceChunk(ticket, new ChunkCoordIntPair(cx, cz));

			if(ticket.getChunkList().size() == ticket.getChunkListDepth() - 1)
			{
				List<Ticket> freeTickets = ticketsWithSpace.get(dim);
				if(freeTickets == null)
					ticketsWithSpace.put(dim, freeTickets = new ArrayList<Ticket>());
				freeTickets.add(ticket);
			}
		}

		public void unloadDimension(int dim)
		{
			ticketsWithSpace.remove(dim);
			for(TLongObjectIterator it = heldChunks.iterator(); it.hasNext();)
			{
				it.advance();
				long key = it.key();
				if((int)(key >> 32) == dim)
					it.remove();
			}
		}
	}
}
