package client;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import src.Environment;
import src.Strain;

import com.google.common.base.Strings;

import core.ImageUtil;

@SuppressWarnings("serial")
class EnvFrame extends JFrame {
  private final EnvironmentPanel environmentPanel;

  private final Timer randTimer;

  private final JButton saveImageButton;
  private final JButton startStopRandomButton;
  private final JButton newImageButton;
  private final JButton multOrgAddButton;
  private final JButton exterminateStrainButton;

  private final JPanel mainControlsPanel;
  private final OrganismControls organismControls;

  private static final int MAX_DELAY = 1000;

  public EnvFrame() {
    setTitle("VIRUS");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    randTimer = addRandomTimer();

    newImageButton = createNewImageButton();
    startStopRandomButton = createStartStopRandomButton();

    environmentPanel = new EnvironmentPanel(newEnvironment());
    saveImageButton = new JButton("Save Image");
    saveImageButton.addActionListener(e -> environmentPanel.environment.saveImage());

    multOrgAddButton = createMultOrgAddButton();
    exterminateStrainButton = createExterminateStrainButton();

    mainControlsPanel = new MainControls(startStopRandomButton, saveImageButton, newImageButton,
        randTimer);

    organismControls = new OrganismControls(new OrgAddPanel(), multOrgAddButton,
        exterminateStrainButton);

    final Container contentPane = getContentPane();
    contentPane.add(environmentPanel, "Center");
    contentPane.add(mainControlsPanel, "North");
    contentPane.add(organismControls, "East");

  }

  private JButton createExterminateStrainButton() {
    final JButton exterminateStrainButton = new JButton("Exterminate Strain");
    exterminateStrainButton.addActionListener(e -> {
      final Object[] strains = environmentPanel.environment.activeStrains.keySet().toArray();
      if (strains.length == 0) {
        return;
      }
      final Strain strChoice = (Strain) JOptionPane.showInputDialog(new JFrame(), "Strain?",
          "Strain Choice", JOptionPane.PLAIN_MESSAGE, null, strains, strains[0]);
      environmentPanel.environment.exterminate(organismControls.orgAddPanel.getChosenStrain(strChoice
          .getStrainName()));
      organismControls.updateData(environmentPanel.environment);
    });
    return exterminateStrainButton;
  }

  private JButton createMultOrgAddButton() {
    final JButton multOrgAddButton = new JButton("Add Organisms");
    multOrgAddButton.addActionListener(e -> {
      final String numberInput = JOptionPane.showInputDialog("Number of Organisms?");
      if (Strings.isNullOrEmpty(numberInput)) {
        return;
      }
      environmentPanel.environment.add(Integer.parseInt(numberInput),
          organismControls.orgAddPanel.getChosenStrain());
      organismControls.updateData(environmentPanel.environment);
    });
    return multOrgAddButton;
  }

  private JButton createStartStopRandomButton() {
    final JButton startStopRandomButton = new JButton("Start Random");
    startStopRandomButton.addActionListener(e -> {
      if (randTimer.isRunning()) {
        randTimer.stop();
        startStopRandomButton.setText("Start Random");
      } else {
        randTimer.start();
        startStopRandomButton.setText("Pause Random");
      }
      organismControls.toggleRandomButtons(!randTimer.isRunning());
    });
    return startStopRandomButton;
  }

  private JButton createNewImageButton() {
    final JButton newImageButton = new JButton("Reset");
    newImageButton.addActionListener(e -> environmentPanel.setEnvironment(newEnvironment()));
    return newImageButton;
  }

  private static Environment newEnvironment() {
    return new Environment(ImageUtil.setupNewEnvironment(800, 600, false));
  }

  private void doTheThing() {
    environmentPanel.environment.update();
    organismControls.updateData(environmentPanel.environment);
    environmentPanel.repaint();
  }

  private Timer addRandomTimer() {
    final ActionListener updater = e -> {
      if (environmentPanel.environment.livingOrgs() == 0) {
        environmentPanel.environment.add(1, organismControls.orgAddPanel.getChosenStrain());
      }
      doTheThing();
    };
    return new Timer(MAX_DELAY / 2, updater);
  }

  private static class MainControls extends JPanel {
    public MainControls(JButton startStopRandomButton, JButton saveImageButton,
        JButton newImageButton, Timer timer) {
      setLayout(new GridLayout(0, 4, 2, 2));

      add(newImageButton);
      add(saveImageButton);
      add(addSpeedSlider(timer));
      add(startStopRandomButton);
    }

    private static JSlider addSpeedSlider(Timer timer) {
      final JSlider speedBar = new JSlider(1, MAX_DELAY, MAX_DELAY);
      speedBar.addChangeListener(ce -> timer.setDelay(1000 / (speedBar.getValue())));
      timer.setDelay(1000 / (speedBar.getValue()));
      return speedBar;
    }
  }
}
