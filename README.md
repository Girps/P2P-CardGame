# STUN Server and Peer To Peer Six Golf Card game
Client registers their public ip address and peer port into the STUN server. When client selects Start Game the STUN server sends each client their peer's ip address and port this enables UDP hole punching for direct client to client communication. 
	

<img width="562" height="231" alt="Untitled Diagram drawio" src="https://github.com/user-attachments/assets/10d9ffb7-9e4a-4cec-a3f2-8eb19eae60fb" />


	
	
After hole punching the dealer (client who started the game) holds the game state. This forms a host-authoritative P2P application. The logical network forms a ring topology where peers communicate their game packets to their right neighbor until it reaches the dealer peer for game processing. The dealer changes the game state and sends a packet to its neighbor notifying a client it's their turn. 

<img width="640" height="531" alt="P2P drawio" src="https://github.com/user-attachments/assets/33988844-116f-4df9-bca3-52dfa9298b7d" />
