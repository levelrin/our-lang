grammar OurGrammar;

@header {package com.levelrin.antlr.generated;}

headers
    : header*
    ;

header
    : DOUBLE_EQUAL NAME DOUBLE_EQUAL content
    ;

content
    : headerPairs
    | statements
    | string
    ;

statements
    : statement*
    ;

statement
    : methodCall SEMICOLON
    ;

methodCall
    : primaryValue postfixExpression+
    ;

postfixExpression
    : DOT NAME OPEN_PARENTHESIS parameters CLOSE_PARENTHESIS
    ;

parameters
    : paramValue (paramSeparator paramValue)*
    ;

paramValue
    : paramPrimaryValue
    | methodCall
    ;

paramSeparator
    : COMMA
    ;

paramPrimaryValue
    : STRING
    | NAME
    ;

primaryValue
    : STRING
    | NAME
    ;

headerPairs
    : headerPair (COMMA headerPair)*
    ;

headerPair
    : NAME ':' headerValue
    ;

headerValue
    : STRING
    | NAME
    ;

string
    : STRING
    | COMPLEX_STRING
    ;

DOUBLE_EQUAL: '==';
SEMICOLON: ';';
COMMA: ',';
DOT: '.';
OPEN_PARENTHESIS: '(';
CLOSE_PARENTHESIS: ')';
NAME: [a-z]([a-z0-9]|'-'[a-z0-9])*;
STRING: '`' ~[`]* '`';
COMPLEX_STRING: '-- our-string-start --' .*? '-- our-string-end --';
WS: [ \t\r\n]+ -> skip;
