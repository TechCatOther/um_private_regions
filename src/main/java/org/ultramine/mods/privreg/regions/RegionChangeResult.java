package org.ultramine.mods.privreg.regions;

public enum RegionChangeResult
{
	ALLOW, TOO_SMALL, OVERTACKTS, INTERSECTS, OUTOFPARENT, OUTOFWORLD;

	public static RegionChangeResult getByOrigin(int id)
	{
		switch(id)
		{
			case 0: return ALLOW;
			case 1: return TOO_SMALL;
			case 2: return OVERTACKTS;
			case 3: return INTERSECTS;
			case 4: return OUTOFPARENT;
			case 5: return OUTOFWORLD;
		}

		return null;
	}
}
