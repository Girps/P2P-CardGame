package CommandPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CreateGame extends Command{

	public CreateGame(String desc, DatagramSocket sock, InetAddress server, int port) {
		super(desc, sock, server, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		String player, n, holes,message; 
		BufferedReader userInput  
		= new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter name of dealer:");
		player = userInput.readLine(); 
		System.out.println("Enter number of players:");
		n = userInput.readLine(); 
		System.out.println("Enter number of rounds:"); 
		holes = userInput.readLine(); 
		message = "START GAME|"+ player +"|"+n+"|"+holes;
		// check if n or holes are ints
		int numberOfPlayers = Integer.valueOf(n); 
		int rounds = Integer.valueOf(holes); 
		if (numberOfPlayers <=0 || numberOfPlayers >= 5) 
		{ 
			System.out.println("Invalid number of players entered."); 
			return; 
		}
		else if (rounds <=0 || rounds > 9)
		{
			System.out.println("Invalid round range entered."); 
			return; 
		}
		SocketUtil.SendMessage.sendDatagram(sender, message, server, port);
	}

}
