package com.oghrairi.graphdb;

import java.sql.Struct;
import java.util.*;
import java.util.regex.Pattern;

//treating a query as an object which takes as input a string representation of a subgraph as a query,
//and a graph to apply that query to.
//going to just do CQs for now

//Test data: L1(1,2) L2(2,3) L1(1,3)

public class Query {
    private Graph target;
    private Graph queryGraph;
//constructor begins building process. prompts the user, but this can eventually be taken out of this class
    public Query(Graph target) {
        this.target = target;
        //create the query graph
        queryGraph = new Graph("query");
        //scanner object for input
        Scanner scan = new Scanner(System.in);
        /* This part is commented out for now as I'm using premade test data, this will be used for actual human input
        System.out.println("Define edges in form LAB(1,2), separated by spaces");
        String rawEdges = scan.nextLine();
         */
        //take edge input, split into individual edges
        String rawEdges = "L1(1,2) L2(2,3) L1(1,3)";
        String[] edgeTokens = rawEdges.split(" ");
        //hashset of vertices to only get distinct vertices
        HashSet<String> vertices = new HashSet<String>();
        //array of strings, each representing an edge's parameters, to be later assigned to actual edges
        String[] edges = new String[edgeTokens.length];
        //for each input edge, do a rudimentary parse tree-like thing where the input is split into three parts;
        //the edge label, incoming vertex and outgoing vertex
        for (int i = 0; i < edgeTokens.length; i++) {
            String edge = "";
            String token = edgeTokens[i];
            //Pattern.quote() converts string into a literal to ignore special characters like the bracket in use here
            String label = token.split(Pattern.quote("("))[0];
            String tail = token.split(Pattern.quote("("))[1];
            String vert1 = tail.split(",")[0];
            String vert2 = tail.split(",")[1];
            vert2 = vert2.substring(0, vert2.length() - 1);
            vertices.add(vert1);
            vertices.add(vert2);
            //build a string of edge parameters to add edges to graph later on
            edge += label + " " + vert1 + " " + vert2;
            edges[i] = edge;
        }
        for (String name : vertices){
            //for now, vertices that are created have the same id and label
            queryGraph.addVertex(name, Integer.parseInt(name));
        }
        for(String e : edges){
            //take the parameter strings generated earlier, split into the individual param for the edge
            String[] params = e.split(" ");
            //edge label, incoming vertex id, outgoing vertex id
            queryGraph.addEdge(params[0],Integer.parseInt(params[1]),Integer.parseInt(params[2]));
        }
        System.out.println(queryGraph.getEdges());
        System.out.println(queryGraph.getVertices());
    }
}
