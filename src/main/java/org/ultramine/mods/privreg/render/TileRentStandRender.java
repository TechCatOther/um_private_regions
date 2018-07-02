package org.ultramine.mods.privreg.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import org.ultramine.mods.privreg.modules.RegionModuleRent;
import org.ultramine.mods.privreg.modules.RegionModuleRent.RentMode;
import org.ultramine.mods.privreg.regions.Region;
import org.ultramine.mods.privreg.tiles.TileRentStand;

public class TileRentStandRender extends TileEntitySpecialRenderer
{
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f)
	{
		TileRentStand rs = (TileRentStand) te;
		Region region = rs.getRegion();
		if(region == null)
			return;
		RegionModuleRent module = region.getModuleWithClass(RegionModuleRent.class);
		if(module == null)
			return;

		GL11.glPushMatrix();
		float var10 = 0.6666667F;
		float var12;

		var12 = 0.0F;
		switch(rs.getWorldObj().getBlockMetadata(rs.xCoord, rs.yCoord, rs.zCoord))
		{
			case 3: var12 = 90.0F;	break;
			case 1: var12 = -90.0F;	break;
			case 0: var12 = 180.0F;	break;
			case 2: 				break;
		}

		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * var10, (float) z + 0.5F);
		GL11.glRotatef(-var12, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.0F, -0.3125F, -0.4160F);

		GL11.glPushMatrix();
		GL11.glScalef(var10, -var10, -var10);
		GL11.glPopMatrix();
		FontRenderer fr = this.func_147498_b();
		var12 = 0.016666668F * var10;
		GL11.glTranslatef(0.0F, 0.5F * var10, 0.07F * var10);
		GL11.glScalef(var12, -var12, var12);
		GL11.glNormal3f(0.0F, 0.0F, -1.0F * var12);
		GL11.glDepthMask(false);
		//
		RentMode mode = module.getMode();
		String label = mode == RentMode.RENT ? "§cСдается в аренду" : "§cПродается";
		String name = module.getAreaName();
		String price = mode == RentMode.RENT ? "§cПо цене §2"+module.getRentalFee()+"§c/день" : "§cПо цене §2"+module.getSellPrice();
		fr.drawString("§c"+name,  -fr.getStringWidth(name ) / 2, 0 * 10 - 6 * 5 + 2, 0xffffff);
		fr.drawString(label, -38, 1 * 10 - 6 * 5 + 2, 0xffffff);
		fr.drawString(price, -38, 2 * 10 - 6 * 5 + 2, 0xffffff);
		//
		GL11.glDepthMask(true);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
}
