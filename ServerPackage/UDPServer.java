package ServerPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import SocketUtil.*; 
public class UDPServer {
	
	private int bufferSize = 0;
	private int port= 0 ; 
	private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private volatile ConcurrentHashMap<String, Peer> peerList = new ConcurrentHashMap<>();	
	private volatile ConcurrentHashMap<Integer, GameSession> gameList = new ConcurrentHashMap<>();
	private volatile  AtomicInteger idCounter = new AtomicInteger(0);
	// intalize port to listen to 
	public UDPServer(int port) 
	{
		this.port = port; 
		this.bufferSize=8192; 
	}
	
	// Start server
	public void runServer() throws UnknownHostException 
	{
		System.out.println("Server On"); 
		byte[] buffer = new byte[bufferSize]; 
		//InetAddress address = InetAddress.getByName("2a12:bec0:20c:1d4c::1");
		try(DatagramSocket socket = new DatagramSocket(port))
		{
			while(true) 
			{
				
				// build packet object to store data in
				DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
				socket.receive(incoming);
				respond(socket, incoming); 
			}
		}
		catch(Exception  e) 
		{
			System.err.println(e); 
		}
	} 
	
	// Respond back to the client 
	private void respond(DatagramSocket socket, DatagramPacket packet)  
	{
		
		try 
		{ 
			String payload = new String(packet.getData(),0,packet.getLength(),"UTF-8");
			String[] messageSplit = payload.split("\\|"); 
			String command = messageSplit[0]; 
			System.out.println(command);  
			switch(command) 
			{
				case "REGISTER":
						this.executor.submit(() -> 
						{
							try 
							{
								register(socket,packet,messageSplit);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 	
						}); 
					break; 
				case "QUERY PLAYERS": 
						this.executor.submit(() -> { 
							try 
							{
								queryPlayers(socket, packet);
							} 
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}); 
					break; 
				case "START GAME":
						this.executor.submit(() -> 
						{
							try 
							{
								startGame(socket, packet, messageSplit);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}); 
					break; 
				case "QUERY GAMES": 
						this.executor.submit(() -> 
						{
							try 
							{
								queryGames(socket,packet);
							} catch (IOException e) 
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}); 
					break;  
				case "DEREGISTER": 
						this.executor.submit(() -> 
						{
							try {
								deRegister(socket,packet,messageSplit[1]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}); 
					break; 
				case "END GAME": 
					this.executor.submit(() ->
					{
						try
						{
							endGame(socket, packet, messageSplit);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}); 
				break; 
				default: 
						System.out.println("Unknown packet command received"); 
					break; 
			}
		}
		catch(Exception e ) 
		{
			System.err.println(e); 
		}
	}
	
	// send message to dealer and each peer of a succesful start of a game or failure otherwise 
	private void startGame(DatagramSocket socket, DatagramPacket packet, String[] message) throws IOException
	{
		String dealer , playerStr,roundStr,response ; 
		dealer = message[1]; 
		playerStr = message[2]; 
		roundStr = message[3]; 
		response = "START GAME|"; 
		
		Integer n , rounds; 
		n = Integer.valueOf(playerStr); 
		rounds = Integer.valueOf(roundStr); 
		// check if enough players in the peerList 
		int count = n; 
		ArrayList<Peer> choosenPeers = new ArrayList<Peer>(); 
		choosenPeers.add(this.peerList.get(dealer)); 
		for(Entry<String,Peer> en: this.peerList.entrySet()) 
		{
			if(en.getValue().name.equals(dealer) == false && 
					en.getValue().inGame == false && count != 0) 
			{
				count -=1; 
				choosenPeers.add(en.getValue()); 
			}
		}
		// check if enough 
		if (count != 0 || this.peerList.get(dealer) == null ) 
		{
			response += "FAILURE"; 
			// send back to dealer 
			SendMessage.sendDatagram(socket, response, packet.getAddress(),packet.getPort());
			return; 
		}
		else 
		{
			response += "SUCCESS"; 
			// set peer to ingame and give each peer their neighbor\
			choosenPeers.get(0).isDealer = true; 
			for (int i =0; i < choosenPeers.size(); ++i) 
			{
				choosenPeers.get(i).inGame = true;
				if (i+1< choosenPeers.size()) 
				{
					// get neighbor refrence 
					Peer neighbor = choosenPeers.get(i+1); 
					// add to current Peer
					choosenPeers.get(i).neighbor = neighbor; 
				}
				else 
				{
					// get neighbor refrence 
					Peer neighbor = choosenPeers.get(0); 
					// add to current Peer
					choosenPeers.get(i).neighbor = neighbor; 
				}
			}
			
			// now add them to the game
			Integer id =  this.idCounter.addAndGet(1); 
			this.gameList.put(id, 
					new GameSession(this.idCounter.get(),choosenPeers, this.peerList.get(dealer),rounds) );
			// send a message to each peer send it by TPORT 
			for (Peer peer: choosenPeers) 
			{
				String currentMsg = "|"+ peer.isDealer+"|"+peer.PPORT +"|" + peer.neighbor.name + "|" 
			+ peer.neighbor.address.getHostAddress() + "|" + peer.neighbor.PPORT + "|" + id + "|" + rounds ; 
				SendMessage.sendDatagram(socket, response + currentMsg, peer.address, peer.TPORT);
			}
		}
		
	}
	
	// Query currently active games 
	private void queryGames(DatagramSocket socket, DatagramPacket packet) throws IOException
	{
		String response = "QUERY GAMES|"; 
		Set<Map.Entry<Integer,GameSession>> games = this.gameList.entrySet(); 
		
		for (Map.Entry<Integer, GameSession> en: games)
		{
			response += en.getValue(); 
		} 
		
		SendMessage.sendDatagram(socket, response, packet.getAddress(), packet.getPort());
	}
	
	// call register method add client: playername, IPV4, tport, pport. Player name must be unique 
	private void register(DatagramSocket socket,DatagramPacket packet, String[] message) throws IOException  
	{
		// get string
		String name,address, tport, pport; 
		name = message[1]; 
		address = packet.getAddress().getHostAddress(); 
		tport = Integer.valueOf(packet.getPort()).toString();  
		pport= message[2]; 
		String response = "REGISTER|"; 
		// now check map

		if (this.peerList.containsKey(name)) 
		{
			response += "FAILURE"; 
		} 
		else
		{
			// add it to the map
			 this.peerList.put(name, new Peer(name,InetAddress.getByName(address),Integer.valueOf(tport),Integer.valueOf(pport))); 
			response += "SUCCESS";
		}
		response += "|"+ name; 
		System.out.println(response); 
		// use socket send back to client 
		SendMessage.sendDatagram(socket,response,packet.getAddress(),packet.getPort()); 
	}
	
	// Remove player out of the hashmap
	private void deRegister(DatagramSocket socket, DatagramPacket packet, String playername) throws IOException 
	{
		String name =""; 
		name = playername;
		String response = "DEREGISTER|"; 
		
		// if name not in the list or the peer does not match the ip address
		if (this.peerList.get(name) == null ||
				(this.peerList.get(name).address.equals(packet.getAddress())== false) ) 
		{
			response += "FAILURE"; 
		}
		else 
		{
			// they match now remove
			this.peerList.remove(name); 
			response += "SUCCESS"; 
		}
		SendMessage.sendDatagram(socket, response, packet.getAddress(), packet.getPort());
	}
	
	// Send player data to client
	private void queryPlayers(DatagramSocket socket, DatagramPacket packet) throws IOException 
	{
		String response = "QUERY PLAYERS|"; 
		Set<Map.Entry<String,Peer>> peers = this.peerList.entrySet(); 
		
		for (Map.Entry<String, Peer> en: peers)
		{
			response += en.getValue() + "\n"; 
		} 
		SendMessage.sendDatagram(socket, response, packet.getAddress(), packet.getPort());
	}
	
	// Receive id of game session to end
	private void endGame(DatagramSocket socket, DatagramPacket packet,String msg[]) throws IOException 
	{	
		String response = "END GAME|";
		// CHECK IF DATA WAS VALID 
		String idStr, playerStr; 
		idStr = msg[1]; 
		playerStr = msg[2]; 
		int id = Integer.valueOf(idStr);
		// now check if game session exists and if playerStr is the dealer and same ip address 
		if (this.gameList.containsKey(id) && this.peerList.containsKey(playerStr) &&
				this.peerList.get(playerStr).address.equals(packet.getAddress())) 
		{
			response += "SUCCESS"; 
			// send it to every peer change their state from LOBBY to NON_GAME
			for (Peer peer : this.gameList.get(id).playerList) 
			{
				SocketUtil.SendMessage.sendDatagram(socket, response, peer.address, peer.TPORT);
			}
			// now remove game from the list
			this.gameList.remove(id); 
		}
		else 
		{	// send to dealer a failure 
			response += "FAILURE"; 
			SocketUtil.SendMessage.sendDatagram(socket, response, packet.getAddress(), packet.getPort() );
		}
	}
}
