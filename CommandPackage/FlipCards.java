package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.Player;

public class FlipCards extends Command{
	private Player player; 
	public FlipCards(String desc, DatagramSocket sock, InetAddress server, int port, Player player) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
		this.player = player; 
	}

	@Override
	public void execute() throws Exception {
		// ask for two card inputs
		String msg = ""; 
		Integer cardOne, cardTwo; 
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Pick first card to flip");
		cardOne = Integer.parseInt(userInput.readLine()); 
		System.out.println("Pick second card to flip");
		cardTwo = Integer.parseInt(userInput.readLine()); 
		msg += "FLIP|"+ this.player.getName() +"|"+ cardOne + "|" + cardTwo; 
		// now send the message 
		SocketUtil.SendMessage.sendDatagram(sender,msg , 
				this.player.getPeerSocket().getInetAddress(),this.player.getPeerSocket().getPort());
	}

}
