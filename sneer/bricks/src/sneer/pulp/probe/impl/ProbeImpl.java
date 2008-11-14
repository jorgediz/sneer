/**
 * 
 */
package sneer.pulp.probe.impl;

import sneer.kernel.container.Inject;
import sneer.pulp.contacts.Contact;
import sneer.pulp.keymanager.KeyManager;
import sneer.pulp.keymanager.PublicKey;
import sneer.pulp.tuples.Tuple;
import sneer.pulp.tuples.TupleSpace;
import wheel.lang.Omnivore;
import wheel.reactive.Signal;
import wheel.reactive.impl.Receiver;

final class ProbeImpl implements Omnivore<Tuple> {

	@Inject static private TupleSpace _tuples;
	@Inject static private KeyManager _keyManager;

	
	private final Contact _contact;
	private PublicKey _contactsPK;
	
	private final Object _isOnlineMonitor = new Object();
	private boolean _isOnline = false;
	final SchedulerImpl _scheduler = new SchedulerImpl();
	
	@SuppressWarnings("unused")
	private final Receiver<Boolean> _receiverToAvoidGC;

	
	ProbeImpl(Contact contact, Signal<Boolean> isOnline) {
		_contact = contact;
		_receiverToAvoidGC = createIsOnlineReceiver(isOnline);
	}


	private Receiver<Boolean> createIsOnlineReceiver(Signal<Boolean> isOnlineSignal) {
		return new Receiver<Boolean>(isOnlineSignal){ @Override public void consume(Boolean isOnline) {
			dealWithIsOnline(isOnline);
		}};
	}

	private void dealWithIsOnline(Boolean isOnline) {
		synchronized (_isOnlineMonitor) {
			boolean wasOnline = _isOnline;
			_isOnline = isOnline;

			if (isOnline) {
				_tuples.addSubscription(Tuple.class, this);
				sendKeptTuples();
			} else if (wasOnline) {
				_tuples.removeSubscription(this);
				_scheduler.drain();
			}
		}
	}


	private void sendKeptTuples() {
		for (Tuple kept : _tuples.keptTuples())
			consume(kept);
	}


	@Override
	public void consume(Tuple tuple) {
		if (!isClearToSend(tuple)) return;
		
		synchronized (_isOnlineMonitor) {
			if (!_isOnline)	return;
			_scheduler.add(tuple);
		}
	}

	private boolean isClearToSend(Tuple tuple) {
		initContactsPKIfNecessary();
		if (_contactsPK == null)
			return false;
		
		return !isEcho(tuple);
	}

	private boolean isEcho(Tuple tuple) {
		return _contactsPK.equals(tuple.publisher());
	}

	private void initContactsPKIfNecessary() {
		if (_contactsPK != null) return;
		_contactsPK = _keyManager.keyGiven(_contact);
	}

	
}

