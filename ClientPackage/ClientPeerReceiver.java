package ClientPackage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import GameObjects.Card;
import GameObjects.Card;

// Client receives packets from peer and changes state of the game 
public class ClientPeerReceiver implements Runnable{
	private DatagramSocket socket; 
	private Game game; 
	private final int BUFFERSIZE= 8192; 
	Player player; 
	public ClientPeerReceiver(DatagramSocket peerSocket, Player player) 
	{
		this.socket = peerSocket; 
		this.player = player;
		this.game = (Game)this.player.getSubject(); 
	}
	
	@Override
	public void run() {
		// run loop iff we are not NON_GAME state
		while((game).getState() != GAMESTATE.NON_GAME) 
		{
			try 
			{
				// receive packet 
				byte[] buffer = new byte[BUFFERSIZE];  
				DatagramPacket peerIncoming = new DatagramPacket(buffer, BUFFERSIZE); 
				socket.receive(peerIncoming);
				handleResponse(peerIncoming); 
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		// disconnect the socket listening on port  
		this.socket.disconnect();
	}
	
	// Receives the packet and forwards it to the neignbor 
	private void handleResponse(DatagramPacket packet) throws IOException
	{
		String msg = new String(packet.getData(),0,packet.getLength(), "UTF-8");
		// now get check
		String[] command = msg.split("\\|"); 
		
		// check if current is the dealer
		if(this.player.isDealer()) 
		{
			// if the dealer received message from peers it changes game state 
			switch(command[0]) 
			{
				case "START GAME":
					startGame(command); // has all players now launch the game 
					break;
				case "FLIP":
					flipCards(command); // flip cards 
				case "STOCK":
					swapDiscard(command); // swaps the cards
					break; 
				case "DISCARD": 
					swapStock(command); // discards the cards 
					break;
				case "TURN": // display info and check if current turn never send it 
					checkTurn(command,msg); 
					break; 
			}
			SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
					this.socket.getInetAddress(), this.player.getPeerSocket().getPort());
		}
		else 
		{
			switch(command[0])			
			{
				case "START GAME":
					// change state from IN_LOBBY into IN_GAME and add players name to the list 
					System.out.println("Dealer started the game!" + "\nIt's" + command[1] + "turn!" ); 
					this.game.setState(GAMESTATE.IN_GAME_NON_TURN);
					msg += "|"+this.player.getName(); 
					break; 
				case "TURN": 
					// check if current turn change state and send it away
					checkTurn(command, msg); 
					break; 
			}
		}
	}
	
	
	private void checkTurn(String[] command,String msg) throws IOException 
	{
		String player = command[1];
		if(this.player.getName().equals(player)) 
		{
			// change state
			this.player.setTurn(true);
		}
		
		// now send this message to others if not the dealer
		if (!this.player.isDealer()) {
			SocketUtil.SendMessage.sendDatagram(socket, msg, 
					this.player.getPeerSocket().getInetAddress(), this.player.getPeerSocket().getPort());
		}
	}
	
	private void swapStock(String[] command)
	{
		
	}
	private void swapDiscard(String[] command) 
	{
		String playerStr; 
		
	}
	
	private void flipCards(String[] command) throws IOException 
	{
		String playerStr, cardOneStr, cardTwoStr;
		
		playerStr = command[1]; 
		Integer cardOne = Integer.valueOf(command[2]) - 1; 
		Integer cardTwo = Integer.valueOf(command[3]) - 1; 
		
		// get the player and their cards
		int index = this.game.getPlayers().lastIndexOf(playerStr); 
		
		// now swap the index 
		ArrayList<Card> deck = this.game.getPlayersDeck().get(index); 
		deck.get(cardOne).setFace(Card.FACE.UP);
		deck.get(cardTwo).setFace(Card.FACE.UP);
		
		// change turn
		this.game.nextTurn();
		Integer currentTurn = this.game.getTurn(); 
		String currentPlayer = this.game.getPlayers().get(currentTurn); 
		String allCards = this.game.getPlayersDeckString(); 
		String currentPlayerDeck = this.game.getPlayersDeckString(index, true); 
		String msg = "TURN|" + currentPlayer + "|" + allCards + "|" + currentPlayerDeck;
		// now send the message to peer
		SocketUtil.SendMessage.sendDatagram(socket, msg, 
				this.player.getPeerSocket().getInetAddress(), this.player.getPeerSocket().getPort());
	}
	
	// start the game
	private void startGame(String[] command) 
	{
		ArrayList<String> playerNames = new ArrayList<String>(); 
		// intalize game players
		for (int i =1; i < command.length; ++i) 
		{
			playerNames.add(command[i]); 
		}
		this.game.startGame(playerNames);
		// get current turn 
		
		// change player state to player turn in order to get options 
		this.game.setState(GAMESTATE.IN_GAME_NON_TURN);
	}
	
}
