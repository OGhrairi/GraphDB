package com.oghrairi.graphdb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Vertex {
    private String label;
    private Map<String,Integer> edges;

    public Vertex(int id, String label) {
        this.label = label;
        edges = new HashMap<>();
    }

    public String getLabel() {
        return label;
    }
    public Map<String, Integer> getEdges() {
        return edges;
    }
    public void addEdge(String label, int toId){
        edges.put(label,toId);
    }

}
