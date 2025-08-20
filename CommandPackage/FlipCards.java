package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.GAMESTATE;
import ClientPackage.Game;
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
		
		// check valid bounds 
		if( 0 < cardOne && cardOne < 7 && 0 < cardTwo && cardTwo < 7) 
		{
			// end turn 
			((Game)this.player.getSubject()).setState(GAMESTATE.IN_GAME_NON_TURN); 
			
			// now send the message 
			SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(),msg, 
					this.player.getPeerSocket().getInetAddress(),this.player.getPeerSocket().getPort());
		}
		else 
		{
			System.out.println("INVALID RANGE"); 
		}
		
	}

}
