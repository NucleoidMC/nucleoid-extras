package xyz.nucleoid.extras.integrations.connection;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class IntegrationsSocketConnection extends SimpleChannelInboundHandler<ByteBuf> implements IntegrationsConnection {
    private static final EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup(
            1,
            new ThreadFactoryBuilder()
                    .setNameFormat("nucleoid-integrations-connection")
                    .setDaemon(true)
                    .build()
    );

    private static final int TIMEOUT_SECONDS = 5;

    private static final int MAX_FRAME_SIZE = 4 * 1024 * 1024;
    private static final int FRAME_HEADER_SIZE = 4;

    private static final Gson GSON = new Gson();
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final Handler handler;

    private final ConcurrentLinkedQueue<ByteBuf> writeQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean scheduledWrite = new AtomicBoolean(false);

    private Channel channel;

    private IntegrationsSocketConnection(Handler handler) {
        this.handler = handler;
    }

    public static CompletableFuture<IntegrationsSocketConnection> connect(SocketAddress address, Handler handler) {
        var connection = new IntegrationsSocketConnection(handler);

        var bootstrap = new Bootstrap();
        bootstrap.group(EVENT_LOOP_GROUP);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(address);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                channel.pipeline()
                        .addLast(new WriteTimeoutHandler(TIMEOUT_SECONDS, TimeUnit.SECONDS))
                        .addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_SIZE, 0, FRAME_HEADER_SIZE, 0, FRAME_HEADER_SIZE))
                        .addLast(new LengthFieldPrepender(FRAME_HEADER_SIZE))
                        .addLast(connection);
            }
        });

        var future = new CompletableFuture<IntegrationsSocketConnection>();
        bootstrap.connect().addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                connection.channel = channelFuture.channel();
                future.complete(connection);
                connection.handler.acceptConnection();
            } else {
                Throwable cause = channelFuture.cause();
                future.completeExceptionally(cause);
                connection.handler.acceptError(cause);
            }
        });

        return future;
    }

    @Override
    public boolean send(String type, JsonObject body) {
        var payload = new JsonObject();
        payload.addProperty("type", type);
        payload.add("body", body);

        var bytes = GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
        this.writeQueue.add(Unpooled.wrappedBuffer(bytes));

        if (this.scheduledWrite.compareAndSet(false, true)) {
            EVENT_LOOP_GROUP.execute(this::writeQueued);
        }

        return true;
    }

    private void writeQueued() {
        this.scheduledWrite.set(false);

        var writeQueue = this.writeQueue;
        if (!writeQueue.isEmpty()) {
            var channel = this.channel;

            ByteBuf message;
            while ((message = writeQueue.poll()) != null) {
                var future = channel.write(message);
                future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }

            channel.flush();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf message) {
        var json = JSON_PARSER.parse(message.toString(StandardCharsets.UTF_8)).getAsJsonObject();
        var type = json.get("type").getAsString();
        var body = json.getAsJsonObject("body");
        this.handler.acceptMessage(type, body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.handler.acceptError(cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        this.handler.acceptClosed();
    }
}
