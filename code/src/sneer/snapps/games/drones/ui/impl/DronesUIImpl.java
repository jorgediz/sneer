package sneer.snapps.games.drones.ui.impl;

import static basis.environments.Environments.my;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.snapps.games.drones.Attributable;
import sneer.snapps.games.drones.UnitAttribute;
import sneer.snapps.games.drones.map.GameMap;
import sneer.snapps.games.drones.ui.DronesUI;

class DronesUIImpl implements DronesUI {
	
	private JFrame jFrame;
	@SuppressWarnings("unused")	private WeakContract refToAvoidGC;

	{
		my(ContactActionManager.class).addContactAction(new ContactAction(){

			@Override
			public String caption() {
				return "Game of Drones";
			}

			@Override
			public void run() {
				open();
			}

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public int positionInMenu() {
				return 0;
			}});
	}

	private void open() {
		initFrame();
		initTimer();
	}

	private void initTimer() {
		refToAvoidGC = my(Timer.class).wakeUpNowAndEvery(100, new Runnable() { @Override public void run() {
			jFrame.repaint();
		}});
	}

	private void initFrame() {
		jFrame = new JFrame("Game of Drones") {
			@Override
			public void paint(Graphics g) {
				my(GameMap.class).step();
							
				g.clearRect(0,0, jFrame.getWidth(), jFrame.getHeight());
				g.drawRect(my(GameMap.class).unit1().x(), 200, 100, 100);
				g.drawRect(my(GameMap.class).unit2().x(), 200, 100, 100);
			}		
			
		};
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setResizable(true);
		jFrame.setBounds(0, 0,800,600);
	}

	private void defineAttributes(Attributable thing) {
		for (UnitAttribute attribute : thing.attributes())
			define(attribute, thing);
	}

	private void define(UnitAttribute attribute, Attributable thing) {
		try {
			tryToDefine(attribute, thing);
		} catch (NumberFormatException e) {
			showMessageDialog(null, "Error. Try Again");
		}
	}

	private void tryToDefine(UnitAttribute attribute, Attributable thing) throws NumberFormatException {
		String value = showInputDialog("Value for " + thing + " " + attribute + ":"); //Value for player2 strength:
		thing.define(attribute, Integer.valueOf(value));
	}

}
