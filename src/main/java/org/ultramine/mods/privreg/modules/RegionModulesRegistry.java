package org.ultramine.mods.privreg.modules;

import java.util.HashMap;
import java.util.Map;

public class RegionModulesRegistry
{
	private static final RegionModulesRegistry INSTANCE = new RegionModulesRegistry();

	private final RegistryItem[] moduleList = new RegistryItem[16];
	private final Map<Class<? extends RegionModule>, RegistryItem> classIdMap = new HashMap<Class<? extends RegionModule>, RegistryItem>();
	private int maxId;

	private RegionModulesRegistry()
	{

	}

	public static RegionModulesRegistry instance()
	{
		return INSTANCE;
	}

	public void register(Class<? extends RegionModule> moduleClass, int id, float energyCost, String name)
	{
		if(id >= moduleList.length && moduleList[id] != null)
			throw new IllegalArgumentException("RegionModule id " + id);
		try {
			Class.forName(moduleClass.getName(), true, moduleClass.getClassLoader()); //forces class static init
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		RegistryItem registryItem = new RegistryItem(moduleClass, id, energyCost, name);
		moduleList[id] = registryItem;
		classIdMap.put(moduleClass, registryItem);
		if(id > maxId)
			maxId = id;
	}

	public RegistryItem getById(int id)
	{
		return moduleList[id];
	}

	public RegistryItem getByModule(RegionModule module)
	{
		return module.registryItem;
	}

	public RegistryItem getByModuleClass(Class<? extends RegionModule> moduleClass)
	{
		return classIdMap.get(moduleClass);
	}

	public int getModulesCount()
	{
		return maxId+1;
	}

	public class RegistryItem
	{
		private final int id;
		private final Class<? extends RegionModule> moduleClass;
		private final double energyCost;
		private final double energyCostPerBlock;
		private final String name;

		private RegistryItem(Class<? extends RegionModule> moduleClass, int id, float energyCost, String name)
		{
			this.moduleClass = moduleClass;
			this.id = id;
			this.energyCost = energyCost;
			this.energyCostPerBlock = energyCost / 1000d;
			this.name = name;
		}

		public Class<? extends RegionModule> getModuleClass()
		{
			return moduleClass;
		}

		public int getId()
		{
			return id;
		}

		public double getEnergyCost()
		{
			return energyCost;
		}

		public double getEnergyCostPerBlock()
		{
			return energyCostPerBlock;
		}

		public String getName()
		{
			return name;
		}
	}
}
