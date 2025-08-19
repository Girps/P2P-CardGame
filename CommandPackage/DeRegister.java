package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DeRegister extends Command{

	public DeRegister(String desc, DatagramSocket sock, InetAddress server, int port) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		String player, message; 
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter player name:"); 
		player = userInput.readLine(); 
		message = "DEREGISTER|" + player; 
		SocketUtil.SendMessage.sendDatagram(sender, message, server, port);
	}

}
