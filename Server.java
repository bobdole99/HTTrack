import java.util.*;
import java.net.*;
import java.io.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
/*
CPSC 441 University of Calgary
Michael Gugala
Assignment 1

Server is responsible for reading requests from user determining if they are valid
reporting any errors and writing to client
*/

class Server implements Runnable {
    
    private int portNumber;
    private String ok = "200 OK";
    private String badRequest = "400 Bad Request";
    private String notFound = "404 File not Found";
    
    
    /*Constructor initialize port number*/
    public Server(int number){
        portNumber = number;
    }

    
    /*
    Checks to see the request is in a proper format, begine with GET
    end with HTTP/1.0 or with HTTP/1.1 (not sure how to just use 1.0)
    @param input to check
    @param return is true
    */
    public boolean checkFormat(String input){
    	if(!input.contains("HTTP") || !input.contains("GET"))
    		return false;
        int begin = input.indexOf(" HTTP");
        return input.startsWith("GET") && (input.startsWith(" HTTP/1.0",begin) 
        || input.startsWith(" HTTP/1.1",begin));
    }
    
    
    /*
    Does the file exist in the current directory?
    @param fileName name of file to check
    @return true if file found
    */
    public boolean isFile(String fileName){
        boolean exists = new File(fileName).exists();
        return exists;
    }
    
    
    	/*Get the last modified date of the file, formats the date as well
    	@param fileName name of file for date modified
    	@return date
    	*/
    	public String getLastModified(String fileName){
    		long lastMod = new File(fileName).lastModified();
  		SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
  		String date = formatter.format(lastMod);
    		return date;
    	}
    
    
    	/*Gets size of file
   	@return size of file in bytes
   	*/
	public long getLength(String fileName){
        	long length = new File(fileName).length();
        	return length;
    	}


	/*Get the current system date and formats it
	@return dateNow current date
	*/
	public String getDate() {
  		Calendar currentDate = Calendar.getInstance();
  		SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
  		String dateNow = formatter.format(currentDate.getTime());
  		return dateNow;
  	}

    	/*
    	Gets the name of the file on the spcified address
    	@param input file name to extract from
    	@param return the file name
    	*/
    	public String getFileName(String input){
        	int end = input.indexOf(" HTTP");
        	int begin = end - 2;
        
        	for(;; --begin){
            	if(input.charAt(begin) == '/')
                	break;
            	if(input.charAt(begin) == ' ')
                	break;
        	}
        	return input.substring(begin+1, end);
    	}
    

	public void run(){
	
	try{     //create a socket on a given port number
        ServerSocket welcomeSocket = new ServerSocket(portNumber); 
        
        while(true) {
            //create a socket
            Socket connectionSocket = welcomeSocket.accept();
            
            //attach socket read from client
            BufferedReader inFromClient = 
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            
            //attach socket write to client
            DataOutputStream outToClient = 
                new DataOutputStream(connectionSocket.getOutputStream());
            
            //getting input from client
            String clientRequest = inFromClient.readLine();
            
            //if requst is valid transmit info to client
            if (checkFormat(clientRequest)){
                        //get the name of the file
            		String fileName = getFileName(clientRequest);
            		
            		//check for file name
            		if(isFile(fileName)){
            		
            			FileReader fileRead = new FileReader(fileName);
                        	BufferedReader buffRead = new BufferedReader(fileRead);
                        	
                        	//send out information to client, could have made all this into a seperate function
                        	outToClient.writeBytes("\nHTTP/1.0 "+ok+"\n");
                        	outToClient.writeBytes("Current date: "+ getDate()+"\n");
                        	outToClient.writeBytes("Server: localhost "+"\n");                                           
                        	outToClient.writeBytes("Last Modified: " + getLastModified(fileName)+"\n");                       
                        	outToClient.writeBytes("Content Length: " +getLength(fileName)+"\n\n");

                        	//send out file requested
            			while(buffRead.ready())
                        		outToClient.writeBytes(buffRead.readLine()+"\n");
                        	buffRead.close();
                        }
                        //close everything up
                        else{
                        	outToClient.writeBytes(notFound+"\n");
                        }
                    }
             else{
                 outToClient.writeBytes(badRequest+"\n");
             }
             
            connectionSocket.close();
        }
        }
        catch(IOException e){
        	System.out.println("failed to open file"); 
        }
    }
} 
