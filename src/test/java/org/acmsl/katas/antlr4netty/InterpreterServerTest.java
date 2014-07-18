/*
                        kata-antlr4-netty

    Copyright (C) 2002-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: InterpreterServerTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Tests InterpreterServer.
 *
 * Date: 2014/07/16
 * Time: 07:36
 *
 */
package org.acmsl.katas.antlr4netty;

/*
 * Importing Netty classes.
 */
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing JUnit classes.
 */
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/*
 * Importing JDK classes.
 */
import java.net.InetSocketAddress;

/**
 * Tests {@link InterpreterServer}.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 3.0
 * Created: 2014/07/16 07:36
 */
@RunWith(JUnit4.class)
public class InterpreterServerTest
{
    /**
     * Checks whether the server sums integers correctly.
     * @throws Exception if the communication fails.
     */
    @Test
    public void server_sums_integers()
        throws Exception
    {
        final int port = findOutPort();

        @NotNull final InterpreterServer server = new InterpreterServer();

        server.listen(port);

        sendTextToServer("localhost", port, "3 + 5", "8");
    }

    /**
     * Finds out the port to launch the server, from "kata.port" environment variable.
     * @return such port.
     */
    protected int findOutPort()
    {
        int result = -1;

        @Nullable final String port = System.getProperty("kata.port");

        if (port != null)
        {
            try
            {
                result = Integer.parseInt(port);
            }
            catch (@NotNull final NumberFormatException invalidPort)
            {
                Assert.fail("Invalid port: " + invalidPort + ".");
            }
        }
        else
        {
            Assert.fail("No kata.port environment variable defined.");
        }

        return result;
    }

    /**
     * Sends a text to the server.
     * @param host the host.
     * @param port the port.
     * @param msg the text to send.
     * @param expectedResponse the expected response.
     */
    protected void sendTextToServer(
        @NotNull final String host, final int port, @NotNull final String msg, @NotNull final String expectedResponse)
        throws Exception
    {
        new NettyClient(host, port, msg, expectedResponse).connect();
    }

    /**
     * Connects to a remote server.
     */
    public static class NettyClient
    {
        /**
         * The host.
         */
        @NotNull
        private final String host;

        /**
         * The port.
         */
        private final int port;

        /**
         * The message.
         */
        @NotNull
        private final String message;

        /**
         * The expected response.
         */
        @NotNull
        private final String expectedResponse;

        /**
         * Creates a new client.
         * @param host the host.
         * @param port the port.
         * @param msg the message to send.
         * @param expectedResponse the expected response.
         */
        public NettyClient(
            @NotNull final String host,
            final int port,
            @NotNull final String msg,
            @NotNull final String expectedResponse)
        {
            this.host = host;
            this.port = port;
            this.message = msg;
            this.expectedResponse = expectedResponse;
        }

        /**
         * Connects to the server.
         */
        public void connect()
            throws InterruptedException
        {
            connect(this.host, this.port, this.message, this.expectedResponse);
        }

        /**
         * Connects to the server.
         * @param host the host.
         * @param port the port.
         * @param msg the message.
         * @param expectedResponse the expected response.
         */
        protected void connect(
            @NotNull final String host, final int port, @NotNull final String msg, @NotNull final String expectedResponse)
            throws InterruptedException
        {
            @NotNull final NioEventLoopGroup group = new NioEventLoopGroup();

            try
            {
                @NotNull final Bootstrap bootstrap = new Bootstrap();
                bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(
                        new ChannelInitializer<SocketChannel>()
                        {
                            /**
                             * {@inheritDoc}
                             */
                            @Override
                            public void initChannel(@NotNull final SocketChannel channel)
                                throws Exception
                            {
                                channel.pipeline().addLast(new NettyClientHandler(msg, expectedResponse));
                            }
                        }
                    );
                @NotNull final ChannelFuture future = bootstrap.connect().sync();
                future.channel().closeFuture().sync();
            }
            finally
            {
                group.shutdownGracefully().sync();
            }
        }
    }

    /**
     * Handles outbound connections.
     */
    protected static class NettyClientHandler
        extends SimpleChannelInboundHandler<ByteBuf>
    {
        /**
         * The message to send.
         */
        private final String message;

        /**
         * The expected response.
         */
        private final String expectedResponse;

        /**
         * Creates an instance to send given message.
         * @param message the message to send.
         * @param expectedResponse the expected response.
         */
        public NettyClientHandler(final String message, final String expectedResponse)
        {
            this.message = message;
            this.expectedResponse = expectedResponse;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void channelActive(@NotNull final ChannelHandlerContext ctx)
            throws Exception
        {
            ctx.writeAndFlush(Unpooled.copiedBuffer(this.message, CharsetUtil.UTF_8));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void messageReceived(@NotNull final ChannelHandlerContext ctx, @NotNull final ByteBuf msg)
            throws Exception
        {
            @NotNull final byte[] aux = new byte[msg.readableBytes()];

            for (int index = 0; index < aux.length; index++)
            {
                aux[index] = msg.readByte();
            }

            Assert.assertEquals(this.expectedResponse + "\n", new String(aux, CharsetUtil.UTF_8));
        }
    }
}
