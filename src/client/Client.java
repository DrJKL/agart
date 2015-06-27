package client;


public class Client {

  public static void main(String[] args) throws Exception {
    javax.swing.SwingUtilities.invokeLater(Client::doGUI);
  }

  private static void doGUI() {
    final EnvFrame frame = new EnvFrame();
    frame.pack();
    frame.setVisible(true);
  }
}
