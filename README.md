# GraphDB - Final Year Project
Broadly speaking, this project is an implementation of a graph database system. My MVP aim is to implement either conjunctive or regular path queries on a directed, labelled graph, and return only existential information about the query.

## Current Progress
- v2 of graph representation - object oriented model which takes inspiration from linked lists in storing edge and vertex relationships. Not full property graph model as of yet
- v2 of query implementation - fairly naive search methods based on edge labels, can handle single edges, transitive closures, edge reversals, and can concatenate a mixture of those results into a full path, which then displays all pairs that are joined by said path
- v1.5 of query handling - An ANTLR grammar has been built to generate a parse tree for input queries. A listener object then walks through that tree, and at certain nodes will perform query operations. Currently this can handle any given RPQ (with the exception of applying a transitive closure to a bracketed subexpression), and will further be able to handle a CRPQ structure.





Post MVP Features:

- CRPQ (conjunctive regular path query) functionality
- full property graph model
- persistent file storage for graphs
- code optimisations
