import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
public class Patient extends Person{
		
	private int patientNo;
	private static int patientNum = 0;
	static final String DATABASE_URL = "jdbc:derby:C:/DentistProject;create=true";
	
	public Patient(String fName, String address){
		super(fName, address);
		patientNum++;
		patientNo = patientNum;
	}
	
	public Patient(){
		
	}

	public int getPatientNo() {
		return patientNo;
	}

	public void setPatientNo(int patientNo) {
		this.patientNo = patientNo;
	}
	
	public static int getPatientNum() {
		return patientNum;
	}

	public static void setPatientNum(int patientNum) {
		Patient.patientNum = patientNum;
	}

	public String toString(){
		return "Patient No: " + patientNo + "\nPatient Name: " + getName() + 
				"\nPatient Address: " + getAddress() + "\n------------------------------\n";
	}
	
	public void print(){
		System.out.println("Patient No: " + patientNo + "\nPatient Name: " + getName() + 
				"\nPatient Address: " + getAddress());
	}
	
	public String data(){
		return getPatientNo() + ", '" + getName() + "', '" + getAddress() + "'";
	}
	
	//Calls a method in invoice
	public BorderPane invoice_Proc_Stuff(Patient patient, Invoice newInvoice, ListView<Patient> patients){	
		Connection conn = null;
		Statement stmt = null;
		ResultSet res = null;
		
		try{
			conn = DriverManager.getConnection(DATABASE_URL);
			stmt = conn.createStatement();
			res = stmt.executeQuery("SELECT * FROM Patients WHERE patientNo = " + 1);
			
			ResultSetMetaData resMeta = res.getMetaData();
			int noOfColumns = resMeta.getColumnCount();
			
			while(res.next()){
				for(int i = 1; i < noOfColumns; i+=3){
					patient = new Patient((String)res.getObject(i+1), (String)res.getObject(i+2));
					patient.setPatientNo((Integer)res.getObject(i));
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
		
		return newInvoice.procedureInvoice(patient, newInvoice, patients);
	}

	//Calls a method in invoice
	public GridPane ProcList(){
		Invoice p = new Invoice();
		return p.procedureGUI();
	}

}
