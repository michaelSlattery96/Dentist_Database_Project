import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class Dentist extends Person{
	
	private String password;
	private int dentistNo;
	private static int dentistNum = 0;
	DecimalFormat df = new DecimalFormat("#.##");
	static final String DATABASE_URL = "jdbc:derby:C:/DentistProject;create=true";
	
	public Dentist(String fname, String address, String password){
		super(fname, address);
		this.password = password;
		dentistNo=dentistNum;
		dentistNum++;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDentistNo() {
		return dentistNo;
	}

	public void setDentistNo(int dentistNo) {
		this.dentistNo = dentistNo;
	}
	
	public String toString(){
		return "Name " + getName() + "\n";
	}
	
	public String data(){
		return getDentistNo() + ",'" + getName() + "','" + getAddress() + "','" + getPassword() + "'";
	}
	
	public Stage dentistGUIMenu(Stage primaryStage){
		Stage dentistMenu = new Stage();
		dentistMenu.setTitle(getName() + "'s Section");
		
		//used a tabpane for the rest of the system
		Tab logout = new Tab();
		TabPane tabPane = new TabPane();
		BorderPane border = new BorderPane();
		StackPane stack = new StackPane();
		
		//logout closes the current stage and goes back to the login stage
		logout.setText("Logout");
		Button back = new Button("Logout");
		back.setOnAction(e-> {
			dentistMenu.close();
			primaryStage.show();
		});
		
		stack.getChildren().add(back);
		logout.setContent(stack);
		
		tabPane.getTabs().addAll(patientTab(), procedureTab(), invoiceTab(), paymentsTab(), logout);
		
		//a tab cannot be closed
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		border.setCenter(tabPane);
		Scene scene = new Scene(border, 800, 600);
		dentistMenu.setScene(scene);
		
		return dentistMenu;	
	}
	
	public Tab patientTab(){
		
		Tab patientManage = new Tab();
		BorderPane border = new BorderPane();
		ListView<Patient> patients = new ListView<>();
		HBox addPatientMenu = new HBox();
		
		patientManage.setText("Patient Management");
		Label patientName = new Label("Patient Name:");
		TextField patientNameInput = new TextField();
		Label patientAddr = new Label("Patient Address:");
		TextField patientAddrInput = new TextField();
		Button addPatient = new Button("Add Patient");
		addPatient.setOnAction(e -> {
				if(patientNameInput.getText().isEmpty() || patientAddrInput.getText().isEmpty()){
					;
				}
				else{
					String name = patientNameInput.getText();
					String address = patientAddrInput.getText();
					Patient newPatient = new Patient(name, address);
					
					Connection conn = null;
					Statement stmt = null;
					ResultSet res = null;
					
					try{
						conn = DriverManager.getConnection(DATABASE_URL);
						stmt = conn.createStatement();
						
						res = stmt.executeQuery("SELECT * FROM Patients");
				        
						int patientID = 0;
						
				        while(res.next()){
				        	patientID = (Integer)res.getObject(1);
				        }
				        
				        patientID++;
				        
				        newPatient.setPatientNo(patientID);
				        
				        //Puts the patients data into a table for all patients
				        String addPatientData = "INSERT INTO Patients "
								+ "VALUES(" + newPatient.data() + ")";
				        stmt.executeUpdate(addPatientData);
				        
				        //Puts the patients id number into a different table
				        String addIds = "INSERT INTO Data "
								+ "VALUES(" + getDentistNo() + ", " + newPatient.getPatientNo()  + ")";
				        stmt.executeUpdate(addIds);
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
						catch(Exception ex){
							ex.printStackTrace();
						}
					}
					
					patients.getItems().add(newPatient);
					patientNameInput.clear();
					patientAddrInput.clear();
				}
		});
		
		addPatientMenu.getChildren().addAll(patientName, patientNameInput, patientAddr, patientAddrInput, addPatient);
		addPatientMenu.setPadding(new Insets(10, 10, 10, 10));
		addPatientMenu.setSpacing(10);
		border.setTop(addPatientMenu);
		
		border.setCenter(patientList(patients));
		
		StackPane bottom = new StackPane();
		Button removePatient = new Button("Remove Patient");
		removePatient.setOnAction(e -> {
			//Gets the patient that is selected in the listview
			int selected = patients.getSelectionModel().getSelectedIndex();
			
			Connection connect = null;
			Statement state = null;
			try{
				connect = DriverManager.getConnection(DATABASE_URL);
				
				state = connect.createStatement();
				
				//Deletes the row that has the chosen patient number
				state.executeUpdate("DELETE FROM Patients "
						+ "WHERE patientNo = " + patients.getItems().get(selected).getPatientNo());
				
				state.executeUpdate("DELETE FROM Data "
						+ "WHERE patientNo = " + patients.getItems().get(selected).getPatientNo());
				
				patients.getItems().remove(selected);
					
			}
			catch(SQLException ex){
				ex.printStackTrace();
			}
			
		});
		bottom.getChildren().add(removePatient);
		bottom.setPadding(new Insets(10, 10, 30, 10));
		border.setBottom(bottom);
		
		patientManage.setContent(border);
		
		return patientManage;
	}
	
	private Tab procedureTab(){
		
		Tab Procedure = new Tab();
		
		Procedure.setText("Procedure Management");
		Patient general = new Patient();
		//Calls on the method ProcList in the Patient class
		Procedure.setContent(general.ProcList());
		
		return Procedure;
	}
	
	private Tab invoiceTab(){
		
		Tab Invoice = new Tab("Invoice Managment");
		BorderPane border = new BorderPane();
		Patient chosen = new Patient();
		Invoice created = new Invoice();
		ListView<Patient> patients = new ListView<Patient>();
		
		Button updatePatients = new Button("Update Patients");
		updatePatients.setOnAction(e -> {
			patientList(patients).getItems().clear();
			patientList(patients);
		});
		
		border.setLeft(chosen.invoice_Proc_Stuff(chosen, created, patients));
		border.setCenter(patientList(patients));
		border.setBottom(updatePatients);
		border.setPadding(new Insets(10, 10, 10, 10));
		Invoice.setContent(border);
		
		return Invoice;
	}

	private Tab paymentsTab(){
		Tab payments = new Tab();
		
		payments.setText("Payments");
		
		GridPane grid = new GridPane();
		HBox errorBox = new HBox();
		
		Label howMuch = new Label("How much: €");
		GridPane.setConstraints(howMuch, 0, 1);
		TextField amt = new TextField();
		GridPane.setConstraints(amt, 1, 1);
		
		ListView<Invoice> invoices = new ListView<Invoice>();
		
		invoiceList(invoices);
		
		GridPane.setConstraints(invoices, 1, 2);
		
		Button select = new Button("Pay");
		GridPane.setConstraints(select, 1, 3);
			select.setOnAction(e -> {
				errorBox.getChildren().clear();
				Invoice selectedInvoice = invoices.getSelectionModel().getSelectedItem();
				
				if(amt.getText() == null || selectedInvoice == null){
					Label error = new Label("Please enter a payment and choose an invoice");
					error.setTextFill(Color.RED);
					errorBox.getChildren().add(error);
					GridPane.setConstraints(errorBox, 1, 6);
				}
				else{
					String remainText = df.format(selectedInvoice.getInvoiceAmt() - Double.parseDouble(amt.getText()));
					double amtRemaining = Double.parseDouble(remainText);
					selectedInvoice.setInvoiceAmt(amtRemaining);
					
					Date now = new Date();
					String date = new SimpleDateFormat("dd/MM/yyyy").format(now);
					
					Connection conn = null;
					Statement stmt = null;
					ResultSet res = null;
					try{
						conn = DriverManager.getConnection(DATABASE_URL);
						stmt = conn.createStatement();
						res = stmt.executeQuery("Select * From Payments");
						
						int paymentNo = 0;
						
						while(res.next()){
							paymentNo = (Integer)res.getObject(1);
						}
						
						paymentNo++;
						
						stmt.executeUpdate("INSERT INTO Payments VALUES(" + paymentNo + ", " + 
								Double.parseDouble(amt.getText()) + ", '" + date + "')");
						
						stmt.executeUpdate("UPDATE Invoices SET invoiceAmt = " + amtRemaining +
								" WHERE invoiceNo = " + selectedInvoice.getInvoiceNo());
						
						if(amtRemaining <= 0){
							selectedInvoice.setPaid(true);
							stmt.executeUpdate("DELETE FROM Invoices WHERE invoiceNo = " 
									+ selectedInvoice.getInvoiceNo());
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
					
					invoiceList(invoices).getItems().clear();
					invoiceList(invoices);
				}
			});
		
		Button update = new Button("Update Payments");
		update.setOnAction(e -> {
			invoiceList(invoices).getItems().clear();
			invoiceList(invoices);
		});
		GridPane.setConstraints(update, 1, 4);
		
		grid.getChildren().addAll(howMuch, amt, invoices, select, update, errorBox);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		payments.setContent(grid);
		
		return payments;
	}
	
	private ListView<Patient> patientList(ListView<Patient> patients){
		Connection conn = null;
		Statement stmt = null;
		ResultSet res = null;
		try{
			conn = DriverManager.getConnection(DATABASE_URL);
			
			stmt = conn.createStatement();
			
			//Uses a nested select to compare patient and dentist id numbers and only put the data that matches into
			//the listview.
			res = stmt.executeQuery("SELECT * "
									+ "FROM Patients P "
									+ "WHERE P.patientNo IN "
									+ "(SELECT patientNo "
									+ "FROM Data D "
									+ "WHERE D.dentistNo = " + getDentistNo() + ")");
			
			ResultSetMetaData metaData = res.getMetaData();
	        int numberOfColumns = metaData.getColumnCount();
			
	        while(res.next()){
				for(int i = 1; i <= numberOfColumns; i+=3){
					Patient dataPatient = new Patient((String)res.getObject(i+1), (String)res.getObject(i+2));
					dataPatient.setPatientNo((Integer)res.getObject(i));
					patients.getItems().add(dataPatient);
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
		
		return patients;
	}
	
	private ListView<Invoice> invoiceList(ListView<Invoice> invoices){
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet res = null;
		try{
			conn = DriverManager.getConnection(DATABASE_URL);
			stmt = conn.createStatement();
			res = stmt.executeQuery("SELECT * "
									+ "FROM Invoices I "
									+ "WHERE I.patientNo IN "
									+ "(SELECT patientNo "
									+ "FROM Data D "
									+ "WHERE D.dentistNo = " + getDentistNo() + ")");
			
			ResultSetMetaData metaData = res.getMetaData();
	        int numberOfColumns = metaData.getColumnCount();
			
	        while(res.next()){
				for(int i = 1; i <= numberOfColumns; i+=6){
					Invoice dataInvoice = new Invoice();
					dataInvoice.setInvoiceNo((Integer)res.getObject(i));
					dataInvoice.setInvoiceAmt(Double.parseDouble((res.getObject(i+1).toString())));
					dataInvoice.setInvoiceDate((Date)res.getObject(i+2));
					dataInvoice.setPaid((Boolean)res.getObject(i+3));
					invoices.getItems().add(dataInvoice);
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
		
		return invoices;
	}
	
}
