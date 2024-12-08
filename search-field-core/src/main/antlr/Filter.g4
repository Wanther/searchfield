grammar Filter;

condition
    : conditionTerm ('|' conditionTerm)*
    ;

conditionTerm
    : conditionFactor ('&' conditionFactor)*
    ;

conditionFactor
    : ('!')? conditionPrimary
    ;

conditionPrimary
    : '(' condition ')'
    | leExpr
    | ltExpr
    | geExpr
    | gtExpr
    | eqExpr
    | likeExpr
    ;

leExpr: '<=' VALUE;
ltExpr: '<' VALUE;
geExpr: '>=' VALUE;
gtExpr: '>' VALUE;
eqExpr: '=' VALUE;
likeExpr: VALUE;

VALUE
    : '"' .*? '"'
    | ~[ &|!<=>()]+
    ;

WS: [ \t\r\n]+ -> skip;