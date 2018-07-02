package org.ultramine.mods.privreg.modifications;

import org.ultramine.mods.privreg.item.ItemRegionModification;

import java.util.HashMap;
import java.util.Map;

public class RegionModificationsRegistry
{
	private static final RegionModificationsRegistry INSTANCE = new RegionModificationsRegistry();

	private final RegistryItem[] modificationList = new RegistryItem[16];
	private final Map<Class<? extends RegionModification>, RegistryItem> classIdMap = new HashMap<Class<? extends RegionModification>, RegistryItem>();

	public static RegionModificationsRegistry instance()
	{
		return INSTANCE;
	}

	public RegistryItem register(Class<? extends RegionModification> cls, int id, String name)
	{
		if(modificationList[id] != null)
			throw new IllegalArgumentException("RegionModification id " + id);
		RegistryItem registryItem = new RegistryItem(cls, id, name);
		modificationList[id] = registryItem;
		classIdMap.put(cls, registryItem);
		return registryItem;
	}

	public RegistryItem getById(int id)
	{
		return modificationList[id];
	}

	public RegistryItem getByModification(RegionModification module)
	{
		return module.registryItem;
	}

	public RegistryItem getByModificationClass(Class<? extends RegionModification> moduleClass)
	{
		return classIdMap.get(moduleClass);
	}

	public class RegistryItem
	{
		private final Class<? extends RegionModification> cls;
		private final int id;
		private final String name;
		public ItemRegionModification item;

		public RegistryItem(Class<? extends RegionModification> cls, int id, String name)
		{
			this.cls = cls;
			this.id = id;
			this.name = name;
		}

		public Class<? extends RegionModification> getModificationClass()
		{
			return cls;
		}

		public int getId()
		{
			return id;
		}

		public String getName()
		{
			return name;
		}

		public ItemRegionModification getItem()
		{
			return item;
		}
	}
}
