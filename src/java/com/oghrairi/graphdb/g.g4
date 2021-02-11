grammar g;

CLOSURE: '+';
INVERSE: '-';
EDGE : [a-z0-9]+;
UNION : '|';
LB : '(';
RB : ')';
CONCAT : '/';

union : atom (UNION atom)*;
atom: edge (CONCAT edge)*;
edge : EDGE | closure | inverse;
closure : EDGE CLOSURE;
inverse : EDGE INVERSE;
