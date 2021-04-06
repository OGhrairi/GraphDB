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
        Stack<HashSet<String>> queryStack;
        public QueryListener(){
            queryStack = new Stack<>();
        }
        @Override
        public void exitCexpression(gParser.CexpressionContext ctx){
            //child 1 = expression, child 4 = var 1, then every other child = the next var
            //pop single pairs list as output
            int cc = ctx.getChildCount();
            int varCount = (cc-4)/2;
            if(!queryStack.empty()){
                outputEdges = queryStack.pop();
            }

        }
        @Override
        public void enterAtom(gParser.AtomContext ctx){
            //run atom search, push to stack
            //for atoms, child 0 = edge, child 1 = operator
            System.out.println("entered atom, stack size: "+queryStack.size());
            String edge = ctx.getChild(0).getText();
            String operator;
            if(ctx.getChildCount()==1){
                operator="none";
            }else{
                operator=ctx.getChild(1).getText();
            }
            queryStack.push(edgeQuery(edge,operator));
        }
        @Override
        public void exitSlash(gParser.SlashContext ctx){
            //pop two lists, run concatenation, push single list
            HashSet<String> step2 = queryStack.pop();
            HashSet<String> step1 = queryStack.pop();
            HashSet<String> combinations = new HashSet<>();
            for(String s1 : step1){
                for(String s2 : step2){
                    String[] pairs1 = s1.split(",");
                    String[] pairs2 = s2.split(",");
                    if(pairs1[1].equals(pairs2[0])){
                        String pair = pairs1[0].toString()+","+pairs2[1].toString();
                        combinations.add(pair);
                    }
                }
            }
            queryStack.push(combinations);
            System.out.println("exited slash, stack size: "+queryStack.size());
        }
        @Override
        public void exitBracket(gParser.BracketContext ctx){
            //pop head list, run bracket op, push back
            //for brackets, child 1 = inner edge labels, child 3 = operator
            HashSet<String> inner = queryStack.pop();
            HashSet<String> out = new HashSet<>();
            String operator = ctx.getChild(3).getText();
            switch (operator){
                case "-":
                    for(String s : inner){
                        String swapped = s.split(",")[1]+","+s.split(",")[0];
                        out.add(swapped);
                    }
                    break;
                case "+":
                    //TODO: IMPLEMENT CLOSURE ON BRACKETS
                    break;
            }
            queryStack.push(out);
            System.out.println("exited bracket, stack size: "+queryStack.size());
        }
        @Override
        public void exitOr(gParser.OrContext ctx){
            //pop two lists, combine, push back
            HashSet<String> set1 = queryStack.pop();
            HashSet<String> set2 = queryStack.pop();
            set1.addAll(set2);
            queryStack.push(set1);
            System.out.println("exited or, stack size: "+queryStack.size());
        }
        public HashSet<String> edgeQuery (String edge, String operator){
            HashSet<String> pairs = new HashSet<>();
            Set<Integer> inverts = vertices.keySet();
            for (Integer v : inverts){
                if(operator.equals("none")){
                    for (Edge e : vertices.get(v).getEdges()) {
                        //for each edge, if the label matches the current label in the path, add its destination to the
                        //next iteration of the search
                        if (e.label.equals(edge)) {
                            String pair = (v.toString()+','+e.destinationId.toString());
                            pairs.add(pair);
                        }
                    }
                }else if(operator.equals("-")){
                    for (Integer v2 : vertices.keySet()){
                        for(Edge e : vertices.get(v2).getEdges()){
                            if(e.label.equals(edge)&&e.destinationId.equals(v)){
                                String pair = (v.toString()+','+v2.toString());
                                pairs.add(pair);
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
                        String pair = (v.toString()+','+i.toString());
                        pairs.add(pair);
                    }
                }
            }
            return pairs;
        }
    }
}
