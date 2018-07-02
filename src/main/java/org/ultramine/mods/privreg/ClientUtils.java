package org.ultramine.mods.privreg;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class ClientUtils
{
	private static final Pattern split1 = Pattern.compile("^[,\\s]+");
	private static final Pattern split2 = Pattern.compile("[,\\s]+");
	private static boolean isAdmin;

	public static boolean isAdminClient()
	{
		return isAdmin;
	}

	public static void setAdmin(boolean ia)
	{
		isAdmin = ia;
	}

	public static String[] commaSplit(String input)
	{
		return split2.split(split1.matcher(input).replaceAll(""));
	}
}
