// Class represents a card 
package GameObjects;


public class Card {
	
	enum COLOR
	{
		RED,
		BLACK
	}
	enum TYPE
	{
		DIAMOND, 
		CLUB, 
		HEART, 
		SPADE
	}
	
	public enum FACE
	{
		UP,
		DOWN
	}
	
	private String value; 
	private int valueInt; 
	private	COLOR color;
	private TYPE type; 
	private FACE face; 
	
	public Card(String value, int valueInt, COLOR color, TYPE type, FACE face) 
	{
		switch(value) 
		{
		case "0": 
			value = "A"; 
			break; 
		case "11":
			value = "J"; 
			break; 
		case "12":
			value = "Q"; 
			break; 
		case "13": 
			value = "K"; 
			break; 
		} 
		this.value = value; 
		this.valueInt = valueInt; 
		this.color= color;
		this.type = type; 
		this.face = face; 
		
	}
	
	public void setFace(FACE face) 
	{
		this.face = face; 
	}
	
	public FACE getFace() 
	{
		return this.face; 
	}
	
	public COLOR getColor() 
	{
		return this.color; 
	}
	
	public TYPE getType() 
	{
		return this.type; 
	}
	
	public String getValueStr() 
	{
		return this.value; 
	}
	
	public int getValueInt() 
	{
		return this.valueInt; 
	}
	
	@Override
	public String toString() {
		
		String res= "***";
		if(this.face == FACE.UP)
		{ 
			res = value; 
			res += this.type.toString().charAt(0); 
		}
		return res; 
	}
}
