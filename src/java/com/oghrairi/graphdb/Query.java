package com.oghrairi.graphdb;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.util.*;

public class Query {
    private Graph graph;
    private HashSet<String> outputEdges;
    private Map<Integer, Vertex> vertices;;
    public Query() { outputEdges = new HashSet<>(); }
    public HashSet<String> RunQuery(ParseTree parseTree, Graph graph){
        //This is the method that runs the query, outputs a hashset of vertex pairs stored as a string
        this.graph = graph;
        this.vertices = graph.getVertices();
        QueryListener queryListener = new QueryListener();
        ParseTreeWalker.DEFAULT.walk(queryListener, parseTree);
        outputEdges = queryListener.getOutput();
        return outputEdges;
    }
    //subclass which inherits from Antlr base listener class, walks through the generated parse tree and runs certain
    //functions at different classes of nodes
    public class QueryListener extends gBaseListener{
        Stack<HashSet<String>> queryStack;
        HashSet<String> regOutput;
        ArrayList<String> outputVariables;
        HashSet<String> boundVars;
        int cexpCounter;
        public QueryListener(){
            queryStack = new Stack<>();
            regOutput = new HashSet<>();
            outputVariables = new ArrayList<>();
            cexpCounter = 0;
            boundVars = new HashSet<>();
        }
        public HashSet<String> getOutput(){
            return queryStack.get(0);
        }
        //atoms are the individual edge labels in a query
        @Override
        public void enterAtom(gParser.AtomContext ctx){
            //run atom search, push to stack
            //for atoms, child 0 = edge, child 1 = operator
            String edge = ctx.getChild(0).getText();
            String operator;
            //check if atom has an operator (i.e. if the node has >1 child)
            if(ctx.getChildCount()==1){
                operator="none";
            }else{
                operator=ctx.getChild(1).getText();
            }
            //run the edgeQuery function, taking the edge and operator (if one is present) as argument, push output to stack
            queryStack.push(edgeQuery(edge,operator));
        }
        //slash is the concatenation operator between atoms
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
        }
        //brackets allows for operators on concatenated paths
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
                    /*
                    Procedure:
                    Iterates through paths inside the bracket, and joins them together in the same way as
                    the slash operator does
                    Main difference is that it repeats this some number of times, and the results at each repetition
                    are added to the output
                    Currently, the number of repetitions = number of edges on the graph
                     */
                    out.addAll(inner);
                    int edgeCount = graph.getEdgeCount();
                    for(int i=0; i<edgeCount; i++){
                        HashSet<String> runner = new HashSet<>();
                        runner.addAll(inner);
                        for(String s1 : runner){
                            for(String s2 : runner){
                                String[] pairs = new String[4];
                                pairs[0] = s1.split(",")[0];
                                pairs[1] = s1.split(",")[1];
                                pairs[2] = s2.split(",")[0];
                                pairs[3] = s2.split(",")[1];
                                if(pairs[1].equals(pairs[2])){
                                    String newPair = pairs[0]+","+pairs[3];
                                    out.add(newPair);
                                }
                            }
                        }
                    }
                    break;
            }
            queryStack.push(out);
        }
        //the OR pipe is pretty self explanatory; unions together two subquery outputs
        @Override
        public void exitOr(gParser.OrContext ctx){
            //pop two lists, combine, push back
            HashSet<String> set1 = queryStack.pop();
            HashSet<String> set2 = queryStack.pop();
            set1.addAll(set2);
            queryStack.push(set1);
        }
        //Method that runs the individual edge queries and returns a set of vertex pairs that are connected by the edge
        //includes handling for closure and reverse operators
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
