package org.kotlina.searchfield;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CriteriaConditionParser {

    private final Expression<String> path;

    private final CriteriaBuilder criteriaBuilder;

    public CriteriaConditionParser(Expression<String> path, CriteriaBuilder criteriaBuilder) {
        this.path = path;
        this.criteriaBuilder = criteriaBuilder;
    }

    public Predicate parse(String searchString) {
        final FilterParser parser = new FilterParser(new CommonTokenStream(new FilterLexer(CharStreams.fromString(searchString))));
        return parseCondition(parser.condition());
    }

    private Predicate parseCondition(FilterParser.ConditionContext condition) {
        final int termSize = condition.conditionTerm().size();

        if (termSize == 1) {
            return parseConditionTerm(condition.conditionTerm(0));
        }

        final Predicate[] predicates = new Predicate[termSize];
        for (int i = 0; i < termSize; i++) {
            predicates[i] = parseConditionTerm(condition.conditionTerm(i));
        }
        return criteriaBuilder.or(predicates);
    }

    private Predicate parseConditionTerm(FilterParser.ConditionTermContext conditionTerm) {
        final int factorSize = conditionTerm.conditionFactor().size();
        if (factorSize == 1) {
            return parseConditionFactor(conditionTerm.conditionFactor(0));
        }

        final Predicate[] predicates = new Predicate[factorSize];
        for (int i = 0; i < factorSize; i++) {
            predicates[i] = parseConditionFactor(conditionTerm.conditionFactor(i));
        }

        return criteriaBuilder.and(predicates);
    }

    private Predicate parseConditionFactor(FilterParser.ConditionFactorContext conditionFactor) {
        if (conditionFactor.getChildCount() == 1) {
            return parseConditionPrimary(conditionFactor.conditionPrimary());
        } else {
            Predicate predicate = parseConditionPrimary(conditionFactor.conditionPrimary());
            return criteriaBuilder.not(predicate);
        }
    }

    private Predicate parseConditionPrimary(FilterParser.ConditionPrimaryContext conditionPrimary) {
        final int childCount = conditionPrimary.getChildCount();
        if (childCount == 1) {
            return parseExpr((ParserRuleContext) conditionPrimary.getChild(0));
        } else if (childCount == 3) {
            return parseCondition(conditionPrimary.condition());
        }

        throw new IllegalStateException("No such grammar");
    }

    private Predicate parseExpr(ParserRuleContext ctx) {
        if (ctx instanceof FilterParser.LeExprContext) {
            return criteriaBuilder.lessThanOrEqualTo(path, getText(((FilterParser.LeExprContext) ctx).VALUE()));
        } else if (ctx instanceof FilterParser.LtExprContext) {
            return criteriaBuilder.lessThan(path, getText(((FilterParser.LtExprContext) ctx).VALUE()));
        } else if (ctx instanceof FilterParser.GeExprContext) {
            return criteriaBuilder.greaterThanOrEqualTo(path, getText(((FilterParser.GeExprContext) ctx).VALUE()));
        } else if (ctx instanceof FilterParser.GtExprContext) {
            return criteriaBuilder.greaterThan(path, getText(((FilterParser.GtExprContext) ctx).VALUE()));
        } else if (ctx instanceof FilterParser.EqExprContext) {
            return criteriaBuilder.equal(path, getText(((FilterParser.EqExprContext) ctx).VALUE()));
        } else if (ctx instanceof FilterParser.LikeExprContext) {
            String text = getText(((FilterParser.LikeExprContext) ctx).VALUE());

            if (text.indexOf('*') < 0 && text.indexOf('_') < 0) {
                text = text + "*";
            }
            text = text.replace('*', '%');
            text = text.replace('?', '_');
            return criteriaBuilder.like(path, text);
        }

        throw new IllegalStateException("no such grammar");
    }

    private String getText(TerminalNode value) {
        final String v = value.getText();
        if (v.indexOf('"') == 0 && v.lastIndexOf('"') == v.length() - 1) {
            return v.substring(1, v.length() - 1);
        }
        return v;
    }
}
