package com.oghrairi.graphdb;
import java.util.*;

 /*calls to this class should be independent of the graph representation
    Methods need to be able to create a graph object, either empty, with some input node(s)/edge(s), or from a
    storage file (I/O operations)
    also, need to be able to query a graph and return some representation of the result (probably start with a boolean exists? query)
    Graph properties which need representing:
        -Edge, vertex Ids
        -Edge, vertex Labels
        -Edge, vertex property key/value pairs
        -Topology; in the form of an adjacency matrix, use edge id instead of 1/0
     */




public class Graph {
    //at each step of a search, need to see label of outgoing edge + vertex reached from that edge
    private Set<Vertex> vertices;
    private Set<Edge> edges;
    private List<Integer> vertexList;
    private List<Integer> edgeList;
    private String name;
    public Graph(String graphName) {
        //create graph object with graph name, instantiate variables
        vertices = new HashSet<Vertex>();
        edges = new HashSet<Edge>();
        name = graphName;
        vertexList = new ArrayList<Integer>();
        edgeList = new ArrayList<Integer>();
    }

    private class Vertex{
        public String label;
        public Set<Integer> outEdges;
        public Integer id;
        public Vertex(String vertexLabel, Integer id){
            this.label = vertexLabel;
            this.id = id;
        }
    }
    private class Edge{
        public String label;
        public Integer source;
        public Integer destination;
        public Integer id;
        public Edge(String edgeLabel, Integer source, Integer destination, Integer id){
            this.label=edgeLabel;
            this.source = source;
            this.destination = destination;
            this.id = id;
        }
    }

    public String getName() {
        return name;
    }
    public Integer[] vertexCount() {
        Integer[] count = new Integer[2];
        count[0] = vertexList.size();
        count[1] = vertices.size();
        return count;
    }
    public Integer[] edgeCount() {
        Integer[] count = new Integer[2];
        count[0] = edgeList.size();
        count[1] = edges.size();
        return count;
    }
    public void addVertex(String label){
        Integer id;
        if(vertexList.size() > 0){
            id = Collections.max(vertexList) + 1;
        }else{
            id = 0;
        }
        vertices.add(new Vertex(label,id));
        vertexList.add(id);
    }
    public void addEdge(String label, Integer source, Integer destination){
        Integer id;
        if(edgeList.size() > 0){
            id = Collections.max(edgeList) + 1;
        }else{
            id = 0;
        }
        boolean in = false;
        boolean out = false;
        for(Integer i : vertexList){
            if(i == source){
                in = true;
            }
            if(i ==destination){
                out = true;
            }
        }
        if(in && out){
            edges.add(new Edge(label, source, destination, id));
            edgeList.add(id);
        }
    }
    public String getVertices(){
        String out = "VERTEX LIST:\n-----------------\n";
        for(Vertex v : vertices){
            out +=  "ID: ";
            out += v.id;
            out += "    LABEL: ";
            out += v.label;
            out += "\n-----------------\n";
        }
        return out;
    }
    public String getEdges(){
        String out = "EDGE LIST:\n-----------------\n";
        for(Edge e : edges){
            out += "ID: ";
            out += e.id;
            out += "    LABEL: ";
            out += e.label;
            out += "\nPATH: ";
            out += e.source + " ------> " + e.destination;
        }
        return out;
    }
}
