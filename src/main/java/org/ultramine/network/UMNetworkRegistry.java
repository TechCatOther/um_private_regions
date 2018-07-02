package org.ultramine.network;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;

@SuppressWarnings("unchecked")
public class UMNetworkRegistry
{
	private static final Method serverRegMethod; //func_150756_b
	private static final Method clientRegMethod; //func_150751_a
	private static final Map<Class<? extends Packet>, EnumConnectionState> packetToStateMap; //field_150761_f
	
	private static final List<Class<? extends UMPacket>> registered = new ArrayList<Class<? extends UMPacket>>();
	private static final Map<String, Class<? extends UMPacket>> nameToPacket = new HashMap<String, Class<? extends UMPacket>>();
	private static final Class<? extends UMPacket>[] idToPacket = new Class[256];
	private static final TObjectIntMap<Class<? extends UMPacket>> packetToId = new TObjectIntHashMap<Class<? extends UMPacket>>();
	
	private static boolean built;
	private static SPacketNetworkList prewritedPacket;
	
	static
	{
		try
		{
			serverRegMethod = EnumConnectionState.class.getDeclaredMethod("func_150756_b", int.class, Class.class);
			clientRegMethod = EnumConnectionState.class.getDeclaredMethod("func_150751_a", int.class, Class.class);
			serverRegMethod.setAccessible(true);
			clientRegMethod.setAccessible(true);
			
			Field map = EnumConnectionState.class.getDeclaredField("field_150761_f");
			map.setAccessible(true);
			packetToStateMap = (Map<Class<? extends Packet>, EnumConnectionState>)map.get(null);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to init packet register methods", e);
		}
		
		FMLCommonHandler.instance().bus().register(new UMNetworkRegistry());
		registerVanillaPacket(Side.SERVER, EnumConnectionState.PLAY, 120, SPacketNetworkList.class);
		registerVanillaPacket(Side.SERVER, EnumConnectionState.PLAY, 121, ServerUMPacketProxy.class);
		registerVanillaPacket(Side.CLIENT, EnumConnectionState.PLAY, 121, ClientUMPacketProxy.class);
	}
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onPlayerLoggedIn(FMLNetworkEvent.ServerConnectionFromClientEvent e)
	{
		if(!built)
			build(); //TODO move out
		if(FMLCommonHandler.instance().getSide().isServer())
			e.manager.scheduleOutboundPacket(prewritedPacket);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent e)
	{
		build(); //backing to original client order
	}
	
	public static void build()
	{
		built = true;
		
		for(int i = 0, s = registered.size(); i < s; i++)
		{
			Class<? extends UMPacket> cls = registered.get(i);
			idToPacket[i] = cls;
			packetToId.put(cls, i);
		}
		
		if(FMLCommonHandler.instance().getSide().isServer())
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			DataOutputStream data = new DataOutputStream(out);
			try
			{
				data.write(registered.size());
				for(int i = 0, s = registered.size(); i < s; i++)
				{
					Class<? extends UMPacket> cls = registered.get(i);
					data.writeUTF(cls.getName());
				}
			}
			catch(IOException ignored){}
			prewritedPacket = new SPacketNetworkList(out.toByteArray());
		}
	}
	
	public static int getPacketID(UMPacket packet)
	{
		return packetToId.get(packet.getClass());
	}
	
	public static UMPacket makePacketServer(int id)
	{
		try
		{
			return idToPacket[id].newInstance();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to make UMPaket for ID: "+id, e);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static synchronized UMPacket makePacketClient(int id)
	{
		return makePacketServer(id);
	}
	
	@SideOnly(Side.CLIENT)
	static synchronized void acceptServerList(String[] classes)
	{
		if(MinecraftServer.getServer() != null) //singleplayer
			return;
		for(int i = 0, s = classes.length; i < s; i++)
		{
			Class<? extends UMPacket> cls = nameToPacket.get(classes[i]);
			idToPacket[i] = cls;
			packetToId.put(cls, i);
		}
	}
	
	public static void registerVanillaPacket(Side side, EnumConnectionState state, int id, Class<? extends Packet> packet)
	{
		try
		{
			if(side == Side.SERVER)
			{
				serverRegMethod.invoke(state, id, packet);
			}
			else
			{
				clientRegMethod.invoke(state, id, packet);
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		packetToStateMap.put(packet, state);
	}
	
	public static void registerPacket(Class<? extends UMPacket> cls)
	{
		if(built) throw new IllegalStateException("UMNetworkRegistry has been locked");
		
		registered.add(cls);
		nameToPacket.put(cls.getName(), cls);
	}
}
