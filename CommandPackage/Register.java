package CommandPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import SocketUtil.SendMessage; 
public class Register extends Command{
	
	
	public Register(String desc, DatagramSocket sock, InetAddress server, int port) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws IOException {
		// TODO Auto-generated method stub
		String playerName, pport,message; 
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter player name");
		playerName = userInput.readLine(); 
		System.out.println("Enter P-PORT");
		pport = userInput.readLine(); 
		
		// have it now send the message 
		message = "REGISTER|"+ playerName+ "|"+ pport; 
		
		// now convert it to bytes to send in socket
		SendMessage.sendDatagram(sender, message, server, port);
		
	}

}
