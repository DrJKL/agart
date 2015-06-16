package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import src.Environment;
import src.Strain;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import core.ImageUtil;

@SuppressWarnings("serial")
class EnvFrame extends JFrame {
  private Environment envr;
  private MyPanel myPanel;

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
    final Container contentPane = getContentPane();

    randTimer = addRandomTimer();

    newImageButton = createNewImageButton(contentPane);
    startStopRandomButton = createStartStopRandomButton();

    saveImageButton = new JButton("Save Image");
    saveImageButton.addActionListener(e -> envr.saveImage());

    multOrgAddButton = createMultOrgAddButton();
    exterminateStrainButton = createExterminateStrainButton();

    mainControlsPanel = new MainControls(startStopRandomButton, saveImageButton, newImageButton,
        randTimer);

    organismControls = new OrganismControls(new OrgAddPanel(), multOrgAddButton,
        exterminateStrainButton);
    setUpEnvironment(contentPane);

    contentPane.add(mainControlsPanel, "North");
    contentPane.add(organismControls, "East");

  }

  private JButton createExterminateStrainButton() {
    final JButton exterminateStrainButton = new JButton("Exterminate Strain");
    exterminateStrainButton.addActionListener(e -> {
      final Object[] strains = envr.activeStrains.keySet().toArray();
      if (strains.length == 0) {
        return;
      }
      final Strain strChoice = (Strain) JOptionPane.showInputDialog(new JFrame(), "Strain?",
          "Strain Choice", JOptionPane.PLAIN_MESSAGE, null, strains, strains[0]);
      envr.exterminate(organismControls.orgAddPanel.getChosenStrain(strChoice.getStrainName()));
      organismControls.updateData(envr);
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
      envr.add(Integer.parseInt(numberInput), organismControls.orgAddPanel.getChosenStrain());
      organismControls.updateData(envr);
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

  private JButton createNewImageButton(final Container contentPane) {
    final JButton newImageButton = new JButton("Reset");
    newImageButton.addActionListener(e -> setUpEnvironment(contentPane));
    return newImageButton;
  }

  private void setUpEnvironment(Container contentPane) {
    envr = new Environment(ImageUtil.setupNewEnvironment(800, 600, false));
    if (myPanel != null) {
      remove(myPanel);
    }
    myPanel = new MyPanel(envr.image);
    myPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    final Dimension d = new Dimension(envr.getWidth() + 213, envr.getHeight() + 66);
    setPreferredSize(d);
    pack();
    contentPane.add(myPanel, "Center");
    myPanel.repaint();
    organismControls.updateData(envr);
  }

  private void doTheThing() {
    envr.update();
    organismControls.updateData(envr);
    myPanel.repaint();
  }

  private Timer addRandomTimer() {
    final ActionListener updater = e -> {
      if (envr.livingOrgs() == 0) {
        final Strain strain = organismControls.orgAddPanel.getChosenStrain();
        strain.resetYoungest();
        envr.add(1, strain);
      }
      doTheThing();
    };
    return new Timer(MAX_DELAY / 2, updater);
  }

  private static class OrganismControls extends JPanel {
    public final OrgAddPanel orgAddPanel;
    private final JLabel numOrgLabel;
    private final JLabel numUpdatesLabel;
    private final Set<JButton> buttons;

    public OrganismControls(OrgAddPanel orgAddPanel, JButton multOrgAddButton,
        JButton exterminateStrainButton) {
      this.orgAddPanel = orgAddPanel;
      this.numOrgLabel = new JLabel();
      this.numUpdatesLabel = new JLabel();
      this.buttons = ImmutableSet.of(multOrgAddButton, exterminateStrainButton);

      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

      add(orgAddPanel);
      add(numOrgLabel);
      add(numUpdatesLabel);
      add(multOrgAddButton);
      add(exterminateStrainButton);

      orgAddPanel.setAlignmentX(LEFT_ALIGNMENT);
      numOrgLabel.setAlignmentX(LEFT_ALIGNMENT);
      numUpdatesLabel.setAlignmentX(LEFT_ALIGNMENT);

      multOrgAddButton.setAlignmentX(LEFT_ALIGNMENT);
      exterminateStrainButton.setAlignmentX(LEFT_ALIGNMENT);
    }

    void updateData(Environment envr) {
      numOrgLabel.setText("Organisms: " + envr.livingOrgs());
      numUpdatesLabel.setText("Updates: " + envr.updates);
    }

    void toggleRandomButtons(boolean enabled) {
      buttons.stream().forEach(button -> button.setEnabled(enabled));
    }
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
      speedBar.addChangeListener(ce -> {
        timer.setDelay(1000 / (speedBar.getValue()));
      });
      timer.setDelay(1000 / (speedBar.getValue()));
      return speedBar;
    }
  }
}
