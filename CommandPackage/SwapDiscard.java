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
				msg += "FLIP|"+ this.player.getName() +"|"+ card ;  
				// now send the message 
				SocketUtil.SendMessage.sendDatagram(sender,msg , 
						this.player.getPeerSocket().getInetAddress(),this.player.getPeerSocket().getPort());
	}

}
