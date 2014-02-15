import java.util.*;
import java.io.*;
import java.net.*;
/*
CPSC 441 University of Calgary
Michael Gugala
Assignment 1

Execution of program begin in class HTTrack, main is responsible for
getting and parsing command line arguments uses URL class to do so
*/

class HTTrack {
	public static void main(String[] args)throws Exception{
		String hostName;
		String urlAddress = args[0];
		int portNumber = 80;
		String fileName;
		int levelNum = 4;
		
		if (args.length !=1){
			System.out.println("Invalid number of command line arguments");
			System.out.println("Usage java HTTrack <url>");
			System.exit(0);
		}
		//remove the http:// from urlAddress
		urlAddress = urlAddress.substring(7);
		
		//returns tge index number of the first occurance of '/'
		int index = urlAddress.indexOf('/');
		
		//parse the host name
		hostName = urlAddress.substring(0, index);
		
		//parse the file name
		fileName = urlAddress.substring(urlAddress.lastIndexOf('/')+1, urlAddress.length());
		
		GetHtmlPage htmlPage = new GetHtmlPage(portNumber, hostName);
		htmlPage.open(args[0], fileName, levelNum);
	}
}
