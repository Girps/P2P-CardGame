package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.Player;

public class EndGame extends Command {
	public EndGame(String desc, DatagramSocket sock, InetAddress server, int port) {
		super(desc, sock, server, port);
	}

	@Override
	public void execute() throws Exception {
		String gameId, player,message; 
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter game ID:"); 
		gameId = userInput.readLine(); 
		System.out.println("Enter player name:");
		player = userInput.readLine(); 
		message = "END GAME|"+ gameId + "|"+ player; 
		SocketUtil.SendMessage.sendDatagram(sender, message, server, port);
	}

}
