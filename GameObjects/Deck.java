package GameObjects;
import java.util.ArrayList;
import java.util.Collections;


// Class composites card class 
public class Deck {
	
	private ArrayList<Card> cards; 
	public Deck() 
	{
		cards = new ArrayList<Card>(); 
		setUpCards(Card.COLOR.RED, Card.TYPE.DIAMOND);
		setUpCards(Card.COLOR.BLACK, Card.TYPE.CLUB); 
		setUpCards(Card.COLOR.RED, Card.TYPE.HEART); 
		setUpCards(Card.COLOR.BLACK, Card.TYPE.SPADE);
		// now shuffle
		Collections.shuffle(cards);
	}
	
	public void setUpCards(Card.COLOR color, Card.TYPE type) 
	{
		for(int i = 0; i < 13; ++i) 
		{
			cards.add(new Card(String.valueOf(i),i, color, type, Card.FACE.DOWN)); 
		}
	}
	
	// now set up the cards 
	public ArrayList<Card> getCards()
	{
		return this.cards; 
	}
	
	
}
