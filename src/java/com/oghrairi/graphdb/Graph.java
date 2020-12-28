package com.oghrairi.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Graph {
    private String graphName;
    private Map<Integer,Vertex> vertices;
    private int vertexIdCounter;

    public Graph(String graphName) {
        this.graphName = graphName;
        vertices = new HashMap<>();
        vertexIdCounter=0;
    }

    public String getGraphName() {
        return graphName;
    }

    public void addVertex(String label){
        int id = vertexIdCounter;
        Vertex v = new Vertex(id, label);
        vertices.put(id,v);
        vertexIdCounter+=1;
    }
    public void addEdge(String label, int fromId, int toId){
        if(vertices.containsKey(fromId)&&vertices.containsKey(toId)){
            vertices.get(fromId).addEdge(label,toId);
        }
        else{
            System.out.println("Error: a vertex ID entered does not exist");
        }

    }

    public String Query(String label){
        String output = "";
        boolean match = false;
        for(Vertex v : vertices.values()){
            if(v.getEdges().containsKey(label)){
                match = true;
                break;
            }
        }
        if (match){
            output = "match found";
        }else{
            output = "no match found";
        }
        return output;
    }
}

