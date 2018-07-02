package org.ultramine.regions;

import java.util.List;

public interface IRegion
{
	int getID();

	Rectangle getShape();

	IRegion getParent();

	List<IRegion> getChildren();

	boolean addChild(IRegion region);

	boolean removeChild(IRegion region);

	void onChildAreaChanged(IRegion region);
}
