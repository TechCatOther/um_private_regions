package org.ultramine.mods.privreg.render;

import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileBlockRegionRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");
	private ModelDragon model = new ModelDragon(0.0F);
	private EntityDragon dragon = new EntityDragon(null);

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float p_147500_8_)
	{
		float rot = 0.0F;
		switch (te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord))
		{
			case 3:
				rot = 90.0F;
				break;
			case 0:
				rot = 180.0F;
				break;
			case 1:
				rot = 270.0F;
				break;
			case 2:
				break;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
		GL11.glRotatef(180, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.2F, 0.2F, 0.2F);

		bindTexture(enderDragonTextures);
		dragon.setPosition(x, y, z);
		model.render(dragon, 0, 0, 0, 0, 0, 0.0625F);

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
}
