package ServerPackage;

import java.util.ArrayList;

public class GameSession {
	public final int id;
	public ArrayList<Peer> playerList; 
	public Peer dealer; 
	public int rounds; 
	public GameSession(int id, ArrayList<Peer> playerList, Peer dealer, int rounds) 
	{
		this.id = id; 
		this.playerList=playerList; 
		this.dealer = dealer; 
		this.rounds = rounds; 
	}
	
	public String toString() 
	{
		String res = "";
		res += "Game id: " + this.id + "\n"; 
		res += "Dealer: " + this.dealer +"\n"; 
		res += "Players: " + this.playerList +"\n\n";  
		return res; 
	}
}
