package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class QueryGames extends Command {

	public QueryGames(String desc, DatagramSocket sock, InetAddress server, int port) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		SocketUtil.SendMessage.sendDatagram(sender, "QUERY GAMES", server, port);
	}

}
