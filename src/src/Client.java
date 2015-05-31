package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import strains.DefaultStrain;
import strains.DrippyStrain;
import strains.FloatyStrain;
import strains.RayStrain;

public class Client {

    public static void main(String[] args) throws Exception {

        // BufferedImage bf = ImageIO.read(new File("cid_596.jpg"));

        // EnvFrame frame = new EnvFrame();
        // frame.pack();
        // frame.setVisible(true);

        javax.swing.SwingUtilities.invokeLater(() -> doGUI());

    }

    private static void doGUI() {
        final EnvFrame frame = new EnvFrame();
        frame.pack();
        frame.setVisible(true);
    }

}

@SuppressWarnings("serial")
class EnvFrame extends JFrame {
    private Environment envr;

    private MyPanel myPanel;

    private javax.swing.Timer myTimer;
    private javax.swing.Timer randTimer;

    JButton startStopButton, singleStepButton, saveImageButton, saveNegButton, orgAddButton,
            orgPlaceButton, multOrgAddButton, exterminateStrainButton, startStopRandomButton;
    JButton newImageButton, setImageButton, showLivingData, showTombData, showStrainData,
            showAllStrainData;
    JLabel numOrgLabel, numStrainsLabel, numTombedLabel, numUpdatesLabel, visStrainLabel,
            allStrains, livingStrains, deadStrains;
    JPanel mainControlsPanel, organismControls, orgAddPanel;
    JRadioButton defStrainButton, dripStrainButton, floatStrainButton;

    HashMap<String, Strain> strToStrain = new HashMap<>();
    Set<JButton> orgButtons = new HashSet<>();
    Set<JButton> dataButtons = new HashSet<>();

    private static final int MAX_DELAY = 140;

    public EnvFrame() {
        setTitle("LangZeitVIRUS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Container contentPane = getContentPane();
        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new ImageFilter());
        setUpEnvironment();
        final Strain defStrain = new DefaultStrain();
        final Strain dripStrain = new DrippyStrain();
        final Strain floatStrain = new FloatyStrain();
        final Strain rayStrain = new RayStrain();
        final Strain[] strainOptions = { defStrain, dripStrain, floatStrain, rayStrain };

        for (final Strain s : strainOptions) {
            final String strName = s.getStrainName();
            strToStrain.put(strName, s);
        }

        myPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        // setSize(envr.width, envr.height);
        contentPane.add(myPanel, "Center");

        addTimer();
        addRandomTimer();

        mainControlsPanel = new JPanel();
        organismControls = new JPanel();
        orgAddPanel = new orgAddPanel();
        final JTabbedPane strainDataPane = new JTabbedPane();

        mainControlsPanel.setLayout(new GridLayout(0, 5, 2, 2));

        organismControls.setLayout(new BoxLayout(organismControls, BoxLayout.Y_AXIS));

        allStrains = new JLabel(envr.listStrainData());
        strainDataPane.addTab("All Strains", allStrains);
        strainDataPane.setMnemonicAt(0, KeyEvent.VK_1);

        livingStrains = new JLabel(envr.listActiveStrainData());
        strainDataPane.addTab("Living Strains", livingStrains);
        strainDataPane.setMnemonicAt(1, KeyEvent.VK_2);

        deadStrains = new JLabel(envr.listTombStrainData());
        strainDataPane.addTab("Tomb Strains", deadStrains);
        strainDataPane.setMnemonicAt(2, KeyEvent.VK_3);

        numOrgLabel = new JLabel("Organisms: " + envr.livingOrgs());
        numTombedLabel = new JLabel("Tombed: " + envr.tombedOrgs());
        numStrainsLabel = new JLabel("Strains: " + envr.strainNames.size());
        numUpdatesLabel = new JLabel("Updates: " + envr.updates);
        visStrainLabel = new JLabel(envr.listStrainData());

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

        setImageButton = new JButton("Set Image");
        setImageButton.addActionListener(e -> {
            fc.showOpenDialog(null);
            final File img = fc.getSelectedFile();
            setUpEnvironment(img);
            setSize(envr.width, envr.height);
            myPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            updateData();
            myPanel.repaint();
            contentPane.add(myPanel, "Center");
            contentPane.validate();
        });

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

        singleStepButton = new JButton("Step");
        singleStepButton.addActionListener(e -> {
            envr.update();
            updateData();
            myPanel.repaint();
        });

        saveImageButton = new JButton("Save Image");
        saveImageButton.addActionListener(e -> envr.saveImage());

        saveNegButton = new JButton("Save Negative");
        saveNegButton.addActionListener(e -> envr.saveNegative());

        orgAddButton = new JButton("Add Organism");
        orgAddButton.addActionListener(e -> {
            // String strChoice = (String) JOptionPane.showInputDialog(
            // new JFrame(), "Strain?", "Strain Choice",
            // JOptionPane.PLAIN_MESSAGE, null, strToStrain.keySet()
            // .toArray(), strToStrain.keySet().toArray()[0]);
                final String strChoice = ((src.orgAddPanel) orgAddPanel).getChosenStrain();
                final Strain str = strToStrain.get(strChoice);
                envr.add(str);
                updateData();
            });

        multOrgAddButton = new JButton("Add Organisms");
        multOrgAddButton.addActionListener(e -> {
            final int num = Integer.parseInt(JOptionPane.showInputDialog("Number of Organisms?"));
            // String strChoice = (String) JOptionPane.showInputDialog(
            // new JFrame(), "Strain?", "Strain Choice",
            // JOptionPane.PLAIN_MESSAGE, null, strToStrain.keySet()
            // .toArray(), strToStrain.keySet().toArray()[0]);
                final String strChoice = ((src.orgAddPanel) orgAddPanel).getChosenStrain();
                final Strain str = strToStrain.get(strChoice);
                envr.add(num, str);
                updateData();
            });

        orgPlaceButton = new JButton("Place Organism");
        orgPlaceButton.addActionListener(e -> {
            final String strChoice = ((src.orgAddPanel) orgAddPanel).getChosenStrain();
            final Strain str = strToStrain.get(strChoice);
            int placeX = envr.width / 2;
            int placeY = envr.height / 2;
            try {
                placeX = Integer.parseInt(JOptionPane.showInputDialog("X?" + " (0-" + envr.width
                        + ")"));
            } catch (final Exception e11) {}
            try {
                placeY = Integer.parseInt(JOptionPane.showInputDialog("Y?" + " (0-" + envr.height
                        + ")"));
            } catch (final Exception e12) {}
            envr.addOneAt(str, placeY, placeX);
            updateData();
        });

        exterminateStrainButton = new JButton("Exterminate Strain");
        exterminateStrainButton.addActionListener(arg0 -> {
            final Strain strChoice = (Strain) JOptionPane.showInputDialog(new JFrame(), "Strain?",
                    "Strain Choice", JOptionPane.PLAIN_MESSAGE, null, envr.activeStrains.keySet()
                            .toArray(), envr.activeStrains.keySet().toArray()[0]);
            final String strX = strChoice.getStrainName();
            envr.exterminate(strToStrain.get(strX));
            updateData();
        });

        showLivingData = new JButton("Living Data");
        showLivingData.addActionListener(e -> {
            final JTextArea strainText = new JTextArea(envr.listLiving(), 10, 50);
            final JScrollPane sText = new JScrollPane(strainText);
            sText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            JOptionPane.showMessageDialog(null, sText);
        });

        showTombData = new JButton("Tombed Data");
        showTombData.addActionListener(e -> {
            final JTextArea strainText = new JTextArea(envr.listTombed(), 10, 50);
            final JScrollPane sText = new JScrollPane(strainText);
            sText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            JOptionPane.showMessageDialog(null, sText);
        });

        showStrainData = new JButton("Strain Data");
        showStrainData.addActionListener(e -> {
            final JTextArea strainText = new JTextArea(envr.listStrains(), 10, 50);
            final JScrollPane sText = new JScrollPane(strainText);
            sText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            JOptionPane.showMessageDialog(null, sText);
        });

        showAllStrainData = new JButton("All Strain Data");
        showAllStrainData.addActionListener(e -> {
            final JFrame strainDataOptPane = new JFrame();
            strainDataOptPane.add(strainDataPane);
            strainDataOptPane.setSize(200, 800);
            strainDataOptPane.setVisible(true);
        });

        final JSlider speedBar = new JSlider(1, MAX_DELAY);
        speedBar.addChangeListener(ce -> {
            myTimer.setDelay(1000 / (speedBar.getValue()));
            randTimer.setDelay(1000 / (speedBar.getValue()));
        });

        numOrgLabel.setAlignmentX((float) 0.5);
        orgAddButton.setAlignmentX((float) 0.5);
        orgPlaceButton.setAlignmentX((float) 0.5);
        multOrgAddButton.setAlignmentX((float) 0.5);
        showStrainData.setAlignmentX((float) 0.5);
        showLivingData.setAlignmentX((float) 0.5);
        showTombData.setAlignmentX((float) 0.5);
        numTombedLabel.setAlignmentX((float) 0.5);
        numStrainsLabel.setAlignmentX((float) 0.5);
        numUpdatesLabel.setAlignmentX((float) 0.5);
        visStrainLabel.setAlignmentX((float) 0.5);
        showAllStrainData.setAlignmentX((float) 0.5);
        exterminateStrainButton.setAlignmentX((float) 0.5);

        mainControlsPanel.add(newImageButton);
        mainControlsPanel.add(startStopButton);
        mainControlsPanel.add(singleStepButton);
        mainControlsPanel.add(saveImageButton);
        mainControlsPanel.add(saveNegButton);
        mainControlsPanel.add(setImageButton);
        organismControls.add(orgAddPanel);
        organismControls.add(numOrgLabel);
        organismControls.add(numTombedLabel);
        organismControls.add(numStrainsLabel);
        organismControls.add(numUpdatesLabel);
        organismControls.add(orgAddButton);
        organismControls.add(orgPlaceButton);
        organismControls.add(multOrgAddButton);
        organismControls.add(exterminateStrainButton);
        organismControls.add(showStrainData);
        organismControls.add(showLivingData);
        organismControls.add(showTombData);
        organismControls.add(showAllStrainData);
        orgButtons.add(orgAddButton);
        orgButtons.add(orgPlaceButton);
        orgButtons.add(multOrgAddButton);
        orgButtons.add(exterminateStrainButton);
        dataButtons.add(showStrainData);
        dataButtons.add(showAllStrainData);
        dataButtons.add(showLivingData);
        dataButtons.add(showTombData);

        mainControlsPanel.add(new JLabel(" Slow"));
        mainControlsPanel.add(speedBar);
        mainControlsPanel.add(new JLabel("Fast "));

        mainControlsPanel.add(startStopRandomButton);

        contentPane.add(mainControlsPanel, "North");
        contentPane.add(organismControls, "East");

    }

    private void toggleRandomButtons() {
        if (randTimer.isRunning()) {
            for (final JButton but : orgButtons) {
                but.setEnabled(false);
            }
            for (final JButton but : dataButtons) {
                but.setEnabled(false);
            }
        } else {
            for (final JButton but : orgButtons) {
                but.setEnabled(true);
            }
            for (final JButton but : dataButtons) {
                but.setEnabled(true);
            }
        }
    }

    private void toggleRunningButtons() {
        if (randTimer.isRunning()) {
            return;
        }
        if (myTimer.isRunning()) {
            for (final JButton but : dataButtons) {
                but.setEnabled(false);
            }
        } else {
            for (final JButton but : dataButtons) {
                but.setEnabled(true);
            }
        }
    }

    private void setUpEnvironment() {
        envr = new Environment(800, 600, false);
        myPanel = new MyPanel(envr.image);
        myPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        final Dimension d = new Dimension(envr.width + 150, envr.height + 100);
        setPreferredSize(d);
        pack();
        myPanel.repaint();
    }

    private void setUpEnvironment(File img) {
        BufferedImage bmg = null;
        try {
            bmg = ImageIO.read(img);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        envr = new Environment(bmg);
        myPanel = new MyPanel(envr.image);
        final Dimension d = new Dimension(envr.width + 150, envr.height + 100);
        setPreferredSize(d);
        this.pack();

    }

    private void addTimer()
    // post: creates a timer that calls the model's update
    // method and repaints the display
    {
        final ActionListener updater = e -> {
            envr.update();
            updateData();
            myPanel.repaint();
        };
        myTimer = new javax.swing.Timer(MAX_DELAY / 2, updater);
    }

    private void addRandomTimer()
    // post: creates a timer that calls the model's update
    // method and repaints the display
    {
        final ActionListener updater = e -> {
            if (envr.livingOrgs() == 0) {
                final String strChoice = ((src.orgAddPanel) orgAddPanel).getChosenStrain();
                final Strain strain = strToStrain.get(strChoice);
                envr.add(strain);
            }
            envr.update();
            updateData();
            myPanel.repaint();
        };
        randTimer = new javax.swing.Timer(MAX_DELAY / 2, updater);
    }

    private void updateData() {
        numOrgLabel.setText("Organisms: " + envr.livingOrgs());
        numStrainsLabel.setText("Strains: " + envr.strains.keySet().size());
        numTombedLabel.setText("Tombed: " + envr.tombedOrgs());
        numUpdatesLabel.setText("Updates: " + envr.updates);
        // visStrainLabel.setText(envr.listStrainData());
        // allStrains.setText(envr.listStrainData());
        // livingStrains.setText(envr.listActiveStrainData());
        // deadStrains.setText(envr.listTombStrainData());
    }
}

@SuppressWarnings("serial")
class MyPanel extends JPanel {

    BufferedImage bf;

    public MyPanel(BufferedImage image) {
        bf = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bf, 0, 0, this);
    }
}

@SuppressWarnings("serial")
class orgAddPanel extends JPanel implements ActionListener {

    static String defStrainString = "AllgemeineVIRUS";
    static String dripStrainString = "FlossVIRUS";
    static String floatStrainString = "TrielFligVIRUS";
    static String rayStrainString = "StrahlVIRUS";

    static String defStrain = "AllgemeineVIRUS ";
    static String dripStrain = "FlossVIRUS ";
    static String floatStrain = "TrielFligVIRUS ";
    static String rayStrain = "StrahlVIRUS ";

    String chosenStrain;

    public orgAddPanel() {

        super(new BorderLayout());

        final JRadioButton defStrainButton = new JRadioButton(defStrainString);
        defStrainButton.setMnemonic(KeyEvent.VK_1);
        defStrainButton.setActionCommand(defStrain);
        defStrainButton.setSelected(true);

        final JRadioButton dripStrainButton = new JRadioButton(dripStrainString);
        dripStrainButton.setMnemonic(KeyEvent.VK_2);
        dripStrainButton.setActionCommand(dripStrain);

        final JRadioButton floatStrainButton = new JRadioButton(floatStrainString);
        floatStrainButton.setMnemonic(KeyEvent.VK_3);
        floatStrainButton.setActionCommand(floatStrain);

        final JRadioButton rayStrainButton = new JRadioButton(rayStrainString);
        rayStrainButton.setMnemonic(KeyEvent.VK_4);
        rayStrainButton.setActionCommand(rayStrain);

        final ButtonGroup group = new ButtonGroup();
        group.add(defStrainButton);
        group.add(dripStrainButton);
        group.add(floatStrainButton);
        group.add(rayStrainButton);

        defStrainButton.addActionListener(this);
        dripStrainButton.addActionListener(this);
        floatStrainButton.addActionListener(this);
        rayStrainButton.addActionListener(this);

        chosenStrain = defStrain;

        final JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(defStrainButton);
        radioPanel.add(dripStrainButton);
        radioPanel.add(floatStrainButton);
        radioPanel.add(rayStrainButton);

        add(radioPanel, BorderLayout.LINE_START);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        final Dimension d = new Dimension();
        d.setSize(5000, 100);
        this.setMaximumSize(d);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        chosenStrain = e.getActionCommand();
    }

    public String getChosenStrain() {
        return chosenStrain;
    }

}
