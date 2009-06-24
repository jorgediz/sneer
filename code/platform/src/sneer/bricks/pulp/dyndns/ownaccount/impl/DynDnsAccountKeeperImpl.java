package sneer.bricks.pulp.dyndns.ownaccount.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.dyndns.ownaccount.DynDnsAccount;
import sneer.bricks.pulp.dyndns.ownaccount.DynDnsAccountKeeper;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Consumer;

class DynDnsAccountKeeperImpl implements DynDnsAccountKeeper {

	private Register<DynDnsAccount> _ownAccount = my(Signals.class).newRegister(null);

	@Override
	public Signal<DynDnsAccount> ownAccount() {
		return _ownAccount.output();
	}

	@Override
	public Consumer<DynDnsAccount> accountSetter() {
		return _ownAccount.setter();
	}

}