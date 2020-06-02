
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleStringProperty;


public class MyClasses {
	public Stage classWindow, registerWindow ;
	public RowSet rowSet;
	TableView classtable = new TableView();



	public MyClasses(String Student_Ssn){
		
		Button remove = new Button("Remove");
		Button add = new Button("ADD");

		HBox paneForRemove = new HBox(10);
		paneForRemove.setAlignment(Pos.CENTER_LEFT);
		paneForRemove.getChildren().addAll(
				remove);

		HBox paneForAdd = new HBox(10);
		paneForAdd.setAlignment(Pos.CENTER_RIGHT);
		paneForAdd.getChildren().addAll(
				add);

		HBox paneForMessage = new HBox(5);
		paneForMessage.setAlignment(Pos.TOP_CENTER);
		Text Message = new Text();
		try {
			Message.setText("Welcome "+DBUtility.getStudentName(Student_Ssn));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message.setTextAlignment(TextAlignment.JUSTIFY);
		paneForMessage.setPadding(new Insets(10, 10, 10, 10));
		paneForMessage.getChildren().addAll(Message);

		VBox paneForRowset = new VBox(5);
		paneForRowset.setAlignment(Pos.CENTER);
		paneForRowset.setPadding(new Insets(10, 10, 10, 10));
		
		final Label label = new Label("Registered Classes");
		label.setFont(new Font("Arial", 20));
		classtable.setEditable(true);

		//get the rowset value
		try {
			rowSet = DBUtility.getStudentRowset("Select Course.*,grade from Course, Enrollment "+
					"where ssn = "+Student_Ssn+"and Enrollment.courseId = Course.courseId");
			ResultSetMetaData metaData = rowSet.getMetaData();
			int noOfCols = metaData.getColumnCount();
			rowSet.beforeFirst(); 

			for (int i = 0; i < rowSet.getMetaData().getColumnCount(); i++) {
				final int j = i;
				TableColumn tableColumn = new TableColumn(rowSet.getMetaData().getColumnName(i + 1));

				tableColumn.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
						// p.getValue() returns the Person instance for a particular TableView row
						return new SimpleStringProperty(param.getValue().get(j).toString()); }
				});
				classtable.getColumns().add(tableColumn);
			}
			System.out.println();

			int rowCounter = 0;
			while (rowSet.next()) {
				rowCounter++;
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= noOfCols; i++) {
					row.add(rowSet.getString(i));
				}
				System.out.println("Row [" + rowCounter + "] added " + row);
				classtable.getItems().add(row);         
			}


			paneForRowset.getChildren().addAll(label,classtable);
			System.out.println("Students Classes Found");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//end of showing rowset


		HBox paneForButton = new HBox(5);
		paneForButton.setAlignment(Pos.BOTTOM_CENTER);
		paneForButton.setPadding(new Insets(10, 10, 10, 10));
		paneForButton.getChildren().addAll(paneForAdd, paneForRemove);
		
		VBox vbox = new VBox(5);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(Message, paneForRowset, paneForButton);

		Scene registerScene = new Scene(vbox, 600, 400);
		classWindow = new Stage();
		classWindow.setTitle("My Classes");
		classWindow.setScene(registerScene);

		
		//Adding Listener and moving RowSet  
	    //rowSet.addRowSetListener(new MyListener());

		//add action button
		add.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event){ 	  


				// New window (Stage)
				registerWindow = new Register(Student_Ssn).registerWindow;

				// Specifies the modality for new window.
				registerWindow.initModality(Modality.WINDOW_MODAL);

				// Specifies the owner Window (parent) for new window
				registerWindow.initOwner(classWindow);

				// Set position of 3rd window, related to 2nd window.
				registerWindow.setX(classWindow.getX() + 100);
				registerWindow.setY(classWindow.getY());

				registerWindow.show();
				
				
				//action do to when the registration window is closed
				registerWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
			          public void handle(WindowEvent we) {

			      		//get new record
			        	  try {
							rowSet = DBUtility.getStudentRowset("Select Course.*,grade from Course, Enrollment "+
										"where ssn = "+Student_Ssn+"and Enrollment.courseId = Course.courseId");
							refresh(classtable,rowSet);
						} catch (ClassNotFoundException | SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}     		

			      		//end of getting new record
			          }
			      });
				   
			}
		});
		

	
		//remove button
		remove.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event){ 	
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Are you sure?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
				    // ... user chose OK
					ObservableList<String> selectedItem = (ObservableList<String>) classtable.getSelectionModel().getSelectedItem();
					classtable.getItems().remove(selectedItem);
		        	//delete(classtable.getSelectionModel().getSelectedIndex());
		        	try {
						DBUtility.updateEnroll("delete from Enrollment where ssn ='"+Student_Ssn+"' and courseId = '"+selectedItem.get(0)+"'");
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
				    // ... user chose CANCEL or closed the dialog
				}
				
			}
		});


	}//end myclasses constructor
	
	public  void delete(int rowIndex){
		//Delete the record from the database   
		try {
			int currentRow = rowIndex + 1;
			System.out.println(rowIndex);
			rowSet.absolute(currentRow);
            rowSet.deleteRow();
            if (rowSet.isAfterLast())
            rowSet.last();
            else if (rowSet.getRow() >= currentRow)
			rowSet.absolute(currentRow);
            //to be used for updating tables but cannot be used because of table restriction
			//((CachedRowSetImpl)rowSet).acceptChanges();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public  void refresh(TableView table, RowSet rowset) throws SQLException{
		//refress table  
		table.getItems().clear();
		ResultSetMetaData metaData = rowset.getMetaData();
		int noOfCols = metaData.getColumnCount();
		rowset.beforeFirst(); 

		int rowCounter = 0;
		while (rowset.next()) {
			rowCounter++;
			ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 1; i <= noOfCols; i++) {
				row.add(rowset.getString(i));
			}
			System.out.println("Row [" + rowCounter + "] added " + row);
			table.getItems().add(row);         
  			
		}
		System.out.println("Table Updated");
		table.refresh();
	}
    //add rowset listener to the code  
    class MyListener implements RowSetListener {  
          public void cursorMoved(RowSetEvent event) {  
                    System.out.println("Cursor Moved...");  
          }  
         public void rowChanged(RowSetEvent event) {  
                    System.out.println("Cursor Changed...");  
         }  
         public void rowSetChanged(RowSetEvent event) {  
                    System.out.println("RowSet changed...");  
         }  
    }  
}