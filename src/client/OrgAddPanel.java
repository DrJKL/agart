package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.Strain;
import strains.DefaultStrain;
import strains.DrippyStrain;
import strains.FloatyStrain;
import strains.MultiTaskingRayStrain;
import strains.MultiTaskingStrain;
import strains.OrthoStrain;
import strains.RayStrain;
import strains.SearchingStrain;

@SuppressWarnings("serial")
class OrgAddPanel extends JPanel implements ActionListener {

  public static final List<Strain> strains = Arrays.asList(new DefaultStrain(), new DrippyStrain(),
      new FloatyStrain(), new RayStrain(), new MultiTaskingStrain(), new SearchingStrain(),
      new MultiTaskingRayStrain(), new OrthoStrain());

  String chosenStrain;

  public OrgAddPanel() {

    super(new BorderLayout());

    chosenStrain = SearchingStrain.NAME;

    final List<JRadioButton> buttons = strains.stream().map(Strain::getStrainName)
        .map(OrgAddPanel::makeStrainButton).collect(Collectors.toList());
    buttons.get(5).setSelected(true);

    buttons.forEach(button -> button.addActionListener(this));

    final ButtonGroup group = new ButtonGroup();
    buttons.forEach(button -> group.add(button));

    final JPanel radioPanel = new JPanel(new GridLayout(0, 1));
    buttons.forEach(button -> radioPanel.add(button));

    add(radioPanel, BorderLayout.LINE_START);
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    final Dimension d = new Dimension();
    d.setSize(5000, 200);
    this.setMaximumSize(d);
  }

  private static JRadioButton makeStrainButton(String name) {
    final JRadioButton strainButton = new JRadioButton(name);
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