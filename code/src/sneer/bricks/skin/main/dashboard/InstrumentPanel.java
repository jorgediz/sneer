package sneer.bricks.skin.main.dashboard;

import java.awt.Container;

import javax.swing.JComponent;

import sneer.bricks.skin.menu.MenuGroup;

public interface InstrumentPanel {

	Container contentPane();
	MenuGroup<? extends JComponent> actions();
}