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

import strains.DefaultStrain;
import strains.DrippyStrain;
import strains.FloatyStrain;
import strains.MultiTaskingStrain;
import strains.RayStrain;
import strains.SearchingStrain;

@SuppressWarnings("serial")
class OrgAddPanel extends JPanel implements ActionListener {

  String chosenStrain;

  public OrgAddPanel() {

    super(new BorderLayout());

    chosenStrain = SearchingStrain.NAME;

    final List<JRadioButton> buttons = Arrays.asList(
        makeStrainButton(DefaultStrain.NAME, KeyEvent.VK_1),
        makeStrainButton(DrippyStrain.NAME, KeyEvent.VK_2), //
        makeStrainButton(FloatyStrain.NAME, KeyEvent.VK_3), //
        makeStrainButton(RayStrain.NAME, KeyEvent.VK_4),
        makeStrainButton(MultiTaskingStrain.NAME, KeyEvent.VK_5),
        makeStrainButton(SearchingStrain.NAME, KeyEvent.VK_6));
    buttons.get(5).setSelected(true);

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