package com.oghrairi.graphdb;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.awt.*;
import java.util.*;
import java.util.List;

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
        public HashSet<List<Integer>> queryApplyer(ParseTree subtree){

            //this method will (probably recursively) handle applying the right query algorithms to the right sections of a query string
            int childCount = subtree.getChildCount();
            //get class of current node in tree
            String nodeClass = subtree.getClass().getName().substring(subtree.getClass().getName().indexOf("$"));
            String nodeText = subtree.getText();
            HashSet<List<Integer>>pairs = new HashSet<>();
            outputEdges.clear();
            //decide what the next step is depending on the current node's class
            switch (nodeClass){
                case "$AtomContext":
                    pairs = edgeQuery(nodeText,"none");
                    break;
                case "$AtomOpContext":
                    String operator = nodeText.substring(nodeText.length()-1);
                    pairs = edgeQuery(nodeText.substring(0,nodeText.length()-1),operator);
                    break;
                case "$SlashContext":
                    //procedure for concatenating edges of a path
                    //First, recursively call this method on the two parts being concatenated to return sets
                    //of vertex pairs
                    HashSet<List<Integer>> out1 = queryApplyer(subtree.getChild(0));
                    HashSet<List<Integer>> out2 = queryApplyer(subtree.getChild(2));
                    HashSet<List<Integer>> combined = new HashSet<>();
                    HashSet<List<Integer>> out = new HashSet<>();
                    for(List<Integer> pair1 : out1){
                        for(List<Integer> pair2 : out2){
                            Integer[] comb = new Integer[4];
                            comb[0] = pair1.get(0);
                            comb[1] = pair1.get(1);
                            comb[2] = pair2.get(0);
                            comb[3] = pair2.get(1);
                            combined.add(Arrays.asList(comb));
                        }
                    }
                    outputEdges.clear();
                    for(List<Integer> pair : combined){
                        if(pair.get(1)==pair.get(2)){
                            System.out.println("Match: "+pair.get(0)+","+pair.get(1)+","+pair.get(2)+","+pair.get(3));
                            Integer[] i = new Integer[2];
                            i[0] = pair.get(0);
                            i[1] = pair.get(3);
                            out.add(Arrays.asList(i));
                        }
                    }
                    pairs=out;
                    break;
                case "$BracketContext":
                    System.out.println("bracket");
                    break;
                case "$BracketOpContext":
                    String op = subtree.getChild(3).getText();
                    HashSet<List<Integer>> inner = queryApplyer(subtree.getChild(1));
                    System.out.println(inner.size());
            }
            for(List<Integer> pair : pairs){
                String str = Integer.toString(pair.get(0));
                str+="->";
                str+= Integer.toString(pair.get(1));
                outputEdges.add(str);
            }
            return pairs;
        }
        public HashSet<List<Integer>> edgeQuery (String edge, String operator){
            HashSet<List<Integer>> pairs = new HashSet<>();
            Set<Integer> inverts = vertices.keySet();
            for (Integer v : inverts){
                if(operator.equals("none")){
                    for (Edge e : vertices.get(v).getEdges()) {
                        //for each edge, if the label matches the current label in the path, add its destination to the
                        //next iteration of the search
                        if (e.label.equals(edge)) {
                            Integer[] p = new Integer[2];
                            p[0]=v;
                            p[1]=e.destinationId;
                            pairs.add(Arrays.asList(p));
                        }
                    }
                }else if(operator.equals("-")){
                    for (Integer v2 : vertices.keySet()){
                        for(Edge e : vertices.get(v2).getEdges()){
                            if(e.label.equals(edge)&&e.destinationId.equals(v)){
                                Integer[] p = new Integer[2];
                                p[0]=v;
                                p[1]=v2;
                                pairs.add(Arrays.asList(p));
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
                        Integer[] p = new Integer[2];
                        p[0]=v;
                        p[1]=i;
                        pairs.add(Arrays.asList(p));

                    }

                }

            }
            return pairs;
        }
    }

}
