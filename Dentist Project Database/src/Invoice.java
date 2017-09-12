import java.util.Date;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.converter.NumberStringConverter;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
public class Invoice {
	
	Scanner kb = new Scanner(System.in);
	
	private static int invoiceNum = 0;
	private int invoiceNo;
	private double invoiceAmt;
	private Date invoiceDate;
	private boolean isPaid;
	static final String DATABASE_URL = "jdbc:derby:C:/DentistProject;create=true";
	
	public Invoice(Date invoiceDate){
		this.invoiceDate = invoiceDate;
		invoiceNo = invoiceNum;
		invoiceNum++;
	}
	
	public Invoice(){
		
	}

	public int getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(int invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public double getInvoiceAmt() {
		return invoiceAmt;
	}

	public void setInvoiceAmt(double invoiceAmt) {
		this.invoiceAmt = invoiceAmt;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public boolean isPaid() {
		return isPaid;
	}

	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
	
	public String toString(){
		return "Invoice No: " + invoiceNo + "\t€" + invoiceAmt;
	}
	
	public String data(){
		String date = new SimpleDateFormat("dd/MM/yyyy").format(invoiceDate);
		return invoiceNo + ", " + invoiceAmt + ", '" + date + "', " + isPaid;
	}
	
	public GridPane procedureGUI(){
		GridPane procMenu = new GridPane();
		TableView<Procedure> tableView = new TableView<>(); 
		tableView.setEditable(true);
		HBox buttons = new HBox();
		Label error = new Label();
		GridPane.setConstraints(error, 2, 3);

		Label procedureName = new Label("Procedure Name:");
		GridPane.setConstraints(procedureName, 0, 1);
		TextField procedureNameInput = new TextField();
		GridPane.setConstraints(procedureNameInput, 1, 1);
		Label procedurePrice = new Label("Procedure Price:");
		GridPane.setConstraints(procedurePrice, 0, 2);
		TextField procedurePriceInput = new TextField();
		GridPane.setConstraints(procedurePriceInput, 1, 2);
		
		Button addProcedure = new Button("Add Procedure");
		addProcedure.setOnAction(e -> {
			
			String procName = procedureNameInput.getText();
			double procPrice;
			try{
				procPrice = Double.parseDouble(procedurePriceInput.getText());
			}
			catch(java.lang.NumberFormatException exception){
				error.setText("Please fill all fields correctly");
				error.setTextFill(Color.RED);
				System.out.println("ERROR:Number Format Exception");
				procPrice = -1;	
			}
			if(procName.isEmpty()){
				error.setText("Please fill all fields correctly");
				error.setTextFill(Color.RED);
			}
			else if(procPrice > 0){
				Procedure newProc = new Procedure(procName, procPrice);
				
				Connection conn = null;
				Statement stmt = null;
				
				try{
					conn = DriverManager.getConnection(DATABASE_URL);
					stmt = conn.createStatement();
					
					stmt.executeUpdate("INSERT INTO Procedures "
							+ "VALUES(" + newProc.data() + ")");
				}
				catch(SQLException ex){
					ex.printStackTrace();
				}
				finally{
					try{
						conn.close();
						stmt.close();
					}
					catch(SQLException ex){
						ex.printStackTrace();
					}
				}
				//updates the tableview in realtime
				procedureList().add(newProc);
				tableView.setItems(procedureList());
				error.setText("Procedure Added");
				error.setTextFill(Color.BLACK);
			}
			procedureNameInput.clear();
			procedurePriceInput.clear();
		});
		
		//The column in the tableview for the procedure name
		TableColumn<Procedure, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setMinWidth(200);
		//uses the variable name for the procedure name to identify it in the procedure class.
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("procName"));
		//Makes the column editable
		nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		nameColumn.setOnEditCommit(
				//Use an event handler to edit the column
			new EventHandler<CellEditEvent<Procedure, String>>() {
				@Override
				public void handle(CellEditEvent<Procedure, String> procedure) {
					((Procedure) procedure.getTableView().getItems().get(
							procedure.getTablePosition().getRow())
		                    ).setProcName(procedure.getNewValue());
					
					//keeps the price of the procedure
					double oldPrice = ((Procedure) procedure.getTableView().getItems().get(
							procedure.getTablePosition().getRow())).getProcCost();
					
					//keeps the procedure number
					int oldProcNum = ((Procedure) procedure.getTableView().getItems().get(
							procedure.getTablePosition().getRow())).getProcNo();
					
					//Gets the procedure that has been clicked on
					Procedure selected = new Procedure(procedure.getNewValue(), oldPrice);
					selected.setProcNo(oldProcNum);
					
					Connection conn = null;
					Statement stmt = null;
					
					try{
						conn = DriverManager.getConnection(DATABASE_URL);
						stmt = conn.createStatement();
						
						stmt.executeUpdate("UPDATE Procedures SET procName = '" + procedure.getNewValue() + 
								"' WHERE procNo = " + oldProcNum);
					}
					catch(SQLException ex){
						ex.printStackTrace();
					}
					finally{
						try{
							conn.close();
							stmt.close();
						}
						catch(SQLException ex){
							ex.printStackTrace();
						}
					}
						
				}
		    }
		);
		//Similar to the column above
		TableColumn<Procedure, Number> priceColumn = new TableColumn<>("Price");
		priceColumn.setMinWidth(200);
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("procCost"));
		priceColumn.setCellFactory(TextFieldTableCell.<Procedure, Number>forTableColumn(new NumberStringConverter()));
		priceColumn.setOnEditCommit(
				new EventHandler<CellEditEvent<Procedure, Number>>() {
					@Override
					public void handle(CellEditEvent<Procedure, Number> t) {
						((Procedure) t.getTableView().getItems().get(
								t.getTablePosition().getRow())
			                    ).setProcCost(t.getNewValue().doubleValue());
						
						String oldName = ((Procedure) t.getTableView().getItems().get(
								t.getTablePosition().getRow())).getProcName();
						
						int oldProcNum = ((Procedure) t.getTableView().getItems().get(
								t.getTablePosition().getRow())).getProcNo();
						
						double value = t.getNewValue().doubleValue();
						
						Procedure selected = new Procedure(oldName, value);
						selected.setProcNo(oldProcNum);
						
						Connection conn = null;
						Statement stmt = null;
						
						try{
							conn = DriverManager.getConnection(DATABASE_URL);
							stmt = conn.createStatement();
							
							stmt.executeUpdate("UPDATE Procedures SET procCost = " + value + 
									" WHERE procNo = " + oldProcNum);
						}
						catch(SQLException ex){
							ex.printStackTrace();
						}
						finally{
							try{
								conn.close();
								stmt.close();
							}
							catch(SQLException ex){
								ex.printStackTrace();
							}
						}
					}
				}
			);
		
		Button deleteProcedure = new Button("Delete Procedure");
		deleteProcedure.setOnAction(e -> {
			ObservableList<Procedure> selectedProc, allProc;
			allProc = tableView.getItems();
			selectedProc = tableView.getSelectionModel().getSelectedItems();
			Procedure selected = tableView.getSelectionModel().getSelectedItem();
			//Deletes the procedure that was cliked on from the table.
			selectedProc.forEach(allProc::remove);
			
			Connection conn = null;
			Statement stmt = null;
			
			try{
				conn = DriverManager.getConnection(DATABASE_URL);
				stmt = conn.createStatement();
				
				stmt.executeUpdate("DELETE FROM Procedures WHERE procNo = " + selected.getProcNo());
			}
			catch(SQLException ex){
				ex.printStackTrace();
			}
			finally{
				try{
					conn.close();
					stmt.close();
				}
				catch(SQLException ex){
					ex.printStackTrace();
				}
			}
			procedureList().remove(selected);
		});
		
		buttons.getChildren().addAll(addProcedure, deleteProcedure);
		buttons.setSpacing(20);
		GridPane.setConstraints(buttons, 1, 3);
		tableView.setItems(procedureList());
		tableView.getColumns().addAll(nameColumn, priceColumn);
		GridPane.setConstraints(tableView, 1, 4);
		tableView.refresh();
			
		procMenu.getChildren().addAll(procedureName, procedureNameInput, procedurePrice, procedurePriceInput,
				buttons, error, tableView);
		procMenu.setPadding(new Insets(10));
		procMenu.setHgap(10);
		procMenu.setVgap(10);
			
		return procMenu;
	}

	
	public BorderPane procedureInvoice(Patient patient, Invoice newInvoice, ListView<Patient> patients){
		BorderPane invoiceManagement = new BorderPane();
		VBox buttons = new VBox();
		
		Date now = new Date();
		newInvoice.setInvoiceDate(now);
		
		ComboBox<Procedure> dropDown = new ComboBox<Procedure>();
		dropDown.setItems(procedureList());
		dropDown.setPromptText("Procedures");
		
		Button updateProcedures = new Button("Update Procedures");
		updateProcedures.setOnAction(e -> {
			dropDown.setItems(procedureList());
		});
		
		Button createInvoice = new Button("Create Invoice");
		createInvoice.setOnAction(e -> {
			
			Patient p = patients.getSelectionModel().getSelectedItem();
			
			Procedure selected = dropDown.getSelectionModel().getSelectedItem();
			if(selected != null){
				invoiceAmt = selected.getProcCost();
				
				Connection conn = null;
				Statement stmt = null;
				ResultSet res = null;
				
				try{
					conn = DriverManager.getConnection(DATABASE_URL);
					stmt = conn.createStatement();
					
					res = stmt.executeQuery("SELECT * FROM Invoices");
			        
					int invoiceId = 0;
					
			        while(res.next()){
			        	invoiceId = (Integer)res.getObject(1);
			        }
			        
			        invoiceId++;
			        
			        newInvoice.setInvoiceNo(invoiceId);
					
					stmt.executeUpdate("INSERT INTO Invoices VALUES(" + newInvoice.data() +", " 
					+ p.getPatientNo() + ", " + selected.getProcNo() + ")");
				}
				catch(SQLException ex){
					ex.printStackTrace();
				}
				finally{
					try{
						conn.close();
						stmt.close();
					}
					catch(SQLException ex){
						ex.printStackTrace();
					}
				}
			}
			else{
				System.out.println("ERROR: No Procedure Selected");
			}
			
		});
		
		buttons.getChildren().addAll(dropDown, updateProcedures, createInvoice);
		buttons.setSpacing(10);
		invoiceManagement.setLeft(buttons);
		invoiceManagement.setPadding(new Insets(10, 10, 10, 10));
		
		return invoiceManagement;
	}

	//The observable list of procedures that are added to the tableview
	private ObservableList<Procedure> procedureList(){
		ObservableList<Procedure> procedures = FXCollections.observableArrayList();
		Connection conn = null;
		Statement stmt = null;
		ResultSet res = null;
		
		try{
			conn = DriverManager.getConnection(DATABASE_URL);
			stmt = conn.createStatement();
			res = stmt.executeQuery("SELECT * FROM Procedures");
			
			ResultSetMetaData metaData = res.getMetaData();
	        int numberOfColumns = metaData.getColumnCount();
	        
	        while(res.next()){
		        for(int i = 1; i <= numberOfColumns; i+=3){
		        	Procedure dataProc = new Procedure((String)res.getObject(i+1), (Double)res.getObject(i+2));
		        	dataProc.setProcNo((Integer)res.getObject(i));
		        	procedures.add(dataProc);
		        }
	        }
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		finally{
			try{
				conn.close();
				stmt.close();
				res.close();
			}
			catch(SQLException ex){
				ex.printStackTrace();
			}
		}
		return procedures;
	}
	
}
