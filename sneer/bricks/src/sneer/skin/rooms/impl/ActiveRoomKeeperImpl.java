package sneer.skin.rooms.impl;

import sneer.skin.rooms.ActiveRoomKeeper;
import wheel.lang.Consumer;
import wheel.reactive.Register;
import wheel.reactive.Signal;
import wheel.reactive.impl.RegisterImpl;

class ActiveRoomKeeperImpl implements ActiveRoomKeeper {

	private final Register<String> _register = new RegisterImpl<String>("");

	@Override
	public Signal<String> room() {
		return _register.output();
	}

	@Override
	public Consumer<String> setter() {
		return _register.setter();
	}

}