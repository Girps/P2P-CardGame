package ClientPackage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import GameObjects.Card;
import GameObjects.Card.FACE;
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
				case "GAME OVER":
					System.out.println("GAME OVER:\n" + command[2] + "\n" + "WINNER:" + command[1]); 
					this.game.setState(GAMESTATE.IN_LOBBY);
					break; 
				case "ROUND OVER": 
					System.out.println("ROUND OVER:" + command[2] + "\n"+ command[1]); 
					// make it dealers turn 
					restartGameRound(); 
					break; 
				case "FLIP":
					flipCards(command); // flip cards 
					break; 
				case "STOCK":
					swapStock(command); // swaps with stock
					break; 
				case "DISCARD": 
					swapDiscard(command); // swaps with discard
					break;
				case "TURN": // display info and check if current turn never send it 
					checkTurn(command,msg); 
					break; 
				case "MESSAGE": 
					if (!command[1].equals(this.player.getName())) 
					{ 
						System.out.println(command[1] + ":" + command[2]);
						// send packet over
						SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
								this.player.getNeighbor().address, this.player.getNeighbor().PPORT);
					} 
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
					this.player.setInit(true);
					System.out.println("Dealer started the game!" ); 
					msg += "|"+this.player.getName(); 
					SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
							this.player.getNeighbor().address, this.player.getNeighbor().PPORT);
					break; 
				case "TURN": 
					// check if current turn change state and send it away
					checkTurn(command, msg); 
					break; 
				case "GAME OVER": 
					// game is over change state 
					System.out.println("GAME OVER:\n" + command[2] + "\n" + "WINNER:" + command[1]); 
					SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
							this.player.getNeighbor().address, this.player.getNeighbor().PPORT);
					this.game.setState(GAMESTATE.IN_LOBBY);
					break; 
				case "ROUND OVER":
					// print the current scores 
					System.out.println("ROUND OVER:" + command[2] + "\n"+ command[1]); 
					SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
							this.player.getNeighbor().address, this.player.getNeighbor().PPORT);
					break;
				case "MESSAGE": 
					if (!command[1].equals(this.player.getName())) 
					{ 
						System.out.println(command[1] + ":" + command[2]);
						// send packet over
						SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
								this.player.getNeighbor().address, this.player.getNeighbor().PPORT);
					} 
					break; 
				default: 
					// pass the message to the next peer 
					SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
							this.player.getNeighbor().address, this.player.getNeighbor().PPORT);
					break; 
			}
		}
	}
	
	
	// Create a new deck and give dealer their turn 
	private void restartGameRound() throws IOException 
	{
		this.game.restartRound(); 
		// now send a message to peer
		sendNextTurnMessage(); 
	}

	// Check if turn packet is ment for this player 
	private void checkTurn(String[] command,String msg) throws IOException 
	{
		String playerName = command[1];
		String allDeck = command[2]; 
		String currDeck = command[3]; 
		String stockCard = command[4]; 
		String discardCard = command[5];
		String stockCardFaceUp=command[6]; 
		String gameId = command[7]; 
		System.out.println("Game ID: " + gameId); 
		System.out.println(allDeck); 
		System.out.println("It's " + playerName + "'s turn!"); 
		if(this.player.getName().equals(playerName)) 
		{
			// change state
			System.out.println( "STOCK: " + stockCard + " DISCARD: " + discardCard); 
			System.out.println(currDeck);			
			this.player.setCard(stockCardFaceUp);
			this.game.setState(GAMESTATE.IN_GAME_TURN);
		}
		
		// now send this message to others if not the dealer
		if (!this.player.isDealer()) {
			SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
					this.player.getNeighbor().address, this.player.getNeighbor().PPORT);
		}
	}
	
	
	// send next Turn message 
	private void sendNextTurnMessage() throws IOException 
	{
			// change turn
			this.game.nextTurn();
			Integer currentTurn = this.game.getTurn(); 
			String currentPlayer = this.game.getPlayers().get(currentTurn); 
			String allCards = this.game.getPlayersDeckString(); 
			String currentPlayerDeck = this.game.getPlayersDeckString(this.game.getTurn(), true);
			Card stockCard = this.game.getStock().getFirst(); 
			Card discardCard = this.game.getDiscard().getFirst(); 
				
			String stockCardStr = stockCard.toString(); 
			String discardCardStr = discardCard.toString(); 
			stockCard.setFace(FACE.UP);
			String faceUpStockCardStr = stockCard.toString(); 
			stockCard.setFace(FACE.DOWN);
			
			String msg = "TURN|" + currentPlayer + "|" + allCards + "|" + currentPlayerDeck + 
					"|" + stockCardStr + "|" + discardCardStr + "|" + faceUpStockCardStr + "|" +this.player.getGameId();
			// now send the message to peer
			SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
					this.player.getNeighbor().address, this.player.getNeighbor().PPORT);
	}
	
	// update player to swap card with card in the stock pile
	private void swapStock(String[] command) throws IOException
	{
		String playerStr = command[1]; 
		String card = command[2]; 
		
		// now swap card index 
		Integer cardIndex = Integer.valueOf(card) - 1; 
		Card stockCard =  this.game.getStock().removeFirst(); 
		stockCard.setFace(FACE.UP); 
		
		ArrayList<String> playerList=  this.game.getPlayers();
		Integer playerIndex = playerList.indexOf(playerStr); 
		ArrayList<ArrayList<Card>> playersDeck= new ArrayList<ArrayList<Card>>(); 
		playersDeck = this.game.getPlayersDeck(); 
		Card playerCard = playersDeck.get(playerIndex).get(cardIndex);
		playerCard.setFace(FACE.UP);
		playersDeck.get(playerIndex).set(cardIndex, stockCard);
		this.game.getDiscard().addFirst(playerCard);
			
		// check is round or game over 
		if(!GameOrRoundOver()) 
			sendNextTurnMessage(); 
	}
	
	
	// update player to swap card with card in discard pile 
	private void swapDiscard(String[] command) throws IOException 
	{
		String playerStr = command[1]; 
		String card = command[2]; 
		
		// now swap card index 
		Integer cardIndex = Integer.valueOf(card) - 1; 
		
		Card discardCard = this.game.getDiscard().removeFirst();
		Integer playerIndex =this.game.getPlayers().indexOf(playerStr); 
		Card playerCard = this.game.getPlayersDeck().get(playerIndex).get(cardIndex); 
		playerCard.setFace(FACE.UP);
		this.game.getPlayersDeck().get(playerIndex).set(cardIndex, discardCard); 
		this.game.getDiscard().addFirst(playerCard);
		
		// check is round or game over 
		if(!GameOrRoundOver()) 
			sendNextTurnMessage(); 
	}
	
	
	// increment the round and sc
	public boolean GameOrRoundOver() throws IOException 
	{
		if(this.game.allFacedUp()) 
		{
			// round is over calculate the scores
			this.game.calculateScores();
			
			String msg = ""; 
			// now check if game is over or just the round 
			this.game.incrementRound();
			if (this.game.isGameOver()) 
			{
				msg += "GAME OVER|";
				// game over get the winner and score 
				String winner = this.game.getWinner(); 
				String scores = this.game.getPlayerScoresStr(); 
				msg += winner + "|" + scores;
			}
			else 
			{
				msg += "ROUND OVER|"; 
				// round over get the current scores and round
				String scores = this.game.getPlayerScoresStr(); 
				msg += scores;
				
				String round = this.game.getRound().toString(); 
				String holes = this.game.getHoles().toString(); 
				msg += "|(" + round + "/" + holes + ")"; 
				
			}
			
			// send message 
			SocketUtil.SendMessage.sendDatagram(this.player.getPeerSocket(), msg, 
					this.player.getNeighbor().address, this.player.getNeighbor().PPORT
					);
			
			return true; 
		} 
		else 
		{
			return false; 
		}
	}
	
	// Flip packet received now edit the game state and send next turn in the packet 
	private void flipCards(String[] command) throws IOException 
	{
		String playerStr;
		
		playerStr = command[1]; 
		Integer cardOne = Integer.valueOf(command[2]) - 1; 
		Integer cardTwo = Integer.valueOf(command[3]) - 1; 
		
		// get the player and their cards
		int index = this.game.getPlayers().lastIndexOf(playerStr); 
		
		// now swap the index 
		ArrayList<Card> deck = this.game.getPlayersDeck().get(index); 
		deck.get(cardOne).setFace(Card.FACE.UP);
		deck.get(cardTwo).setFace(Card.FACE.UP);
		
		
		// check is round or game over 
		if(!GameOrRoundOver()) 
			sendNextTurnMessage(); 
		
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
		this.player.setInit(true);
		sendNextTurnMessage(); 
	}
	
}
