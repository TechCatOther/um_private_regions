package org.ultramine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ILinkable
{
	static final int PREV = 0;
	static final int NEXT = 1;
	
	boolean linkTo(ILinkable to);
	
	void setPrevious(ILinkable to);

	ILinkable getPrevious();
	
	ILinkable getNext();
	
	void otherElementAction(ILinkable element, int side);
}
