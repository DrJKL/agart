package src;

/** */
public interface Strain {

  /** Returns the Strain's Name as a String. */
  String getStrainName();

  /** Sets the youngest generation for check methods. */
  void updateYoungest(int orgGen);

  /** Sets the youngest generation for check methods. */
  int getYoungest();

  /** This method defines the 'AI' of the organisms in this strain. */
  void update(Organism org);
}
