package fun.rubicon.cluster.impl;

import fun.rubicon.cluster.ClusterClient;
import fun.rubicon.cluster.event.ClusterEventManager;
import fun.rubicon.cluster.event.ClusterListenerAdapter;
import fun.rubicon.cluster.events.ClusterConnectedEvent;
import fun.rubicon.cluster.events.ClusterDisconnectedEvent;
import fun.rubicon.cluster.events.ClusterMessageReceivedEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ClusterClientImpl implements ClusterClient {

    private final Logger logger = LoggerFactory.getLogger(ClusterClientImpl.class.getSimpleName());
    private final String host;
    private final int port;
    @Getter private final List<Channel> channels;
    @Getter private final ClusterEventManager eventManager;

    public ClusterClientImpl(String host, int port, List<ClusterListenerAdapter> listenerAdapters) {
        this.host = host;
        this.port = port;
        eventManager = new ClusterEventManager(listenerAdapters);
        channels = new ArrayList<>();
    }

    public void start() throws Exception {
        logger.info("Starting Cluster Client...");
        ClusterClientHandler handler = new ClusterClientHandler() {
            @Override
            protected void addChannel(Channel channel) {
                channels.add(channel);
                eventManager.fire(new ClusterConnectedEvent(getInstance(), channel));
            }

            @Override
            protected void removeChannel(Channel channel) {
                channels.add(channel);
                eventManager.fire(new ClusterDisconnectedEvent(getInstance(), channel));
            }

            @Override
            protected void receivedMessage(Channel channel, String s) {
                eventManager.fire(new ClusterMessageReceivedEvent(getInstance(), channel, s));
            }
        };
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(handler);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            logger.info("Started Cluster Client.");

            //Shutdown
            channelFuture.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

    @Override
    public void send(String invoke, String message) {
        for(Channel channel : channels)
            channel.writeAndFlush(Unpooled.copiedBuffer(String.format("[%s] %s", invoke, message), CharsetUtil.UTF_8));
    }

    private ClusterClient getInstance() {
        return this;
    }
}
