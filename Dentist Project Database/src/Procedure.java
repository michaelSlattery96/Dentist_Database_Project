
public class Procedure {
	
	private int procNo;
	private static int procNum = 0;
	private String procName;
	private double procCost;
	
	public Procedure(String procName,double procCost){
		this.procName = procName;
		this.procCost = procCost;
		procNum++;
		procNo = procNum;
	}

	public int getProcNo() {
		return procNo;
	}

	public void setProcNo(int procNo) {
		this.procNo = procNo;
	}

	public String getProcName() {
		return procName;
	}

	public void setProcName(String procName) {
		this.procName = procName;
	}

	public double getProcCost() {
		return procCost;
	}

	public void setProcCost(double procCost) {
		this.procCost = procCost;
	}
	
	public String toString(){
		return procName;
	}
	
	public String data(){
		return getProcNo() + ", '" + getProcName() + "', " + getProcCost();
	}

}
