package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.Player;

public class SwapStock extends Command{
	private Player player; 
	public SwapStock(String desc, DatagramSocket sock, InetAddress server, int port, Player player) {
		super(desc, sock, server, port);
		this.player = player; 
	}

	@Override
	public void execute() throws Exception {
		String msg = "SWAP STOCK|"; 
		Integer cardOne;  
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		System.out.println("STOCK: " + this.player.getFacedCard()); 
		System.out.println("Enter card to swap with."); 
		cardOne = Integer.valueOf(userInput.readLine()); 
		
		msg += this.player.getName() + "|" + cardOne; 
		if ( 0 < cardOne && cardOne < 7) {  
		// send message 
		SocketUtil.SendMessage.sendDatagram(sender, msg, this.player.getPeerSocket().getInetAddress(),
				this.player.getPeerSocket().getPort());
		}
		else
		{
			System.out.println("INVALID RANGE"); 
		}
	}

}
