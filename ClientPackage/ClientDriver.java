package ClientPackage;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientDriver {
	
	
	private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	public static void main(String [] args) throws UnknownHostException, SocketException { 
		
		final int SERVERPORT = 7; 
		final InetAddress SERVERADDRESS = InetAddress.getLocalHost(); 
		DatagramSocket serverSocket = new DatagramSocket(); 
		Game game = new Game(); 
		Player player = new Player(game,SERVERPORT,SERVERADDRESS,serverSocket); 
		game.register(player); 
		player.setSubject(game);
		game.setState(GAMESTATE.NON_GAME);
		 
		ClientServerReceiver receiver = new ClientServerReceiver(executor,player,serverSocket, SERVERADDRESS, SERVERPORT); 

		executor.submit(player);
		executor.submit(receiver);  
		
	} 
}
