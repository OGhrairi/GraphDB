package com.oghrairi.graphdb;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Controller {
    //Graph object which can be referenced between scenes
    Graph graph;
    //Each of the links to an fxml object defined in the fxml files
    @FXML
    private Text queryOutputField;
    @FXML
    private TextField queryEntryField;
    @FXML
    private Button homeToQueryButton;
    @FXML
    private Button queryToHomeButton;
    @FXML
    private Button graphToHomeButton;
    @FXML
    private Button homeToGraphButton;
    @FXML
    private Button graphCreateGraphButton;
    @FXML
    private TextField graphCreateGraphField;
    @FXML
    private Text graphCreatePromptText;
    @FXML
    private TextField graphCreateVertexField;
    @FXML
    private Button graphCreateVertexButton;
    @FXML
    private TextField graphCreateEdgeOriginField;
    @FXML
    private TextField graphCreateEdgeDestField;
    @FXML
    private Button graphCreateEdgeButton;
    @FXML
    private Text graphCreateVertexList;
    @FXML
    private Text graphCreateEdgeList;
    @FXML
    private TextField graphCreateEdgeLabel;
    //Method to easily provide error handling alert boxes
    public void alertMaker(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.show();
    }
    //Runs a given regular query
    public void queryRun(ActionEvent e){
        String testString = queryEntryField.getText();
        //current test mode, entering parts of the graph here
        Graph graph = new Graph("Test Graph");
        graph.addVertex("person0");
        graph.addVertex("person1");
        graph.addVertex("person2");
        graph.addVertex("person3");
        graph.addVertex("person4");
        graph.addEdge("knows",0,1);
        graph.addEdge("likes",1,2);
        graph.addEdge("knows",2,0);
        graph.addEdge("knows",0,3);
        graph.addEdge("likes",3,2);
        graph.addEdge("knows",2,1);
        graph.addEdge("knows",3,2);
        graph.addEdge("knows",2,4);
        graph.addEdge("likes",4,0);
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
            ParseTree parseTree = parser.expression();
            //send parse tree to query method to process
            HashSet<String> str = query.RunQuery(parseTree,graph);
            String out = "";
            //build output, either list of pairs or "no matches"
            if(!(str.isEmpty())){
                for(String s : str){
                    int v1 = Integer.parseInt(s.split(",")[0]);
                    int v2 = Integer.parseInt(s.split(",")[1]);
                    out += graph.getVertexById(v1).getLabel()+"->"+graph.getVertexById(v2).getLabel()+"\n";
                }
            }else{
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
    //Creates a new graph with a given graph name
    public void graphMaker(ActionEvent e){
        if(!graphCreateGraphField.getText().isEmpty()) {
            graph = new Graph(graphCreateGraphField.getText());
            graphCreatePromptText.setText("Graph: "+graphCreateGraphField.getText());
            graphCreateGraphField.setDisable(true);
            graphCreateGraphButton.setDisable(true);
            graphCreateVertexButton.setDisable(false);
            graphCreateVertexField.setDisable(false);
        }else{
            alertMaker("Please enter graph name");
        }

    }
    //creates a new vertex in a currently loaded graph with a given vertex name
    public void vertexMaker(ActionEvent e){
        String vName = graphCreateVertexField.getText();
        if(!vName.isEmpty()){
            graph.addVertex(vName);
        }else{
            alertMaker("Please enter vertex label");
        }
        Map<Integer,Vertex> vList = graph.getVertices();
        String vertices = "";
        for(Integer key : vList.keySet()){
            vertices += "ID: "+key+", Label: "+vList.get(key).getLabel()+"\n";
        }
        graphCreateVertexList.setText(vertices);
        graphCreateVertexField.clear();
        graphCreateEdgeButton.setDisable(false);
        graphCreateEdgeOriginField.setDisable(false);
        graphCreateEdgeDestField.setDisable(false);
        graphCreateEdgeLabel.setDisable(false);

    }
    //creates a new edge in a currently loaded graph, given origin and destination vertex ids plus edge label
    public void edgeMaker(ActionEvent e){
        String origin = graphCreateEdgeOriginField.getText();
        String destination = graphCreateEdgeDestField.getText();
        String edgeLabel = graphCreateEdgeLabel.getText();
        if(!origin.isEmpty()&&!destination.isEmpty()){
            Integer or = Integer.parseInt(origin);
            Integer des = Integer.parseInt(destination);
            if(graph.getVertices().containsKey(or)){
                if(graph.getVertices().containsKey(des)){
                    if(!edgeLabel.isEmpty()){
                        graph.addEdge(edgeLabel,or,des);
                    }else{
                        alertMaker("Please enter an edge label");
                    }
                }else{
                    alertMaker("Invalid destination ID");
                }
            }else{
                alertMaker("Invalid drigin ID");
            }
        }
        else{
            alertMaker("Please enter origin and destination IDs");
        }
        Map<Integer,Vertex> vList = graph.getVertices();
        String edgeList = "";
        for(Integer id : vList.keySet()){
            Vertex v = vList.get(id);
            List<Edge> edges = v.getEdges();
            for(Edge edge : edges){
                edgeList+=id+"->"+edge.label+"->"+edge.destinationId+"\n";
            }
        }
        graphCreateEdgeList.setText(edgeList);
        graphCreateEdgeOriginField.clear();
        graphCreateEdgeDestField.clear();
        graphCreateEdgeLabel.clear();
    }

    //these methods are called when navigating between scenes
    public void homeToQuery(ActionEvent e){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("queryScreen.fxml"));
            homeToQueryButton.getScene().setRoot(root);

        }
        catch (Exception x){
            x.printStackTrace();
        }

    }
    public void queryToHome(ActionEvent e){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            queryToHomeButton.getScene().setRoot(root);

        }
        catch (Exception x){
            x.printStackTrace();
        }
    }
    public void graphToHome(ActionEvent e){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            graphToHomeButton.getScene().setRoot(root);

        }
        catch (Exception x){
            x.printStackTrace();
        }
    }
    public void homeToGraph(ActionEvent e){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("graphCreator.fxml"));
            homeToGraphButton.getScene().setRoot(root);

        }
        catch (Exception x){
            x.printStackTrace();
        }
    }
}
