grammar OurGrammar;

@header {package com.levelrin.antlr.generated;}

sections
    : section*
    ;

section
    : metadataSection
    | logicSection
    | parametersSection
    | nativeLogicSection
    ;

metadataSection
    : METADATA_HEADER metadataContent
    ;

metadataContent
    : metadataPair (COMMA metadataPair)*
    ;

metadataPair
    : metadataPairKey COLON metadataPairValue
    ;

metadataPairKey
    : NAME
    ;

metadataPairValue
    : NAME
    | STRING
    ;

logicSection
    : LOGIC_HEADER logicContent
    ;

logicContent
    : statement+
    ;

statement
    // todo: define more statements
    : (
        methodCall
    ) SEMICOLON
    ;

methodCall
    : primaryCaller postfixExpression+
    ;

primaryCaller
    : string
    | NAME
    ;

postfixExpression
    : DOT NAME OPEN_PARENTHESIS parameters CLOSE_PARENTHESIS
    ;

parameters
    : parameter (parameterSeparator parameter)*
    ;

parameter
    : string
    | methodCall
    | NAME
    ;

parameterSeparator
    : COMMA
    ;

parametersSection
    : PARAMETERS_HEADER parametersContent
    ;

parametersContent
    : parametersPair (COMMA parametersPair)*
    ;

parametersPair
    : parametersPairKey COLON parametersPairValue
    ;

parametersPairKey
    : NAME
    ;

parametersPairValue
    : NAME
    | STRING
    ;

nativeLogicSection
    : NATIVE_LOGIC_HEADER nativeLogicContent
    ;

nativeLogicContent
    : STRING
    | COMPLEX_STRING
    ;

string
    : STRING
    | COMPLEX_STRING
    ;

METADATA_HEADER: '== metadata ==';
LOGIC_HEADER: '== logic ==';
PARAMETERS_HEADER: '== parameters ==';
NATIVE_LOGIC_HEADER: '== native-logic ==';
SEMICOLON: ';';
COLON: ':';
COMMA: ',';
DOT: '.';
OPEN_PARENTHESIS: '(';
CLOSE_PARENTHESIS: ')';
NAME: [a-z]([a-z0-9]|'-'[a-z0-9])*;
STRING: '`' ~[`]* '`';
COMPLEX_STRING: '-- our-string-start --' .*? '-- our-string-end --';
WS: [ \t\r\n]+ -> skip;
