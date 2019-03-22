import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.Random;
import java.sql.Timestamp;
import java.util.Scanner; 
import java.io.*;  
import java.util.*;
import java.util.concurrent.ThreadLocalRandom; 

public class BlockChain {
  
  private ArrayList<Block> blockList;
  public BlockChain() {
	  blockList = new ArrayList<>();
  }
  
  public ArrayList<Block> getChainArrayList() {
    return blockList;
  }
  
  //PART1---------------------------------------------------------------------------------------------
  public static BlockChain fromFile(String fileName) throws IOException {

    BlockChain blockList = new BlockChain();
    
    //variables
    int index = 0; //actual index
    int blockIndex = 0; // index for line within the blocks of text file
    
    FileReader reader1 = new FileReader(fileName); //imported FileReader
    BufferedReader reader = new BufferedReader(reader1); //imported BufferedReader
    
    String line = ""; //create blank strings
    String nonce = "";
    String sender = "";
    String receiver = "";
    int amount = 0;
    
    java.sql.Timestamp timestamp = new Timestamp(0);
    Transaction transaction = new Transaction(); 
    
    String hash = "";
    String previousHash = "00000"; //first block must have previous hash of 00000
    
    //read line by line, extracting each attribute of block
    while((line = reader.readLine()) != null) { 
     
    	if(blockIndex == 0) { 
        index = Integer.parseInt(line); 
        blockIndex++;
        
      } else if(blockIndex == 1) { 
        long timestamp1 = Long.parseLong(line);
        timestamp = new Timestamp(timestamp1);
        blockIndex++;
        
      } else if(blockIndex == 2) {
        sender = line;
        blockIndex++;
        
      } else if(blockIndex == 3) { 
        receiver = line;
        blockIndex++;
        
      } else if(blockIndex == 4) { 
        amount = Integer.parseInt(line);
        blockIndex++;
        
      } else if(blockIndex == 5) {
        nonce = line;
        blockIndex++;
        
      } else if(blockIndex == 6) { 
        hash = line;
        blockIndex = 0; //reset index
        
        transaction = new Transaction(sender, receiver, amount);
        Block block = new Block(index, timestamp, transaction, nonce, previousHash, hash);
        previousHash = new String(hash); 
        
        //add to blockChain
        blockList.getChainArrayList().add(block); 
      }
    }
    
    reader.close();
    reader1.close();
    return blockList;
  }
  
  
  public void toFile (String fileName) {
    try {
      //create and write to file
      PrintWriter pencil = new PrintWriter(fileName, "UTF-8");
      
      for(Block block : blockList) { //traverse through blockchain block by block 
        pencil.println(Integer.toString(block.getIndex()));
        pencil.println(Long.toString(block.getTimestamp().getTime()));
        pencil.println(block.getTransaction().getSender());
        pencil.println(block.getTransaction().getReceiver());
        pencil.println(Integer.toString(block.getTransaction().getAmount()));
        pencil.println(block.getNonce());
        pencil.println(block.getHash());        
      }
      
      System.out.println("file was created");
      pencil.close();
      
    } catch (IOException e) {
      System.out.println("file could not be created");
    }
    
  }
  
  //PART2---------------------------------------------------------------------------------------------- 
  public boolean validateBlockchain() {
    for(int i = 0; i < blockList.size(); i++ ) {
    	Block block = blockList.get(i);
    	
    	String expectedHash = "";
        String actualHash = block.getHash();
        String nextBlockHashPrev = null;  
      
      //get next block's previous hash
      if (i+1!=blockList.size()) { 
    	  nextBlockHashPrev = blockList.get(i+1).getPreviousHash();
      }
      
      try {
        expectedHash = Sha1.hash(block.toString());
      } catch(UnsupportedEncodingException e) {
        System.out.println("could not create transaction hash");
        return false;
      }
      
      //verifying transaction using getBalance method (Part 3) 
      
      //check if the sender's balance is less than transaction amount
      if (getBalance(block.getTransaction().getSender(), i) < block.getTransaction().getAmount()) { 
    	//check if any users (other than bitcoin) have a negative balance
    	if (!block.getTransaction().getSender().equals("bitcoin")) { 
          System.out.println("invalid. sender sending more money then they have");
          return false;
        }
      }
      
      //verifying index
      if(block.getIndex() != i) {
        System.out.println("indexes are not equivalent");
        return false;
      }
      
      //verifying hash 
      if(!expectedHash.equals(actualHash)) {
        System.out.println("hashes are not equivalent");
        return false;
      }
      
      //verifying next block's previous hash
      if (i+1 != blockList.size()) { 
          if(!nextBlockHashPrev.equals(actualHash)) {
            System.out.println("previous hashes are not equivalent");
            return false;
          }
        }
     
    }
    
    //transaction has passed all inspections, therefore true
    return true;  
  }
  
  //PART3------------------------------------------------------------------------------------------
  public int getBalance(String name) { //get user's balance
    
	int balance = 0;
    for (Block block : blockList) {
      if(block.getTransaction().getSender().equals(name)) { //if  sender, subtract the transaction amount from  balance
        balance -= block.getTransaction().getAmount();
      } else if (block.getTransaction().getReceiver().equals(name)) { //if receiver, add the transaction amount balance
        balance += block.getTransaction().getAmount();
      }
    }
    return balance; 
  }
  
  //get user's balance upto certain transaction number
  public int getBalance(String name, int x) { 
    
	int balance = 0;
    for (int i = 0; i < x; i++) {
      Block block = blockList.get(i);
      //subtract amount from balance if person is the sender
      if(block.getTransaction().getSender().equals(name)) {
        balance -= block.getTransaction().getAmount();

      } else if (block.getTransaction().getReceiver().equals(name)) {  //add amount to balance if person is receiver
        balance += block.getTransaction().getAmount();
      }
    }
    return balance; 
  }
  
  //PART4------------------------------------------------------------------------------------------
  public Block generateNonce(Block block) {
	Random rand = new Random (); //imported Random
	String nonceString = "";
    int counter = 0;
    
    while(!block.getHash().substring(0, Math.min(block.getHash().length(), 5)).equals("00000")){
      for (int i=0; i < rand.nextInt(18)+1; i++) { //give nonce value a random length of 1-18
        nonceString += (char) rand.nextInt(94)+33; //fill nonce value random ASCII values of [33, 126] 
      }
      block.setNonce(nonceString); //add nonce to block
      nonceString = ""; //reset nonce
      counter++;
    } 
    System.out.println(counter+"-amount of attempts to generate nonce"); //print the number attempts it took to obtain the nonce value
    return block;
  }
  
  public void add(Block block) {
	  blockList.add(block); //simply add block to block chain
  }
  
  
  //MAIN (including parts 5 and 6)---------------------------------------------------------------------------
  public static void main (String[] args) {
	BlockChain blockChain = new BlockChain();
	  
    Scanner scanner = new Scanner(System.in); //create scanner to process user input, imported Scanner
    System.out.println("enter the name of the file that contains the blocks that will be added to the blockChain");
    String file = scanner.next();
   
    try {
      blockChain = BlockChain.fromFile(file); //Part1 reading and writing
      ArrayList<Block> blockList = blockChain.getChainArrayList();

      if (blockChain.validateBlockchain()) {//Part2 validation
        System.out.println("blockchain has been validated\n");
        
        boolean more = true; //Part5, create boolean variable to allow user to keep adding transactions
        while(more != false) { //back to Part3 to add transaction
          System.out.println("what is the name of the transaction's sender");
          String newSender = scanner.next();
          System.out.println("what is the name of the transaction's recipient");
          String newReceiver = scanner.next();
          System.out.println("how much money is being sent in this transaction");
          int newAmount = scanner.nextInt();
          
          if (blockChain.getBalance(newSender) < newAmount) {
            System.out.println("transaction cannot be made. sender is sending more money than they have");
          } else {
        	//Part4 to generate nonce value and add block to block chain
        	System.out.println("transaction may take a minute to process...");
            Block newBlock = new Block(blockList.size(), new Timestamp(System.currentTimeMillis()), new Transaction(newSender, newReceiver, newAmount), "", new String(blockList.get(blockList.size()-1).getHash()), "");
            newBlock = blockChain.generateNonce(newBlock); 
            blockChain.add(newBlock);
            System.out.println("transaction has been added. you can view the blockchain data below");
            System.out.println(blockList);
            String addMore = "";
            
            do { 
              //ask user if they want to continue adding transactions
              System.out.println("");
              System.out.println("would you like to add more transactions?('yes' or 'no')");
              addMore = scanner.next();
              if (addMore.equals("no")) {//user inputs 'no'
                more = false;
              }
            } while ((!addMore.equals("yes")) && (!addMore.equals("no"))); //user must enter either 'yes' or 'no'
          }
        }
        
        //Part6 writing blockchain to textfile
        System.out.println("enter the name of the file that will store the blockchain. Don't forget '.txt' at the end");
        blockChain.toFile(scanner.next());       
        
      } else {
        System.out.println("blockchain is invalid");
      }
      
    } catch (IOException e) {
      System.out.println("file not found");
    }
    scanner.close();
  }
  
}