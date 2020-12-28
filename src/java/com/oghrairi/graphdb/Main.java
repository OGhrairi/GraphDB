package com.oghrairi.graphdb;

public class Main {

    public static void main(String[] args) {

        Graph test1 = new Graph("testGraph");
        System.out.println(test1.getGraphName());
        test1.addVertex("person");
        test1.addVertex("person");
        test1.addVertex("person");
        test1.addEdge("knows",0,1);
        test1.addEdge("likes",0,2);
        test1.addEdge("knows",2,1);
        System.out.println(test1.Query("knows"));
        System.out.println(test1.Query("likes"));
        System.out.println(test1.Query("loves"));
    }
}
