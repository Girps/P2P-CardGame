package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.Player;

public class StealCard extends Command{
	private Player player; 
	public StealCard(String desc, DatagramSocket sock, InetAddress server, int port, Player player) {
		super(desc, sock, server, port);
		this.player = player; 
	}

	@Override
	public void execute() throws Exception {
		// ask for card to swap
		String msg = ""; 
		Integer cardOne; 
		String playerTarget =""; 
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Pick card form you deck to swap.");
		cardOne = Integer.parseInt(userInput.readLine()); 
		System.out.println("Pick player to steal from.");
		playerTarget = userInput.readLine(); 
		msg += "FLIP|"+ this.player.getName() +"|"+ cardOne + "|" +  playerTarget; 
		// now send the message 
		SocketUtil.SendMessage.sendDatagram(sender,msg , 
				this.player.getPeerSocket().getInetAddress(),this.player.getPeerSocket().getPort());
	}

}
