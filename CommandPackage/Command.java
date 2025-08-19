package CommandPackage;

import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class Command {
	protected String description;
	protected DatagramSocket sender; 
	protected InetAddress server; // server address
	protected int port; // server port
	public abstract void execute() throws Exception; 
	public Command(String desc,DatagramSocket sock, InetAddress server, int port) 
	{
		this.description = desc; 
		this.sender = sock;
		this.port= port; 
		this.server=server; 
		
	} 
	
	
	public String getDescription() 
	{
		return this.description; 
	}
	
}
