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
    | defaultObjectSection
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
        | variableDeclaration
        | quickArithmeticOperation
    ) SEMICOLON
    ;

variableDeclaration
    : NAME EQUAL value
    ;

value
    : STRING
    | NUMBER
    | NAME
    | methodCall
    | arithmeticOperation
    | boolean
    ;

boolean
    : TRUE | FALSE
    ;

arithmeticOperation
    : arithmeticOperation (arithmeticOperator arithmeticOperation)+
    | OPEN_PARENTHESIS arithmeticOperation CLOSE_PARENTHESIS
    | MINUS arithmeticOperation
    | NUMBER
    | NAME
    ;

quickArithmeticOperation
    : increment
    | decrement
    ;

increment
    : NAME DOUBLE_PLUS
    ;

decrement
    : NAME DOUBLE_MINUS
    ;

arithmeticOperator
    : PLUS
    | MINUS
    | STAR
    | DOUBLE_STAR
    | SLASH
    | PERCENTAGE
    ;

methodCall
    : primaryCaller postfixExpression+
    ;

primaryCaller
    : string
    | NAME
    ;

postfixExpression
    : DOT NAME OPEN_PARENTHESIS parameters? CLOSE_PARENTHESIS
    ;

parameters
    : value (parameterSeparator value)*
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

defaultObjectSection
    : DEFAULT_OBJECT_HEADER defaultObjectContent
    ;

defaultObjectContent
    : defaultObjectConstructorCall SEMICOLON
    ;

defaultObjectConstructorCall
    : constructorCall
    ;

constructorCall
    : NAME OPEN_PARENTHESIS parameters? CLOSE_PARENTHESIS
    ;

string
    : STRING
    | COMPLEX_STRING
    ;

METADATA_HEADER: '== metadata ==';
LOGIC_HEADER: '== logic ==';
PARAMETERS_HEADER: '== parameters ==';
NATIVE_LOGIC_HEADER: '== native-logic ==';
DEFAULT_OBJECT_HEADER: '== default-object ==';
SEMICOLON: ';';
COLON: ':';
COMMA: ',';
DOT: '.';
OPEN_PARENTHESIS: '(';
CLOSE_PARENTHESIS: ')';
EQUAL: '=';
PLUS: '+';
MINUS: '-';
STAR: '*';
DOUBLE_STAR: '**';
SLASH: '/';
PERCENTAGE: '%';
DOUBLE_PLUS: '++';
DOUBLE_MINUS: '--';
TRUE: 'true';
FALSE: 'false';
NUMBER: DIGIT+ ('.' DIGIT+)? | '.' DIGIT+;
NAME: [a-z]([a-z0-9]|'-'[a-z0-9])*;
STRING: '`' ~[`]* '`';
COMPLEX_STRING: '-- our-string-start --' .*? '-- our-string-end --';
WS: [ \t\r\n]+ -> skip;

fragment DIGIT: [0-9];
