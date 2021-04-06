// Generated from C:/Users/Omarg/Desktop/Projects/Projects - year 3/FYP/src/java/com/oghrairi/graphdb\g.g4 by ANTLR 4.9.1
package com.oghrairi.graphdb;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link gParser}.
 */
public interface gListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link gParser#crpq}.
	 * @param ctx the parse tree
	 */
	void enterCrpq(gParser.CrpqContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#crpq}.
	 * @param ctx the parse tree
	 */
	void exitCrpq(gParser.CrpqContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#variables}.
	 * @param ctx the parse tree
	 */
	void enterVariables(gParser.VariablesContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#variables}.
	 * @param ctx the parse tree
	 */
	void exitVariables(gParser.VariablesContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#cexpression}.
	 * @param ctx the parse tree
	 */
	void enterCexpression(gParser.CexpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#cexpression}.
	 * @param ctx the parse tree
	 */
	void exitCexpression(gParser.CexpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code or}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterOr(gParser.OrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code or}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitOr(gParser.OrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bracket}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBracket(gParser.BracketContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bracket}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBracket(gParser.BracketContext ctx);
	/**
	 * Enter a parse tree produced by the {@code slash}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSlash(gParser.SlashContext ctx);
	/**
	 * Exit a parse tree produced by the {@code slash}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSlash(gParser.SlashContext ctx);
	/**
	 * Enter a parse tree produced by the {@code atom}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAtom(gParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code atom}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAtom(gParser.AtomContext ctx);
}