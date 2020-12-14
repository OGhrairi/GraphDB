package com.oghrairi.graphdb;

//treating a query as an object which takes as input a subgraph as the query, and a graph to apply that query to
//going to just do CQs for now
public class Query {
    private Graph subgraph;
    private Graph target;
    public Query(Graph subgraph, Graph target) {
        this.subgraph = subgraph;
        this.target = target;
    }

}
