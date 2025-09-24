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
    : (
        methodCall
        | variableDeclaration
        | quickArithmeticOperation
    ) SEMICOLON
    | ifStatement
    | forLoop
    ;

forLoop
    : FOR OPEN_PARENTHESIS forLoopHeader CLOSE_PARENTHESIS block
    ;

forLoopHeader
    : forLoopInitialization forLoopCondition forLoopIteration
    ;

forLoopInitialization
    : number EQUAL number SEMICOLON
    ;

forLoopCondition
    : numberComparison SEMICOLON
    ;

forLoopIteration
    : NAME (DOUBLE_PLUS|DOUBLE_MINUS)
    ;

block
    : OPEN_BRACE statement* CLOSE_BRACE
    ;

ifStatement
    : ifPart elseIfPart* elsePart?
    ;

ifPart
    : IF OPEN_PARENTHESIS condition CLOSE_PARENTHESIS block
    ;

elseIfPart
    : ELSE IF OPEN_PARENTHESIS condition CLOSE_PARENTHESIS block
    ;

elsePart
    : ELSE block
    ;

condition
    : NAME
    | boolean
    | methodCall
    ;

relationalOperator
    : OPEN_ANGLE_BRACKET EQUAL?
    | CLOSE_ANGLE_BRACKET EQUAL?
    ;

number
    : NUMBER
    | NAME
    | methodCall
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
    // Equality check.
    | value equalityOperator value
    ;

boolean
    : booleanLiterals
    | numberComparison
    ;

booleanLiterals
    : TRUE | FALSE
    ;

numberComparison
    : number relationalOperator number
    ;

equalityOperator
    : DOUBLE_EQUAL
    | NOT_EQUAL
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
OPEN_ANGLE_BRACKET: '<';
CLOSE_ANGLE_BRACKET: '>';
OPEN_PARENTHESIS: '(';
CLOSE_PARENTHESIS: ')';
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
EQUAL: '=';
DOUBLE_EQUAL: '==';
NOT_EQUAL: '!=';
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
IF: 'if';
ELSE: 'else';
FOR: 'for';
NUMBER: DIGIT+ ('.' DIGIT+)? | '.' DIGIT+;
NAME: [a-z]([a-z0-9]|'-'[a-z0-9])*;
STRING: '`' ~[`]* '`';
COMPLEX_STRING: '-- our-string-start --' .*? '-- our-string-end --';
WS: [ \t\r\n]+ -> skip;

fragment DIGIT: [0-9];
