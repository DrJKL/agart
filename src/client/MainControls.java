package client;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

class MainControls extends JPanel {
  public MainControls(JButton startStopRandomButton, JButton saveImageButton,
      JButton newImageButton, Timer timer) {
    setLayout(new GridLayout(0, 4, 2, 2));

    add(newImageButton);
    add(saveImageButton);
    add(addSpeedSlider(timer));
    add(startStopRandomButton);
  }

  private static JSlider addSpeedSlider(Timer timer) {
    final JSlider speedBar = new JSlider(1, EnvFrame.MAX_DELAY, EnvFrame.MAX_DELAY);
    speedBar.addChangeListener(ce -> timer.setDelay(1000 / (speedBar.getValue())));
    timer.setDelay(1000 / (speedBar.getValue()));
    return speedBar;
  }
}