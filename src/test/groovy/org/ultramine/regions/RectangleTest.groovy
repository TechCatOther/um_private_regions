package org.ultramine.regions

import net.minecraftforge.common.util.ForgeDirection
import spock.lang.Specification

class RectangleTest extends Specification {
	def "Test expand"() {
		expect:
		new Rectangle(0, 0, 0, 0, 0, 0).expand(ForgeDirection.WEST,		1) == new Rectangle(-1,  0,  0, 0, 0, 0)
		new Rectangle(0, 0, 0, 0, 0, 0).expand(ForgeDirection.DOWN,		1) == new Rectangle( 0, -1,  0, 0, 0, 0)
		new Rectangle(0, 0, 0, 0, 0, 0).expand(ForgeDirection.NORTH,	1) == new Rectangle( 0,  0, -1, 0, 0, 0)
		new Rectangle(0, 0, 0, 0, 0, 0).expand(ForgeDirection.EAST,		1) == new Rectangle( 0,  0,  0, 1, 0, 0)
		new Rectangle(0, 0, 0, 0, 0, 0).expand(ForgeDirection.UP,		1) == new Rectangle( 0,  0,  0, 0, 1, 0)
		new Rectangle(0, 0, 0, 0, 0, 0).expand(ForgeDirection.SOUTH,	1) == new Rectangle( 0,  0,  0, 0, 0, 1)
	}

	def "Test some transformations"() {
		setup:
		Rectangle shape = new Rectangle(0, 0, 0, 10, 0, 10);
		ForgeDirection dir = ForgeDirection.EAST; //x+
		int amount = 1;
		int cd = 10;

		Rectangle nshape = shape.compress(dir.getOpposite(), shape.getLen(dir)).expand(dir, amount).expandAll(cd).compress(dir.getOpposite(), cd);

		expect:
		nshape == new Rectangle(11, -10, -10, 21, 10, 20);
	}
}
