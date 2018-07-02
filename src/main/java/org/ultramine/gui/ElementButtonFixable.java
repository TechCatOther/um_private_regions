package org.ultramine.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ElementButtonFixable extends ElementButton implements ILinkable
{
	private boolean pressed = false;
	
	private ILinkable previous;
	private ILinkable next;
	
	public ElementButtonFixable(int id, int x, int y, int width, int height, String str)
	{
		super(id, x, y, width, height, str);
	}
	
	public boolean isPressed()
	{
		return pressed;
	}
	
	public void setPressed()
	{
		pressed = true;
	}
	
	@Override
	protected boolean canPress()
	{
		return !pressed;
	}
	
	@Override
	protected void buttonActivate()
	{
		pressed = true;
		if(previous != null)	previous.otherElementAction(this, PREV);
		if(next != null)		next.otherElementAction(this, NEXT);
	}
	
	@Override
	protected int getHoverState(boolean par1)
	{
		if (!enabled || pressed)
		{
			return 0;
		}
		else if (par1)
		{
			return 2;
		}

		return 1;
	}

	// ILinkable

	@Override
	public boolean linkTo(ILinkable to)
	{
		if(!(to instanceof ElementButtonFixable))
			return false;
		if(next != null)
		{
			next.linkTo(to);
		}
		else
		{
			next = to;
			to.setPrevious(this);
		}
		
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
		pressed = false;
		if(side == PREV && previous != null)	previous.otherElementAction(this, PREV);
		if(side == NEXT && next != null)		next.otherElementAction(this, NEXT);
	}
}
