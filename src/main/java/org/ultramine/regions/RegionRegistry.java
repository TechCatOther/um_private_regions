package org.ultramine.regions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RegionRegistry
{
	private static Map<Class<? extends IRegion>, IRegionProvider> map = new HashMap<Class<? extends IRegion>, IRegionProvider>();

	public static void registerProvider(Class<? extends IRegion> rclass, IRegionProvider prov)
	{
		if(map.containsKey(rclass))
			throw new IllegalStateException("Region provider for class "+rclass.getName()+" is already registered");
		map.put(rclass, prov);
	}

	public static IRegionProvider getProviderFor(Class<? extends IRegion> rclass)
	{
		return map.get(rclass);
	}

	public static Collection<IRegionProvider> getAllProviders()
	{
		return map.values();
	}
}
