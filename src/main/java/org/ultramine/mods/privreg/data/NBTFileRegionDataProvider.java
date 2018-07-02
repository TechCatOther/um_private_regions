package org.ultramine.mods.privreg.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.mods.privreg.modifications.RegionModification;
import org.ultramine.mods.privreg.modules.RegionModulesStorage;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManager;
import org.ultramine.regions.BlockPos;
import org.ultramine.regions.Rectangle;
import org.ultramine.server.util.AsyncIOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@SideOnly(Side.SERVER)
public class NBTFileRegionDataProvider implements IRegionDataProvider
{
	private static final Logger log = LogManager.getLogger();
	private final File storageDir;
	private RegionManager regMrg;

	public NBTFileRegionDataProvider(File storageDir)
	{
		if(!storageDir.isDirectory() && !storageDir.mkdirs())
			throw new RuntimeException("Failed to create storage directory for NBT region data provider");

		this.storageDir = storageDir;
	}

	@Override
	public void init(RegionManager regMrg)
	{
		this.regMrg = regMrg;
	}

	@Override
	public void createRegion(Region region)
	{
		saveRegion(region);
	}

	@Override
	public void saveAll(Iterable<Region> regions)
	{
		for(Region region : regions)
		{
			if(region != null)
				saveRegion(region);
		}
	}

	@Override
	public void saveRegion(Region region)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("id", region.getID());
		nbt.setTag("b", region.getBlock().toNBT());
		nbt.setTag("s", region.getShape().toNBT());
		if(region.hasParent())
			nbt.setInteger("p", region.getParent().getID());
		nbt.setInteger("t", region.getTacts());
		nbt.setInteger("mt", region.getMaxTacts());
		nbt.setDouble("c", region.getCharge());
		nbt.setInteger("mc", region.getMaxCharge());
		nbt.setLong("l", region.getLastPayedTime());
		nbt.setTag("owners", region.getOwnerStorage().toNBT());
		nbt.setTag("modules", region.getModulesStorage().toNBT());
		NBTTagList modifs = new NBTTagList();
		for(RegionModification mod : region.getModifications())
			modifs.appendTag(mod.toNBT());
		nbt.setTag("modifs", modifs);

		AsyncIOUtils.safeWriteNBT(new File(storageDir, region.getID() + ".nbt"), nbt);
	}

	@Override
	public void loadAll(List<Region> regions)
	{
		for(File file : storageDir.listFiles())
		{
			if(file.getName().endsWith(".nbt"))
			{
				Region region = loadRegion(file);
				if(region != null)
					regions.add(region);
			}
		}
	}

	private Region loadRegion(File file)
	{
		try
		{
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
			int id = nbt.getInteger("id");

			Region region = new Region(regMrg, id, true);
			region.setBlock(BlockPos.fromNBT(nbt.getCompoundTag("b")));
			region.setShape(Rectangle.fromNBT(nbt.getCompoundTag("s")));
			if(nbt.hasKey("p"))
				region.parentWaiting = nbt.getInteger("p");
			region.setTacts(nbt.getInteger("t"));
			region.setMaxTacts(nbt.getInteger("mt"));
			region.setCharge(nbt.getDouble("c"));
			region.setMaxCharge(nbt.getInteger("mc"));
			region.setLastPayedTime(nbt.getLong("l"));
			region.getOwnerStorage().fromNBT(nbt.getCompoundTag("owners"));
			region.setModulesStorage(RegionModulesStorage.parseNBT(nbt.getCompoundTag("modules")));
			NBTTagList modifs = nbt.getTagList("modifs", 10);
			for(int i = 0; i < modifs.tagCount(); i++)
				region.getModifications().add(RegionModification.fromNBT(modifs.getCompoundTagAt(i)));

			return region;
		}
		catch(IOException e)
		{
			log.error("Failed to load region NBT file: " + file.getAbsolutePath(), e);
		}

		return null;
	}

	@Override
	public boolean destroyRegion(Region region)
	{
		return new File(storageDir, region.getID() + ".nbt").delete();
	}

	@Override
	public void close()
	{

	}
}
