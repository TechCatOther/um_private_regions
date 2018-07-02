package org.ultramine.mods.privreg.packets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import org.ultramine.mods.privreg.render.OverlayMessageRender;
import org.ultramine.network.UMPacket;

import java.io.IOException;

public class PacketTitle extends UMPacket
{
	private String title;
	private String subtitle;
	private int timeStay;
	private int timeFadeIn;
	private int timeFadeOut;
	private float position;

	public PacketTitle(){}
	public PacketTitle(String title, String subtitle, int timeStay, int timeFadeIn, int timeFadeOut, float position)
	{
		this.title = title;
		this.subtitle = subtitle;
		this.timeStay = timeStay;
		this.timeFadeIn = timeFadeIn;
		this.timeFadeOut = timeFadeOut;
		this.position = position;
	}

	public PacketTitle(String title, String subtitle, int timeStay, int timeFadeIn, int timeFadeOut)
	{
		this(title, subtitle, timeStay, timeFadeIn, timeFadeOut, 2F);
	}

	public PacketTitle(String title, String subtitle)
	{
		this(title, subtitle, 100, 20, 20);
	}

	@Override
	public void write(PacketBuffer buf) throws IOException
	{
		buf.writeStringToBuffer(title);
		buf.writeStringToBuffer(subtitle);
		buf.writeInt(timeStay);
		buf.writeInt(timeFadeIn);
		buf.writeInt(timeFadeOut);
		buf.writeFloat(position);
	}

	@Override
	public void read(PacketBuffer buf) throws IOException
	{
		title = buf.readStringFromBuffer(128);
		subtitle = buf.readStringFromBuffer(256);
		timeStay = buf.readInt();
		timeFadeIn = buf.readInt();
		timeFadeOut = buf.readInt();
		position = buf.readFloat();
	}

	@SideOnly(Side.CLIENT)
	public void processClient(NetHandlerPlayClient net)
	{
		OverlayMessageRender.display(title, subtitle, timeStay, timeFadeIn, timeFadeOut, position);
	}
}
