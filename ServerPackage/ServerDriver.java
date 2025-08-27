package ServerPackage;

import java.net.UnknownHostException;

public class ServerDriver {
	
	public static void main(String[] args) throws UnknownHostException {
		int serverPort = 5000; 
		UDPServer server = new UDPServer(serverPort); 
		server.runServer();
	}

}
