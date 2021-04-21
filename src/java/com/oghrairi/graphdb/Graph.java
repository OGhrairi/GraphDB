package com.oghrairi.graphdb;
import java.util.*;

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
        Vertex v = new Vertex(label);
        vertices.put(id,v);
        vertexIdCounter+=1;
    }
    public void addVertex(Vertex v){
        int id = vertexIdCounter;
        vertices.put(id,v);
        vertexIdCounter+=1;
    }
    public void addVertex(Vertex v, Integer id){
        vertices.put(id,v);
        vertexIdCounter=vertices.size();
    }
    public void addVertex(String label, HashMap<String,String> properties){
        int id = vertexIdCounter;
        Vertex v = new Vertex(label);
        for(String key : properties.keySet()){
            v.addProperty(key,properties.get(key));
        }
        vertices.put(id,v);
        vertexIdCounter+=1;
    }
    public void deleteVertex(Integer id){
        vertices.remove(id);
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
    public Map<Integer,Vertex> getVertices(){
        return vertices;
    }
    public Vertex getVertexById(int id){
        return vertices.get(id);
    }
    public Integer getEdgeCount(){
        Integer count = 0;
        for(Integer v : vertices.keySet()){
            for(Edge e : vertices.get(v).getEdges()){
                count+=1;
            }
        }
        return count;
    }
}

