package com.oghrairi.graphdb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class EditController {
    Graph graph;
    Integer selectedVertex;
    String graphPath;
    //Each of the links to an fxml object defined in the fxml files
    @FXML
    private AnchorPane currentAnchor;
    @FXML
    private Button editToHomeButton;
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
    private TextField changeVertexLabelField;

    //Because of how files are saved, all input strings need to be cleansed of / , | #
    public String inputCleaner(String input){
        String output = input.replace("/","");
        output = output.replace(",","");
        output = output.replace("|","");
        output = output.replace("#","");
        return output;
    }
    //Method called to easily create alert dialogues, takes an alert message as argument
    public void alertMaker(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.show();
    }
    //Creates a new graph with a given graph name
    public void graphMaker(ActionEvent e){
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText("Enter New Graph Name");
        Optional<String> label = td.showAndWait();
        if(label.isPresent()){
            if(!label.get().isEmpty()){
                graph = new Graph(inputCleaner(label.get()));
                graphPath = System.getProperty("user.dir");

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

    }
    //creates a new vertex in a currently loaded graph
    public void addVertex(ActionEvent e){
        graph.addVertex("New Object");
        vertexListUpdater();
    }
    //alter the name of a selected vertex
    public void changeVertexLabel(ActionEvent e){
        if(graphEditVertexList.getSelectionModel().getSelectedItem()==null){
            alertMaker("Select an object");
        }else{
            String listSelection = graphEditVertexList.getSelectionModel().getSelectedItem();
            selectedVertex = Integer.parseInt(listSelection.split(",")[0].substring(4));
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter New Object Type");
            Optional<String> label = td.showAndWait();
            if(label.isPresent()){
                if(!label.get().isEmpty()){
                    graph.getVertexById(selectedVertex).changeLabel(inputCleaner(label.get()));
                    vertexListUpdater();
                }else{
                    alertMaker("Enter an object type");
                }
            }
        }

    }
    //delete a selected vertex
    public void deleteVertex(ActionEvent e){

        String listSelection = graphEditVertexList.getSelectionModel().getSelectedItem();
        if(listSelection==null){
            alertMaker("Please select an object");
        }
        else{
            Integer vertId = Integer.parseInt(listSelection.split(",")[0].substring(4));
            graph.deleteVertex(vertId);
            vertexListUpdater();
        }

    }

    //Add new edge to selected vertex
    public void addEdge(ActionEvent e){
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText("Enter Edge Label");
        Optional<String> label = td.showAndWait();
        String lab;
        if(label.isPresent()) {
            if (!label.get().isEmpty()) {
                lab = inputCleaner(label.get());
                td = new TextInputDialog();
                td.setHeaderText("Enter Destination Object ID");
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
    //edit selected edge
    public void editEdge(ActionEvent e){
        if(graphEditEdgeList.getSelectionModel().isEmpty()){
            alertMaker("Please select an edge");
        }
        else {
            String edgeString = graphEditEdgeList.getSelectionModel().getSelectedItem();
            String label = edgeString.split(",")[0].substring(7);
            Integer destId = Integer.parseInt(edgeString.split(",")[1].substring(24));
            for(Edge edge : graph.getVertexById(selectedVertex).getEdges()){
                if(edge.label.equals(label)&&edge.destinationId.equals(destId)){
                    TextInputDialog tx = new TextInputDialog(label);
                    tx.setHeaderText("Edit Label");
                    Optional<String> newLabel = tx.showAndWait();
                    if(newLabel.isPresent()){
                        if(!newLabel.get().isEmpty()){
                            edge.setLabel(inputCleaner(newLabel.get()));
                            tx = new TextInputDialog(destId.toString());
                            tx.setHeaderText("Edit Destination Object ID");
                            Optional<String> newDest = tx.showAndWait();
                            if(newDest.isPresent()){
                                try{
                                    if(!newDest.get().isEmpty()){
                                        edge.setDestinationId(Integer.parseInt(newDest.get()));
                                        edgeListUpdater();
                                    }else{
                                        alertMaker("Edge must have a destination object");
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
    //delete selected edge
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

    //add property to selected vertex
    public void addProperty(ActionEvent e){
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText("Enter Property Name");
        Optional<String> label = td.showAndWait();
        if(label.isPresent()){
            if(!label.get().isEmpty()){
                String name = inputCleaner(label.get());
                if(!graph.getVertexById(selectedVertex).getProperties().containsKey(name)){
                    td = new TextInputDialog();
                    td.setHeaderText("Enter Property Value");
                    Optional<String> value = td.showAndWait();
                    if(value.isPresent()){
                        if(!value.get().isEmpty()){
                            graph.getVertexById(selectedVertex).addProperty(name,inputCleaner(value.get()));
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
    //edit selected property value
    public void editProperty(ActionEvent e){
        if(graphEditPropertiesList.getSelectionModel().isEmpty()){
            alertMaker("Please select a property");
        }
        else {
            String propertyString = graphEditPropertiesList.getSelectionModel().getSelectedItem();
            String key = propertyString.split(",")[0].substring(5);

            String value = propertyString.split(",")[1].substring(8);

            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter New Property Value");
            Optional<String> label = td.showAndWait();
            if(label.isPresent()){
                if(!label.get().isEmpty()){
                    String newValue = inputCleaner(label.get());
                    graph.getVertexById(selectedVertex).changePropertyValue(key,newValue);
                    propertyListUpdater();
                }else{
                    alertMaker("Please enter a property value");
                }
            }
        }
    }
    //delete selected property
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

    //save graph to a text file, file name = graph name
    public void saveGraph(ActionEvent e){
        if(Objects.isNull(graph)){
            alertMaker("Please create a graph before saving");
        }else{
            /*
            Need to store: graph name, vertices - (vertex id, vertex label, vertex properties, edges from vertex, edge labels)
            graph name is stored as the name of the file
            split into: vertexId#vertexLabel#prop1Name/prop1Value,prop2Name/prop2Value#edge1Label/edge1Destinationid,edge2Label/edge2Destinationid|NEXTVERTEX
            reserved characters for graph name, vertex and edge fields : | , # /
             */
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose save location");
            File selectedDir = directoryChooser.showDialog(currentAnchor.getScene().getWindow());
            if(!(selectedDir==null)){
                String graphName = selectedDir.getAbsolutePath()+"\\"+graph.getGraphName()+".txt";
                Map<Integer,Vertex> vertices = graph.getVertices();

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

                try{
                    File file = new File(graphName);
                    FileWriter writer = new FileWriter(file);
                    writer.write(graphString);
                    writer.close();
                    file.createNewFile();

                }catch (IOException x){

                    x.printStackTrace();
                }

            }

        }


    }
    //Method which allows loading of previously saved graph file
    public void graphEditLoader(ActionEvent e){
        graphEditLoadVertexButton.setDisable(true);
        graphEditChangeLabelButton.setDisable(true);
        graphEditLoadVertexButton.setDisable(true);
        graphEditAddVertexButton.setDisable(true);
        graphEditDeleteVertexButton.setDisable(true);
        graphEditVertexList.getItems().clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load graph txt file");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(currentAnchor.getScene().getWindow());
        if(!(file==null)){
            graphPath = file.getParent();

            Boolean isLoaded = false;
            if (!(file == null)) {
                String graphName = file.getName().split(".txt")[0];
                graph = new Graph(graphName);
                try {
                    Scanner reader = new Scanner(file);

                    if(!reader.hasNext()){
                        alertMaker("Loaded empty graph");
                    }else{
                        String graphString = reader.nextLine();
                        //Highest level of text file, each vertex representation is split by a '|'
                        String[] vertexStringArray = graphString.split("\\|");
                        for (String vertexString : vertexStringArray) {
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
                            } catch (ArrayIndexOutOfBoundsException x) {

                            }
                            //the fourth and final field is a list of edges, delimited by a ','
                            try {
                                String[] edgeArray = vertexParts[3].split(",");
                                for (String s : edgeArray) {
                                    //for each edge, label and destination id are delimited by a '/'
                                    String label = s.split("/")[0];
                                    int destination = Integer.parseInt(s.split("/")[1]);
                                    vertex.addEdge(label, destination);
                                }
                            } catch (ArrayIndexOutOfBoundsException x) {

                            }
                            graph.addVertex(vertex, vertexId);
                        }
                    }

                    isLoaded = true;
                } catch (IOException x) {
                    alertMaker("File Loading Error");
                    x.printStackTrace();
                } catch (NumberFormatException | NoSuchElementException | ArrayIndexOutOfBoundsException x) {
                    alertMaker("File Invalid");
                    x.printStackTrace();
                }
            }
            if(isLoaded){
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

    }
    //Method responsible for updating the list of properties when a change is made
    public void propertyListUpdater() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        for(String key : graph.getVertexById(selectedVertex).getProperties().keySet()){
            String propertyString = "Key: "+key+", Value: "+graph.getVertexById(selectedVertex).getProperties().get(key);
            observableList.add(propertyString);
        }
        graphEditPropertiesList.setItems(observableList);
    }
    //Method responsible for updating list of edges when a change is made
    public void edgeListUpdater() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        for(Edge e : graph.getVertexById(selectedVertex).getEdges()){
            String edgeString = "Label: "+e.label+", Destination Object ID: "+e.destinationId;
            observableList.add(edgeString);
        }
        graphEditEdgeList.setItems(observableList);

    }
    //Method responsible for updating list of vertices when a change is made
    public void vertexListUpdater() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        Map<Integer, Vertex> vertices = graph.getVertices();
        for (Integer key : vertices.keySet()) {
            String vert = "ID: " + key + ", Type: " + vertices.get(key).getLabel() + ", " + vertices.get(key).getProperties().size() + " properties, ";
            vert += vertices.get(key).getEdges().size() + " outbound edge";
            if (vertices.get(key).getEdges().size() > 1) {
                vert += "s";
            }

            observableList.add(vert);
        }
        graphEditVertexList.setItems(observableList);
    }
    //Method which loads edge and property information about a selected vertex
    public void showVertexInfo(ActionEvent e){
        if(graphEditVertexList.getSelectionModel().isEmpty()){
            alertMaker("Please select an object");
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
    //Exit back to home screen
    public void editToHome(ActionEvent e){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            editToHomeButton.getScene().setRoot(root);

        }
        catch (Exception x){
            x.printStackTrace();
        }
    }
}
