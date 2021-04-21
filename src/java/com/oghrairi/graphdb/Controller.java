package com.oghrairi.graphdb;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;

public class Controller {
    //Graph object which can be referenced between scenes
    Graph graph;
    HashMap<String,String> aFilter;
    HashMap<String,String> bFilter;
    Vertex newVertex;
    Integer selectedVertex;
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
    private Button homeToEditButton;
    @FXML
    private Button editToHomeButton;
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
    @FXML
    private ListView<String> graphEditVertexList;
    @FXML
    private Button graphEditLoadVertexButton;
    @FXML
    private Button graphEditChangeLabelButton;
    @FXML
    private ListView<String> graphEditEdgeList;
    @FXML
    private ListView<String> graphEditPropertiesList;
    @FXML
    private Button graphEditEdgeAddButton;
    @FXML
    private Button graphEditEdgeEditButton;
    @FXML
    private Button graphEditEdgeDeleteButton;
    @FXML
    private Button graphEditPropertyAddButton;
    @FXML
    private Button graphEditPropertyEditButton;
    @FXML
    private Button graphEditPropertyDeleteButton;
    @FXML
    private Button graphEditAddVertexButton;
    @FXML
    private Button graphEditDeleteVertexButton;
    @FXML
    private TextField createGraphNameField;
    @FXML
    private Button createGraphButton;
    @FXML
    private TextField changeVertexLabelField;
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
                        out += vertexA.getLabel()+"->"+vertexB.getLabel()+"\n";
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
        if(!createGraphNameField.getText().isEmpty()){
            graph = new Graph(createGraphNameField.getText());
            graphEditVertexList.setDisable(false);
            graphEditAddVertexButton.setDisable(false);
            graphEditLoadVertexButton.setDisable(false);
            graphEditDeleteVertexButton.setDisable(false);
            graphEditPropertiesList.getItems().clear();
            graphEditEdgeList.getItems().clear();
            graphEditVertexList.getItems().clear();
        }
        else{
            alertMaker("Please enter graph name");
        }
    }

    //creates a new vertex in a currently loaded graph with a given vertex name
    public void addVertex(ActionEvent e){
        graph.addVertex("New Vertex");
        vertexListUpdater();
    }
    public void changeVertexLabel(ActionEvent e){
        if(!changeVertexLabelField.getText().isEmpty()){
            graph.getVertexById(selectedVertex).changeLabel(changeVertexLabelField.getText());
            vertexListUpdater();
        }else{
            alertMaker("Enter a vertex label");
        }
    }
    public void deleteVertex(ActionEvent e){

        String listSelection = graphEditVertexList.getSelectionModel().getSelectedItem();
        if(listSelection==null){
            alertMaker("Please select a vertex");
        }
        else{
            Integer vertId = Integer.parseInt(listSelection.split(",")[0].substring(4));
            graph.deleteVertex(vertId);
            vertexListUpdater();
        }

    }

    public void addEdge(ActionEvent e){
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText("Enter Edge Label");
        Optional<String> label = td.showAndWait();
        String lab;
        if(label.isPresent()) {
            if (!label.get().isEmpty()) {
                lab = label.get();
                td = new TextInputDialog();
                td.setHeaderText("Enter Destination Vertex ID");
                Optional<String> id = td.showAndWait();
                if (id.isPresent()) {
                    if (!id.get().isEmpty()) {
                        Integer destId = 0;
                        try {
                            destId = Integer.parseInt(id.get());
                            if(graph.getVertices().containsKey(destId)){
                                graph.getVertexById(selectedVertex).addEdge(lab, destId);
                                edgeListUpdater();
                                vertexListUpdater();
                            }
                            else{
                                alertMaker("Destination ID does not exist");
                            }

                        } catch (NumberFormatException x) {
                            alertMaker("Invalid Index Value");
                        }

                    } else {
                        alertMaker("Index entry empty");
                    }
                }
            }
            else {
                alertMaker("Label entry empty");
            }
        }
    }
    public void editEdge(ActionEvent e){
        if(graphEditEdgeList.getSelectionModel().isEmpty()){
            alertMaker("Please select an edge");
        }
        else {
            String edgeString = graphEditEdgeList.getSelectionModel().getSelectedItem();
            String label = edgeString.split(",")[0].substring(7);
            Integer destId = Integer.parseInt(edgeString.split(",")[1].substring(17));
            for(Edge edge : graph.getVertexById(selectedVertex).getEdges()){
                if(edge.label.equals(label)&&edge.destinationId.equals(destId)){
                    TextInputDialog tx = new TextInputDialog(label);
                    tx.setHeaderText("Edit Label");
                    Optional<String> newLabel = tx.showAndWait();
                    if(newLabel.isPresent()){
                        if(!newLabel.get().isEmpty()){
                            edge.setLabel(newLabel.get());
                            tx = new TextInputDialog(destId.toString());
                            tx.setHeaderText("Edit Destination ID");
                            Optional<String> newDest = tx.showAndWait();
                            if(newDest.isPresent()){
                                try{
                                    if(!newDest.get().isEmpty()){
                                        edge.setDestinationId(Integer.parseInt(newDest.get()));
                                        edgeListUpdater();
                                    }else{
                                        alertMaker("Edge must have destination");
                                    }
                                }catch (NumberFormatException x){
                                    alertMaker("Invalid ID");
                                }
                            }
                        }else{
                            alertMaker("Edge must have a label");
                        }

                    }
                }
            }


        }
    }
    public void deleteEdge(ActionEvent e){
        if(graphEditEdgeList.getSelectionModel().isEmpty()){
            alertMaker("Please select an edge");
        }
        else {
            String edgeString = graphEditEdgeList.getSelectionModel().getSelectedItem();
            String label = edgeString.split(",")[0].substring(7);
            Integer destId = Integer.parseInt(edgeString.split(",")[1].substring(17));
            for(int i=0; i<graph.getVertexById(selectedVertex).getEdges().size();i++) {
                Edge edge = graph.getVertexById(selectedVertex).getEdges().get(i);
                if (edge.label.equals(label) && edge.destinationId.equals(destId)) {
                    graph.getVertexById(selectedVertex).getEdges().remove(i);
                }
            }
            edgeListUpdater();
            vertexListUpdater();
        }
    }

    public void addProperty(ActionEvent e){
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText("Enter Property Name");
        Optional<String> label = td.showAndWait();
        if(label.isPresent()){
            if(!label.get().isEmpty()){
                String name = label.get();
                if(!graph.getVertexById(selectedVertex).getProperties().containsKey(name)){
                    td = new TextInputDialog();
                    td.setHeaderText("Enter Property Value");
                    Optional<String> value = td.showAndWait();
                    if(value.isPresent()){
                        if(!value.get().isEmpty()){
                            graph.getVertexById(selectedVertex).addProperty(name,value.get());
                            propertyListUpdater();
                            vertexListUpdater();
                        }else{
                            alertMaker("Property must have a value");
                        }
                    }
                }else{
                    alertMaker("Property already exists");
                }
            }else{
                alertMaker("Property must have a name");
            }
        }
    }
    public void editProperty(ActionEvent e){
        if(graphEditPropertiesList.getSelectionModel().isEmpty()){
            alertMaker("Please select a property");
        }
        else {
            String propertyString = graphEditPropertiesList.getSelectionModel().getSelectedItem();
            String key = propertyString.split(",")[0].substring(5);
            System.out.println(key);
            String value = propertyString.split(",")[1].substring(8);
            System.out.println(value);
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter New Property Value");
            Optional<String> label = td.showAndWait();
            if(label.isPresent()){
                if(!label.get().isEmpty()){
                    String newValue = label.get();
                    graph.getVertexById(selectedVertex).changePropertyValue(key,newValue);
                    propertyListUpdater();
                }else{
                    alertMaker("Please enter a property value");
                }
            }
        }
    }
    public void deleteProperty(ActionEvent e){
        if(graphEditPropertiesList.getSelectionModel().isEmpty()){
            alertMaker("Please select a property");
        }
        else {
            String propertyString = graphEditPropertiesList.getSelectionModel().getSelectedItem();
            String key = propertyString.split(",")[0].substring(5);
            graph.getVertexById(selectedVertex).getProperties().remove(key);
            propertyListUpdater();
            vertexListUpdater();
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
                vString += id+"#";
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
                boolean isFirstEdge = true;
                for(Edge edge : vertex.getEdges()){

                    if(isFirstEdge){
                        isFirstEdge=false;
                    }else{
                        vString+=",";
                    }
                    vString += edge.label+"/"+edge.destinationId;
                }
                graphString+= vString;
            }
            System.out.println(graphString);
            try{
                File file = new File(graphName);
                FileWriter writer = new FileWriter(file);
                writer.write(graphString);
                writer.close();
                file.createNewFile();

            }catch (IOException x){
                System.out.println("IOException in file creation");
                x.printStackTrace();
            }

        }


    }
    public Boolean loadGraph(ActionEvent e){
        //split into: vertexId#vertexLabel#prop1Name/prop1Value,prop2Name/prop2Value#edge1Label/edge1Destinationid,edge2Label/edge2Destinationid|NEXTVERTEX
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load graph txt file");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(queryScreenAnchor.getScene().getWindow());
        String graphName = file.getName().split(".txt")[0];
        graph = new Graph(graphName);
        Boolean isLoaded = false;
        try{
            Scanner reader = new Scanner(file);
            String graphString = reader.nextLine();
            //Highest level of text file, each vertex representation is split by a '|'
            String[] vertexStringArray = graphString.split("\\|");
            for(String vertexString : vertexStringArray){
                //Within each vertex representation, each field is split by a '#'
                String[] vertexParts = vertexString.split("#");
                int vertexId = Integer.parseInt(vertexParts[0]);
                String vertexLabel = vertexParts[1];
                Vertex vertex = new Vertex(vertexLabel);
                //The third field is a list of properties, delimited by a ','
                try {
                    String[] propertyArray = vertexParts[2].split(",");
                    for (String s : propertyArray) {
                        //for each property, name and value are delimited by '/'
                        String name = s.split("/")[0];
                        String val = s.split("/")[1];
                        vertex.addProperty(name, val);
                    }
                }catch (ArrayIndexOutOfBoundsException x){

                }
                //the fourth and final field is a list of edges, delimited by a ','
                try{
                    String[] edgeArray = vertexParts[3].split(",");
                    for(String s : edgeArray){
                        //for each edge, label and destination id are delimited by a '/'
                        String label = s.split("/")[0];
                        int destination = Integer.parseInt(s.split("/")[1]);
                        vertex.addEdge(label,destination);
                    }
                }
                catch(ArrayIndexOutOfBoundsException x){

                }
                graph.addVertex(vertex,vertexId);
            }
            isLoaded = true;
        }
        catch (IOException x){
            alertMaker("File Loading Error");
            x.printStackTrace();
        }
        catch (NumberFormatException | NoSuchElementException | ArrayIndexOutOfBoundsException x){
            alertMaker("File Invalid");
            x.printStackTrace();

        }
        return isLoaded;
    }
    public void propertyListUpdater() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        for(String key : graph.getVertexById(selectedVertex).getProperties().keySet()){
            String propertyString = "Key: "+key+", Value: "+graph.getVertexById(selectedVertex).getProperties().get(key);
            observableList.add(propertyString);
        }
        graphEditPropertiesList.setItems(observableList);
    }
    public void edgeListUpdater() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        for(Edge e : graph.getVertexById(selectedVertex).getEdges()){
            String edgeString = "Label: "+e.label+", Destination ID: "+e.destinationId;
            observableList.add(edgeString);
        }
        graphEditEdgeList.setItems(observableList);

    }
    public void vertexListUpdater() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        Map<Integer, Vertex> vertices = graph.getVertices();
        for (Integer key : vertices.keySet()) {
            String vert = "ID: " + key + ", Label: " + vertices.get(key).getLabel() + ", " + vertices.get(key).getProperties().size() + " properties, ";
            vert += vertices.get(key).getEdges().size() + " outbound edge";
            if (vertices.get(key).getEdges().size() > 1) {
                vert += "s";
            }
            System.out.println(vert);
            observableList.add(vert);
        }
        graphEditVertexList.setItems(observableList);
    }
    public void graphEditLoader(ActionEvent e){
        graphEditLoadVertexButton.setDisable(true);
        graphEditChangeLabelButton.setDisable(true);
        graphEditLoadVertexButton.setDisable(true);
        graphEditAddVertexButton.setDisable(true);
        graphEditDeleteVertexButton.setDisable(true);
        graphEditVertexList.getItems().clear();
        Boolean loaded = loadGraph(e);
        if(loaded){
            vertexListUpdater();
            graphEditEdgeList.getItems().clear();
            graphEditPropertiesList.getItems().clear();
        }
        graphEditLoadVertexButton.setDisable(false);
        graphEditChangeLabelButton.setDisable(false);
        graphEditLoadVertexButton.setDisable(false);
        graphEditAddVertexButton.setDisable(false);
        graphEditDeleteVertexButton.setDisable(false);
    }
    public void showVertexInfo(ActionEvent e){
        if(graphEditVertexList.getSelectionModel().isEmpty()){
            alertMaker("Please select a vertex");
        }else {
            graphEditEdgeAddButton.setDisable(false);
            graphEditEdgeEditButton.setDisable(false);
            graphEditEdgeDeleteButton.setDisable(false);
            graphEditPropertyAddButton.setDisable(false);
            graphEditPropertyEditButton.setDisable(false);
            graphEditPropertyDeleteButton.setDisable(false);
            graphEditChangeLabelButton.setDisable(false);
            String listSelection = graphEditVertexList.getSelectionModel().getSelectedItem();
            Integer vertId = Integer.parseInt(listSelection.split(",")[0].substring(4));
            Vertex vertex = graph.getVertexById(vertId);
            selectedVertex = vertId;
            edgeListUpdater();
            propertyListUpdater();
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
    public void homeToEdit(ActionEvent e){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("graphEditor.fxml"));
            homeToEditButton.getScene().setRoot(root);
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }
    public void editToHome(ActionEvent e){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            editToHomeButton.getScene().setRoot(root);
        }
        catch (Exception x){
            x.printStackTrace();
        }
    }


}
