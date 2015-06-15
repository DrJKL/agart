package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

import src.Environment;
import src.Strain;
import core.ImageUtil;

@SuppressWarnings("serial")
class EnvFrame extends JFrame {
  private Environment envr;

  private MyPanel myPanel;

  private final Timer randTimer;

  private final JButton saveImageButton;
  private final JButton orgAddButton;
  private final JButton multOrgAddButton;
  private final JButton exterminateStrainButton;
  private final JButton startStopRandomButton;
  private final JButton newImageButton;

  private final JLabel numOrgLabel, numUpdatesLabel;
  private final JPanel mainControlsPanel, organismControls;
  private final OrgAddPanel orgAddPanel;

  private final Map<String, Strain> strToStrain;
  private final Set<JButton> orgButtons = new HashSet<>();

  private static final int MAX_DELAY = 1000;

  public EnvFrame() {
    setTitle("VIRUS");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final Container contentPane = getContentPane();
    final JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter(new ImageFilter());
    setUpEnvironment();
    strToStrain = OrgAddPanel.strains.stream().collect(
        Collectors.toMap(Strain::getStrainName, Function.identity()));

    myPanel.setBorder(BorderFactory.createLineBorder(Color.black));

    contentPane.add(myPanel, "Center");

    randTimer = addRandomTimer();

    mainControlsPanel = new JPanel();
    organismControls = new JPanel();
    orgAddPanel = new OrgAddPanel();

    mainControlsPanel.setLayout(new GridLayout(0, 5, 2, 2));

    organismControls.setLayout(new BoxLayout(organismControls, BoxLayout.Y_AXIS));

    numOrgLabel = new JLabel("Organisms: " + envr.livingOrgs());
    numUpdatesLabel = new JLabel("Updates: " + envr.updates);

    newImageButton = createNewImageButton(contentPane);
    startStopRandomButton = createStartStopRandomButton();

    saveImageButton = new JButton("Save Image");
    saveImageButton.addActionListener(e -> envr.saveImage());

    orgAddButton = createOrgAddButton();
    multOrgAddButton = createMultOrgAddButton();
    exterminateStrainButton = createExterminateStrainButton();

    numOrgLabel.setAlignmentX((float) 0.5);
    numUpdatesLabel.setAlignmentX((float) 0.5);

    orgAddButton.setAlignmentX((float) 0.5);
    multOrgAddButton.setAlignmentX((float) 0.5);
    exterminateStrainButton.setAlignmentX((float) 0.5);

    mainControlsPanel.add(newImageButton);
    mainControlsPanel.add(saveImageButton);

    organismControls.add(orgAddPanel);

    organismControls.add(numOrgLabel);
    organismControls.add(numUpdatesLabel);
    organismControls.add(orgAddButton);
    organismControls.add(multOrgAddButton);
    organismControls.add(exterminateStrainButton);

    orgButtons.add(orgAddButton);
    orgButtons.add(multOrgAddButton);
    orgButtons.add(exterminateStrainButton);

    addSpeedSlider();

    mainControlsPanel.add(startStopRandomButton);

    contentPane.add(mainControlsPanel, "North");
    contentPane.add(organismControls, "East");

  }

  private void addSpeedSlider() {
    final JSlider speedBar = new JSlider(1, MAX_DELAY);
    speedBar.addChangeListener(ce -> {
      randTimer.setDelay(1000 / (speedBar.getValue()));
    });
    speedBar.setValue(MAX_DELAY / 2);

    mainControlsPanel.add(speedBar);
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
      final String strX = strChoice.getStrainName();
      envr.exterminate(strToStrain.get(strX));
      updateData();
    });
    return exterminateStrainButton;
  }

  private JButton createMultOrgAddButton() {
    final JButton multOrgAddButton = new JButton("Add Organisms");
    multOrgAddButton.addActionListener(e -> {
      final int num = Integer.parseInt(JOptionPane.showInputDialog("Number of Organisms?"));
      envr.add(num, strToStrain.get(orgAddPanel.getChosenStrain()));
      updateData();
    });
    return multOrgAddButton;
  }

  private JButton createOrgAddButton() {
    final JButton orgAddButton = new JButton("Add Organism");
    orgAddButton.addActionListener(e -> {
      envr.add(1, strToStrain.get(orgAddPanel.getChosenStrain()));
      updateData();
    });
    return orgAddButton;
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
      toggleRandomButtons();
    });
    return startStopRandomButton;
  }

  private JButton createNewImageButton(final Container contentPane) {
    final JButton newImageButton = new JButton("Reset");
    newImageButton.addActionListener(e -> {
      if (randTimer.isRunning()) {
        randTimer.stop();
        startStopRandomButton.setText("Start Random");
      }
      setUpEnvironment();
      updateData();
      toggleRandomButtons();
      contentPane.add(myPanel, "Center");
      myPanel.repaint();
    });
    return newImageButton;
  }

  private void toggleRandomButtons() {
    orgButtons.stream().forEach(button -> button.setEnabled(!randTimer.isRunning()));
  }

  private void setUpEnvironment() {
    envr = new Environment(ImageUtil.setupNewEnvironment(800, 600, false));
    if (myPanel != null) {
      remove(myPanel);
    }
    myPanel = new MyPanel(envr.image);
    myPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    final Dimension d = new Dimension(envr.getWidth() + 213, envr.getHeight() + 66);
    setPreferredSize(d);
    pack();
    myPanel.repaint();
  }

  private void doTheThing() {
    envr.update();
    updateData();
    myPanel.repaint();
  }

  private Timer addRandomTimer() {
    final ActionListener updater = e -> {
      if (envr.livingOrgs() == 0) {
        final Strain strain = strToStrain.get(orgAddPanel.getChosenStrain());
        strain.resetYoungest();
        envr.add(1, strain);
      }
      doTheThing();
    };
    return new Timer(MAX_DELAY / 2, updater);
  }

  private void updateData() {
    numOrgLabel.setText("Organisms: " + envr.livingOrgs());
    numUpdatesLabel.setText("Updates: " + envr.updates);
  }
}
