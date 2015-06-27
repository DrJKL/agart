package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import src.Environment;

@SuppressWarnings("serial")
class EnvironmentPanel extends JPanel {

  Environment environment;

  public EnvironmentPanel(Environment environment) {
    setEnvironment(environment);
    setBorder(BorderFactory.createLineBorder(Color.black));
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
    setPreferredSize(new Dimension(environment.getWidth(), environment.getHeight()));
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.drawImage(environment.image, 0, 0, this);
  }
}
