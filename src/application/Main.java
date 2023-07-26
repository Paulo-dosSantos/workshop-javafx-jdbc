package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage stage) { 
	try { 
	 ScrollPane scrollPane = FXMLLoader.load(getClass().getResource("/gui/MainView.fxml")); 
	
	 scrollPane.setFitToHeight(true);
	 scrollPane.setFitToWidth(true);
	 
	 Scene scene = new Scene(scrollPane); 
	 stage.setScene(scene); 
	 stage.show(); 
	 } 
	catch (IOException e) { 
	 e.printStackTrace(); 
	 } 
	} 
	
	public static void main(String[] args) {
		launch(args);
	}
}
