/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ftccommunity.ftcxtensible.networking.http;

import android.util.Log;

import org.ftccommunity.ftcxtensible.RobotContext;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in stopMode pretty plaintext form.
 */
public final class HttpHelloWorldServer implements Runnable {
    static final int PORT = 8080;
    private RobotContext context;

    public HttpHelloWorldServer(RobotContext ctx) {
        context = ctx;
    }

    public void run() {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.option(ChannelOption.SO_BACKLOG, 1024);
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                                //.childHandler(new HttpHelloWorldServerInitializer(sslCtx, main));
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            /**
                             * This method will be called once the {@link Channel} was registered. After the method returns this instance
                             * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
                             *
                             * @param ch the {@link Channel} which was registered.
                             * @throws Exception is thrown if an error occurs. In that case the {@link Channel} will be closed.
                             */
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline()
                                        .addLast(new HttpServerCodec(),
                                                new HttpHelloWorldServerHandler(context));
                            }
                        });

                Channel ch = b.bind(PORT).sync().channel();

                System.err.println("Open your web browser and navigate to " +
                        ("http") + "://127.0.0.1:" + PORT + '/');

                ch.closeFuture().sync();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        Log.i("NET_OP_MODE::", "Op Mode Server is shuting down.");
    }
}

