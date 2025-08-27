# STUN Server and Peer To Peer Six Golf Card game
Client registers their public ip address and peer port into the STUN server. When client selects Start Game the STUN server sends each client their peer's ip address and port this enables UDP hole punching for direct client to client communication. 
	



	
	
After hole punching the dealer (client who started the game) holds the game state. This forms a host-authoritative P2P application. The logical network forms a ring topology where peers communicate their game packets to their right neighbor until it reaches the dealer peer for game processing. The dealer changes the game state and sends a packet to its neighbor notifying a client it's their turn. 