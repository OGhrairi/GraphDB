package com.oghrairi.graphdb;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.text.ParseException;
import java.util.HashSet;

public class Controller {
    @FXML
    private Text queryOutputField;
    @FXML
    private TextField queryEntryField;
    //method that runs on button click, takes entered query and outputs matches
    public void queryRun(ActionEvent e){
        //String testString = ("(a,b)<-[likes](a,b)");
        String testString = queryEntryField.getText();
        //current test mode, entering parts of the graph here
        Graph test1 = new Graph("testGraph");
        test1.addVertex("person0");
        test1.addVertex("person1");
        test1.addVertex("person2");
        test1.addVertex("person3");
        test1.addVertex("person4");
        test1.addEdge("knows",0,1);
        test1.addEdge("likes",1,2);
        test1.addEdge("knows",2,0);
        test1.addEdge("knows",0,3);
        test1.addEdge("likes",3,2);
        test1.addEdge("knows",2,1);
        test1.addEdge("knows",3,2);
        test1.addEdge("knows",2,4);
        test1.addEdge("likes",4,0);
        //instantiate a new query
        Query query = new Query();
        //begin query processing pipeline
        //step 1: convert query string to a charstream
        CharStream stream = CharStreams.fromString(testString);
        //step 2: create lexer object with charstream input
        gLexer gl = new gLexer(stream);
        //replace default error handling of lexer
        gl.removeErrorListeners();
        gl.addErrorListener(ThrowingErrorListener.INSTANCE);
        //step 3: get token stream from lexer
        CommonTokenStream tokens = new CommonTokenStream(gl);
        //step 4: create parser object with token stream
        gParser parser = new gParser(tokens);
        //replace default error handling of parser
        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);
        //step 5: attempt to parse the token stream, with try/catch for error handling
        try{
            //build parse tree
            ParseTree parseTree = parser.crpq();
            //send parse tree to query method to process
            HashSet<String> str = query.RunQuery(parseTree,test1);
            String out = "";
            //build output, either list of pairs or "no matches"
            if(!(str==null)){
                for(String s : str){
                    int v1 = Integer.parseInt(s.split(",")[0]);
                    int v2 = Integer.parseInt(s.split(",")[1]);
                    out += test1.getVertexById(v1).getLabel()+"->"+test1.getVertexById(v2).getLabel();
                    System.out.println(test1.getVertexById(v1).getLabel()+"->"+test1.getVertexById(v2).getLabel());

                }
            }else{
                System.out.println("No Matches");
                out = "No Matches";
            }
            //set output field to query output
            queryOutputField.setText(out);
        }
        //if parse error, set output message in text field
        catch (ParseCancellationException p){
            queryOutputField.setText(p.getMessage());
        }

    }
}
