package fun.rubicon.cluster_server.cluster;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private final Logger logger = LoggerFactory.getLogger(ServerHandler.class.getSimpleName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        logger.info("[ClusterServerImpl] Received: {}", byteBuf.toString(CharsetUtil.UTF_8));
    }
}
