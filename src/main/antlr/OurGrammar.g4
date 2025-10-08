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
    ) semicolon
    | ifStatement
    | forLoop
    ;

forLoop
    : for openParenthesis forLoopHeader closeParenthesis block
    ;

forLoopHeader
    : forLoopInitialization forLoopCondition forLoopIteration
    ;

forLoopInitialization
    : number equal number semicolon
    ;

forLoopCondition
    : numberComparison semicolon
    ;

forLoopIteration
    : variableName (doublePlus|doubleMinus)
    ;

block
    : openBrace statement* closeBrace
    ;

ifStatement
    : ifPart elseIfPart* elsePart?
    ;

ifPart
    : if openParenthesis condition closeParenthesis block
    ;

elseIfPart
    : else if openParenthesis condition closeParenthesis block
    ;

elsePart
    : else block
    ;

condition
    : variableName
    | boolean
    | methodCall
    ;

relationalOperator
    : openAngleBracket equal?
    | closeAngleBracket equal?
    ;

variableDeclaration
    : variableName equal value
    ;

value
    : simpleString
    | number
    | variableName
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

numberComparison
    : number relationalOperator number
    ;

arithmeticOperation
    : arithmeticOperation (arithmeticOperator arithmeticOperation)+
    | openParenthesis arithmeticOperation closeParenthesis
    | minus arithmeticOperation
    | number
    | variableName
    ;

quickArithmeticOperation
    : increment
    | decrement
    ;

increment
    : variableName doublePlus
    ;

decrement
    : variableName doubleMinus
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
    : simpleString
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

simpleString
    : STRING
    ;

number
    : NUMBER
    | NAME
    | methodCall
    ;

variableName
    : NAME
    ;

doublePlus
    : DOUBLE_PLUS
    ;

doubleMinus
    : DOUBLE_MINUS
    ;

equal
    : EQUAL
    ;

semicolon
    : SEMICOLON
    ;

for
    : FOR
    ;

if
    : IF
    ;

else
    : ELSE
    ;

openParenthesis
    : OPEN_PARENTHESIS
    ;

closeParenthesis
    : CLOSE_PARENTHESIS
    ;

openBrace
    : OPEN_BRACE
    ;

closeBrace
    : CLOSE_BRACE
    ;

openAngleBracket
    : OPEN_ANGLE_BRACKET
    ;

closeAngleBracket
    : CLOSE_ANGLE_BRACKET
    ;

booleanLiterals
    : TRUE | FALSE
    ;

minus
    : MINUS
    ;

equalityOperator
    : DOUBLE_EQUAL
    | NOT_EQUAL
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
