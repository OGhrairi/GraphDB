package com.oghrairi.graphdb;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Query {
    private String queryString;
    private Graph graph;
    private HashSet<String> outputEdges;
    private Map<Integer, Vertex> vertices;
    public Query() {
        outputEdges = new HashSet<>();
    }
    public HashSet<String> RunQuery(String queryString, Graph graph){
        //This is the method that runs the query, outputs a hashset of vertex pairs stored as a string
        this.queryString = queryString;
        this.graph = graph;
        this.vertices =graph.getVertices();
        //these lines are some code needed to generate and use the parse tree;
        //convert query string into a charstream, generate a lexer from the lexer class created from my grammar with
        //the charstream as an input, take the tokens from that lexer into a tokenstream, feed that stream into a parser
        //then use a tree walker to step through the generated parse tree
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

        //this method runs when the tree enters the root of a query expression, including the query string and given bound variables.
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
                    System.out.println("entering atom");
                    edgeQuery(nodeText,"none");
                    break;
                case "$AtomOpContext":
                    String operator = nodeText.substring(nodeText.length()-1);
                    edgeQuery(nodeText.substring(0,nodeText.length()-1),operator);
                    break;
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
        public HashSet<String> edgeQuery (String edge, String operator, Set<String> startVertices){
            HashSet<String> pairs = new HashSet<>();
            Set<Integer> inverts = new HashSet<>();
            if(startVertices.contains("none")){
                inverts = vertices.keySet();
            }else{
                for (String s : startVertices){
                    inverts.add(Integer.parseInt(s));
                }
            }
            System.out.println("using edge: "+edge);
            for (Integer v : inverts){
                if(operator.equals("none")){

                    for (Edge e : vertices.get(v).getEdges()) {
                        //for each edge, if the label matches the current label in the path, add its destination to the
                        //next iteration of the search
                        if (e.label.equals(edge)) {
                            System.out.println("match found : "+v+"->"+e.destinationId);
                            pairs.add(v+"->"+e.destinationId);
                            outputEdges.add(v+"->"+e.destinationId);

                        }
                    }

                }else if(operator.equals("-")){
                    for (Integer v2 : vertices.keySet()){
                        for(Edge e : vertices.get(v2).getEdges()){
                            if(e.label.equals(edge)&&e.destinationId.equals(v)){
                                pairs.add(v+"->"+v2);
                                outputEdges.add(v+"->"+v2);
                                System.out.println("match found : "+v+"->"+v2);
                            }
                        }
                    }
                }
                else if(operator.equals("+")){
                    ArrayList<Integer> reachable = new ArrayList<>();
                    ArrayList<Integer> l1 = new ArrayList<>();
                    ArrayList<Integer> l2 = new ArrayList<>();
                    l1.add(v);
                    for(int i=0; i<graph.getEdgeCount(); i++){
                        for(Integer v2 : l1){
                            for(Edge e : vertices.get(v2).getEdges()){
                                if(e.label.equals(edge)){
                                    l2.add(e.destinationId);
                                    reachable.add(e.destinationId);
                                }
                            }
                        }
                        l1.clear();
                        l1.addAll(l2);
                        l2.clear();
                    }
                    for(Integer i : reachable){
                        pairs.add(v+"->"+i);
                        outputEdges.add(v+"->"+i);
                        System.out.println("match found : "+v+"->"+i);
                    }

                }

            }


            return pairs;
        }
        public HashSet<String> edgeQuery (String edge, String operator){
            HashSet<String> sv = new HashSet<>();
            sv.add("none");
            return edgeQuery(edge, operator, sv);
        }
    }

}
