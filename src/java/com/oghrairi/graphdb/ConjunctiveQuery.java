package com.oghrairi.graphdb;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.util.*;


public class ConjunctiveQuery {
    private String queryString;
    private Graph graph;
    private HashSet<String> outputEdges;
    private Map<Integer, Vertex> vertices;
    public ConjunctiveQuery() {
        outputEdges = new HashSet<>();
    }
    public HashSet<String> RunQuery(ParseTree parseTree, Graph graph){
        //This is the method that runs the query, outputs a hashset of vertex pairs stored as a string
        this.queryString = queryString;
        this.graph = graph;
        this.vertices =graph.getVertices();
        //these lines are some code needed to generate and use the parse tree;
        //convert query string into a charstream, generate a lexer from the lexer class created from my grammar with
        //the charstream as an input, take the tokens from that lexer into a tokenstream, feed that stream into a parser
        //then use a tree walker to step through the generated parse tree
        ParseTreeWalker.DEFAULT.walk(new QueryListener(), parseTree);
        return outputEdges;
    }
    //subclass which inherits from Antlr base listener class, walks through the generated parse tree and runs certain
    //functions at different classes of nodes
    public class QueryListener extends gBaseListener{
        Stack<HashSet<String>> queryStack;
        HashSet<String> regOutput;
        String[] conjunctiveVariableArray;
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
//        @Override
//        public void enterCrpq(gParser.CrpqContext ctx){
//            int expCount=0;
//            //get count of number of regular expressions in this query
//            for(int i=0; i<ctx.getChildCount(); i++){
//                if (ctx.getChild(i).getClass().toString().contains("Cexpression")){
//                    expCount+=1;
//                }
//            }
//            conjunctiveVariableArray = new String[expCount];
//        }
//        @Override
//        public void enterVariables(gParser.VariablesContext ctx){
//            //output variables stored at children index 1,3,5 etc
//            for(int i=0; i<ctx.getChildCount()-1; i++){
//                if(i%2!=0){
//                    outputVariables.add(ctx.getChild(i).getText());
//                }
//            }
//        }
        public void conjunctionMatcher(){
            Map<String,String> mapper = new HashMap<>();
            //a,b|c,d|x,y|
            for(int i=0;i<cexpCounter;i++){
                String[] pairs = conjunctiveVariableArray[i].split("\\|");
                int pairCount = pairs.length;
                String[] bound = conjunctiveVariableArray[pairCount].split(",");
                for(int j=0;j<pairCount-1;j++){

                }
            }

        }
        //exitCrpq is the top level of the query, including all regular queries and bound variables
//        @Override
//        public void exitCrpq(gParser.CrpqContext ctx){
//            String v = "";
//            v = ctx.getChild(0).getText().split("\\)")[0].substring(1);
//            String[] vars = v.split(",");
//            //check that all output variables have been bound
//            boolean varsFound = true;
//            for(String var : outputVariables){
//                if(!boundVars.contains(var)){
//                    varsFound = false;
//                }
//            }
//            if(varsFound){
//                //conjunctionMatcher();
//            }else{
//                System.out.println("VARIABLE(S) NOT BOUND");
//            }
//            //this section is placeholder, to be removed
//            for(String s : conjunctiveVariableArray){
//                String[] p1 = s.split("\\|");
//                int l = p1.length;
//                for(int i=0;i<l-1;i++){
//                    outputEdges.add(p1[i]);
//                }
//            }
//            /*
//            conjunction Procedure:
//            attach variable labels to each pairs list
//            compare to desired bound variables defined at the start of the query
//            return bound variables that match
//            IDEAS:
//            map values to variables in each rpq
//            once mapped, try combinations for ones that align
//             */
//        }
        //cexpression is the root of each regular query expression within the overall conjunctive query, including the
        //regular query plus its bound variables
//        @Override
//        public void enterCexpression(gParser.CexpressionContext ctx){
//            queryStack.clear();
//
//        }
//        @Override
//        public void exitCexpression(gParser.CexpressionContext ctx){
//            //child 1 = expression, child 4 = var 1, then every other child = the next var
//            //pop single pairs list as output
//            int cc = ctx.getChildCount();
//            int varCount = (cc-4)/2;
//            //builds an output string for each expression, of form |a,b|c,d|#x,y
//            //where a,b and c,d are pairs matching the regular query, and x,y are the bound variables for the regular expression
//            if(!queryStack.empty()){
//                String proto = "";
//                for(String s : queryStack.pop()){
//                    proto+=s+"|";
//                }
//                for(int i=0; i<varCount; i++){
//                    String b = ctx.getChild(2*(i+1)+2).getText();
//                    proto+=b;
//                    boundVars.add(b);
//                    if(i<varCount-1){
//                        proto+=",";
//                    }
//                }
//                conjunctiveVariableArray[cexpCounter]=proto;
//                cexpCounter+=1;
//            }
//
//        }
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
            System.out.println("exited slash, stack size: "+queryStack.size());
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
            System.out.println("exited bracket, stack size: "+queryStack.size());
        }
        //the OR pipe is pretty self explanatory; unions together two subquery outputs
        @Override
        public void exitOr(gParser.OrContext ctx){
            //pop two lists, combine, push back
            HashSet<String> set1 = queryStack.pop();
            HashSet<String> set2 = queryStack.pop();
            set1.addAll(set2);
            queryStack.push(set1);
            System.out.println("exited or, stack size: "+queryStack.size());
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
