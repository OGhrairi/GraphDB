package com.oghrairi.graphdb;

//pretty simple class for edges, they're instantiated and stored in arrays within the vertex objects
public class Edge{
    String label;
    Integer destinationId;
    public Edge(String label, Integer destinationId) {
        this.label = label;
        this.destinationId = destinationId;
    }
}
