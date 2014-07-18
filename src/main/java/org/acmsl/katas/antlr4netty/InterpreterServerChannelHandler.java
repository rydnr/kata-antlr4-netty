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
 * Filename: InterpreterServerChannelHandler.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Gets spawned on incoming connections and calculates the input.
 *
 * Date: 2014/07/17
 * Time: 07:22
 *
 */
package org.acmsl.katas.antlr4netty;

/*
 * Importing Netty classes.
 */
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

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
import java.math.BigDecimal;

/**
 * Gets spawned on incoming connections and calculates the input.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 3.0
 * Created: 2014/07/17 07:22
 */
@ThreadSafe
@Sharable
public class InterpreterServerChannelHandler
    extends ChannelHandlerAdapter
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void channelRead(
        @NotNull final ChannelHandlerContext ctx, @NotNull final Object msg)
        throws Exception
    {
        @NotNull final String input;

        @NotNull final ByteBuf buffer = (ByteBuf) msg;

        @NotNull final byte[] aux = new byte[buffer.readableBytes()];

        for (int index = 0; index < aux.length; index++)
        {
            aux[index] = buffer.readByte();
        }

        ReferenceCountUtil.release(msg);

        input = new String(aux, CharsetUtil.UTF_8);

        @NotNull final BigDecimal output = calculate(input);

        @NotNull final ByteBuf out = Unpooled.copiedBuffer(("" + output + "\n").getBytes(CharsetUtil.UTF_8));

        ctx.writeAndFlush(out);
    }

    /**
     * Performs the arithmetic calculations expressed in given input,
     * according to Interpreter grammar.
     * @param input the input.
     * @return the outcome of the calculation.
     */
    @NotNull
    public BigDecimal calculate(@NotNull final String input)
    {
        return new Interpreter().eval(input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception
    {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
        throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }
}
