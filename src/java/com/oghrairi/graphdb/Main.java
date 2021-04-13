package com.oghrairi.graphdb;
import java.io.IOException;
import java.util.HashSet;
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
            //get program icon
            Image icon = new Image("file:resources/chat.png");
            //get fxml file
            Parent root = FXMLLoader.load(getClass().getResource("sbtest.fxml"));
            //build scene from fxml
            Scene scene = new Scene(root);
            stage.setTitle("Stage Test");
            stage.getIcons().add(icon);
            //set scene on the stage
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
        /*
        Graph test1 = new Graph("testGraph");
        System.out.println(test1.getGraphName());
        test1.addVertex("person");
        test1.addVertex("person");
        test1.addVertex("person");
        test1.addVertex("person");
        test1.addEdge("knows",0,1);
        test1.addEdge("likes",1,2);
        test1.addEdge("knows",2,0);
        test1.addEdge("knows",0,3);
        test1.addEdge("likes",3,2);
        test1.addEdge("knows",2,1);
        System.out.println(test1.query("knows")); //should return 0->1, 2->0, 2->1, 0->3
        System.out.println(test1.query("likes")); //should return 1->2, 3->2
        System.out.println(test1.query("loves")); //should return empty
        System.out.println(test1.query("likes/knows")); //should return 1->1, 1->0, 3->0, 3->1
        System.out.println(test1.query("knows/likes/knows")); //should return 0->1, 0->0, 2->0, 2->1
        System.out.println(test1.query("knows-")); //should return 1->0, 3->0, 0->2, 1->2
        System.out.println(test1.query("knows/knows-")); //should return 2->0, 2->2, 0->0, 0->2
        System.out.println(test1.query("knows+")); //should return 2->0, 0->3, 2->1, 2->3, 0->1
        System.out.println(test1.query("likes/knows+/likes")); //returns 3->2 1->2
        //test1.q2("((knows)-/likes)+/knows#knows");
        System.out.println(test1.cq("likes/knows(x,a1) knows-(a1,y)"));
    */
    }

}
