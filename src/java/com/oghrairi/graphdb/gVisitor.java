// Generated from C:/Users/Omarg/Desktop/Projects/Projects - year 3/FYP/src/java/com/oghrairi/graphdb\g.g4 by ANTLR 4.9.1
package com.oghrairi.graphdb;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link gParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface gVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link gParser#crpq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCrpq(gParser.CrpqContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#variables}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariables(gParser.VariablesContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#cexpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCexpression(gParser.CexpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bracketOp}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBracketOp(gParser.BracketOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code or}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr(gParser.OrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code bracket}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBracket(gParser.BracketContext ctx);
	/**
	 * Visit a parse tree produced by the {@code slash}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSlash(gParser.SlashContext ctx);
	/**
	 * Visit a parse tree produced by the {@code atom}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(gParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code atomOp}
	 * labeled alternative in {@link gParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomOp(gParser.AtomOpContext ctx);
}