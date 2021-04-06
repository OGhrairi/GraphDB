grammar g;

PLUS: '+';
MINUS: '-';
EDGE : [A-Za-z0-9]+;
PIPE : '|';
LB : '(';
RB : ')';
SLASH : '/';
ARROW : '<-';
COMMA : ',';
LS : '[';
RS : ']';

crpq :
    variables cexpression (COMMA cexpression)*;

variables :
    LB EDGE (COMMA EDGE)* RB ARROW;

cexpression :
    LS expression RS LB EDGE COMMA EDGE RB;

expression :
    expression SLASH expression # slash
   | LB expression RB (PLUS|MINUS) # bracket
   | EDGE (PLUS|MINUS)? # atom
   | expression PIPE expression # or
   ;


