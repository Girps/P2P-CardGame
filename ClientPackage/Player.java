package ClientPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import CommandPackage.*;
import ServerPackage.Peer; 



public class Player implements Observer, Runnable {
	
	private Subject game; 
	private boolean terminate = false; 
	private GAMESTATE state = GAMESTATE.NON_GAME; 
	private GAMESTATE prevState = GAMESTATE.NON_GAME; 
	private boolean isDealer = false;  
	private boolean currentTurn = false; 
	private boolean initalTurn = true; 
	private String facedUpCard = ""; 
	
	
	
	private DatagramSocket socket;
	private DatagramSocket peerSocket; 
	private int serverPort; 
	private InetAddress serverAddress; 
	private Peer neighbor ; 
	private String playerName; 
	private  HashMap<String,Command> commands = null; 
	private HashMap<String, Command> mainCmds= new HashMap<String, Command>(); 
	private HashMap<String, Command> lobbyCmdsDealer = new HashMap<String, Command>(); 
	private HashMap<String, Command> lobbyCmdsRegular = new HashMap<String, Command>(); 
	
	private HashMap<String, Command> gameCmdsCurrentTurnInit = new HashMap<String, Command>(); // 4 options
	private HashMap<String, Command> gameCmdsCurrentTurn = new HashMap<String, Command>(); // 4 options
	private HashMap<String, Command> gameCmdsNonTurn = new HashMap<String, Command>(); // nothing
	
	
	
	public Player( Game game, int serverPort, InetAddress serverAddress, DatagramSocket serverSocket) {
		
		this.serverAddress= serverAddress; 
		this.serverPort = serverPort; 
		socket = serverSocket; 
		this.game = game; 
		mainCmds.put("1", new Register("Register", socket, serverAddress, serverPort));
		mainCmds.put("2", new QueryPlayer("Query players", socket, serverAddress, serverPort) ); 
		mainCmds.put("3", new CreateGame("Start Games", socket, serverAddress, serverPort)); 
		mainCmds.put("4", new QueryGames("Query Games", socket, serverAddress, serverPort)); 
		mainCmds.put("5", new DeRegister("Deregister", socket, serverAddress, serverPort)); 
		mainCmds.put("6", new TerminateProgram("Terminate program",socket,serverAddress, serverPort)); 
		
		// Dealer only commands
		lobbyCmdsDealer.put("1", new Message("Send Message to peers", socket, serverAddress, serverPort, this )); 
		lobbyCmdsDealer.put("2", new StartGame("Start game", socket, serverAddress, serverPort, this)); 
		lobbyCmdsDealer.put("3", new EndGame("End game", socket, serverAddress, serverPort)); 

		// Non dealer commands 
		lobbyCmdsRegular.put("1", new Message("Send Message to peers", socket, serverAddress, serverPort, this )); 
		commands = mainCmds; 
		
		// game cmds 
		gameCmdsCurrentTurn.put("1", new SwapDiscard("Swap a card with Discard deck.", serverSocket, serverAddress, serverPort, this)); 
		gameCmdsCurrentTurn.put("2", new SwapStock("Swap a card with Stock deck.", serverSocket, serverAddress, serverPort, this)); 
		
		
		gameCmdsCurrentTurnInit.put("1", new FlipCards("Pick 2 cards to flip", serverSocket, serverAddress, serverPort, this)); 
		
	} 
	
	
	
	@Override 
	public void update() {
		GAMESTATE state = (GAMESTATE) game.getUpdate(this); 
		this.state = state; 
		// determines which menu to print in and options allowed d
		switch (state) 
		{
			case NON_GAME:
				commands = mainCmds; 
				break; 
			case IN_LOBBY:
				// check if dealer 
				if (this.isDealer) 
				{
					commands = lobbyCmdsDealer; 
				}
				else 
				{
					commands = lobbyCmdsRegular;
				}
				break; 
			case IN_GAME_TURN:
				
				// check initali turn
				if(this.initalTurn) 
				{ 
					commands = gameCmdsCurrentTurnInit;
				}
				else
				{
					commands = gameCmdsCurrentTurn; 
				}
				break; 
			case IN_GAME_NON_TURN: 
				commands = gameCmdsNonTurn; 
				break; 
			default: 
				throw new RuntimeException("Undefined gamestate"); 
		}
	}
	
	@Override
	public void setSubject(Subject sub) {
		// TODO Auto-generated method stub
		game = sub; 
	}
	
	public Subject getSubject() 
	{
		return this.game; 
	}
	
	// print current menu 
	public  void printMenu() 
	{
		 
		// check if empty 
		if (this.state == GAMESTATE.IN_GAME_NON_TURN) 
		{
			System.out.println("WAITING TURN..."); 
		}
		Set<Map.Entry<String, Command>> entrySet = commands.entrySet(); 
		for(Map.Entry<String, Command> en: entrySet) 
		{
			String desc = en.getValue().getDescription(); 
			String choice = en.getKey(); 
			System.out.println("[" + choice+ "] " + desc); 
		} 
		System.out.println("\n");
	}

	
	public void menuLoop() throws Exception 
	{
		printMenu(); 
		// make sure piror state did not change
		while(prevState == state)
		{ 
			BufferedReader userInput  
			= new BufferedReader(new InputStreamReader(System.in));
			// input not ready continue 
			if(!userInput.ready()) 
			{
				continue; 
			}
			String choice = userInput.readLine(); 
			if(commands.get(choice) != null) 
			{ 
				commands.get(choice).execute(); 
			}
			else{
				System.out.println("Invalid selection"); 
			}
			
			if(prevState == state)
				printMenu(); 
		} 
	} 
			

	@Override
	public void run() {
		
		// run this method till terminate
		while(!terminate) 
		{
			prevState = state; 
			try {
				menuLoop();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public void setNeighbor(Peer neighbor) throws SocketException {
		this.neighbor = neighbor; 
		// create the socket
		this.peerSocket= new DatagramSocket(); 
		peerSocket.connect(neighbor.address, neighbor.PPORT); // connect to neighbor 
	}
	
	public void setName(String name) 
	{
		this.playerName = name; 
	}
	
	public String getName() 
	{
		return this.playerName; 
	}
	
	public DatagramSocket getPeerSocket() 
	{
		return this.peerSocket; 
	}
	
	public void setDealer(boolean isDealer)
	{
		this.isDealer=isDealer; 
		// if they are a dealer remove 
	}
	
	public boolean isDealer() 
	{
		return this.isDealer;
	}
	
	public void setTurn(Boolean turn) 
	{
		this.currentTurn = turn; 
	}
	
	public boolean getTurn() 
	{
		return this.currentTurn; 
	}
	
	
	// puts up the stock card 
	public void setCard(String card) 
	{
		this.facedUpCard = card; 
	}
	
	public String getFacedCard() 
	{
		return this.facedUpCard; 
	}
	
	
	public void setInit(boolean intial) 
	{
		this.initalTurn = intial; 
	}



	public void setTerminate(boolean term) {
		this.terminate = term; 
	}
}
