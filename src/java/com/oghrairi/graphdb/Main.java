package com.oghrairi.graphdb;

public class Main {

    public static void main(String[] args) {
       /* String gname = "graph1";
        String v1name = "vertex 1";
        String v2name = "vertex 2";
        String e1name = "edge 1";
	    Graph g = new Graph(gname);
	    g.addVertex(v1name);
	    g.addVertex(v2name);
	    g.addEdge(e1name,0,1);
	    System.out.println(g.getVertices());
	    System.out.println(g.getEdges());
	    */
        Graph g = new Graph("graph");
        Query q = new Query(g);
    }
}
