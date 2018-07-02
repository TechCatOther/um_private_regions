package org.ultramine.mods.privreg;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;

import java.lang.reflect.Field;
import java.util.List;

@SideOnly(Side.CLIENT)
public class KeyBindingUnbindable extends KeyBinding
{
	private static final Field keybindArray = ReflectionHelper.findField(KeyBinding.class, "field_74516_a", "keybindArray");
	private static final Field hash = ReflectionHelper.findField(KeyBinding.class, "field_74514_b", "hash");

	static
	{
		keybindArray.setAccessible(true);
		hash.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	private static List<KeyBinding> getKeybindArray()
	{
		try
		{
			return (List<KeyBinding>)keybindArray.get(null);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static IntHashMap getHash()
	{
		try
		{
			return (IntHashMap)hash.get(null);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void unsafeBind(KeyBinding kb)
	{
		List<KeyBinding> list = getKeybindArray();
		if(!list.contains(kb))
			list.add(kb);
		getHash().addKey(kb.getKeyCode(), kb);
	}

	public static void unsafeUnbind(KeyBinding kb)
	{
		List<KeyBinding> list = getKeybindArray();
		if(list.contains(kb))
			list.remove(kb);
		getHash().removeObject(kb.getKeyCode());
	}

	private boolean isBound;

	public KeyBindingUnbindable(String label, int id, String category)
	{
		super(label, id, category);
		isBound = true;
	}

	public void unbind()
	{
		if(isBound)
		{
			isBound = false;
			getKeybindArray().remove(this);
			getHash().removeObject(getKeyCode());
		}
	}

	public void bind()
	{
		if(!isBound)
		{
			isBound = true;
			getKeybindArray().add(this);
			getHash().addKey(getKeyCode(), this);
		}
	}

	public boolean isBound()
	{
		return isBound;
	}
}
