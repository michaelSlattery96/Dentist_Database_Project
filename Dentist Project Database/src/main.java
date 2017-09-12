import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

//Michael Slattery
//R00138543 DCOM2B

public class main extends Application{
	
	Button signIn;
	Button createAccount;
	static final String DATABASE_URL = "jdbc:derby:C:/DentistProject;create=true";

	public static void main(String[] args){
		//Starts the GUI
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage){

		primaryStage.setTitle("Dentist Project");
		
		BorderPane root = new BorderPane();
		GridPane grid = new GridPane();
		Scene dentistLogIn = new Scene(root, 600, 400);
		
		//creates all tables
		createTables();
		
		//A GridPane for the signing in section that dentists use.
		signIn = new Button("Sign In");
		signIn.setOnAction(e -> {
				
			//clears the grid when the sign in button is clicked to clear the screen.
			grid.getChildren().clear();
				
			//Setting where the labels, textfields and buttons will be placed.
			Label name = new Label("Name:");
			GridPane.setConstraints(name, 0, 0);
				
			TextField nameInput = new TextField();
			GridPane.setConstraints(nameInput, 1, 0);
				
			Label pass = new Label("Password:");
			GridPane.setConstraints(pass, 0, 1);
				
			TextField passInput = new TextField();
			GridPane.setConstraints(passInput, 1, 1);
				
			Label id = new Label("ID No:");
			GridPane.setConstraints(id, 0, 2);
			
			TextField idInput = new TextField();
			GridPane.setConstraints(idInput, 1, 2);
				
			Button logIn = new Button("Log In");
			GridPane.setConstraints(logIn, 1, 3);
			HBox error = new HBox();
			Label wrong = new Label();
			
			logIn.setOnAction(d -> {
				//Checks if all info entered is correct
				String dName = nameInput.getText();
				String dPass = passInput.getText();
				int dentistNo = isInt(idInput.getText());
				
				Connection conn = null;
				Statement stmt = null;
				ResultSet res = null;
				
				try{
					conn = DriverManager.getConnection(DATABASE_URL);
					
					stmt = conn.createStatement();
					
					res = stmt.executeQuery("SELECT * FROM Dentists");
					
					ResultSetMetaData metaData = res.getMetaData();
			        int numberOfColumns = metaData.getColumnCount();
			        
			        boolean loggedIn = false;
			        //checks if the table is empty
			        if(!(res.next())){
						error.getChildren().clear();
						Label noDentist = new Label("ERROR:No dentists in system");
						noDentist.setTextFill(Color.RED);
						error.getChildren().add(noDentist);
						GridPane.setConstraints(error, 1, 4);
					}
			        else{
			        	//use a do while since the first line of res is being read
				        do{
				        	//increments in 4's since a Dentist consists of 4 attributes.
				        	for(int i = 1; i <= numberOfColumns; i+=4){
					        	if((dName.equals(res.getObject(i+1)) && dPass.equals(res.getObject(i+3)) && ((dentistNo-1) == (Integer)res.getObject(i)))){
					        		loggedIn = true;
					        		Dentist returningDentist = new Dentist(dName, (String)res.getObject(i+2), dPass);
					        		returningDentist.setDentistNo(dentistNo-1);
					        		returningDentist.dentistGUIMenu(primaryStage).show();
					        		wrong.setText("");
									primaryStage.close();
									break;
					        	}
					        	if(!(dName.equals(res.getObject(i+1)) || dPass.equals(res.getObject(i+3)) || ((dentistNo-1) == (Integer)res.getObject(i)))){
					        		error.getChildren().clear();
					        		wrong.setText("ERROR:Data not found");
									wrong.setTextFill(Color.RED);
									error.getChildren().add(wrong);
									GridPane.setConstraints(error, 1, 4);
								}
					        }
				        	//stops the error message appearing after logging in successfully
				        	if(loggedIn == true){
				        		break;
				        	}
				        }
				        while(res.next());
				        
				        nameInput.clear();
						passInput.clear();
						idInput.clear();
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
					catch(Exception ex){
						ex.printStackTrace();
					}
				}
			});
				
			grid.setPadding(new Insets(100, 20, 20, 60));
			grid.setVgap(20);
			grid.getChildren().addAll(name, nameInput, pass, passInput, id, idInput, logIn, error);
		});
		
		//Block of code used to create an account
		createAccount = new Button("Create Account");
		createAccount.setOnAction(e -> {
			
			grid.getChildren().clear();
			
			Label name = new Label("Name:");
			GridPane.setConstraints(name, 0, 0);
			
			TextField nameInput = new TextField();
			GridPane.setConstraints(nameInput, 1, 0);
			
			Label address = new Label("Address:");
			GridPane.setConstraints(address, 0, 1);
			
			TextField addressInput = new TextField();
			GridPane.setConstraints(addressInput, 1, 1);
			
			Label pass = new Label("Password:");
			GridPane.setConstraints(pass, 0, 2);
			
			TextField passInput = new TextField();
			GridPane.setConstraints(passInput, 1, 2);
			
			Button create = new Button("Create");
			GridPane.setConstraints(create, 1, 3);
			HBox id = new HBox();
			HBox acc = new HBox();
			HBox error = new HBox();
			create.setOnAction(s -> {
				
				id.getChildren().clear();
				acc.getChildren().clear();
				error.getChildren().clear();
				
				String dName = nameInput.getText();
				String dAddr = addressInput.getText();
				String dPass = passInput.getText();
				if(isNull(dName, dAddr, dPass) == true ){
					grid.getChildren().removeAll(acc, id);
					Dentist newDentist = new Dentist(dName, dAddr, dPass);
					
					Connection conn = null;
					Statement stmt = null;
					ResultSet res = null;
					
					try{
						conn = DriverManager.getConnection(DATABASE_URL);
						
						stmt = conn.createStatement();
						
						res = stmt.executeQuery("SELECT * FROM Dentists");
						
				        int dentistID = 0;
				        
				        while(res.next()){
				        	dentistID++;
				        }
				        
				        newDentist.setDentistNo(dentistID);
				        
				        String query = "INSERT INTO Dentists "
								+ "VALUES (" + newDentist.data() + ")";
				        stmt.executeUpdate(query);
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
					
					Label congrats = new Label("Account created.");
					Label dentistNo = new Label("Your id is: " + (newDentist.getDentistNo()+1));
					
					acc.getChildren().add(congrats);
					GridPane.setConstraints(acc, 1, 4);
					
					id.getChildren().add(dentistNo);
					GridPane.setConstraints(id, 1, 5);
					
					grid.getChildren().addAll(acc, id);
					
					nameInput.clear();
					addressInput.clear();
					passInput.clear();
				}
				else{
					error.getChildren().clear();
					Label wrong = new Label("ERROR:All fields must be filled");
					wrong.setTextFill(Color.RED);
					error.getChildren().add(wrong);
					GridPane.setConstraints(error, 1, 4);
				}
			});

			grid.setPadding(new Insets(100, 20, 20, 60));
			grid.setVgap(20);
			grid.getChildren().addAll(name, nameInput, address, addressInput, pass, passInput, create, error);
		});
		
		signIn.setMaxWidth(Double.MAX_VALUE);
		createAccount.setMaxWidth(Double.MAX_VALUE);
		
		VBox v = new VBox(50, signIn, createAccount);
		v.setPadding(new Insets(120, 20, 20, 20));
		v.setStyle("-fx-background-color: #336698;");
		
		root.setLeft(v);
		root.setCenter(grid);
		
		primaryStage.setScene(dentistLogIn);
		primaryStage.show();
	}
	//Method to see if all values are entered
	private boolean isNull(String name, String addr, String pass){
		if(name.isEmpty() == true || addr.isEmpty() == true || pass.isEmpty() == true){
			return false;
		}
		else{
			return true;
		}
	}
	//Method to see if integer is entered
	private int isInt(String message){
		try{
			return Integer.parseInt(message);
		}
		catch(java.lang.NumberFormatException exception){
			System.out.println("ERROR: Number Format Exception");
			return -1;
		}
	}
	
	private void createTables(){
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = DriverManager.getConnection(DATABASE_URL);
			stmt = conn.createStatement();
			
			stmt.executeUpdate("CREATE TABLE Dentists("
					+ "dentistId INT PRIMARY KEY, "
					+ "dentistName VARCHAR(30), "
					+ "dentistAddress VARCHAR(100), "
					+ "password VARCHAR(32))");
			
			stmt.executeUpdate("CREATE TABLE Patients("
					+ "patientNo INT PRIMARY KEY, "
					+ "patientName VARCHAR(30), "
					+ "patientAddress VARCHAR(100))");
			
			stmt.executeUpdate("CREATE TABLE Data("
					+ "dentistNo INT, "
					+ "patientNo INT)");
			
			stmt.executeUpdate("CREATE TABLE Invoices("
					+ "invoiceNo INT PRIMARY KEY, "
					+ "invoiceAmt DOUBLE, "
					+ "invoiceDate DATE, "
					+ "isPaid BOOLEAN, "
					+ "patientNo INT, "
					+ "procedureNo INT)");
			
			stmt.executeUpdate("CREATE TABLE Procedures("
					+ "procNo INT PRIMARY KEY, "
					+ "procName VARCHAR(50), "
					+ "procCost DOUBLE)");
			
			stmt.executeUpdate("CREATE TABLE Payments("
					+ "paymentNo INT PRIMARY KEY, "
					+ "paymentAmt DOUBLE, "
					+ "paymentDate DATE)");
		}
		catch(SQLException ex){
			
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
