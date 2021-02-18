package com.oghrairi.graphdb;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

public class QueryParser {
    public QueryParser(){
    }
    public void parse(String query){
        String test1 = query;
        CharStream stream = CharStreams.fromString(test1);
        gLexer gl = new gLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(gl);
        gParser parser = new gParser(tokens);
        ParseTree parseTree = parser.crpq();
        ParseTreeWalker.DEFAULT.walk(new gListener() {
            @Override
            public void enterCrpq(gParser.CrpqContext ctx) {

            }

            @Override
            public void exitCrpq(gParser.CrpqContext ctx) {

            }

            @Override
            public void enterVariables(gParser.VariablesContext ctx) {

            }

            @Override
            public void exitVariables(gParser.VariablesContext ctx) {

            }

            @Override
            public void enterCexpression(gParser.CexpressionContext ctx) {
                for(int i=0; i<ctx.getChildCount();i++){
                    System.out.println(ctx.getChild(i).getText());
                }
                System.out.println(ctx.getChild(0).getText());
            }

            @Override
            public void exitCexpression(gParser.CexpressionContext ctx) {

            }

            @Override
            public void enterBracketOp(gParser.BracketOpContext ctx) {

            }

            @Override
            public void exitBracketOp(gParser.BracketOpContext ctx) {

            }

            @Override
            public void enterOr(gParser.OrContext ctx) {

            }

            @Override
            public void exitOr(gParser.OrContext ctx) {

            }

            @Override
            public void enterBracket(gParser.BracketContext ctx) {

            }

            @Override
            public void exitBracket(gParser.BracketContext ctx) {

            }

            @Override
            public void enterSlash(gParser.SlashContext ctx) {

            }

            @Override
            public void exitSlash(gParser.SlashContext ctx) {

            }

            @Override
            public void enterAtom(gParser.AtomContext ctx) {

            }

            @Override
            public void exitAtom(gParser.AtomContext ctx) {

            }

            @Override
            public void enterAtomOp(gParser.AtomOpContext ctx) {

            }

            @Override
            public void exitAtomOp(gParser.AtomOpContext ctx) {

            }

            @Override
            public void visitTerminal(TerminalNode terminalNode) {

            }

            @Override
            public void visitErrorNode(ErrorNode errorNode) {

            }

            @Override
            public void enterEveryRule(ParserRuleContext parserRuleContext) {

            }

            @Override
            public void exitEveryRule(ParserRuleContext parserRuleContext) {

            }
        }, parseTree);
        /*
        tokens.fill();
        for(Token token : tokens.getTokens()){
            System.out.println("Token: "+token.toString());
        }
        */
    }
}
