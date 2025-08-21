package ClientPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

import ServerPackage.Peer;

// Object listens to packets received from the Server
public class ClientServerReceiver implements Runnable{
	
	private DatagramSocket serverSocket;
	private InetAddress serverAddress;
	private int serverPort; 
	private final int BufferSize; 
	private final Player player; 
	private final ExecutorService executor; 
	public ClientServerReceiver(ExecutorService executor, Player player, DatagramSocket socket, InetAddress serverAddress, int serverPort) 
	{
		// connect to the server
		this.executor = executor; 
		this.player = player; 
		this.serverSocket = socket; 
		this.serverAddress = serverAddress;
		this.BufferSize = 8192; 
		this.serverPort=serverPort;
		this.serverSocket.connect(serverAddress,serverPort);
	}
	@Override
	public void run() {
		
		while(true) 
		{
			byte[] buffer = new byte[BufferSize]; 
			DatagramPacket packet = new DatagramPacket(buffer, BufferSize); 
			try
			{
				this.serverSocket.receive(packet);
				// now respond to the packet 
				this.handleResponse(packet);
			}
			catch (IOException e) {
			}
		}
	}
	
	private void handleResponse(DatagramPacket packet) throws UnsupportedEncodingException 
	{
		String msg = new String(packet.getData(),0,packet.getLength(), "UTF-8");
		// now get check
		String[] command = msg.split("\\|"); 
		
		switch(command[0]) 
		{
			case "REGISTER":
					System.out.println(command[1]); 
					// create new object that is a thread receiving packets from other players
					if(command[1].equals("SUCCESS")) 
					{ 
						this.player.setName(command[2]); 
					}
				break; 
			case "QUERY PLAYERS":
					System.out.println(command[1]);
				break;
			case "START GAME":
					
					// change game state to in_lobby and set up peers
					if (command[1].equals("SUCCESS")) 
					{
						try {
							startGame(packet,command);
						} catch (UnknownHostException | SocketException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
					System.out.println(command[1]);
				break; 
			case "QUERY GAMES":
					System.out.println(command[1]);
				break; 
			case "DEREGISTER":
					System.out.println(command[1]);
				break; 
			case "END GAME":
					System.out.println(command[1]);
					// if success changes player state back to non_game 
					if (command[1].equals("SUCCESS")) 
					{
						((Game)this.player.getSubject()).setState(GAMESTATE.NON_GAME); 
					}
				break; 
			default: 
					System.out.println("Unknown packet from server to client"); 
				break; 
		}
	}
	
	
	// Start game 
	private void startGame(DatagramPacket packet, String[] msg) throws UnknownHostException, SocketException 
	{
		// get player information 
		String isDealerStr, pportStr, neighborNameStr, neighborAddressStr, neighborPortStr, idStr, holesStr; 
		
		isDealerStr = msg[2]; 
		pportStr= msg[3];
		neighborNameStr= msg[4]; 
		neighborAddressStr = msg[5]; 
		neighborPortStr = msg[6]; 
		idStr = msg[7]; 
		holesStr = msg[8]; 
		
		boolean isDealer; 
		int pport, neighborPort; 
		InetAddress neighborAddress; 
		
		isDealer =  Boolean.valueOf(isDealerStr); 
		pport = Integer.valueOf(pportStr);
		neighborPort = Integer.valueOf(neighborPortStr);
		neighborAddress = InetAddress.getByName(neighborAddressStr); 
		
		// create the peer and datagram to connect to
		Peer neighbor = new Peer(neighborNameStr, neighborAddress, serverPort, neighborPort);  
		// change player state to have its neighbor and change game state to IN_LOBBY
		this.player.setNeighbor(neighbor); 
		this.player.setDealer(isDealer); 
		
		// check if is dealer store the number of rounds, id session in the players' game object 
		Game game = (Game)(this.player.getSubject()) ;
		game.setId(Integer.valueOf(idStr));
		game.setHoles(Integer.valueOf(holesStr)); 
		game.setState(GAMESTATE.IN_LOBBY);
		// now create a new thread to listen for portStr and datagram socket to listen to other peers 
		DatagramSocket socket = new DatagramSocket(pport); 
		this.executor.submit(new ClientPeerReceiver(socket, this.player)); 
		
		
	}
}
