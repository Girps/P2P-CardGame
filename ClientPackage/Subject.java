package ClientPackage;

public interface Subject {
	// register and unregister obsersvers
	public void register(Observer obj); 
	
	public void unregister( ); 
	
	public void notifyObservers(); 
	
	public Object getUpdate(Observer object); 
}
