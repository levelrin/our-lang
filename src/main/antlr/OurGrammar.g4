grammar OurGrammar;

@header {package com.levelrin.antlr.generated;}

string
    : STRING
    | COMPLEX_STRING
    ;

NAME: [a-z]([a-z0-9]|'-'[a-z0-9])*;
STRING: '`' ~[`]* '`';
COMPLEX_STRING: '-- our-string-start --' .*? '-- our-string-end --';
WS: [ \t\r\n]+ -> skip;
