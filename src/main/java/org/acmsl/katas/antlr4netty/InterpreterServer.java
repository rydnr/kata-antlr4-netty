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
 * Filename: InterpreterServer.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Netty-based server to perform simple calculations.
 *
 * Date: 2014/07/16
 * Time: 08:05
 *
 */
package org.acmsl.katas.antlr4netty;

/*
 * Importing Netty classes.
 */
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing checkthread.org annotations.
 */
import org.checkthread.annotations.ThreadSafe;

/*
 * Importing JDK classes.
 */
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Netty-based server to perform simple calculations.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 3.0
 * Created: 2014/07/16 08:05
 */
@ThreadSafe
public class InterpreterServer
{
    /**
     * Creates a new instance.
     */
    public InterpreterServer()
    {
    }

    /**
     * Launches the server to accept incoming requests on given port.
     * @param port the port.
     * @return the {@link ChannelFuture} when the server stops accepting connections.
     */
    @NotNull
    public ChannelFuture listen(final int port)
    {
        @NotNull final ChannelFuture result;

        @NotNull final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        @NotNull final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(
                new ChannelInitializer<SocketChannel>()
                {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    protected void initChannel(@NotNull final SocketChannel ch)
                        throws Exception
                    {
                        ch.pipeline().addLast(new InterpreterServerChannelHandler());
                    }
                })
            .option(ChannelOption.SO_BACKLOG, 128)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .childOption(ChannelOption.SO_KEEPALIVE, true);

        result = wrap(bootstrap.bind(port), bossGroup, workerGroup);

        return result;
    }

    /**
     * Wraps given {@link ChannelFuture} to ensure the event loops
     * shut down gracefully.
     * @param target the original channel future.
     * @param bossGroup the boss group.
     * @param workerGroup the worker group.
     * @return the wrapped future.
     */
    @NotNull
    protected ChannelFuture wrap(
        @NotNull final ChannelFuture target,
        @NotNull final NioEventLoopGroup bossGroup,
        @NotNull final NioEventLoopGroup workerGroup)
    {
        return
            new ChannelFuture()
            {
                @Override
                public Channel channel()
                {
                    return target.channel();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public ChannelFuture addListener(
                    @NotNull final GenericFutureListener<? extends Future<? super Void>> listener)
                {
                    return target.addListener(listener);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public ChannelFuture addListeners(
                    @NotNull final GenericFutureListener<? extends Future<? super Void>>... listeners)
                {
                    return target.addListeners(listeners);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public ChannelFuture removeListener(
                    @NotNull final GenericFutureListener<? extends Future<? super Void>> listener)
                {
                    return target.removeListener(listener);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public ChannelFuture removeListeners(
                    @NotNull final GenericFutureListener<? extends Future<? super Void>>... listeners)
                {
                    return target.removeListeners(listeners);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public ChannelFuture sync()
                    throws InterruptedException
                {
                    ChannelFuture result = null;

                    try
                    {
                        result = target.sync();
                    }
                    finally
                    {
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    }

                    return result;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public ChannelFuture syncUninterruptibly()
                {
                    return target.syncUninterruptibly();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public ChannelFuture await()
                    throws InterruptedException
                {
                    return target.await();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public ChannelFuture awaitUninterruptibly()
                {
                    return target.awaitUninterruptibly();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean isSuccess()
                {
                    return target.isSuccess();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean isCancellable()
                {
                    return target.isCancellable();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Throwable cause()
                {
                    return target.cause();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean await(final long timeout, @NotNull final TimeUnit unit)
                    throws InterruptedException
                {
                    return target.await(timeout, unit);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean await(final long timeoutMillis)
                    throws InterruptedException
                {
                    return target.await(timeoutMillis);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean awaitUninterruptibly(final long timeout, @NotNull final TimeUnit unit)
                {
                    return target.awaitUninterruptibly(timeout, unit);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean awaitUninterruptibly(final long timeoutMillis)
                {
                    return target.awaitUninterruptibly(timeoutMillis);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Void getNow()
                {
                    return target.getNow();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean cancel(final boolean mayInterruptIfRunning)
                {
                    return target.cancel(mayInterruptIfRunning);
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean isCancelled()
                {
                    return target.isCancelled();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public boolean isDone()
                {
                    return target.isDone();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Void get()
                    throws InterruptedException,
                           ExecutionException
                {
                    return target.get();
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public Void get(final long timeout, @NotNull final TimeUnit unit)
                    throws  InterruptedException,
                            ExecutionException,
                            TimeoutException
                {
                    return target.get(timeout, unit);
                }
            };
    }

    /**
     * Runs the interpreter from the command line.
     * @param args the arguments.
     */
    public static void main(@NotNull final String[] args)
    {
        final int port = Integer.parseInt(args[0]);

        @NotNull ChannelFuture future = new InterpreterServer().listen(port);

        try
        {
            future.channel().closeFuture().sync();

            future.sync();

            System.out.println("Finishing...");
        }
        catch (@NotNull final InterruptedException interrupted)
        {
            System.err.println("Error: " + interrupted.getMessage());
        }
    }
}
