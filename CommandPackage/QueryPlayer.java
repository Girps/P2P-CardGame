package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class QueryPlayer extends Command{

	public QueryPlayer(String desc, DatagramSocket sock, InetAddress server, int port) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		String message; 
		message = "QUERY PLAYERS"; 
		SocketUtil.SendMessage.sendDatagram(sender, message, server, port);
	}
	
	
	

}
