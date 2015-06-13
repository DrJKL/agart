package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
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

  private final Timer myTimer;
  private final Timer randTimer;

  private final JButton startStopButton;

  private final JButton singleStepButton;
  private final JButton saveImageButton;
  private final JButton saveNegButton;
  private final JButton orgAddButton;
  private final JButton multOrgAddButton;
  private final JButton exterminateStrainButton;
  private final JButton startStopRandomButton;
  private final JButton newImageButton;

  private final JButton setImageButton;
  private final JLabel numOrgLabel, numUpdatesLabel;
  private final JPanel mainControlsPanel, organismControls;
  private final OrgAddPanel orgAddPanel;

  private final Map<String, Strain> strToStrain;
  private final Set<JButton> orgButtons = new HashSet<>();
  private final Set<JButton> dataButtons = new HashSet<>();

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

    myTimer = addTimer();
    randTimer = addRandomTimer();

    mainControlsPanel = new JPanel();
    organismControls = new JPanel();
    orgAddPanel = new OrgAddPanel();

    mainControlsPanel.setLayout(new GridLayout(0, 5, 2, 2));

    organismControls.setLayout(new BoxLayout(organismControls, BoxLayout.Y_AXIS));

    numOrgLabel = new JLabel("Organisms: " + envr.livingOrgs());
    numUpdatesLabel = new JLabel("Updates: " + envr.updates);

    newImageButton = createNewImageButton(contentPane);
    setImageButton = createSetImageButton(contentPane, fc);
    startStopRandomButton = createStartStopRandomButton();

    startStopButton = createStartStopButton();

    singleStepButton = createSingleStepButton();

    saveImageButton = new JButton("Save Image");
    saveImageButton.addActionListener(e -> envr.saveImage());

    saveNegButton = new JButton("Save Negative");
    saveNegButton.addActionListener(e -> envr.saveNegative());

    orgAddButton = createOrgAddButton();
    multOrgAddButton = createMultOrgAddButton();
    exterminateStrainButton = createExterminateStrainButton();

    numOrgLabel.setAlignmentX((float) 0.5);
    numUpdatesLabel.setAlignmentX((float) 0.5);

    orgAddButton.setAlignmentX((float) 0.5);
    multOrgAddButton.setAlignmentX((float) 0.5);
    exterminateStrainButton.setAlignmentX((float) 0.5);

    mainControlsPanel.add(newImageButton);
    mainControlsPanel.add(startStopButton);
    mainControlsPanel.add(singleStepButton);
    mainControlsPanel.add(saveImageButton);
    mainControlsPanel.add(saveNegButton);
    mainControlsPanel.add(setImageButton);

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
      myTimer.setDelay(1000 / (speedBar.getValue()));
      randTimer.setDelay(1000 / (speedBar.getValue()));
    });
    speedBar.setValue(MAX_DELAY / 2);

    mainControlsPanel.add(new JLabel(" Slow"));
    mainControlsPanel.add(speedBar);
    mainControlsPanel.add(new JLabel("Fast "));
  }

  private JButton createExterminateStrainButton() {
    final JButton exterminateStrainButton = new JButton("Exterminate Strain");
    exterminateStrainButton.addActionListener(arg0 -> {
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

  private JButton createSingleStepButton() {
    final JButton singleStepButton = new JButton("Step");
    singleStepButton.addActionListener(e -> {
      envr.update();
      updateData();
      myPanel.repaint();
    });
    return singleStepButton;
  }

  private JButton createStartStopRandomButton() {
    final JButton startStopRandomButton = new JButton("Start Random");
    startStopRandomButton.addActionListener(e -> {
      if (myTimer.isRunning()) {
        myTimer.stop();
        startStopButton.setText("Start");
      }
      if (randTimer.isRunning()) {
        randTimer.stop();
        startStopRandomButton.setText("Start Random");
      } else {
        randTimer.start();
        startStopRandomButton.setText("Pause Random");
      }
      toggleRandomButtons();
      toggleRunningButtons();
    });
    return startStopRandomButton;
  }

  private JButton createSetImageButton(final Container contentPane, final JFileChooser fc) {
    final JButton setImageButton = new JButton("Set Image");
    setImageButton.addActionListener(e -> {
      fc.showOpenDialog(null);
      final File img = fc.getSelectedFile();
      setUpEnvironment(img);
      setSize(envr.getWidth(), envr.getHeight());
      myPanel.setBorder(BorderFactory.createLineBorder(Color.black));
      updateData();
      myPanel.repaint();
      contentPane.add(myPanel, "Center");
      contentPane.validate();
    });
    return setImageButton;
  }

  private JButton createNewImageButton(final Container contentPane) {
    final JButton newImageButton = new JButton("Reset");
    newImageButton.addActionListener(e -> {
      if (myTimer.isRunning()) {
        myTimer.stop();
        startStopButton.setText("Start");
      }
      if (randTimer.isRunning()) {
        randTimer.stop();
        startStopRandomButton.setText("Start Random");
      }
      setUpEnvironment();
      updateData();
      toggleRandomButtons();
      toggleRunningButtons();
      contentPane.add(myPanel, "Center");
      myPanel.repaint();
    });
    return newImageButton;
  }

  private JButton createStartStopButton() {
    final JButton startStopButton = new JButton("Start");
    startStopButton.addActionListener(e -> {
      if (randTimer.isRunning()) {
        randTimer.stop();
        startStopRandomButton.setText("Start Random");
      }
      if (myTimer.isRunning()) {
        myTimer.stop();
        startStopButton.setText("Start");
      } else {
        myTimer.start();
        startStopButton.setText("Pause");
      }
      toggleRandomButtons();
      toggleRunningButtons();
    });
    return startStopButton;
  }

  private void toggleRandomButtons() {
    Stream.concat(orgButtons.stream(), dataButtons.stream()).forEach(button -> {
      button.setEnabled(!randTimer.isRunning());
    });
  }

  private void toggleRunningButtons() {
    if (randTimer.isRunning()) {
      return;
    }
    dataButtons.forEach(button -> {
      button.setEnabled(!myTimer.isRunning());
    });
  }

  private void setUpEnvironment() {
    envr = new Environment(ImageUtil.setupNewEnvironment(800, 600, false));
    myPanel = new MyPanel(envr.image);
    myPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    final Dimension d = new Dimension(envr.getWidth() + 160, envr.getHeight() + 95);
    setPreferredSize(d);
    pack();
    myPanel.repaint();
  }

  private void setUpEnvironment(File img) {
    BufferedImage bmg = null;
    try {
      bmg = ImageIO.read(img);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    envr = new Environment(bmg);
    myPanel = new MyPanel(envr.image);
    final Dimension d = new Dimension(envr.getWidth() + 160, envr.getHeight() + 95);
    setPreferredSize(d);
    pack();

  }

  private Timer addTimer() {
    final ActionListener updater = e -> {
      envr.update();
      updateData();
      myPanel.repaint();
    };
    return new Timer(MAX_DELAY / 2, updater);
  }

  private Timer addRandomTimer() {
    final ActionListener updater = e -> {
      if (envr.livingOrgs() == 0) {
        final Strain strain = strToStrain.get(orgAddPanel.getChosenStrain());
        strain.resetYoungest();
        envr.add(1, strain);
      }
      envr.update();
      updateData();
      myPanel.repaint();
    };
    return new Timer(MAX_DELAY / 2, updater);
  }

  private void updateData() {
    numOrgLabel.setText("Organisms: " + envr.livingOrgs());
    numUpdatesLabel.setText("Updates: " + envr.updates);
  }
}