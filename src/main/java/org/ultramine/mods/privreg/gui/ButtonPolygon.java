package org.ultramine.mods.privreg.gui;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ButtonPolygon extends GuiButton
{
	private List<Point> polygon;
	
	public ButtonPolygon(List<Point> polygon)
	{
		super(0, 0, 0, 0, 0, "");
		this.polygon = polygon;
	}
	
	public void drawButton(Minecraft par1Minecraft, int x, int y)
    {
		
    }
	
	public boolean mouseClicked(Minecraft mc, int x, int y)
	{
		if(mousePressed(x, y))
		{
			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int x, int y)
    {
		return mousePressed(x, y);
    }
	
	public boolean mousePressed(int x, int y)
    {
		int q_patt[][] = { {0,1}, {3,2} };
		
		if(polygon.size() < 3)
			return false;

		Point pred_pt = new Point(polygon.get(polygon.size()-1));
		pred_pt.x -= x;
		pred_pt.y -= y;
		
		int pred_q = q_patt[bti(pred_pt.y<0)][bti(pred_pt.x<0)];

		int w = 0;
		
		for(Point cur_pt : polygon)
		{
			cur_pt = new Point(cur_pt);

			cur_pt.x -= x;
			cur_pt.y -= y;
			
			int q = q_patt[bti(cur_pt.y<0)][bti(cur_pt.x<0)];
			
			switch (q - pred_q)
			{
			case -3:	++w;	break;
			case  3:	--w;	break;
			case -2:	if(pred_pt.x*cur_pt.y >= pred_pt.y*cur_pt.x) ++w;	break;
			case  2:	if(pred_pt.x*cur_pt.y <  pred_pt.y*cur_pt.x) --w;	break;
			}

			pred_pt = cur_pt;
			pred_q = q;
		}
		return w != 0;
    }
	
	private static int bti(boolean f){return f ? 1 : 0 ;}
}
