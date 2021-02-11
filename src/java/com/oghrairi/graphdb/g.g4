grammar g;

PLUS: '+';
MINUS: '-';
EDGE : [A-Za-z0-9]+;
PIPE : '|';
LB : '(';
RB : ')';
SLASH : '/';


atom : EDGE;
expression :
    expression  SLASH expression
   |  LB expression RB (PLUS|MINUS)?
   |   atom (PLUS|MINUS)?
   ;


