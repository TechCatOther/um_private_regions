package org.ultramine.util;

public class I18n
{
	/**
	 * Translate the key by Minecraft translation tables.<br>
	 * Use it with <code>import static angel.core.util.I18n.tlt</code> and <code>tlt(input)</code>
	 * @param input key for translate
	 * @return translated string
	 */
	public static String tlt(String input, Object... params)
	{
		return net.minecraft.client.resources.I18n.format(input, params);
	}
}
