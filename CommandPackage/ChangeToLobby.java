package CommandPackage;

import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.GAMESTATE;
import ClientPackage.Game;
import ClientPackage.Subject;

public class ChangeToLobby extends Command {
	
	
	Subject game = null; 
	public ChangeToLobby(String desc, DatagramSocket sock, InetAddress server, int port) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
	}
	public ChangeToLobby(String desc, DatagramSocket sock, InetAddress server, int port, Subject game) {
		super(desc, sock, server, port);
		this.game = game; 
	}
	@Override
	public void execute() throws Exception {
		((Game) game).setState(GAMESTATE.IN_LOBBY);
		System.out.println("Changed to IN_LOBBY state"); 
	}

}
