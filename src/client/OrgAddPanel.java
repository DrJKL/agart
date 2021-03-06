package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import src.Strain;
import strains.DefaultStrain;
import strains.DirectionalStrain;
import strains.MultiTaskingRayStrain;
import strains.MultiTaskingStrain;
import strains.OrthoStrain;
import strains.RayStrain;
import strains.SearchingStrain;
import strains.TurningStrain;

@SuppressWarnings("serial")
class OrgAddPanel extends JPanel implements ActionListener {

  public static final List<Strain> strains = Arrays.asList(new DefaultStrain(),
      DirectionalStrain.drippy(), DirectionalStrain.floaty(), new RayStrain(),
      new MultiTaskingStrain(), new SearchingStrain(), new MultiTaskingRayStrain(),
      new OrthoStrain(), new TurningStrain());

  private static final Map<String, Strain> strToStrain = strains.stream().collect(
      Collectors.toMap(Strain::getStrainName, Function.identity()));

  String chosenStrain;

  public OrgAddPanel() {

    super(new BorderLayout());

    final List<JRadioButton> buttons = strains.stream().map(Strain::getStrainName)
        .map(OrgAddPanel::makeStrainButton).collect(Collectors.toList());
    JRadioButton initialSelection = buttons.get(6);
    initialSelection.setSelected(true);
    chosenStrain = initialSelection.getActionCommand();

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

  public Strain getChosenStrain() {
    return getChosenStrain(chosenStrain);
  }

  public Strain getChosenStrain(String strain) {
    return strToStrain.get(strain);
  }

}