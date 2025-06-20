grammar OurGrammar;

@header {package com.levelrin.antlr.generated;}

file
    : metadata optionalSection*
    ;

metadata
    : METADATA_HEADER metadataBody
    ;

optionalSection
    : objects
    | logic
    | parameters
    ;

objects
    : OBJECTS_HEADER objectsBody
    ;

logic
    : LOGIC_HEADER logicBody
    ;

parameters
    : PARAMETERS_HEADER parametersBody
    ;

metadataBody
    : pairs
    ;

objectsBody
    : objectDeclaration (COMMA objectDeclaration)*
    ;

parametersBody
    : pairs
    ;

objectDeclaration
    : objectFromSdk
    ;

objectFromSdk
    : NAME FROM_SDK
    ;

logicBody
    : statement+
    ;

statement
    : voidMethodCall
    | returnMethodCall
    ;

voidMethodCall
    : variableName COMMA methodName callSuffix
    ;

returnMethodCall
    : variableName COMMA WE_NEED methodName callSuffix
    ;

callSuffix
    : DOT
    | simpleArguments DOT
    | namedArguments
    ;

simpleArguments
    : value
    ;

namedArguments
    : OPEN_BRACE pairs CLOSE_BRACE
    ;

variableName
    : NAME
    ;

methodName
    : NAME
    ;

pairs
    : pair (COMMA pair)*
    ;

pair
    : NAME COLON value
    ;

value
    : NAME
    | STRING_LITERAL
    ;

METADATA_HEADER: '== metadata ==';
OBJECTS_HEADER: '== objects ==';
LOGIC_HEADER: '== logic ==';
PARAMETERS_HEADER: '== parameters ==';
FROM_SDK: 'from sdk';
WE_NEED: 'we need';
COLON: ':';
NAME: [a-z] ([a-z0-9-]* [a-z0-9])?;
COMMA: ',';
DOT: '.';
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
STRING_LITERAL: '`' ~[`]* '`';
WS: [ \t\r\n]+ -> skip;
