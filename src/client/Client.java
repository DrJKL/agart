package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import src.Environment;

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

  Environment environment;

  public MyPanel(Environment environment) {
    setEnvironment(environment);
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
    setBorder(BorderFactory.createLineBorder(Color.black));
    final Dimension d = new Dimension(environment.getWidth(), environment.getHeight());
    setPreferredSize(d);
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.drawImage(environment.image, 0, 0, this);
  }
}
