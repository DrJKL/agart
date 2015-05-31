package src;

/** */
public interface Strain {

	/** Returns the Strain's Name as a String. */
	public String getStrainName();
	
	/** Renames the Strain to give unique Strain. */
	public void renameStrain(String str);
	
	/** Sets the youngest generation for check methods. */
	public void youngest(int orgGen);
	
	/** Sets the youngest generation for check methods. */
	public int getYoungest();
	
	/** This method defines the 'AI' of the organisms in this strain. */
	public void update(Organism org);
}
