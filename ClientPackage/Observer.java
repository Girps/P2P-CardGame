package ClientPackage;

public interface Observer {
	// method to update observer it is called by subject
	public void update(); 
	// attach with subject to observer
	public void setSubject(Subject sub); 
}
