package org.ultramine.regions;

public class RegionUtil
{
	public static boolean isIntersects(BlockPos min1, BlockPos max1, BlockPos min2, BlockPos max2)
	{
		return
				((min1.x <= min2.x && min2.x <= max1.x) || (min1.x <= max2.x && max2.x <= max1.x) || (min1.x < min2.x && max1.x > max2.x) || (min2.x < min1.x && max2.x > max1.x)) &&
				((min1.y <= min2.y && min2.y <= max1.y) || (min1.y <= max2.y && max2.y <= max1.y) || (min1.y < min2.y && max1.y > max2.y) || (min2.y < min1.y && max2.y > max1.y)) &&
				((min1.z <= min2.z && min2.z <= max1.z) || (min1.z <= max2.z && max2.z <= max1.z) || (min1.z < min2.z && max1.z > max2.z) || (min2.z < min1.z && max2.z > max1.z));
	}

	public static boolean isIntersects(Rectangle rect1, Rectangle rect2)
	{
		return isIntersects(rect1.getMin(), rect1.getMax(), rect2.getMin(), rect2.getMax());
	}

	public static boolean isBoxInBox(BlockPos min1, BlockPos max1, BlockPos min2, BlockPos max2)
	{
		return
				min1.x >= min2.x && max1.x <= max2.x &&
				min1.y >= min2.y && max1.y <= max2.y &&
				min1.z >= min2.z && max1.z <= max2.z;
	}

	public static boolean isBoxInBox(Rectangle child, Rectangle parent)
	{
		return isBoxInBox(child.getMin(), child.getMax(), parent.getMin(), parent.getMax());
	}
}
