
import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;




public class Login extends Application {
	
	@Override // Override the start method in the Application class
	public void start(Stage primaryStage)  {
		// Set properties for text fields
		TextField SSN = new TextField();
		SSN.setText("1232447");
		SSN.setPrefColumnCount(10);         //number of character in the texfield


		// Create  button
		Button login = new Button("Login");
		Button exit = new Button("Exit");
		
		HBox paneForLog = new HBox(10);
		paneForLog.setAlignment(Pos.CENTER_LEFT);
		paneForLog.getChildren().addAll(
			login);
		
		HBox paneForExit = new HBox(10);
		paneForExit.setAlignment(Pos.CENTER_RIGHT);
		paneForExit.getChildren().addAll(
			exit);

		HBox paneForSSN = new HBox(5);
		paneForSSN.setAlignment(Pos.CENTER);
		paneForSSN.setPadding(new Insets(10, 10, 10, 10));
		paneForSSN.getChildren().addAll(
			new Label("SSN"), SSN);
		
		HBox paneForButton = new HBox(5);
		paneForButton.setAlignment(Pos.BOTTOM_CENTER);
		paneForButton.setPadding(new Insets(10, 10, 10, 10));
		paneForButton.getChildren().addAll(paneForLog, paneForExit);
		
		HBox paneForMessage = new HBox(5);
		paneForMessage.setAlignment(Pos.TOP_CENTER);
		Text Message = new Text();
		Message.setTextAlignment(TextAlignment.JUSTIFY);
		paneForMessage.setPadding(new Insets(10, 10, 10, 10));
		paneForMessage.getChildren().addAll(Message);
		
		
		VBox vbox = new VBox(5);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(paneForSSN, paneForButton);

		
		login.setOnAction(new EventHandler<ActionEvent>() {
	    	  
	          @Override
          public void handle(ActionEvent event) {
	        	  
	        	//create message
		          try {
					Message.setText(DBUtility.login(SSN.getText()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		        //add text to the display
		          if (!vbox.getChildren().contains(Message))
					vbox.getChildren().add(0,Message);	  
	        	  
		          if (DBUtility.connected){

					// New window (Stage)
					Stage ClassWindow = new MyClasses(SSN.getText()).classWindow;        
	
					// Specifies the modality for new window.
					ClassWindow.initModality(Modality.WINDOW_MODAL);
	  
					// Specifies the owner Window (parent) for new window
					ClassWindow.initOwner(primaryStage);
	  
					// Set position of second window, related to primary window.
					ClassWindow.setX(primaryStage.getX() + 200);
					ClassWindow.setY(primaryStage.getY() + 50);
	  
					ClassWindow.show();
					}
          }
       });
		
		exit.setOnAction(new EventHandler<ActionEvent>() {
	    	  
	          @Override
        public void handle(ActionEvent event) {
	        	  
	    primaryStage.close();   	
		}
        
     });
		

		// Create a scene and place it in the stage
		Scene scene = new Scene(vbox, 300, 150);
		primaryStage.setTitle("Login"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.setX(200);
		primaryStage.setY(100);
		primaryStage.show(); // Display the stage
		
		
	}
	
public static void main(String[] args) {
	launch(args);
	}
}
