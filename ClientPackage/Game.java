package ClientPackage;

import java.util.ArrayList;

import GameObjects.Card;
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
				res += "[" + count +"]" + playerDeck.get(index).get(i); 
			}
			else 
			{
				res += playerDeck.get(index).get(i); 
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

}
