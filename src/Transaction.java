import java.sql.*;
import java.util.*;
import java.io.UnsupportedEncodingException;

public class Transaction {
	
	//instance variables
	private String sender;
	private String receiver;
	private int amount;
	
	//empty constructor
	public Transaction (){
		
	}
	
	//actual constructor 
	public Transaction (String sender, String receiver, int amount){
		this.sender= sender;
		this.receiver = receiver;
		this.amount = amount;
	}
	
	//getters and setters
	public String getSender(){
		return sender;
	}
	public String getReceiver(){
		return receiver;
	}
	public int getAmount(){
		return amount;
	}
	public String toString() { //toString for transaction data
		return sender + ":" + receiver + "=" + amount; }
}
