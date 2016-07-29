package client;

import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.common.collect.ImmutableSet;

import src.Environment;

public class OrganismControls extends JPanel {
  private static final long serialVersionUID = -3186503206426099003L;
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