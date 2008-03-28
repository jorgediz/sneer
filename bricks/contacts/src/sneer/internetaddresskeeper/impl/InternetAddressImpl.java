package sneer.internetaddresskeeper.impl;

import sneer.contacts.Contact;
import sneer.internetaddresskeeper.InternetAddress;

class InternetAddressImpl implements InternetAddress {

	private Contact _contact;
	
	private String _host;
	
	private int _port;
	
	public InternetAddressImpl(Contact contact, String host, int port) {
		_contact = contact;
		_host = host;
		_port = port;
	}

	@Override
	public Contact contact() {
		return _contact;
	}

	@Override
	public String host() {
		return _host;
	}

	@Override
	public int port() {
		return _port;
	}
}
