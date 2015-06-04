package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
class OrgAddPanel extends JPanel implements ActionListener {

  static String defaultStrainString = "Default VIRUS";
  static String dripStrainString = "Drippy VIRUS";
  static String floatStrainString = "Floaty VIRUS";
  static String rayStrainString = "Ray VIRUS";

  static String defaultStrain = "Default VIRUS ";
  static String dripStrain = "Drippy VIRUS ";
  static String floatStrain = "Floaty VIRUS ";
  static String rayStrain = "Ray VIRUS ";

  String chosenStrain;

  public OrgAddPanel() {

    super(new BorderLayout());

    final JRadioButton defStrainButton = makeStrainButton(defaultStrain, KeyEvent.VK_1);

    final JRadioButton dripStrainButton = makeStrainButton(dripStrain, KeyEvent.VK_2);

    final JRadioButton floatStrainButton = makeStrainButton(floatStrain, KeyEvent.VK_3);

    final JRadioButton rayStrainButton = makeStrainButton(rayStrain, KeyEvent.VK_4);

    defStrainButton.setSelected(true);

    final ButtonGroup group = new ButtonGroup();
    group.add(defStrainButton);
    group.add(dripStrainButton);
    group.add(floatStrainButton);
    group.add(rayStrainButton);

    defStrainButton.addActionListener(this);
    dripStrainButton.addActionListener(this);
    floatStrainButton.addActionListener(this);
    rayStrainButton.addActionListener(this);

    chosenStrain = defaultStrain;

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

  private static JRadioButton makeStrainButton(String name, int mnemonic) {
    final JRadioButton strainButton = new JRadioButton(name.substring(0, name.length() - 1));
    strainButton.setMnemonic(mnemonic);
    strainButton.setActionCommand(name);
    return strainButton;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    chosenStrain = e.getActionCommand();
  }

  public String getChosenStrain() {
    return chosenStrain;
  }

}