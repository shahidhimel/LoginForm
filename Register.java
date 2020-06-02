import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.RowSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;


public class Register  {
	public Stage registerWindow;
	public RowSet rowSet;
	TableView registertable = new TableView();
	ArrayList<String> listclasstoadd = new ArrayList<>();
	
	public Register(String Student_Ssn){
		Button cancel = new Button("Cancel");
		Button add = new Button("Add");
		Button confirm = new Button("Confirm & Close!");
		
		HBox paneForCancel = new HBox(10);
		paneForCancel.setAlignment(Pos.CENTER_LEFT);
		paneForCancel.getChildren().addAll(
			cancel);
		
		HBox paneForConfirm = new HBox(10);
		paneForConfirm.setAlignment(Pos.CENTER_RIGHT);
		paneForConfirm.getChildren().addAll(
				confirm);
		
		HBox paneForAdd = new HBox(10);
		paneForAdd.setAlignment(Pos.CENTER_RIGHT);
		paneForAdd.getChildren().addAll(
				add);

		HBox paneForMessage = new HBox(5);
		paneForMessage.setAlignment(Pos.TOP_CENTER);
		Text Message = new Text();
		Message.setText("Choose a class and click add !");
		Message.setTextAlignment(TextAlignment.JUSTIFY);
		paneForMessage.setPadding(new Insets(10, 10, 10, 10));
		paneForMessage.getChildren().addAll(Message);
		
		VBox paneForRowset = new VBox(5);
		paneForRowset.setAlignment(Pos.CENTER);
		paneForRowset.setPadding(new Insets(10, 10, 10, 10));
		

        final Label label = new Label("Available Classes for Reistration");
        label.setFont(new Font("Arial", 20));
        registertable.setEditable(true);
        		
		//get the rowset value
		try {
			RowSet rowSet = DBUtility.getStudentRowset("Select * from Course where courseid not in "
					+ "(Select courseId from Enrollment where ssn = "+Student_Ssn+")");
			ResultSetMetaData metaData = rowSet.getMetaData();
	        int noOfCols = metaData.getColumnCount();
	        rowSet.beforeFirst();

	        for (int i = 0; i < rowSet.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn tableColumn = new TableColumn(rowSet.getMetaData().getColumnName(i + 1));
                
                /*with lamda expression
                tableColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param ->
                new SimpleStringProperty(param.getValue().get(j).toString()));
                end with lamda expression
                 */
                
                tableColumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        // p.getValue() returns the Person instance for a particular TableView row
                        return new SimpleStringProperty(param.getValue().get(j).toString()); }
               });
                registertable.getColumns().add(tableColumn);
	        }
	        System.out.println();
	
	        
	        ObservableList<ObservableList> data = FXCollections.observableArrayList();
	        int rowCounter = 0;
	        while (rowSet.next()) {
	        	rowCounter++;
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= noOfCols; i++) {
                    row.add(rowSet.getString(i));
                }
                System.out.println("Row [" + rowCounter + "] added " + row);
                data.add(row);         
            }
	        registertable.setItems(data);
            data = null;
	        
	        paneForRowset.getChildren().addAll(label,registertable);
	        
	        System.out.println("Students Classes not registered Found");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		HBox paneForButton = new HBox(5);
		paneForButton.setAlignment(Pos.BOTTOM_CENTER);
		paneForButton.setPadding(new Insets(10, 10, 10, 10));
		paneForButton.getChildren().addAll(paneForAdd, paneForConfirm, paneForCancel);
		
	
		VBox vbox = new VBox(5);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(Message, paneForRowset, paneForButton);

        Scene classScene = new Scene(vbox, 600, 400);
        registerWindow = new Stage();
        registerWindow.setTitle("Register");
        registerWindow.setScene(classScene);
        
		//add action button
		add.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event){ 	
				ObservableList<String> selectedItem = (ObservableList<String>) registertable.getSelectionModel().getSelectedItem();
				registertable.getItems().remove(selectedItem);
	        	//delete(registertable.getSelectionModel().getSelectedIndex());
				
				listclasstoadd.add(selectedItem.get(0));

			}
		});
        
		
		//cancel action
        cancel.setOnAction(new EventHandler<ActionEvent>() {
	    	  
        	@Override
	        public void handle(ActionEvent event) { 
              		
        		listclasstoadd.clear();
        		System.out.println(listclasstoadd);
        		//registerWindow.close(); // close window but doesn't create event for signal to refresh parent window
        		registerWindow.fireEvent(new WindowEvent(registerWindow,
        			    WindowEvent.WINDOW_CLOSE_REQUEST));
        		
		}
      
        });
        
        //action at closing : update Enrollment table 
        confirm.setOnAction(new EventHandler<ActionEvent>() {
	    	  
        	@Override
	        public void handle(ActionEvent event) { 
              		
        		for (String classid : listclasstoadd){
  	        	  try {
  					DBUtility.updateEnroll("insert into Enrollment values('"+Student_Ssn+"','"+classid+"','2019-05-04','A')");
  					} catch (ClassNotFoundException | SQLException e) {
  						// TODO Auto-generated catch block
  						e.printStackTrace();
  					}     		
  	        	  }
        		listclasstoadd.clear();
        		registerWindow.fireEvent(new WindowEvent(registerWindow,
        			    WindowEvent.WINDOW_CLOSE_REQUEST));
        		
		}
      
        });   
	
		
	}//end register constructor


}