package org.ultramine.mods.privreg.render;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class OverlayMessageRender
{
	private static int timeFull;
	private static int timeFadeIn = 20;
	private static int timeStay = 100;
	private static int timeFadeOut = 20;
	private static String title;
	private static String subtitle;
	private static float position = 2;

	static {
		FMLCommonHandler.instance().bus().register(new OverlayMessageRender());
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onRenderTick(TickEvent.RenderTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END)
		{
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.theWorld != null && !mc.skipRenderWorld && !mc.gameSettings.hideGUI && mc.currentScreen == null)
			{
				draw(mc, e.renderTickTime);
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent e)
	{
		if(e.phase == TickEvent.Phase.END && timeFull > 0)
		{
			--timeFull;
			if(timeFull <= 0)
			{
				title = "";
				subtitle = "";
			}
		}
	}

	public static void display(String title_, String subtitle_)
	{
		display(title_, subtitle_, 100, 20, 20);
	}

	public static void display(String title_, String subtitle_, float position_)
	{
		display(title_, subtitle_, 100, 20, 20, position_);
	}

	public static void display(String title_, String subtitle_, int timeStay_, int timeFadeIn_, int timeFadeOut_)
	{
		display(title_, subtitle_, timeStay_, timeFadeIn_, timeFadeOut_, 2F);
	}

	public static void display(String title_, String subtitle_, int timeStay_, int timeFadeIn_, int timeFadeOut_, float position_)
	{
		title = title_ == null ? "" : title_;
		subtitle = subtitle_ == null ? "" : subtitle_;
		timeStay = timeStay_;
		timeFadeIn = timeFadeIn_;
		timeFadeOut = timeFadeOut_;
		timeFull = timeFadeIn + timeStay + timeFadeOut;
		position = position_;
	}

	private static void draw(Minecraft mc, float partialTicks)
	{
		ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int width = sr.getScaledWidth();
		int height = sr.getScaledHeight();
		if(timeFull > 0)
		{
			float f2 = (float) timeFull - partialTicks;
			int alpha = 255;

			if(timeFull > timeFadeOut + timeStay)
			{
				float f3 = (float)(timeFadeIn + timeStay + timeFadeOut) - f2;
				alpha = (int)(f3 * 255.0F / (float) timeFadeIn);
			}

			if(timeFull <= timeFadeOut)
			{
				alpha = (int)(f2 * 255.0F / (float) timeFadeOut);
			}

			alpha = MathHelper.clamp_int(alpha, 0, 255);

			if(alpha > 8)
			{
				GL11.glPushMatrix();
				GL11.glTranslatef((float) (width / 2), (float) (height / position), 0.0F);
				GL11.glEnable(GL11.GL_BLEND);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glPushMatrix();
				GL11.glScalef(3.7F, 3.7F, 3.7F);
				int l = alpha << 24 & -16777216;
				mc.fontRenderer.drawString(title, -mc.fontRenderer.getStringWidth(title) / 2, -10, 16777215 | l, true);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glScalef(2.0F, 2.0F, 2.0F);
				mc.fontRenderer.drawString(subtitle, -mc.fontRenderer.getStringWidth(subtitle) / 2, 5, 16777215 | l, true);
				GL11.glPopMatrix();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();
			}
		}
	}
}
