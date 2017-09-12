import java.util.Date;
public class Payment {
	
	private int paymentNo;
	private static int paymentNum = 0;
	private double paymentAmt;
	private Date paymentDate;
	
	public Payment(double paymentAmt, Date paymentDate){
		this.paymentAmt = paymentAmt;
		this.paymentDate = paymentDate;
		paymentNum++;
		paymentNo = paymentNum;
	}

	public double getPaymentAmt() {
		return paymentAmt;
	}

	public void setPaymentAmt(double paymentAmt) {
		this.paymentAmt = paymentAmt;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public int getpaymentNo() {
		return paymentNo;
	}

	public void setpaymentNo(int paymentNo) {
		this.paymentNo = paymentNo;
	}
	
	public String toString(){
		return "Payment No: " + paymentNo + "\nPayment Amount: €" + paymentAmt +
				"\nPayment Date: " + paymentDate;
	}

}
