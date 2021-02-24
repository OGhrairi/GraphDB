package com.oghrairi.graphdb;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.HashSet;

public class Query {
    private String queryString;
    private Graph graph;
    private HashSet<String> outputEdges;
    public Query() {
    }
    public HashSet<String> RunQuery(String queryString, Graph graph){
        //This is the method that runs the query, outputs a hashset of vertex pairs stored as a string
        this.queryString = queryString;
        this.graph = graph;
        //these lines are some boilerplate code needed to generate and use the parse tree
        CharStream stream = CharStreams.fromString(queryString);
        gLexer gl = new gLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(gl);
        gParser parser = new gParser(tokens);
        ParseTree parseTree = parser.crpq();
        ParseTreeWalker.DEFAULT.walk(new QueryListener(), parseTree);
        return outputEdges;
    }
    //subclass which inherits from Antlr base listener class, walks through the generated parse tree and runs certain
    //functions at different classes of nodes
    public class QueryListener extends gBaseListener{
        private ArrayList<String> conjunctiveVariables;


        public QueryListener() {
            conjunctiveVariables = new ArrayList<String>();
        }

        //this method runs when the parser reaches the conjunctive variable declaration part of the query, and
        //stores those variables in an arraylist
        @Override
        public void enterVariables(gParser.VariablesContext ctx) {
            int childCount = ctx.getChildCount();
            for (int i=1; i<childCount-2;i=i+2){
                String child = ctx.getChild(i).getText();
                conjunctiveVariables.add(child);
            }
        }

        //this method runs when the tree enteres the root of a query expression, including the query string and given bound variables.
        //From this method, the other listener methods are abandoned in favour of a personalised approach
        @Override
        public void enterCexpression(gParser.CexpressionContext ctx) {
            String[] boundVariables = new String[2];
            int childCount = ctx.getChildCount();
            boundVariables[0] = ctx.getChild(childCount-4).getText();
            boundVariables[1] = ctx.getChild(childCount-2).getText();
            ParseTree exp = ctx.getChild(1);
            queryApplyer(exp);

        }
        /*
        class names of importance:
        $SlashContext
        $AtomContext
        $AtomOpContext
        $BracketOpContext
        $BracketContext
        $OrContext
         */
        public void queryApplyer(ParseTree subtree){
            HashSet<String> outputPairs = new HashSet<>();
            //this method will (probably recursively) handle applying the right query algorithms to the right sections of a query string
            int childCount = subtree.getChildCount();
            //get class of current node in tree
            String nodeClass = subtree.getClass().getName().substring(subtree.getClass().getName().indexOf("$"));
            System.out.println(subtree.getText());
            String nodeText = subtree.getText();
            ArrayList<Integer> nextChildren = new ArrayList<>();
            //decide what the next step is depending on the current node's class
            switch (nodeClass){
                case "$AtomContext":
                    outputPairs.addAll(edgeQuery(nodeText));
                    break;
                case "$AtomOpContext":

            }
            for (int i=0; i<childCount; i++){
                String cls = subtree.getChild(i).getClass().getName();
                if(cls.indexOf("$")>=0){
                    nextChildren.add(i);
                    cls = cls.substring(cls.indexOf("$"));
                }
            }
            for(int child : nextChildren){
                System.out.println("********************");

                queryApplyer(subtree.getChild(child));

            }

        }
        public HashSet<String> edgeQuery (String edge, String operator){
            HashSet<String> pairs = new HashSet<>();



            return pairs;
        }
        public HashSet<String> edgeQuery(String edge){
            return(edgeQuery(edge, "none"));
        }
    }

}
