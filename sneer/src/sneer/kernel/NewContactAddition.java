package sneer.kernel;

import java.util.Date;

import org.prevayler.Transaction;

import wheel.io.ui.User;

public class NewContactAddition implements Transaction {

	private final String _nick;

	public NewContactAddition(User user) {
		_nick = user.answer("New contact's nickname:");
	}

	public void executeOn(Object domain, Date ignored) {
		((Domain)domain).addContact(_nick);
	}

	private static final long serialVersionUID = 1L;
}
