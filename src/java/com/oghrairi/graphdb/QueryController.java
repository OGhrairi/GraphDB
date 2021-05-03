package com.oghrairi.graphdb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class QueryController {
    Graph graph;
    HashMap<String,String> aFilter;
    HashMap<String,String> bFilter;
    String originObjectType;
    String destinationObjectType;
    @FXML
    private AnchorPane currentAnchor;
    @FXML
    private TextField queryEntryField;
    @FXML
    private Button queryToHomeButton;
    @FXML
    private TextField propertyFilterName;
    @FXML
    private TextField propertyFilterValue;
    @FXML
    private ListView filterList;
    @FXML
    private ListView listOutput;
    @FXML
    private ComboBox originLabelCombo;
    @FXML
    private ComboBox destinationLabelCombo;
    //Method called to easily create alert dialogues, takes an alert message as argument
    public QueryController(){
        originObjectType = "Any";
        destinationObjectType = "Any";
        aFilter = new HashMap<>();
        bFilter = new HashMap<>();
    }
    public void alertMaker(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.show();
    }
    public Boolean loadGraph(ActionEvent e) {
        //split into: vertexId#vertexLabel#prop1Name/prop1Value,prop2Name/prop2Value#edge1Label/edge1Destinationid,edge2Label/edge2Destinationid|NEXTVERTEX
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load graph txt file");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("txt files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(currentAnchor.getScene().getWindow());
        Boolean isLoaded = false;
        if (!(file == null)) {
            String graphName = file.getName().split(".txt")[0];
            graph = new Graph(graphName);
            try {
                Scanner reader = new Scanner(file);
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
                isLoaded = true;
                ObservableList objectList = FXCollections.observableArrayList();
                HashSet<String> vertexList = new HashSet<>();
                for(Integer key : graph.getVertices().keySet()){
                    vertexList.add(graph.getVertices().get(key).getLabel());
                }
                objectList.add("Any");
                objectList.addAll(vertexList);
                originLabelCombo.setItems(objectList);
                destinationLabelCombo.setItems(objectList);
                originLabelCombo.getSelectionModel().select(0);
                destinationLabelCombo.getSelectionModel().select(0);
                originLabelCombo.setDisable(false);
                destinationLabelCombo.setDisable(false);
                queryEntryField.clear();
                propertyFilterName.clear();
                propertyFilterValue.clear();
                filterList.getItems().clear();
            } catch (IOException x) {
                alertMaker("File Loading Error");
                x.printStackTrace();
            } catch (NumberFormatException | NoSuchElementException | ArrayIndexOutOfBoundsException x) {
                alertMaker("File Invalid");
                x.printStackTrace();

            }

        }
        return isLoaded;
    }
    //Runs a given regular query
    public void queryRun(ActionEvent e){
        if(graph==null){
            alertMaker("Please load a graph before querying");
        }else{
            String testString = queryEntryField.getText();
            if(testString.isEmpty()){
                alertMaker("Empty query string");
            }
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
                ObservableList<String> observableList = FXCollections.observableArrayList();
                //build output, either list of pairs or "no matches"
                if(!(str.isEmpty())){
                    for(String s : str){
                        int v1 = Integer.parseInt(s.split(",")[0]);
                        int v2 = Integer.parseInt(s.split(",")[1]);
                        Vertex vertexA = graph.getVertexById(v1);
                        Vertex vertexB = graph.getVertexById(v2);
                        boolean fits = true;
                        if(!originObjectType.equals("Any")){
                            if(!vertexA.getLabel().equals(originObjectType)){
                                fits = false;
                            }
                        }
                        if(!destinationObjectType.equals("Any")){
                            if(!vertexB.getLabel().equals(destinationObjectType)){
                                fits = false;
                            }
                        }
                        fits = isFits(vertexA, fits, aFilter);
                        fits = isFits(vertexB, fits, bFilter);
                        if(fits){
                            observableList.add(v1+"->"+v2);
                        }
                    }
                    listOutput.setItems(observableList);
                }else{
                    out = "No Matches";
                }

            }
            //if parse error, set output message in text field
            catch (ParseCancellationException p){

                ObservableList<String> observableList = FXCollections.observableArrayList();
                observableList.add(p.getMessage());
                listOutput.setItems(observableList);
            }
        }

    }
    //Adds property filters to a query output
    public void typeSelectionA(ActionEvent e){
        try{
            originObjectType = originLabelCombo.getValue().toString();
        }
        catch(NullPointerException x){

        }
    }
    public void typeSelectionB(ActionEvent e){
        try{
            destinationObjectType = destinationLabelCombo.getValue().toString();
        }
        catch(NullPointerException x){

        }

    }
    public void addAFilter(ActionEvent e){
        if(!propertyFilterName.getText().isEmpty()&&!propertyFilterValue.getText().isEmpty()){
            aFilter.put(propertyFilterName.getText(),propertyFilterValue.getText());
            filterListPopulate();
        }else{
            alertMaker("Please enter property name and value to add a filter");
        }
    }
    public void addBFilter(ActionEvent e){
        if(!propertyFilterName.getText().isEmpty()&&!propertyFilterValue.getText().isEmpty()){
            bFilter.put(propertyFilterName.getText(),propertyFilterValue.getText());
            filterListPopulate();
        }else{
            alertMaker("Please enter property name and value to add a filter");
        }
    }
    public void filterListPopulate(){
        ObservableList<String> observableList = FXCollections.observableArrayList();
        String list = "";
        for(String name : aFilter.keySet()){
            observableList.add("A   "+name+"   "+aFilter.get(name));
        }
        for(String name : bFilter.keySet()){
            observableList.add("B   "+name+"   "+bFilter.get(name));
        }
        filterList.setItems(observableList);
    }
    public void clearFilters(){
        aFilter.clear();
        bFilter.clear();
        filterList.getItems().clear();
    }
    //Helper method to keep logic tidy with query filters
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
    public void queryToHome(ActionEvent e){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
            queryToHomeButton.getScene().setRoot(root);

        }
        catch (Exception x){
            x.printStackTrace();
        }
    }
}

