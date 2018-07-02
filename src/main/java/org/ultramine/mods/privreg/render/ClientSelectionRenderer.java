package org.ultramine.mods.privreg.render;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.regions.RegionManagerClient;
import org.ultramine.regions.Rectangle;

public class ClientSelectionRenderer
{
	private static TIntSet regions = new TIntHashSet();

	public static void toggleRender(int id)
	{
		if(!regions.contains(id))
			regions.add(id);
		else
			regions.remove(id);
	}

	public static void addToRender(int id)
	{
		regions.add(id);
	}

	public static void clear()
	{
		regions.clear();
	}

	public static void renderAllRegions()
	{
		if(regions.isEmpty())
			return;

		//RenderHelper.disableStandardItemLighting();
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		//GL11.glDisable(GL11.GL_FOG);
		//GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		GL11.glPushMatrix();

		GL11.glTranslated(-RenderManager.renderPosX,
				-RenderManager.renderPosY,
				-RenderManager.renderPosZ);

		GL11.glDepthFunc(GL11.GL_LESS);
		float alpha = 0.8F;

		for(int j = 0; j < 2; j++)
		{
			if(j == 1)
			{
				GL11.glDepthFunc(GL11.GL_GEQUAL);
				alpha = 0.2F;
			}

			for(TIntIterator it = regions.iterator(); it.hasNext();)
			{
				Region region = RegionManagerClient.getInstance().getRegion(it.next());
				if(region == null)
				{
					it.remove();
				}
				else
				{
					Rectangle shape = region.getShape();
					double x1 = shape.getMin().x;
					double y1 = shape.getMin().y;
					double z1 = shape.getMin().z;
					double x2 = shape.getMax().x + 1;
					double y2 = shape.getMax().y + 1;
					double z2 = shape.getMax().z + 1;
					int color = region.getGeneration() % 6;

					GL11.glLineWidth(3.0f);
					switch (color)
					{
						default:
						case 0:
							GL11.glColor4f(0.8F, 0.3F, 0.3F, alpha);
							break;
						case 1:
							GL11.glColor4f(0.3F, 0.8F, 0.3F, alpha);
							break;
						case 2:
							GL11.glColor4f(0.3F, 0.3F, 0.8F, alpha);
							break;
						case 3:
							GL11.glColor4f(0.8F, 0.8F, 0.3F, alpha);
							break;
						case 4:
							GL11.glColor4f(0.3F, 0.8F, 0.8F, alpha);
							break;
						case 5:
							GL11.glColor4f(0.8F, 0.3F, 0.8F, alpha);
							break;
					}

					Tessellator.instance.startDrawing(GL11.GL_LINE_LOOP);
					//tempColor.prepareColor();
					Tessellator.instance.addVertex(x1, y1, z1);
					Tessellator.instance.addVertex(x2, y1, z1);
					Tessellator.instance.addVertex(x2, y1, z2);
					Tessellator.instance.addVertex(x1, y1, z2);
					Tessellator.instance.draw();

					// Draw top face
					Tessellator.instance.startDrawing(GL11.GL_LINE_LOOP);
					//tempColor.prepareColor();
					Tessellator.instance.addVertex(x1, y2, z1);
					Tessellator.instance.addVertex(x2, y2, z1);
					Tessellator.instance.addVertex(x2, y2, z2);
					Tessellator.instance.addVertex(x1, y2, z2);
					Tessellator.instance.draw();

					// Draw join top and bottom faces
					Tessellator.instance.startDrawing(GL11.GL_LINES);
					//tempColor.prepareColor();

					Tessellator.instance.addVertex(x1, y1, z1);
					Tessellator.instance.addVertex(x1, y2, z1);

					Tessellator.instance.addVertex(x2, y1, z1);
					Tessellator.instance.addVertex(x2, y2, z1);

					Tessellator.instance.addVertex(x2, y1, z2);
					Tessellator.instance.addVertex(x2, y2, z2);

					Tessellator.instance.addVertex(x1, y1, z2);
					Tessellator.instance.addVertex(x1, y2, z2);

					Tessellator.instance.draw();

					//
					//
					//

					GL11.glLineWidth(1.0f);
					switch (color)
					{
						default:
						case 0:
							GL11.glColor4f(0.8F, 0.2F, 0.2F, alpha);
							break;
						case 1:
							GL11.glColor4f(0.2F, 0.8F, 0.2F, alpha);
							break;
						case 2:
							GL11.glColor4f(0.2F, 0.2F, 0.8F, alpha);
							break;
					}

					Tessellator.instance.startDrawing(GL11.GL_LINES);

					double x, y, z;
					double offsetSize = 1.0;

					// Zmax XY plane, y axis
					z = z2;
					y = y1;
					int msize = 257;
					if((y2 - y / offsetSize) < msize)
					{
						for(double yoff = 0; yoff + y <= y2; yoff += offsetSize)
						{
							Tessellator.instance.addVertex(x1, y + yoff, z);
							Tessellator.instance.addVertex(x2, y + yoff, z);
						}
					}

					// Zmin XY plane, y axis
					z = z1;
					if((y2 - y / offsetSize) < msize)
					{
						for(double yoff = 0; yoff + y <= y2; yoff += offsetSize)
						{
							Tessellator.instance.addVertex(x1, y + yoff, z);
							Tessellator.instance.addVertex(x2, y + yoff, z);
						}
					}

					// Xmin YZ plane, y axis
					x = x1;
					if((y2 - y / offsetSize) < msize)
					{
						for(double yoff = 0; yoff + y <= y2; yoff += offsetSize)
						{
							Tessellator.instance.addVertex(x, y + yoff, z1);
							Tessellator.instance.addVertex(x, y + yoff, z2);
						}
					}

					// Xmax YZ plane, y axis
					x = x2;
					if((y2 - y / offsetSize) < msize)
					{
						for(double yoff = 0; yoff + y <= y2; yoff += offsetSize)
						{
							Tessellator.instance.addVertex(x, y + yoff, z1);
							Tessellator.instance.addVertex(x, y + yoff, z2);
						}
					}

					// Zmin XY plane, x axis
					x = x1;
					z = z1;
					if((x2 - x / offsetSize) < msize)
					{
						for(double xoff = 0; xoff + x <= x2; xoff += offsetSize)
						{
							Tessellator.instance.addVertex(x + xoff, y1, z);
							Tessellator.instance.addVertex(x + xoff, y2, z);
						}
					}
					// Zmax XY plane, x axis
					z = z2;
					if((x2 - x / offsetSize) < msize)
					{
						for(double xoff = 0; xoff + x <= x2; xoff += offsetSize)
						{
							Tessellator.instance.addVertex(x + xoff, y1, z);
							Tessellator.instance.addVertex(x + xoff, y2, z);
						}
					}
					// Ymin XZ plane, x axis
					y = y2;
					if((x2 - x / offsetSize) < msize)
					{
						for(double xoff = 0; xoff + x <= x2; xoff += offsetSize)
						{
							Tessellator.instance.addVertex(x + xoff, y, z1);
							Tessellator.instance.addVertex(x + xoff, y, z2);
						}
					}
					// Ymax XZ plane, x axis
					y = y1;
					if((x2 - x / offsetSize) < msize)
					{
						for(double xoff = 0; xoff + x <= x2; xoff += offsetSize)
						{
							Tessellator.instance.addVertex(x + xoff, y, z1);
							Tessellator.instance.addVertex(x + xoff, y, z2);
						}
					}

					// Ymin XZ plane, z axis
					z = z1;
					y = y1;
					if((z2 - z / offsetSize) < msize)
					{
						for(double zoff = 0; zoff + z <= z2; zoff += offsetSize)
						{
							Tessellator.instance.addVertex(x1, y, z + zoff);
							Tessellator.instance.addVertex(x2, y, z + zoff);
						}
					}
					// Ymax XZ plane, z axis
					y = y2;
					if((z2 - z / offsetSize) < msize)
					{
						for(double zoff = 0; zoff + z <= z2; zoff += offsetSize)
						{
							Tessellator.instance.addVertex(x1, y, z + zoff);
							Tessellator.instance.addVertex(x2, y, z + zoff);
						}
					}
					// Xmin YZ plane, z axis
					x = x2;
					if((z2 - z / offsetSize) < msize)
					{
						for(double zoff = 0; zoff + z <= z2; zoff += offsetSize)
						{
							Tessellator.instance.addVertex(x, y1, z + zoff);
							Tessellator.instance.addVertex(x, y2, z + zoff);
						}
					}
					// Xmax YZ plane, z axis
					x = x1;
					if((z2 - z / offsetSize) < msize)
					{
						for(double zoff = 0; zoff + z <= z2; zoff += offsetSize)
						{
							Tessellator.instance.addVertex(x, y1, z + zoff);
							Tessellator.instance.addVertex(x, y2, z + zoff);
						}
					}

					Tessellator.instance.draw();
				}
			}
		}

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glPopMatrix();

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		//GL11.glEnable(GL11.GL_ALPHA_TEST);
		//GL11.glEnable(GL11.GL_FOG);
		//RenderHelper.enableStandardItemLighting();
	}
}
