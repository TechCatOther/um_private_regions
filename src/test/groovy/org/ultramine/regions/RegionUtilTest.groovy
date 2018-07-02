package org.ultramine.regions

import spock.lang.Specification
import static org.ultramine.regions.RegionUtil.isIntersects
import static org.ultramine.regions.RegionUtil.isBoxInBox

class RegionUtilTest extends Specification {
	def "Test isIntersects"() {
		expect:
		isIntersects(new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), new BlockPos(0, 0, 0))
		isIntersects(new BlockPos(0, 0, 0), new BlockPos(1, 1, 1), new BlockPos(0, 0, 0), new BlockPos(2, 2, 2))
		isIntersects(new BlockPos(0, 0, 0), new BlockPos(1, 1, 1), new BlockPos(1, 1, 1), new BlockPos(2, 2, 2))
		isIntersects(new BlockPos(0, 0, 0), new BlockPos(1, 1, 1), new BlockPos(0, 0, 0), new BlockPos(1, 1, 1))
		isIntersects(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2), new BlockPos(0, 0, 0), new BlockPos(3, 3, 3))
		isIntersects(new BlockPos(0, 0, 0), new BlockPos(3, 3, 3), new BlockPos(1, 1, 1), new BlockPos(2, 2, 2))
		isIntersects(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2), new BlockPos(1, 1, 1), new BlockPos(3, 3, 3))
		isIntersects(new BlockPos(1, 1, 1), new BlockPos(3, 3, 3), new BlockPos(0, 0, 0), new BlockPos(2, 2, 2))
		!isIntersects(new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), new BlockPos(1, 1, 1), new BlockPos(1, 1, 1))
		!isIntersects(new BlockPos(0, 0, 0), new BlockPos(1, 1, 1), new BlockPos(2, 2, 2), new BlockPos(3, 3, 3))
	}

	def "Test isBoxInBox"() {
		expect:
		isBoxInBox(new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), new BlockPos(0, 0, 0))
		isBoxInBox(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2), new BlockPos(1, 1, 1), new BlockPos(2, 2, 2))
		isBoxInBox(new BlockPos(1, 1, 1), new BlockPos(2, 2, 2), new BlockPos(0, 0, 0), new BlockPos(3, 3, 3))
		!isBoxInBox(new BlockPos(1, 1, 1), new BlockPos(3, 3, 3), new BlockPos(0, 0, 0), new BlockPos(2, 2, 2))
		!isBoxInBox(new BlockPos(0, 0, 0), new BlockPos(2, 2, 2), new BlockPos(1, 1, 1), new BlockPos(3, 3, 3))
		!isBoxInBox(new BlockPos(0, 0, 0), new BlockPos(3, 3, 3), new BlockPos(1, 1, 1), new BlockPos(2, 2, 2))
	}
}
