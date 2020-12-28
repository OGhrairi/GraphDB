package com.oghrairi.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

//graph stores its name, a hashmap of each Vertex object to its id, and an iterating counter that assigns the ids
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
//vertex id is auto generated, new Vertex object is put into hashmap with id as the key
    public void addVertex(String label){
        int id = vertexIdCounter;
        Vertex v = new Vertex(id, label);
        vertices.put(id,v);
        vertexIdCounter+=1;
    }
//edges are stored in the source vertex object
    public void addEdge(String label, int fromId, int toId){
        //check that vertex keys exist
        if(vertices.containsKey(fromId)&&vertices.containsKey(toId)){
            //add a new edge to the source vertex
            vertices.get(fromId).addEdge(label,toId);
        }
        else{
            System.out.println("Error: a vertex ID entered does not exist");
        }

    }

    public String Query(String label){
        String output = "";
        //iterate through vertices in the hashmap
        for(Integer v : vertices.keySet()){
            //if the vertex has an edge with a matching label, this is true
            for(Edge e : vertices.get(v).getEdges()){
                if(e.label==label) {
                    output+="("+v+"->"+e.destinationId+") ";
                }
            }
        }

        return output;
    }
}

