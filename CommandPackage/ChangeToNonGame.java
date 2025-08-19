package CommandPackage;

import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.GAMESTATE;
import ClientPackage.Game;
import ClientPackage.Subject;

public class ChangeToNonGame extends Command{

	private Subject game; 
	public ChangeToNonGame(String desc, DatagramSocket sock, InetAddress server, int port) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
		
	}
	
	public ChangeToNonGame(String desc, DatagramSocket sock, InetAddress server, int port, Subject game) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
		this.game = game; 
	}

	@Override
	public void execute() throws Exception {
		((Game)this.game).setState(GAMESTATE.NON_GAME);
	}

}
