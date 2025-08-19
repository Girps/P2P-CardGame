package ServerPackage;


public class ServerDriver {
	
	public static void main(String[] args) {
		int serverPort = 7; 
		UDPServer server = new UDPServer(serverPort); 
		server.runServer();
	}

}
