package sneer.kernel.gui.contacts;

import static wheel.i18n.Language.translate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import wheel.graphics.JpgImage;
import wheel.io.ui.Action;
import wheel.lang.Omnivore;
import wheel.lang.Threads;
import wheel.reactive.Signal;

public class ReactiveJpgImageField extends JPanel implements Omnivore<JpgImage>{
	
	final static String IMAGE_PATH = "/sneer/kernel/gui/contacts/images/";
	final static ImageIcon NO_IMAGE = new ImageIcon(LateralRootInfo.class.getResource(IMAGE_PATH + "questionmark.jpg"));
	
	private JLabel _label = new JLabel();
	private final Signal<JpgImage> _source;
	private final boolean _editable;
	private final Omnivore<JpgImage> _setter;
	private final String _description;

	public ReactiveJpgImageField(String description, Signal<JpgImage> source, Omnivore<JpgImage> setter) {
		_description = description;
		_source = source;
		_setter = setter;
		_editable = (setter != null); //if setter == null, different textfield behaviour
		setLayout(new BorderLayout());
		JpgImage picture = _source.currentValue();
		ImageIcon pictureIcon = NO_IMAGE;
		if (picture != null)
			pictureIcon = new ImageIcon(picture.contents());
		_label.setIcon(pictureIcon);
		setBorder(new LineBorder(Color.black));
		
		if (_editable) 
			addChangeListeners();
		add(_label);
		_source.addReceiver(this);
	}
	
	private void addChangeListeners() {
		_label.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				final boolean rightClick = mouseEvent.getButton() == MouseEvent.BUTTON3;
				if (!rightClick) return;
				JPopupMenu menu = new JPopupMenu();
				addToContactMenu(menu,changePictureAction());
				menu.show(_label, mouseEvent.getX(), mouseEvent.getY());
			}
		});
	}
	
	private void addToContactMenu(JPopupMenu menu, final Action action) {
		final JMenuItem item = new JMenuItem(action.caption());
		item.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent ignored) {
			Threads.startDaemon(new Runnable() { @Override public void run() {
				action.run();
			}});
		}});
		menu.add(item);
	}

	private Action changePictureAction() {
		return new Action(){

			public String caption() {
				return translate("Change %1$s",_description);
			}

			public void run() {
				commitChange();
			}

		};
	}

	private void commitChange() {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				try{
					final JFileChooser fc = new JFileChooser(); //Refactor: this should be moved to _user and apps should use the same system to choose a file
					fc.setDialogTitle(translate("Choose a Picture"));
					fc.setApproveButtonText(translate("Use"));
					int value = fc.showOpenDialog(null);
					if (value != JFileChooser.APPROVE_OPTION) return;
					File file = fc.getSelectedFile();
					setPicture(new FileInputStream(file));
				}catch(Exception ignored){
					
				}
				_label.revalidate();
			}
		});
	}
	
	private void setPicture(InputStream input) {
		try{
			_setter.consume(new JpgImage(input));
		}catch(Exception ignored){
		}
	}
	
	public void consume(final JpgImage image) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				ImageIcon pictureIcon = NO_IMAGE;
				if (image != null)
					pictureIcon = new ImageIcon(image.contents());
				_label.setIcon(pictureIcon);
				_label.revalidate();
			}
		});
	}
	
	private static final long serialVersionUID = 1L;

}
