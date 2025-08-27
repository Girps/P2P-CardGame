package ClientPackage;

import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientDriver {
	
	
	private static ExecutorService executor = Executors.newFixedThreadPool(6);
	
	public static void main(String [] args) throws UnknownHostException, SocketException { 
		
		
		
		InputStream inputStream = null;
		try
		{
			inputStream = ClientDriver.class.getResourceAsStream("config.properties"); 
			// Use the inputStream to load the properties
			Properties properties = new Properties();
			properties.load(inputStream);
			final String serverStrAd = properties.getProperty("SERVER_IP"); 
			final int SERVERPORT = 5000; 
			final InetAddress SERVERADDRESS = InetAddress.getByName(serverStrAd);  
			DatagramSocket serverSocket = new DatagramSocket(); 
			Game game = new Game(); 
			Player player = new Player(game,SERVERPORT,SERVERADDRESS,serverSocket); 
			game.register(player); 
			player.setSubject(game);
			game.setState(GAMESTATE.NON_GAME);
			 
			ClientServerReceiver receiver = new ClientServerReceiver(executor,player,serverSocket, SERVERADDRESS, SERVERPORT); 

			executor.submit(player);
			executor.submit(receiver);  
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("failed to load config file"); 
		}

		
		
	} 
}
