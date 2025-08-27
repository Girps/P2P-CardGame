package SocketUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendMessage {
	
	// method to send packets to an address 
	public static void sendDatagram(DatagramSocket sender,String payload, InetAddress address, int port) throws IOException 
	{
		byte[] data = payload.getBytes("UTF-8");
		DatagramPacket packet = new DatagramPacket(data,data.length, address,port); 
		sender.send(packet);
	
		
	}
}
