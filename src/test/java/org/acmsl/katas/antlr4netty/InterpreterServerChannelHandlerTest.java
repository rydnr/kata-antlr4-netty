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
 * Filename: InterpreterServerChannelHandlerTest.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Tests for InterpreterServerChannelHandler.
 *
 * Date: 2014/07/17
 * Time: 07:28
 *
 */
package org.acmsl.katas.antlr4netty;

/*
 * Importing JetBrains annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/*
 * Importing JDK classes.
 */
import java.math.BigDecimal;

/**
 * Tests for {@link InterpreterServerChannelHandler}.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 3.0
 * Created: 2014/07/17 07:28
 */
@RunWith(JUnit4.class)
public class InterpreterServerChannelHandlerTest
{
    /**
     * Simple check to ensure everything is bound together.
     */
    @Test
    public void calculates_correctly()
    {
        @NotNull final InterpreterServerChannelHandler instance = new InterpreterServerChannelHandler();

        @NotNull final BigDecimal outcome = instance.calculate("3+3.5");

        Assert.assertEquals(new BigDecimal("6.5"), outcome);
    }
}
