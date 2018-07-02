package org.ultramine.mods.privreg.owner;

public class OwnerRight implements Comparable<OwnerRight>
{
	private final String group;
	private final String name;
	private final int id;
	private final String key;

	OwnerRight(String group, String name, int id)
	{
		this.group = group;
		this.name = name;
		this.id = id;
		this.key = group + "." + name;
	}

	public String getGroup()
	{
		return group;
	}

	public String getName()
	{
		return name;
	}

	public int getID()
	{
		return id;
	}

	public String getKey()
	{
		return key;
	}

	@Override
	public int compareTo(OwnerRight o)
	{
		int c1 = group.compareTo(o.group);
		return c1 != 0 ? c1 : name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof OwnerRight && ((OwnerRight)o).getID() == getID();
	}

	@Override
	public int hashCode()
	{
		return id;
	}
}
