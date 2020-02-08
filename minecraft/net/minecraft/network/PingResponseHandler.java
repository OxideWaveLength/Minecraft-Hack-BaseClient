package net.minecraft.network;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.server.MinecraftServer;

public class PingResponseHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LogManager.getLogger();
	private NetworkSystem networkSystem;

	public PingResponseHandler(NetworkSystem networkSystemIn) {
		this.networkSystem = networkSystemIn;
	}

	public void channelRead(ChannelHandlerContext p_channelRead_1_, Object p_channelRead_2_) throws Exception {
		ByteBuf bytebuf = (ByteBuf) p_channelRead_2_;
		bytebuf.markReaderIndex();
		boolean flag = true;

		try {
			if (bytebuf.readUnsignedByte() == 254) {
				InetSocketAddress inetsocketaddress = (InetSocketAddress) p_channelRead_1_.channel().remoteAddress();
				MinecraftServer minecraftserver = this.networkSystem.getServer();
				int i = bytebuf.readableBytes();

				switch (i) {
				case 0:
					logger.debug("Ping: (<1.3.x) from {}:{}", new Object[] { inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort()) });
					String s2 = String.format("%s\u00a7%d\u00a7%d", new Object[] { minecraftserver.getMOTD(), Integer.valueOf(minecraftserver.getCurrentPlayerCount()), Integer.valueOf(minecraftserver.getMaxPlayers()) });
					this.writeAndFlush(p_channelRead_1_, this.getStringBuffer(s2));
					break;

				case 1:
					if (bytebuf.readUnsignedByte() != 1) {
						return;
					}

					logger.debug("Ping: (1.4-1.5.x) from {}:{}", new Object[] { inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort()) });
					String s = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[] { Integer.valueOf(127), minecraftserver.getMinecraftVersion(), minecraftserver.getMOTD(), Integer.valueOf(minecraftserver.getCurrentPlayerCount()), Integer.valueOf(minecraftserver.getMaxPlayers()) });
					this.writeAndFlush(p_channelRead_1_, this.getStringBuffer(s));
					break;

				default:
					boolean flag1 = bytebuf.readUnsignedByte() == 1;
					flag1 = flag1 & bytebuf.readUnsignedByte() == 250;
					flag1 = flag1 & "MC|PingHost".equals(new String(bytebuf.readBytes(bytebuf.readShort() * 2).array(), Charsets.UTF_16BE));
					int j = bytebuf.readUnsignedShort();
					flag1 = flag1 & bytebuf.readUnsignedByte() >= 73;
					flag1 = flag1 & 3 + bytebuf.readBytes(bytebuf.readShort() * 2).array().length + 4 == j;
					flag1 = flag1 & bytebuf.readInt() <= 65535;
					flag1 = flag1 & bytebuf.readableBytes() == 0;

					if (!flag1) {
						return;
					}

					logger.debug("Ping: (1.6) from {}:{}", new Object[] { inetsocketaddress.getAddress(), Integer.valueOf(inetsocketaddress.getPort()) });
					String s1 = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[] { Integer.valueOf(127), minecraftserver.getMinecraftVersion(), minecraftserver.getMOTD(), Integer.valueOf(minecraftserver.getCurrentPlayerCount()), Integer.valueOf(minecraftserver.getMaxPlayers()) });
					ByteBuf bytebuf1 = this.getStringBuffer(s1);

					try {
						this.writeAndFlush(p_channelRead_1_, bytebuf1);
					} finally {
						bytebuf1.release();
					}
				}

				bytebuf.release();
				flag = false;
				return;
			}
		} catch (RuntimeException var21) {
			return;
		} finally {
			if (flag) {
				bytebuf.resetReaderIndex();
				p_channelRead_1_.channel().pipeline().remove("legacy_query");
				p_channelRead_1_.fireChannelRead(p_channelRead_2_);
			}
		}
	}

	private void writeAndFlush(ChannelHandlerContext ctx, ByteBuf data) {
		ctx.pipeline().firstContext().writeAndFlush(data).addListener(ChannelFutureListener.CLOSE);
	}

	private ByteBuf getStringBuffer(String string) {
		ByteBuf bytebuf = Unpooled.buffer();
		bytebuf.writeByte(255);
		char[] achar = string.toCharArray();
		bytebuf.writeShort(achar.length);

		for (char c0 : achar) {
			bytebuf.writeChar(c0);
		}

		return bytebuf;
	}
}
