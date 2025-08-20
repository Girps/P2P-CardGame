package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.Player;

public class SwapDiscard extends Command{
	private Player player; 
	public SwapDiscard(String desc, DatagramSocket sock, InetAddress server, int port, Player player) {
		super(desc, sock, server, port);
		this.player = player; 
	}

	@Override
	public void execute() throws Exception {
		// ask for two card inputs
		String msg = ""; 
		Integer card; 
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Pick card to swap with.");
		card = Integer.parseInt(userInput.readLine());
		if (0 < card && card < 7) {  
		msg += "FLIP|"+ this.player.getName() +"|"+ card ;  
		// now send the message 
		SocketUtil.SendMessage.sendDatagram(sender,msg , 
				this.player.getPeerSocket().getInetAddress(),this.player.getPeerSocket().getPort());
		}
		else 
		{
			System.out.println("INVALID RANGE");
		}
	}

}
