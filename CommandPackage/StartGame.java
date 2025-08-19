package CommandPackage;

import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.GAMESTATE;
import ClientPackage.Game;
import ClientPackage.Player;

public class StartGame extends Command {
	Player player; 
	public StartGame(String desc, DatagramSocket sock, InetAddress server, int port, Player player) {
		super(desc, sock, server, port);
		this.player = player; 
	}

	@Override
	public void execute() throws Exception {
		// send message to every player to start the game and change current state of player
		String msg = "START GAME|"+ this.player.getName();
		SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
				this.player.getPeerSocket().getInetAddress(), this.player
				.getPeerSocket().getPort());
		((Game)this.player.getSubject()).setState(GAMESTATE.IN_GAME_NON_TURN);  
	}

}
