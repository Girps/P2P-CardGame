package CommandPackage;

import java.net.DatagramSocket;
import java.net.InetAddress;

import ClientPackage.GAMESTATE;
import ClientPackage.Game;
import ClientPackage.Player;
import ClientPackage.Subject;

public class TerminateProgram extends Command{

	public TerminateProgram(String desc, DatagramSocket sock, InetAddress server, int port ) {
		super(desc, sock, server, port);
	}
	

	@Override
	public void execute() throws Exception {
		this.sender.close(); 
		System.exit(0);		
	}

}
