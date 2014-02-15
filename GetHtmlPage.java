import java.util.*;
import java.io.*;
import java.net.*;
/*
CPSC 441 University of Calgary
Michael Gugala
Assignment 1

GetHtmlPage is responsible for opening up a connection, looking for 
reference within a particular html page saving file to disk. GetHtmlPage
recursively opens link looking for references
*/
class GetHtmlPage{

	private int portNumber;
	private String hostName;
	FileWriter dbaseStream;
	BufferedWriter dbaseOut;
	File dir;
	
	/*Constructor, set up output to dbase and creates directory*/
	public GetHtmlPage(int num, String name) throws Exception{
		portNumber = num;
		hostName = name;
		dbaseStream = new FileWriter("dbase.txt");
  		dbaseOut = new BufferedWriter(dbaseStream);
  		dir = new File("./"+hostName);
  		dir.mkdir();

	}
		
		
	/* getFileName parses lines of code that contain a link, looks for the 
	name of the file and returns it
	@param url to parse and look for file name
	@return the parsed url, returns file name
	*/
	public String getFileName(String url){
		//find the index of the suffix .html
		int indexOfHtml = url.lastIndexOf(".html")+5;
		
		//if we find an equals sign break from loop and add 2 to the index
		//if we find an foreward slash break from loop add 1
		int i = indexOfHtml;
		for (;; --i){
			if(url.charAt(i) == '='){
				i = i+2;
				break;
			}
			if(url.charAt(i) == '/'){
				i = i +1;
				break;
			}
		}
		return url.substring(i, indexOfHtml);

	}	


	/* getReference gets the reference from the input (what was read from the server)
	@param input line to be parse
	@param address the current address, will be updated
	@return new address
	*/
	public String getReference(String input, String address){
		
		//get new file name and olf file names
		String newFileName = getFileName(input+"\n");
		String oldFileName = getFileName(address+"\n");
		
		//get old/new file name index
		int newIndex = input.indexOf(newFileName);
		int oldIndex = address.indexOf(oldFileName);
		
		//append file name to old address, deals with url type  d1.html
		if(!input.contains("http") && input.charAt(newIndex - 1) != '/'){

			//replace file names
			address = address.replaceAll(oldFileName, newFileName);
		}
		//deals with url type test/a/d1.html
		else if(!input.contains("http") && input.charAt(newIndex -1 ) == '/'){		
			int equalSign = input.indexOf('=') + 2;
			String temp = input.substring(equalSign, (input.indexOf(".html")+5));
			address = address.replace(oldFileName, temp);
		}
		//deals with url type http://test/a/d1.html
		else{
			int start = input.indexOf("http");
			int end = input.indexOf(".html") + 5;
			address = input.substring(start, end);

		}
		return address;
	}
			

	/*open sets up a connection with the server, reads to server and writes
	data into a text file, call to getFilename and getReference in order to
	update new address and file name
	@param address current url address
	@param fileName name of file to open for parsing
	@param levelNum depth to search and grap web pages
	*/
	public void open(String address, String fileName, int levelNum) throws Exception{
       		if(levelNum == 0){
       			//dbaseOut.close();
			return;
		}
		else{       
        	//create socket connet to port
        	Socket clientSocket = new Socket(hostName, portNumber);
        
        	//set up connection, attach socket to output
        	DataOutputStream outToServer = new DataOutputStream (clientSocket.getOutputStream());
        
        	//attach socket to input, read from server
        	BufferedReader inFromServer = 
        	new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        	//write to server
        	outToServer.writeBytes("GET "+ address + " HTTP/1.0\r\n\n");
        
        	//set up a buffered writer to file on the local machine
		FileWriter fstream = new FileWriter(fileName);
  		BufferedWriter out = new BufferedWriter(fstream);
  		
     		//writes everything to file
         	String input = inFromServer.readLine();
         	while (inFromServer.ready()) {
         	 	input = inFromServer.readLine();
                	out.write(input+"\n");
 			System.out.println(input); 
               	}
               	out.close();
               	
               	//open up file, created above
               	FileReader fileRead = new FileReader(fileName);
                BufferedReader buffRead = new BufferedReader(fileRead);
                
               	String line = buffRead.readLine();
               	
               	//read file when ever a link is found open new link 
               	//with new address and file name
                while(buffRead.ready()){
			line = buffRead.readLine();
			if (line.contains("a href") && !line.contains("www.")){
				String newFileName = getFileName(line+"\n");
				String newAddress = getReference(line, address);
				
				//write to dbase.txt file
				dbaseOut.write(newAddress + "\tRenamed to\t" + newFileName);
				
				//tries to move files to directory source of error?
				Runtime.getRuntime().exec("mv "+newFileName+" "+hostName+"/");
				
				//call back to itself opening next url
        			open(newAddress, newFileName, --levelNum);
			}       
               }
                              
               	buffRead.close();
               	clientSocket.close();
        }
        }
}
