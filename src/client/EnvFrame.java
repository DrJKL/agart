package client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import src.Environment;
import src.Strain;
import strains.DefaultStrain;
import strains.DrippyStrain;
import strains.FloatyStrain;
import strains.RayStrain;
import core.ImageUtil;

@SuppressWarnings("serial")
class EnvFrame extends JFrame {
  private Environment envr;

  private MyPanel myPanel;

  private javax.swing.Timer myTimer;
  private javax.swing.Timer randTimer;

  private JButton startStopButton, singleStepButton;
  private final JButton saveImageButton;
  private final JButton saveNegButton;
  private JButton orgAddButton;
  private JButton multOrgAddButton;
  private JButton exterminateStrainButton;
  private JButton startStopRandomButton;
  private JButton newImageButton, setImageButton;
  private final JLabel numOrgLabel, numUpdatesLabel;
  private final JPanel mainControlsPanel, organismControls;
  private final OrgAddPanel orgAddPanel;

  private final HashMap<String, Strain> strToStrain = new HashMap<>();
  private final Set<JButton> orgButtons = new HashSet<>();
  private final Set<JButton> dataButtons = new HashSet<>();

  private static final int MAX_DELAY = 140;

  public EnvFrame() {
    setTitle("VIRUS");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final Container contentPane = getContentPane();
    final JFileChooser fc = new JFileChooser();
    fc.addChoosableFileFilter(new ImageFilter());
    setUpEnvironment();
    final List<Strain> strainOptions = Arrays.asList(new DefaultStrain(), new DrippyStrain(),
        new FloatyStrain(), new RayStrain());
    strainOptions.forEach(s -> {
      strToStrain.put(s.getStrainName(), s);
    });

    myPanel.setBorder(BorderFactory.createLineBorder(Color.black));

    contentPane.add(myPanel, "Center");

    addTimer();
    addRandomTimer();

    mainControlsPanel = new JPanel();
    organismControls = new JPanel();
    orgAddPanel = new OrgAddPanel();

    mainControlsPanel.setLayout(new GridLayout(0, 5, 2, 2));

    organismControls.setLayout(new BoxLayout(organismControls, BoxLayout.Y_AXIS));

    numOrgLabel = new JLabel("Organisms: " + envr.livingOrgs());
    numUpdatesLabel = new JLabel("Updates: " + envr.updates);

    createNewImageButton(contentPane);
    createSetImageButton(contentPane, fc);
    createStartStopRandomButton();

    createStartStopButton();

    createSingleStepButton();

    saveImageButton = new JButton("Save Image");
    saveImageButton.addActionListener(e -> envr.saveImage());

    saveNegButton = new JButton("Save Negative");
    saveNegButton.addActionListener(e -> envr.saveNegative());

    createOrgAddButton();
    createMultOrgAddButton();
    createExterminateStrainButton();

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

    mainControlsPanel.add(new JLabel(" Slow"));
    mainControlsPanel.add(speedBar);
    mainControlsPanel.add(new JLabel("Fast "));
  }

  private void createExterminateStrainButton() {
    exterminateStrainButton = new JButton("Exterminate Strain");
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
  }

  private void createMultOrgAddButton() {
    multOrgAddButton = new JButton("Add Organisms");
    multOrgAddButton.addActionListener(e -> {
      final int num = Integer.parseInt(JOptionPane.showInputDialog("Number of Organisms?"));
      envr.add(num, strToStrain.get(orgAddPanel.getChosenStrain()));
      updateData();
    });
  }

  private void createOrgAddButton() {
    orgAddButton = new JButton("Add Organism");
    orgAddButton.addActionListener(e -> {
      envr.add(1, strToStrain.get(orgAddPanel.getChosenStrain()));
      updateData();
    });
  }

  private void createSingleStepButton() {
    singleStepButton = new JButton("Step");
    singleStepButton.addActionListener(e -> {
      envr.update();
      updateData();
      myPanel.repaint();
    });
  }

  private void createStartStopRandomButton() {
    startStopRandomButton = new JButton("Start Random");
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
  }

  private void createSetImageButton(final Container contentPane, final JFileChooser fc) {
    setImageButton = new JButton("Set Image");
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
  }

  private void createNewImageButton(final Container contentPane) {
    newImageButton = new JButton("Reset");
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
  }

  private void createStartStopButton() {
    startStopButton = new JButton("Start");
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
    final Dimension d = new Dimension(envr.getWidth() + 150, envr.getHeight() + 100);
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
    final Dimension d = new Dimension(envr.getWidth() + 150, envr.getHeight() + 100);
    setPreferredSize(d);
    this.pack();

  }

  private void addTimer() {
    final ActionListener updater = e -> {
      envr.update();
      updateData();
      myPanel.repaint();
    };
    myTimer = new javax.swing.Timer(MAX_DELAY / 2, updater);
  }

  private void addRandomTimer() {
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
    randTimer = new javax.swing.Timer(MAX_DELAY / 2, updater);
  }

  private void updateData() {
    numOrgLabel.setText("Organisms: " + envr.livingOrgs());
    numUpdatesLabel.setText("Updates: " + envr.updates);
  }
}