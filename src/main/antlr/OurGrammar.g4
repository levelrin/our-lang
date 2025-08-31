grammar OurGrammar;

@header {package com.levelrin.antlr.generated;}

headers
    : header*
    ;

header
    : DOUBLE_EQUAL NAME DOUBLE_EQUAL content
    ;

content
    : pairs
    | statements
    | string
    ;

statements
    : statement*
    ;

statement
    : methodCall
    ;

methodCall
    : value DOT NAME OPEN_PARENTHESIS parameters CLOSE_PARENTHESIS SEMICOLON
    ;

parameters
    : value (COMMA value)*
    ;

value
    : STRING
    | NAME
    ;

pairs
    : pair (COMMA pair)*
    ;

pair
    : NAME ':' value
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
