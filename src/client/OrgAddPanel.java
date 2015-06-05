package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
class OrgAddPanel extends JPanel implements ActionListener {

  static String defaultStrain = "Default VIRUS";
  static String dripStrain = "Drippy VIRUS";
  static String floatStrain = "Floaty VIRUS";
  static String rayStrain = "Ray VIRUS";

  String chosenStrain;

  public OrgAddPanel() {

    super(new BorderLayout());

    chosenStrain = defaultStrain;

    final List<JRadioButton> buttons = Arrays.asList(
        makeStrainButton(defaultStrain, KeyEvent.VK_1),
        makeStrainButton(dripStrain, KeyEvent.VK_2), //
        makeStrainButton(floatStrain, KeyEvent.VK_3), //
        makeStrainButton(rayStrain, KeyEvent.VK_4));
    buttons.get(0).setSelected(true);

    buttons.forEach(button -> button.addActionListener(this));

    final ButtonGroup group = new ButtonGroup();
    buttons.forEach(button -> group.add(button));

    final JPanel radioPanel = new JPanel(new GridLayout(0, 1));
    buttons.forEach(button -> radioPanel.add(button));

    add(radioPanel, BorderLayout.LINE_START);
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    final Dimension d = new Dimension();
    d.setSize(5000, 100);
    this.setMaximumSize(d);
  }

  private static JRadioButton makeStrainButton(String name, int mnemonic) {
    final JRadioButton strainButton = new JRadioButton(name);
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