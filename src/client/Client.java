package client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

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

@SuppressWarnings("serial")
class MyPanel extends JPanel {

  private final BufferedImage bf;

  public MyPanel(BufferedImage image) {
    bf = image;
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.drawImage(bf, 0, 0, this);
  }
}
