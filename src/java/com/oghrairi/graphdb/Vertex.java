package com.oghrairi.graphdb;

import java.util.*;

public class Vertex {
    //vertex stores its label, plus a list of outbound edges
    private String label;
    private List<Edge> edges;

    public Vertex(int id, String label) {
        this.label = label;
        edges = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }
    public List<Edge> getEdges() {
        return edges;
    }
    public void addEdge(String label, int toId){
        Edge e = new Edge(label,toId);
        edges.add(e);
    }

}
