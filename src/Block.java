import java.sql.*;
import java.util.*;
import java.io.UnsupportedEncodingException;

public class Block {
	
	//instance variables
	private int index;
	private Timestamp timestamp; 
	private Transaction transaction; 
	private String nonce; 
	private String hash;
	private String previousHash;  
	
	// empty constructor
	public Block() {
		
	} 
	//actual constructor
	public Block ( int index, Timestamp timestamp, Transaction transaction, String nonce, String previousHash, String hash){
		this.index=index;
		this.nonce=nonce;
		this.previousHash= previousHash;
		this.timestamp= timestamp;
		timestamp = new Timestamp(System.currentTimeMillis());
		this.transaction= transaction;
		this.hash=hash;
	}
	//getters and setter 
	public int getIndex(){
		return index;
	}
	
	public Transaction getTransaction(){
		return transaction;
	}
	
	public String getNonce(){
		return nonce;
	}
	
	public void setNonce(String string){
		nonce = string;
		try {
			hash = Sha1.hash(this.toString());
		}catch(UnsupportedEncodingException e){
			System.out.println("");
		}
		
	}
	
	public String getHash(){
		return hash;
	}
	
	public String getPreviousHash(){
		return previousHash;
	}
	
	
	public Timestamp getTimestamp(){
		return timestamp;
	}

	public String toString() { //toString for block data
		return timestamp.toString() + ":" + transaction.toString()+ "." + nonce+ previousHash;
	}
}
