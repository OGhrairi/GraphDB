package com.oghrairi.graphdb;

public class Main {

    public static void main(String[] args) {

        Graph test1 = new Graph("testGraph");
        System.out.println(test1.getGraphName());
        test1.addVertex("person");
        test1.addVertex("person");
        test1.addVertex("person");
        test1.addVertex("person");
        test1.addEdge("knows",0,1);
        test1.addEdge("likes",1,2);
        test1.addEdge("knows",2,0);
        test1.addEdge("knows",0,3);
        test1.addEdge("likes",3,2);
        test1.addEdge("knows",2,1);
        System.out.println(test1.Query("knows")); //should return 0->1, 2->0, 2->1, 0->3
        System.out.println(test1.Query("likes")); //should return 1->2, 3->2
        System.out.println(test1.Query("loves")); //should return empty
        System.out.println(test1.Query("likes/knows")); //should return 1->1, 1->0, 3->0, 3->1
        System.out.println(test1.Query("knows/likes/knows")); //should return 0->1, 0->0, 2->0, 2->1
        System.out.println(test1.Query("knows-")); //should return 1->0, 3->0, 0->2, 1->2
        System.out.println(test1.Query("knows/knows-")); //should return 2->0, 2->2, 0->0, 0->2
    }
}
