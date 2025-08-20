package ClientPackage;

import java.util.ArrayList;

import GameObjects.Card;
import GameObjects.Card.FACE;
import GameObjects.Deck;

/*
 * class represents game state holds game objects to be used by players 
 * 
 * */ 
public class Game implements Subject{
	
	private Observer player; 
	private boolean changed; 
	private GAMESTATE state; 
	private Object lock = new Object(); 
	private Integer gameId; 
	private ArrayList<String> players; 
	private ArrayList<Integer> scores ;
	private Integer holes = 0; 
	private Integer rounds =0; 
	private ArrayList<ArrayList<Card>> playerDeck;
	private ArrayList<Card> stock = new Deck().getCards(); 
	private ArrayList<Card> discard = new ArrayList<Card>(); 
	private int currentTurn = 0; 
	
	// accepts parameter list of players in the game and sets the current turn 
	public void startGame(ArrayList<String> players) 
	{
		this.players=players; 
		this.scores = new ArrayList<Integer>(); 
		this.playerDeck = new ArrayList<ArrayList<Card>>(); 
		Card firstDiscard = this.stock.removeFirst(); 
		firstDiscard.setFace(FACE.UP);
		this.discard.addFirst(firstDiscard);
		for (int i =0; i < players.size(); ++i) 
		{
			scores.addLast(0);
		}
		// now initalize each players deck
		for (int i =0; i < players.size();i++) 
		{
			ArrayList<Card> deck = new ArrayList<Card>();  
			intalizeDeck(deck);
			playerDeck.addLast(deck);
		}
		currentTurn = players.size() - 1; 
	}
	
	// returns score of each player 
	public String getPlayerScoresStr()
	{
		String res= "Score List\n";
		ArrayList<Integer> scores = this.getScores(); 
		for (int i =0; i < this.players.size(); ++i) 
		{
			res += this.players.get(i) + " : " + scores.get(i) + "\n"; 
		}
		
		return res; 
	}
	
	// get winner of the game 
	public String getWinner() 
	{
		String winner = "";
		Integer idx = 0; 
		Integer minScore = this.scores.get(0); 
		for(int i =1; i < this.scores.size(); ++i) 
		{
			if (minScore > this.scores.get(i)) 
			{
				minScore = this.scores.get(i); 
				idx = i; 
			} 
		}
		winner = this.getPlayers().get(idx); 
		return winner; 
	}
	
	// get scores 
	public ArrayList<Integer> getScores() 
	{
		return this.scores; 
	}
	
	// get id session
	public Integer getGameId() 
	{
		return this.gameId; 
	}
	
	// get holes
	public Integer getHoles() 
	{
		return this.holes; 
	}
	
	// get round
	public Integer getRound() 
	{
		return this.rounds; 
	}
	
	// check if game is over
	public boolean isGameOver() 
	{
		if (rounds >= holes) 
		{
			return true; 
		}
		return false; 
	} 
	
	public void incrementRound() 
	{
		this.rounds++; 
	}
	
	// calculate the scores of each player 
	public void calculateScores() 
	{
		// get values of cards 
		ArrayList<ArrayList<Card>> deck = this.playerDeck; 
		for (int i =0; i < deck.size() ; i++) 
		{
			
			int score = 0;
			ArrayList<Card> playerDeck = deck.get(i); 
			for(int j=0; j < 3; j++ ) 
			{
				Integer p1 = playerDeck.get(j).getValueInt(); 
				Integer p2 = playerDeck.get(j+3).getValueInt(); 
				if(p1 != p2) 
				{
					score += p1 + p2;  
				}
			}
			// change current score
			this.scores.set(i, (this.scores.get(i) + score ) ); 
			
		} 
	}
	
	// Check if all cards are faced up indicating the round is over 
	public boolean allFacedUp() 
	{
		// check each player deck if any are down 
		// return false
		ArrayList<ArrayList<Card>> deck = this.playerDeck; 
		for (int i =0; i < deck.size() ; i++) 
		{
			// check cards 
			for(int j =0 ; j < deck.get(i).size(); ++j) 
			{
				// check if faced down 
				if (deck.get(i).get(j).getFace() == FACE.DOWN ) 
				{
					return false; 
				} 
			}
		} 
		
		return true; 
	}
	// get all players deck in string 
	public String getPlayersDeckString() 
	{
		String res = ""; 
		for (int i =0; i < playerDeck.size(); ++i) 
		{
			res += this.players.get(i) + "'s deck \n"; 
			res += getPlayersDeckString(i,false) + "\n"; 
		}
		return res; 
	}
	
	// get current players deck for tring information
	public String getPlayersDeckString(int index, boolean withCount) 
	{
		String res = ""; 
		Integer count = 1; 
		for (int i = 0; i < playerDeck.get(index).size(); ++i) 
		{
			if (withCount) 
			{ 
				res += "[" + count +"]" + playerDeck.get(index).get(i) + " "; 
			}
			else 
			{
				res += playerDeck.get(index).get(i) + " "; 
			}
			if (count % 3 == 0) 
			{
				res +="\n"; 
			}
			count++; 
		}
		return res;
	}
	
	// get playerDeck 
	public ArrayList<ArrayList<Card>> getPlayersDeck()
	{
		return this.playerDeck; 
	} 
	
	// get player list 
	public ArrayList<String> getPlayers()
	{
		return players;
	}
	
	// set next Turn
	public void nextTurn() 
	{
		currentTurn = ((currentTurn + 1 ) % players.size()); 
	}
	
	// get current Turn
	public int getTurn() 
	{
		return this.currentTurn; 
	}
	
	// get discard pile
	public ArrayList<Card> getDiscard()
	{
		return this.discard; 
	}
	
	
	// get stock pile
	public ArrayList<Card> getStock()
	{
		return this.stock; 
	}
	
	// Add 6 cards to each deck 
	private void intalizeDeck(ArrayList<Card> deck) 
	{
		for (int i =0; i < 6; ++i) 
		{
			Card card = stock.removeFirst(); 
			deck.add(card); 
		}
	}
	
	@Override
	public void register(Observer obj) {
		if (obj == null) 
		{
			throw new NullPointerException("Empty observer"); 
		}
		synchronized(lock) 
		{
			player= obj; 
		}
	}

	@Override
	public void unregister() {
		// TODO Auto-generated method stub
		synchronized(lock)
		{
			player = null; 
		} 
	}

	@Override
	public void notifyObservers() {
		// TODO Auto-generated method stub
		synchronized(lock)
		{
			if(changed) 
			{
				this.changed = false; 
				player.update(); 
			}
		}
	}

	@Override
	public Object getUpdate(Observer object) {
		// TODO Auto-generated method stub
		return this.state; 
	}

	public void setState(GAMESTATE state) 
	{
		this.state = state ; 
		this.changed = true; 
		this.notifyObservers(); 
	} 
	
	// get state
	public GAMESTATE getState() 
	{
		return this.state; 
	}
	
	public Observer getObserver() 
	{
		return this.player; 
	}

	
	public void setId(Integer id)
	{
		this.gameId = id; 
	}

	public void setHoles(Integer holes) 
	{
		this.holes = holes; 
	}
	
	// Call when round is over and give each player new set of cards 
	public void restartRound() 
	{
		this.stock = new Deck().getCards(); 
		this.discard = new ArrayList<Card>(); 
		Card firstCard = this.stock.removeFirst(); 
		firstCard.setFace(FACE.UP);
		this.discard.addFirst(firstCard);
		
		// now initalize each players deck
		for (int i =0; i < players.size();i++) 
		{
			ArrayList<Card> deck = new ArrayList<Card>();  
			intalizeDeck(deck);
			playerDeck.set(i, deck); 
		}
		
		currentTurn = players.size()-1; 
	}

}
