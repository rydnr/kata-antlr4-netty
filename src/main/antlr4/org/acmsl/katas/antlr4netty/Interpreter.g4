//;-*- mode: antlr -*-
/*
                        Interpreter

    Copyright (C) 2014-today  Jose San Leandro Armendariz
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
    Contact info: chous@acm-sl.org
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Filename: Interpreter.g4
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: Defines a very simple interpreter.
 *
 */
grammar Interpreter;

@parser::header
{
/*
                        Interpreter

    Copyright (C) 2014-today  Jose San Leandro Armendariz
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
    Contact info: chous@acm-sl.org
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Generated from Interpreter.g4 by ANTLR.
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: ANTLR parser for Interpreter.g4
 *
 */
}

@lexer::header
{
/*
                        Interpreter

    Copyright (C) 2014-today  Jose San Leandro Armendariz
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
    Contact info: chous@acm-sl.org
    Postal Address: c/Playa de Lagoa, 1
                    Urb. Valdecabanas
                    Boadilla del monte
                    28660 Madrid
                    Spain

 ******************************************************************************
 *
 * Generated from Interpreter.g4 by ANTLR.
 *
 * Author: Jose San Leandro Armendariz
 *
 * Description: ANTLR lexer for Interpreter.g4
 *
 */
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

command : exp op? exp;

exp : NUMBER;
op : OPERATOR;

/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/

WS : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ -> skip;

NUMBER: ('-')? POSITIVE_NUMBER;

OPERATOR: '+' | '-';

fragment POSITIVE_NUMBER :
    POSITIVE_INTEGER
  | POSITIVE_REAL;

fragment POSITIVE_INTEGER : ('0'..'9')+;
fragment POSITIVE_REAL : ('0'..'9')+'.'('0'..'9')+;