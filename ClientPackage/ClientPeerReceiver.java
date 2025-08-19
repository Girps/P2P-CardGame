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
		
		}
		else 
		{
			switch(command[0])			
			{
				case "START GAME":
					// change state from IN_LOBBY into IN_GAME and add players name to the list 
					this.game.setState(GAMESTATE.IN_GAME_NON_TURN);
					System.out.println("Dealer started the game!" + "\nIt's " + command[1] + " turn!" ); 
					msg += "|"+this.player.getName(); 
					SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
							this.player.getPeerSocket().getInetAddress(), this.player.getPeerSocket().getPort());
					break; 
				case "TURN": 
					// check if current turn change state and send it away
					checkTurn(command, msg); 
					break; 
			}
		}
	}
	
	// Check if turn packet is ment for this player 
	private void checkTurn(String[] command,String msg) throws IOException 
	{
		String playerName = command[1];
		String allDeck = command[2]; 
		String currDeck = command[3]; 
		
		System.out.println(allDeck); 
		if(this.player.getName().equals(playerName)) 
		{
			// change state
			System.out.println(currDeck);
			this.game.setState(GAMESTATE.IN_GAME_TURN);
		}
		
		// now send this message to others if not the dealer
		if (!this.player.isDealer()) {
			SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
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
	
	// Flip packet received now edit the game state and send next turn in the packet 
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
		
		
		// change state
		this.game.setState(GAMESTATE.IN_GAME_NON_TURN);
		
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
	private void startGame(String[] command) throws IOException 
	{
		ArrayList<String> playerNames = new ArrayList<String>(); 
		// intalize game players
		for (int i =1; i < command.length; ++i) 
		{
			playerNames.add(command[i]); 
		}
		this.game.startGame(playerNames);
		// get current turn 
		// get the player and their cards
		int index = this.game.getPlayers().lastIndexOf(this.player.getName()); 
		String allCards = this.game.getPlayersDeckString(); 
		String currentPlayerDeck = this.game.getPlayersDeckString(index, true); 
		// now send turn for the next player in this case it will be dealer 
		String msg = "TURN|" + this.player.getName() + "|" + allCards + "|" + currentPlayerDeck;
		// now send the message to peer
		SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
				this.player.getPeerSocket().getInetAddress(), this.player.getPeerSocket().getPort());
		
	}
	
}
