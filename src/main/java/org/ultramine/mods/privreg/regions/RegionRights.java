package org.ultramine.mods.privreg.regions;

import org.ultramine.mods.privreg.owner.OwnerRight;
import org.ultramine.mods.privreg.owner.RightRegistry;

public class RegionRights
{
	public static final OwnerRight CREATOR = RightRegistry.register("basic", "creator");
	public static final OwnerRight ALL_RIGHTS = RightRegistry.register("basic", "all-rights");
	public static final OwnerRight OPEN_BLOCK = RightRegistry.register("basic", "open-block");
	public static final OwnerRight RESIZE = RightRegistry.register("basic", "expand");
	public static final OwnerRight EDIT_USERS = RightRegistry.register("basic", "edit-owners");
	public static final OwnerRight EDIT_MODULES = RightRegistry.register("basic", "edit-modules");
	public static final OwnerRight PLACE_SUBREGIONS = RightRegistry.register("basic", "place-subregions");
}
