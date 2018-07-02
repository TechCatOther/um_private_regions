package org.ultramine.mods.privreg.packets;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.Action;
import org.ultramine.mods.privreg.PrivateRegions;
import org.ultramine.mods.privreg.modules.RegionModule;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.mods.privreg.regions.RegionRights;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketRegionModule extends UMPacket
{
	private static final Logger log = LogManager.getLogger();
	private int regionId;
	private int moduleRegistryId;
	private NBTTagCompound nbt;
	private Action action;

	public PacketRegionModule(){}
	public PacketRegionModule(Action action, int regionId, int moduleRegistryId, NBTTagCompound nbt)
	{
		this.regionId = regionId;
		this.moduleRegistryId = moduleRegistryId;
		this.nbt = nbt;
		this.action = action;
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeInt(regionId);
		buf.writeInt(moduleRegistryId);
		buf.writeByte(action.ordinal());
		buf.writeNBTTagCompoundToBuffer(nbt);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		regionId = buf.readInt();
		moduleRegistryId = buf.readInt();
		action = Action.getAction(buf.readByte());
		nbt = buf.readNBTTagCompoundFromBuffer();
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		Region region = RegionManagerClient.getInstance().getRegion(regionId);

		if(region != null)
			switch(action)
			{
				case ADD:
					if (region.hasModuleWithRegistryId(moduleRegistryId))
						region.removeModule(moduleRegistryId); // Считаем то, что пришло от серва >> важнее
					region.addModule(RegionModule.createFromNBT(nbt, moduleRegistryId));
					break;
				case REMOVE:
					region.removeModule(moduleRegistryId);
					break;
				case UPDATE:
					RegionModule module = region.getModuleByRegistryId(moduleRegistryId);
					if (module == null)
					{
						log.warn("received PacketRegionModule for region without correct module {} - {}", regionId, moduleRegistryId);
						return;
					}
					module.receiveSettings(nbt);
					break;
			}
	}

	public void processServer(NetHandlerPlayServer net)
	{
		Region region = PrivateRegions.instance().getServerRegion(net.playerEntity.dimension, regionId);
		if (region == null)
		{
			log.warn("received PacketRegionModule for undefined region id {}", regionId);
			return;
		}

		GameProfile profile = net.playerEntity.getGameProfile();
		if (!region.hasRight(profile, RegionRights.EDIT_MODULES))
		{
			log.warn("received region PacketRegionModule from not region owner with needed rights: {}", profile.getName());
			return;
		}

		switch (action)
		{
			case UPDATE:
				RegionModule module = region.getModuleByRegistryId(moduleRegistryId);
				if (module == null)
				{
					log.warn("received PacketRegionModule for region without correct module {} - {}", regionId, moduleRegistryId);
					return;
				}
				module.receiveSettingsServer(nbt, net.playerEntity);
				break;
			default:
				log.warn("{} action is not supported by server {} - {}", action.name(), regionId, moduleRegistryId);
				break;
		}
	}
}
