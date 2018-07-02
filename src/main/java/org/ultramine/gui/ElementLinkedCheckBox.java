package org.ultramine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElementLinkedCheckBox extends ElementCheckBox implements ILinkable
{
	private ILinkable previous;
	private ILinkable next;
	
	public ElementLinkedCheckBox(int id, int x, int y)
	{
		super(id, x, y);
	}
	
	public ElementLinkedCheckBox(int id, int x, int y, String str)
	{
		super(id, x, y, str);
	}
	
	@Override
	protected void stateChanged()
	{
		if(isChecked)
		{
			if(previous != null)	previous.otherElementAction(this, PREV);
			if(next != null)		next.otherElementAction(this, NEXT);
		}
	}

	@Override
	public boolean linkTo(ILinkable to)
	{
		if(!(to instanceof ElementLinkedCheckBox)) return false;
		next = to;
		to.setPrevious(this);
		return true;
	}

	@Override
	public void setPrevious(ILinkable to)
	{
		previous = to;
	}

	@Override
	public ILinkable getPrevious()
	{
		return previous;
	}

	@Override
	public ILinkable getNext()
	{
		return next;
	}

	@Override
	public void otherElementAction(ILinkable element, int side)
	{
		isChecked = false;
		if(side == PREV && previous != null)	previous.otherElementAction(this, PREV);
		if(side == NEXT && next != null)		next.otherElementAction(this, NEXT);
	}

}
