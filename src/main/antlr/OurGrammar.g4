grammar OurGrammar;

@header {package com.levelrin.antlr.generated;}

file
    : metadata optionalSection*
    ;

metadata
    : metadataHeader metadataBody
    ;

optionalSection
    : objects
    | logic
    ;

objects
    : objectsHeader objectsBody
    ;

logic
    : logicHeader logicBody
    ;

metadataHeader
    : DOUBLE_EQUAL METADATA DOUBLE_EQUAL
    ;

metadataBody
    : aboutAttribute
    ;

aboutAttribute
    : ABOUT COLON EXECUTABLE
    ;

objectsHeader
    : DOUBLE_EQUAL OBJECTS DOUBLE_EQUAL
    ;

objectsBody
    : objectDeclaration+
    ;

objectDeclaration
    : objectFromSdk
    ;

objectFromSdk
    : NAME FROM SDK
    ;

logicHeader
    : DOUBLE_EQUAL LOGIC DOUBLE_EQUAL
    ;

logicBody
    : statement+
    ;

statement
    : voidMethodCall
    ;

voidMethodCall
    : variableName COMMA methodName arguments? DOT
    ;

arguments
    : argument+
    ;

argument
    : STRING_LITERAL
    ;

variableName
    : NAME
    ;

methodName
    : NAME
    ;

DOUBLE_EQUAL: '==';
METADATA: 'metadata';
OBJECTS: 'objects';
LOGIC: 'logic';
ABOUT: 'about';
EXECUTABLE: 'executable';
COLON: ':';
FROM: 'from';
SDK: 'sdk';
NAME: [a-z] ([a-z0-9-]* [a-z0-9])?;
COMMA: ',';
DOT: '.';
STRING_LITERAL: '`' ~[`]* '`';
WS: [ \t\r\n]+ -> skip;
