package org.ultramine.mods.privreg.modules;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Iterator;

public class RegionModulesStorage implements Iterable<RegionModule>
{
	private static final Logger log = LogManager.getLogger();
	private static final RegionModulesRegistry REGISTRY = RegionModulesRegistry.instance();
	private static final String NBT_MODULES_ARRAY = "ids";
	private static final String NBT_MODULES_NBT_LIST = "data";

	private final RegionModule[] modules;

	public RegionModulesStorage()
	{
		this.modules = new RegionModule[REGISTRY.getModulesCount()];
	}

	public void addModule(RegionModule module)
	{
		if (modules[module.getRegistryId()] == null)
			modules[module.getRegistryId()] = module;
		else
			throw new IllegalArgumentException("Storage already has module with id " + module.getRegistryId());
	}

	public void removeModule(int registryId)
	{
		modules[registryId] = null;
	}

	public boolean hasModule(Class<? extends RegionModule> modulesClass)
	{
		return hasModuleWithRegistryId(REGISTRY.getByModuleClass(modulesClass).getId());
	}

	public boolean hasSameModule(RegionModule module)
	{
		return hasModuleWithRegistryId(module.getRegistryId());
	}

	public boolean hasModuleWithRegistryId(int registryId)
	{
		return getModuleByRegistryId(registryId) != null;
	}

	public RegionModule getModuleByRegistryId(int registryId)
	{
		return modules[registryId];
	}

	@SuppressWarnings("unchecked")
	public <T extends RegionModule> T getModuleWithClass(Class<T> modulesClass)
	{
		int registryId = REGISTRY.getByModuleClass(modulesClass).getId();
		return (T) getModuleByRegistryId(registryId);
	}

	public int getModulesCount()
	{
		int count = 0;
		for (RegionModule module : modules)
			if (module != null)
				count++;
		return count;
	}

	public NBTTagCompound toNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		byte[] modulesIds = new byte[modules.length];
		int count = 0;
		for (int i = 0; i < modules.length; i++)
			if (modules[i] != null)
			{
				NBTTagCompound nbt1 = new NBTTagCompound();
				modules[i].writeToNBT(nbt1);
				list.appendTag(nbt1);
				modulesIds[count] = (byte)i;
				count++;
			}
		nbt.setByteArray(NBT_MODULES_ARRAY, Arrays.copyOf(modulesIds, count));
		nbt.setTag(NBT_MODULES_NBT_LIST, list);
		return nbt;
	}

	public static RegionModulesStorage parseNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey(NBT_MODULES_ARRAY))
		{
			byte[] ids = nbt.getByteArray(NBT_MODULES_ARRAY);
			NBTTagList list = nbt.getTagList(NBT_MODULES_NBT_LIST, 10);
			return parseNBT(list, ids);
		} else
		{
			log.warn("NBT doesn't contains modulesIds arr");
			return new RegionModulesStorage();
		}
	}

	public static RegionModulesStorage parseNBT(NBTTagList nbt, byte[] modulesIds)
	{
		RegionModulesStorage storage = new RegionModulesStorage();
		for (int i = 0; i < modulesIds.length; i++)
		{
			int id = modulesIds[i];
			storage.modules[id] = RegionModule.createFromNBT(nbt.getCompoundTagAt(i), id);
		}
		return storage;
	}

	@Override
	public Iterator<RegionModule> iterator()
	{
		return new ModulesIterator();
	}

	private class ModulesIterator implements Iterator<RegionModule>
	{

		private int next = 0;

		private ModulesIterator()
		{
			findNext();
		}

		private void findNext()
		{
			while ((next < modules.length) && (modules[next] == null))
				next++;
		}

		@Override
		public boolean hasNext()
		{
			return next < modules.length;
		}

		@Override
		public RegionModule next()
		{
			RegionModule module = modules[next];
			next++;
			findNext();
			return module;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Operation is not supported");
		}
	}
}
