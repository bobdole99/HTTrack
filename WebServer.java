/*
CPSC 441 University of Calgary
Michael Gugala
Assignment 1

Exceution of program beings here*/
class WebServer{

    public static void main (String[] args){
    
    	//check number of arguments
        if(args.length != 1){
            System.out.println("Invalid number of paramters");
            System.exit(0);
        }
        
        String arg = args[0];
        int portNumber = Integer.parseInt(arg);
        
        //check range of port numbers
        if(portNumber < 1024 || portNumber > 65536){
            System.out.println("Port number out of range");
            System.exit(0);
        }
	Server server = new Server(portNumber);
	new Thread(server).start();

    }
}
