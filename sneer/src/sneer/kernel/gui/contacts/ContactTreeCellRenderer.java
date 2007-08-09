package sneer.kernel.gui.contacts;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import sneer.kernel.pointofview.Party;

public class ContactTreeCellRenderer extends DefaultTreeCellRenderer{

	final static String IMAGE_PATH = "/sneer/kernel/gui/contacts/images/";

	final static ImageIcon ONLINE_ICON = new ImageIcon(ContactTreeCellRenderer.class.getResource(IMAGE_PATH + "operator.gif"));

	final static ImageIcon OFFLINE_ICON = new ImageIcon(ContactTreeCellRenderer.class.getResource(IMAGE_PATH + "operator_disabled.gif"));

	final static ImageIcon ERROR_ICON = new ImageIcon(ContactTreeCellRenderer.class.getResource(IMAGE_PATH + "redled.gif"));

	final static ImageIcon UNCONFIRMED_ICON = new ImageIcon(ContactTreeCellRenderer.class.getResource(IMAGE_PATH + "yellowled.gif"));

	final static ImageIcon CONFIRMED_ICON = new ImageIcon(ContactTreeCellRenderer.class.getResource(IMAGE_PATH + "greenled.gif"));
	
	final static Color selected = new Color(230,240,255);

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object treeNode, boolean isSelected, boolean expanded, boolean leaf, int row, boolean focus) {
		
		//if (treeNode == ContactTreeNode.NO_CONTACTS)
			//return super.getTreeCellRendererComponent(tree,treeNode,isSelected, expanded, leaf, row, focus);
			
		Party party = treeNode instanceof FriendNode 
			? ((FriendNode) treeNode).contact().party()
			: ((MeNode) treeNode).party();
		
		String nick = treeNode instanceof FriendNode
			? ((FriendNode) treeNode).contact().nick().currentValue() + " - "
			: "";
			
		ImageIcon stateIcon = stateIconFor(party);

		ImageIcon onlineIcon = party.isOnline().currentValue()
			? ONLINE_ICON
			: OFFLINE_ICON;

		FlowLayout layout = new FlowLayout();
		JPanel panel = new JPanel(layout);
		layout.setAlignment(FlowLayout.LEFT);
		panel.setBackground(isSelected ? selected : Color.white);
		//panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

		panel.add(new JLabel(onlineIcon));
		panel.add(new JLabel(stateIcon));
		String name = party.name().currentValue();
		panel.add(new JLabel(nick + name));

		return panel;
	}
	
	private ImageIcon stateIconFor(Party party) {
		return party.publicKeyConfirmed().currentValue()
			? CONFIRMED_ICON
			: UNCONFIRMED_ICON;
	}

	private static final long serialVersionUID = 1L;

}
