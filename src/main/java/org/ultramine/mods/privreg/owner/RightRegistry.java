package org.ultramine.mods.privreg.owner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RightRegistry
{
	private static final List<OwnerRight> idToRight = new ArrayList<OwnerRight>();
	private static final Map<String, OwnerRight> keyToRight = new HashMap<String, OwnerRight>();
	private static int idCounter;

	public static OwnerRight register(String group, String name)
	{
		OwnerRight right = new OwnerRight(group, name, idCounter++);
		idToRight.add(right);
		keyToRight.put(right.getKey(), right);
		return right;
	}

	public static OwnerRight getRightByID(int id)
	{
		return idToRight.get(id);
	}

	public static OwnerRight getRightByKey(String key)
	{
		return keyToRight.get(key);
	}

	public static int getRightCount()
	{
		return idCounter;
	}
}
