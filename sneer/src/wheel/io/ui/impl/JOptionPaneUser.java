package wheel.io.ui.impl;

import javax.swing.JOptionPane;

import wheel.io.ui.User;

public class JOptionPaneUser implements User {
	
	public JOptionPaneUser(String title) {
		_title = title;
	}

	
	private final String _title;

	
	public boolean choose(String proposition, Object... options) {	int todo_revise_this_signature;
		int chosen = JOptionPane.showOptionDialog(null, proposition + "\n\n", _title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		return chosen == 0;
	}


	public void acknowledgeUnexpectedProblem(String description) {
		JOptionPane.showMessageDialog(null, description + "\n\n", _title + " - Unexpected Problem", JOptionPane.ERROR_MESSAGE);
	}

	
	public String answer(String prompt) {
		return answer(prompt, "");
	}

	
	public String answer(String prompt, String defaultAnswer) {
		return JOptionPane.showInputDialog(prompt + "\n\n", defaultAnswer);
	}

	
	public void acknowledgeNotification(String notification) {
		choose(notification, "OK");
	}
}
