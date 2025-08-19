package ServerPackage;

import java.net.InetAddress;

// Class holds player name , IPV4, PPORT , TPORT etc 
public class Peer {
	public final String name; 
	public final InetAddress address; 
	public final int PPORT; 
	public final int TPORT; 
	public boolean inGame; 
	public boolean isDealer; 
	Peer neighbor; 
	public Peer(String name, InetAddress address, int TPORT, int PPORT) 
	{
		this.name=name; 
		this.address=address; 
		this.PPORT= PPORT;
		this.TPORT=TPORT;
		inGame = false;
		this.isDealer = false; 
	}
	
	@Override 
	public String toString() 
	{
		return "Name: " +this.name + " Address: " + address.toString() + " TPORT: " + TPORT + " PPORT: " + PPORT;
	}
}
