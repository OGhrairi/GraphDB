package com.oghrairi.graphdb;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.*;

public class Controller {
    //Graph object which can be referenced between scenes
    Graph graph;
    HashMap<String,String> aFilter;
    HashMap<String,String> bFilter;
    Vertex newVertex;
    //Each of the links to an fxml object defined in the fxml files
    @FXML
    private AnchorPane queryScreenAnchor;
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
    @FXML
    private TextField propertyFilterName;
    @FXML
    private TextField propertyFilterValue;
    @FXML
    private Text filterList;
    @FXML
    private TextField graphCreateVertexPropertyNameField;
    @FXML
    private TextField graphCreateVertexPropertyValueField;
    @FXML
    private Button graphCreateVertexLabelButton;
    @FXML
    private Button graphCreateVertexPropertyButton;
    @FXML
    private Button graphCreateSaveButton;
    //Method to easily provide error handling alert boxes
    public Controller(){
        aFilter = new HashMap<>();
        bFilter = new HashMap<>();
    }
    public void alertMaker(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.show();
    }
    //Runs a given regular query
    public void queryRun(ActionEvent e){
        String testString = queryEntryField.getText();
        //current test mode, entering parts of the graph here
//        Graph graph = new Graph("Test Graph");
//        HashMap<String,String> testProperties = new HashMap<>();
//        testProperties.put("property1","value1");
//        graph.addVertex("person0",testProperties);
//        graph.addVertex("person1",testProperties);
//        graph.addVertex("person2");
//        graph.addVertex("person3");
//        graph.addVertex("person4");
//        Map<Integer,Vertex> v = graph.getVertices();
//        graph.addEdge("knows",0,1);
//        graph.addEdge("likes",1,2);
//        graph.addEdge("knows",2,0);
//        graph.addEdge("knows",0,3);
//        graph.addEdge("likes",3,2);
//        graph.addEdge("knows",2,1);
//        graph.addEdge("knows",3,2);
//        graph.addEdge("knows",2,4);
//        graph.addEdge("likes",4,0);
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
                    Vertex vertexA = graph.getVertexById(v1);
                    Vertex vertexB = graph.getVertexById(v2);
                    boolean fits = true;
                    fits = isFits(vertexA, fits, aFilter);
                    fits = isFits(vertexB, fits, bFilter);
                    if(fits){
                        out += graph.getVertexById(v1).getLabel()+"->"+graph.getVertexById(v2).getLabel()+"\n";
                    }
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
    private boolean isFits(Vertex vertexA, boolean fits, HashMap<String, String> filter) {
        if(!filter.isEmpty()){
            for(String property : filter.keySet()){
                if(!vertexA.getProperties().containsKey(property)){
                    fits = false;
                }else if(!vertexA.getProperties().get(property).equals(filter.get(property))){
                    fits = false;
                }
            }
        }
        return fits;
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
            graphCreateVertexPropertyNameField.setDisable(false);
            graphCreateVertexPropertyValueField.setDisable(false);
            graphCreateVertexLabelButton.setDisable(false);
            graphCreateVertexPropertyButton.setDisable(false);
            graphCreateSaveButton.setDisable(false);
        }else{
            alertMaker("Please enter graph name");
        }

    }
    //creates a new vertex in a currently loaded graph with a given vertex name
    public void vertexMaker(ActionEvent e){
        String vName = graphCreateVertexField.getText();
        if(!vName.isEmpty()){
            newVertex = new Vertex(vName);
        }else{
            alertMaker("Please enter vertex label");
        }

        graphCreateVertexField.clear();
        graphCreateEdgeButton.setDisable(false);
        graphCreateEdgeOriginField.setDisable(false);
        graphCreateEdgeDestField.setDisable(false);
        graphCreateEdgeLabel.setDisable(false);
        graphCreateVertexButton.setDisable(false);
        graphCreateVertexPropertyNameField.setDisable(false);
        graphCreateVertexPropertyValueField.setDisable(false);
        graphCreateVertexPropertyButton.setDisable(false);
    }
    public void addProperty(ActionEvent e){
        if(graphCreateVertexPropertyNameField.getText().isEmpty()|graphCreateVertexPropertyValueField.getText().isEmpty()){
            alertMaker("Enter property name and value");
        }else{
            newVertex.addProperty(graphCreateVertexPropertyNameField.getText(),graphCreateVertexPropertyValueField.getText());
            System.out.println(newVertex.getProperties().size());
            graphCreateVertexPropertyNameField.clear();
            graphCreateVertexPropertyValueField.clear();
        }
    }
    public void saveVertex(ActionEvent e){
        graph.addVertex(newVertex);
        String vertices = "";
        Map<Integer,Vertex> vList = graph.getVertices();
        for(Integer key : vList.keySet()){
            vertices += "ID: "+key+", Label: "+vList.get(key).getLabel()+"\n";
        }
        graphCreateVertexList.setText(vertices);
        graphCreateVertexField.clear();
        graphCreateVertexButton.setDisable(true);
        graphCreateVertexPropertyNameField.setDisable(true);
        graphCreateVertexPropertyValueField.setDisable(true);
        graphCreateVertexPropertyButton.setDisable(true);
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
    //Adds property filters to a query output
    public void addAFilter(ActionEvent e){
        if(!propertyFilterName.getText().isEmpty()&&!propertyFilterValue.getText().isEmpty()){
            aFilter.put(propertyFilterName.getText(),propertyFilterValue.getText());
            filterListPopulate();
        }else{
            alertMaker("Please enter property name and value to add a filter");
        }
    }
    public void filterListPopulate(){
        String list = "";
        for(String name : aFilter.keySet()){
            list += "\nA   "+name+"   "+aFilter.get(name);
        }
        for(String name : bFilter.keySet()){
            list += "\nB   "+name+"   "+bFilter.get(name);
        }
        filterList.setText(list);
    }
    public void addBFilter(ActionEvent e){
        if(!propertyFilterName.getText().isEmpty()&&!propertyFilterValue.getText().isEmpty()){
            bFilter.put(propertyFilterName.getText(),propertyFilterValue.getText());
            filterListPopulate();
        }else{
            alertMaker("Please enter property name and value to add a filter");
        }
    }
    public void saveGraph(ActionEvent e){
        if(Objects.isNull(graph)){
            alertMaker("Please create a graph before saving");
        }else{
            /*
            Need to store: graph name, vertices - (vertex id, vertex label, vertex properties, edges from vertex, edge labels)
            split into: graphName|vertexId#vertexLabel#prop1Name/prop1Value,prop2Name/prop2Value#edge1Label/edge1Destinationid,edge2Label/edge2Destinationid|NEXTVERTEX
            reserved characters for graph name, vertex and edge fields : | , # /
             */
            String graphName = "C:\\Users\\Omarg\\Desktop\\"+graph.getGraphName()+".txt";
            Map<Integer,Vertex> vertices = graph.getVertices();
            System.out.println(graph.getEdgeCount());
            String graphString = "";
            boolean isFirstVert = true;
            for(Integer id : vertices.keySet()){
                Vertex vertex = vertices.get(id);
                String vString = "";
                if(isFirstVert){
                    isFirstVert=false;
                }else{
                    vString+="|";
                }
                vString += Integer.toString(id)+"#";
                vString += vertex.getLabel()+"#";
                boolean isFirstProp=true;
                for(String name : vertex.getProperties().keySet()){
                    if(isFirstProp){
                        isFirstProp=false;
                    }else{
                        vString+=",";
                    }
                    vString += name+"/"+vertex.getProperties().get(name);
                }
                vString += "#";
                for(Edge edge : vertex.getEdges()){
                    vString += edge.label+"/"+Integer.toString(edge.destinationId);
                }
                graphString+= vString;
            }
            System.out.println(graphString);
            try{
                File file = new File(graphName);
                FileWriter writer = new FileWriter(file);
                writer.write(graphString);
                writer.close();
                if(file.createNewFile()){
                    System.out.println("file created");
                }else{
                    System.out.println("File already exists.");
                }

            }catch (IOException x){
                System.out.println("IOException in file creation");
                x.printStackTrace();
            }

        }


    }
    public void loadGraph(ActionEvent e){
        //split into: vertexId#vertexLabel#prop1Name/prop1Value,prop2Name/prop2Value#edge1Label/edge1Destinationid,edge2Label/edge2Destinationid|NEXTVERTEX
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load graph txt file");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(queryScreenAnchor.getScene().getWindow());
        String graphName = file.getName().split(".txt")[0];
        graph = new Graph(graphName);
        try{
            Scanner reader = new Scanner(file);
            String graphString = reader.nextLine();
            //Highest level of text file, each vertex representation is split by a '|'
            String[] vertexStringArray = graphString.split("\\|");
            for(String vertexString : vertexStringArray){
                //Within each vertex representation, each field is split by a '#'
                String[] vertexParts = vertexString.split("#");
                for(String v : vertexParts){
                    System.out.println(v);
                }
                int vertexId = Integer.parseInt(vertexParts[0]);
                String vertexLabel = vertexParts[1];
                Vertex vertex = new Vertex(vertexLabel);
                //The third field is a list of properties, delimited by a ','
                String[] propertyArray = vertexParts[2].split(",");
                for(String s : propertyArray){
                    //for each property, name and value are delimited by '/'
                    String name = s.split("/")[0];
                    String val = s.split("/")[1];
                    vertex.addProperty(name,val);
                }
                //the fourth and final field is a list of edges, delimited by a ','
                String[] edgeArray = vertexParts[3].split(",");
                for(String s : edgeArray){
                    //for each edge, label and destination id are delimited by a '/'
                    String label = s.split("/")[0];
                    int destination = Integer.parseInt(s.split("/")[1]);
                    vertex.addEdge(label,destination);
                }
                graph.addVertex(vertex,vertexId);
            }
        }
        catch (IOException x){
            System.out.println("File Not found");
            x.printStackTrace();
        }
    }
    //these methods are called when navigating between scenes
    public void homeToQuery(ActionEvent e){
        aFilter.clear();
        bFilter.clear();
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
