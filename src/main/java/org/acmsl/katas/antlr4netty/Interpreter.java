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
 * Filename: Interpreter.java
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Interpreter to perform basic arithmetic calculations.
 *
 * Date: 2014/07/15
 * Time: 08:09
 *
 */
package org.acmsl.katas.antlr4netty;

/*
 * Importing JetBrains annotations.
 */
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

/*
 * Importing checkthread.org annotations.
 */
import org.checkthread.annotations.ThreadSafe;

import java.math.BigDecimal;

/**
 * Interpreter to perform basic arithmetic calculations.
 * @author <a href="mailto:queryj@acm-sl.org">Jose San Leandro</a>
 * @since 3.0
 * Created: 2014/07/15 08:09
 */
@ThreadSafe
public class Interpreter
{
    /**
     * Creates a new instance to evaluate given operation.
     */
    public Interpreter()
    {
    }

    /**
     * Processes given input.
     */
    @NotNull
    protected InterpreterParser setupParser(@NotNull final String input)
    {
        @NotNull final InterpreterParser result;

        @NotNull final InterpreterLexer t_Lexer = new InterpreterLexer(new ANTLRInputStream(input));

        @NotNull final CommonTokenStream t_Tokens = new CommonTokenStream(t_Lexer);

        result = new InterpreterParser(t_Tokens);

        return result;

    }
    /**
     * Evaluates the operation.
     * @param operation the operation to evaluate.
     * @return the result of the operation.
     */
    @NotNull
    public BigDecimal eval(@NotNull final String operation)
    {
        InterpreterParser parser = setupParser(operation);

        ParseTree tree = parser.command();

        return
            new InterpreterBaseVisitor<BigDecimal>()
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public BigDecimal visitCommand(
                    @org.antlr.v4.runtime.misc.NotNull final InterpreterParser.CommandContext ctx)
                {
                    @NotNull final BigDecimal result;

                    @NotNull final BigDecimal left = new BigDecimal(ctx.getChild(0).getText());
                    @NotNull final String operator = ctx.getChild(1).getText();

                    if ("+".equals(operator))
                    {
                        @NotNull final BigDecimal right = new BigDecimal(ctx.getChild(2).getText());
                        result = left.add(right);
                    }
                    else if ("-".equals(operator))
                    {
                        @NotNull final BigDecimal right = new BigDecimal(ctx.getChild(2).getText());
                        result = left.subtract(right);
                    }
                    else
                    {
                        // the operator is missing -> implicit in right operand
                        @NotNull final BigDecimal right = new BigDecimal(ctx.getChild(1).getText());
                        result = left.add(right);
                    }

                    return result;
                }
            }.visit(tree);
    }
}
