// this is not an antlr grammar file. it's a pseudo grammar file for the language.

moduleRoot: imports? topLevels*

imports: 'import' '{' (Word EOS)* '}'
topLevels: topLevelVarDecl | funcDecl | typeDecl
topLevelVarDecl: 'let' Word (':' typeId)? '=' call EOS
funcDecl: 'def' Word '(' paramList? ')' '->' typeId block // func type is forced.
typeDecl: 'type' Word '{' (Word (':' typeId)? '=' call EOS)* '}
// typeId: 'Any' | 'Number' | 'Boolean' | 'String' | Word
typeId: Word

block: '{' stmt* '};
lambdaLit: '{' (paramList '->')? stmt* '}'

stmt
:  'if' expr block ('else' 'if' expr block)* ('else' block)?
|  'while' expr block
|  'for' Word 'from' expr 'to' expr ('step' expr)? block // range for
|  'for' Word 'in' expr block  // foreach
|  'break' EOS
|  'continue' EOS
|  'return' call? EOS
|  'let' Word (':' typeId)? '=' call EOS // var decl
|  assign EOS // assignment
|  call EOS // expression statement, value ignored

assign: assignableExpr '=' call
assignableExpr: postfixExpr ('.' Word | '[' call ']')

call: (expr | word)+ // at least 1 word. expr alone is not a call
primary: '(' call ')' | varName | StringLit | NumberLit | BooleanLit | NullLit | lambdaLit
expr
:  primary
|  expr ('.' Word | '[' call ']') // postfix operators
|  ('+' | '-' | 'not') expr // unary operators, parse time 1 max each. +1 is ok but +-1 --1 is not ok
|  expr ('as') type // type cast
|  expr ('**' | '^') expr // pow level operators
|  expr ('*' | '/' | '%' | '//') expr // mul level operators
|  expr ('+' | '-') expr // add level operators
|  expr ('is' 'not'? | 'not'? 'in') expr // named operators
|  expr ('<' | '>' | '<=' | '>=') expr // comparison. chained comparison is supported
|  expr ('=' | '==' | '!=') expr // equality. chained equality is supported, same side effect with logic and ver
|  expr ('and' | '&&') expr // logic and
|  expr ('or' | '||') expr  // logic or


