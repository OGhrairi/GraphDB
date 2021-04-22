package com.oghrairi.graphdb;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HomeController {
    @FXML
    private Button homeToQueryButton;
    @FXML
    private Button homeToEditButton;
    //Navigation methods, one to the query screen, one to the edit screen
    public void homeToQuery(ActionEvent e){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("queryScreen.fxml"));
            homeToQueryButton.getScene().setRoot(root);
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
}
