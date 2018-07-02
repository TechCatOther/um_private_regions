package org.ultramine.regions

import spock.lang.Specification

class RegionMapTest extends Specification {
	def "Test add, get and remove for one region"() {
		setup:
		RegionMap map = new RegionMap();
		IRegion region = Mock(IRegion);
		region.getShape() >> {new Rectangle(-500, -500, -500, 1000, 1000, 1000)}
		map.add(region);

		expect:
		map.get(new BlockPos(0, 0, 0)) == region
		map.get(new BlockPos(-500, -500, -500)) == region
		map.get(new BlockPos(1000, 1000, 1000)) == region
		map.get(new BlockPos(1001, 1000, 1000)) == null

		when:
		map.remove(region)

		then:
		map.map.isEmpty()
	}

	def "Test add, get and remove for two regions without intersects"() {
		setup:
		RegionMap map = new RegionMap();
		IRegion region1 = Mock(IRegion);
		region1.getShape() >> {new Rectangle(0, 0, 0, 150, 150, 150)}
		IRegion region2 = Mock(IRegion);
		region2.getShape() >> {new Rectangle(151, 0, 0, 350, 350, 350)}
		map.add(region1);
		map.add(region2);

		expect:
		map.get(new BlockPos(150, 0, 0)) == region1
		map.get(new BlockPos(151, 0, 0)) == region2
		map.get(new BlockPos(151, 0, 351)) == null

		when:
		map.remove(region1)
		map.remove(region2)

		then:
		map.map.isEmpty()
	}

	def "Test add, get and remove for two regions with intersects"() {
		setup:
		RegionMap map = new RegionMap();
		IRegion region1 = Mock(IRegion);
		region1.getShape() >> {new Rectangle(0, 0, 0, 150, 150, 150)}
		IRegion region2 = Mock(IRegion);
		region2.getShape() >> {new Rectangle(1, 1, 1, 140, 140, 140)}
		region1.getChildren() >> {Arrays.asList(region2)}
		map.add(region1);
		map.add(region2);

		expect:
		map.get(new BlockPos(130, 130, 130)) == region2
		map.get(new BlockPos(141, 141, 141)) == region1

		when:
		map.remove(region1)
		map.remove(region2)

		then:
		map.map.isEmpty()
	}

	def "Test getInRange for two regions without intersects"() {
		setup:
		RegionMap map = new RegionMap();
		IRegion region1 = Mock(IRegion);
		region1.getShape() >> {new Rectangle(0, 0, 0, 150, 150, 150)}
		IRegion region2 = Mock(IRegion);
		region2.getShape() >> {new Rectangle(151, 0, 0, 350, 350, 350)}
		map.add(region1);
		map.add(region2);

		expect:
		map.getInRange(new Rectangle(0, 0, 0, 1, 1, 1)) == [region1] as Set
		map.getInRange(new Rectangle(0, 0, 0, 151, 1, 1)) == [region1, region2] as Set
		map.getInRange(new Rectangle(151, 0, 0, 152, 1, 1)) == [region2] as Set
		map.getInRange(new Rectangle(351, 0, 0, 352, 1, 1)) == [] as Set
	}
}
