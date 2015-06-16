package src;

/** */
public interface Strain {

  /** Returns the Strain's Name as a String. */
  String getStrainName();

  /** This method defines the 'AI' of the organisms in this strain. */
  void update(Organism org);
}
