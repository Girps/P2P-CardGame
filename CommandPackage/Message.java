package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.Player;
import SocketUtil.SendMessage;

public class Message extends Command{
	
	Player player; 
	public Message(String desc, DatagramSocket sock, InetAddress server, int port, Player player) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
		this.player= player;
	}

	@Override
	public void execute() throws Exception {
		System.out.println("Enter a message to send:");
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		String input = userInput.readLine(); 
		String msg = "MESSAGE|" + player.getName() + "|"+ input;
		SocketUtil.SendMessage.sendDatagram(player.getPeerSocket(), msg, player.getPeerSocket().getInetAddress(), 
				player.getPeerSocket().getPort());
	}

}
