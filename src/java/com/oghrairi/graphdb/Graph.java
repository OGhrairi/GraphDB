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
    public Map<Integer,Vertex> getVertices(){
        return vertices;
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

    //WIP class for proper parsing
    public void q2(String queryPath){
        StringTokenizer queryTokens = new StringTokenizer(queryPath,"()/-+#",true);
        while(queryTokens.hasMoreTokens()){
            System.out.println(queryTokens.nextToken());
        }
    }
    public String cq(String query){
        String output = "";
        String[] rpqs = query.split(" ");
        for(String str : rpqs){
            String rpq = str.split("\\(")[0];
            output += query(rpq)+"|| ";
        }
        return output;
    }
//query iteration number two, can handle concatenation of edge labels into a path query
    public String query(String queryPath){
        System.out.println("-------------------");
        //very basic input parsing, a sequence of labels, separated by a slash. this is split into an array of labels
        String[] path = queryPath.split("/");
        String output = "";
        //an output list of matching pairs will be populated. it's a hashset as I'm going for set semantics here, so no dupes
        HashSet<String> outList = new HashSet<>();
        //the query essentially does a kind of bfs, and repeats for every vertex in the graph, taking each as the start vertex of the search
        for(Integer v : vertices.keySet()){
            //two arraylists here which essentially keep track of which vertices the search is currently passing through
            ArrayList<Integer> current = new ArrayList<>();
            current.add(v);
            ArrayList<Integer> next = new ArrayList<>();
            //In my head this runs a bit like an automata; it runs through the input word (i.e. the label array)
            //and at each step it records every step that can be reached by applying the label as a transition, then repeats.
            //basically, it's a fairly dumb exhaustive search of an NFA
            for(String w : path){
                //at each step of the path, look at every vertex(or state if you want to look at is as the automata)
                //that the search is currently in, and iterate through them
                for(Integer vert : current) {
                    //if label has a '+' in it, this represents the transitive closure of the label.
                    //first implementation here basically builds a new path filled with the label n times, where n is
                    //the total number of edges in the graph currently, and then runs through that path in the same
                    //way as the parent path query, but instead of returning only the end vertices, returns all vertices
                    //that were reachable along the way
                    if(w.contains("+")){
                        String sub = w.substring(0,w.length()-1);
                        ArrayList<Integer> reachable = new ArrayList<>();
                        ArrayList<Integer> l1 = new ArrayList<>();
                        ArrayList<Integer> l2 = new ArrayList<>();
                        l1.add(vert);
                        for(int i=0; i<getEdgeCount(); i++){
                            for(Integer v2 : l1){
                                for(Edge e : vertices.get(v2).getEdges()){
                                    if(e.label.equals(sub)){
                                        l2.add(e.destinationId);
                                        reachable.add(e.destinationId);
                                    }
                                }
                            }
                            l1.clear();
                            l1.addAll(l2);
                            l2.clear();
                        }
                        next.addAll(reachable);
                    }
                    //if the label has a '-' in it, this is an inverse label. instead of looking at edges leading out
                    //from current vertices, look at the rest of the graph and find edges going into the current vertices
                    if(w.contains("-")){
                        for (Integer v2 : vertices.keySet()){
                            for(Edge e : vertices.get(v2).getEdges()){
                                if(e.label.equals(w.substring(0,w.length()-1))&&e.destinationId.equals(vert)){
                                    next.add(v2);
                                }
                            }
                        }
                    }

                    else{
                        //for each current state, iterate through its edges
                        for (Edge e : vertices.get(vert).getEdges()) {
                            //for each edge, if the label matches the current label in the path, add its destination to the
                            //next iteration of the search
                            if (e.label.equals(w)) {
                                next.add(e.destinationId);
                            }
                        }
                    }
                }

                current.clear();
                current.addAll(next);
                next.clear();
            }
            //once the search has finished for each start vertex, add each pair from the start vertex to the
            //end vertices to the output list
            for(Integer i : current){
                String pair = "("+v+"->"+i+")";
                outList.add(pair);

            }
        }
        for(String s : outList){
            output += s;
            output += " ";
        }
        return output;
    }


/* this is the first iteration, a simple query for single edges
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
 */
}

