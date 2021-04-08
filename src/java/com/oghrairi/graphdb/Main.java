package com.oghrairi.graphdb;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) {
        //String testString = ("(x1,x2)<-[(knows/likes+)/knows-|(likes/loves)+](x1,x2)");
        String testString = ("(x1,x2,x3)<-[likes](x1,x2),[knows](x2,x3)");
        Graph test1 = new Graph("testGraph");
        test1.addVertex("person");
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
        test1.addEdge("knows",3,2);
        test1.addEdge("knows",2,4);
        test1.addEdge("likes",4,0);
        Query query = new Query();
        HashSet<String> str = query.RunQuery(testString,test1);
        if(!(str==null)){
            for(String s : str){
                System.out.println(s);
            }
        }else{
            System.out.println("No Matches");
        }



        /*
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
        System.out.println(test1.query("knows")); //should return 0->1, 2->0, 2->1, 0->3
        System.out.println(test1.query("likes")); //should return 1->2, 3->2
        System.out.println(test1.query("loves")); //should return empty
        System.out.println(test1.query("likes/knows")); //should return 1->1, 1->0, 3->0, 3->1
        System.out.println(test1.query("knows/likes/knows")); //should return 0->1, 0->0, 2->0, 2->1
        System.out.println(test1.query("knows-")); //should return 1->0, 3->0, 0->2, 1->2
        System.out.println(test1.query("knows/knows-")); //should return 2->0, 2->2, 0->0, 0->2
        System.out.println(test1.query("knows+")); //should return 2->0, 0->3, 2->1, 2->3, 0->1
        System.out.println(test1.query("likes/knows+/likes")); //returns 3->2 1->2
        //test1.q2("((knows)-/likes)+/knows#knows");
        System.out.println(test1.cq("likes/knows(x,a1) knows-(a1,y)"));
    */
    }

}
