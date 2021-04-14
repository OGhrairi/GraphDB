package com.oghrairi.graphdb;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//extends Application for javafx
public class Main extends Application{
    //method which is called from the launch() method in main, builds the fx window
    public void start(Stage stage){
        //try/catch necessary for the FXMLLoader
        try {
            Parent homeRoot = FXMLLoader.load(getClass().getResource("home.fxml"));
            Scene homeScreen = new Scene(homeRoot);
            stage.setScene(homeScreen);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        stage.setTitle("QueryDB");
        Image icon = new Image("file:resources/chat.png");
        stage.getIcons().add(icon);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
