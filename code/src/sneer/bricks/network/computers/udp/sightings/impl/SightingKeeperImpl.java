package sneer.bricks.network.computers.udp.sightings.impl;

import static basis.environments.Environments.my;

import java.net.InetSocketAddress;

import sneer.bricks.network.computers.udp.sightings.SightingKeeper;
import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import basis.lang.CacheMap;
import basis.lang.Producer;

class SightingKeeperImpl implements SightingKeeper {
	
	private final CacheMap<Contact, SetRegister<InetSocketAddress>> addresses = CacheMap.newInstance();
	private final static Producer<SetRegister<InetSocketAddress>> newSetRegister = new Producer<SetRegister<InetSocketAddress>>() {  @Override public SetRegister<InetSocketAddress> produce() {
		return my(CollectionSignals.class).newSetRegister();
	}};

	@Override
	public void keep(Contact contact, InetSocketAddress sighting) {
		getAddresses(contact).add(sighting);
	}

	@Override
	public SetSignal<InetSocketAddress> sightingsOf(Contact contact) {
		return getAddresses(contact).output();
	}

	private SetRegister<InetSocketAddress> getAddresses(Contact contact) {
		return addresses.get(contact, newSetRegister);
	}
	
}
