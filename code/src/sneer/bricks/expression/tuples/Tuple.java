package sneer.bricks.expression.tuples;


import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.foundation.lang.Immutable;

public abstract class Tuple extends Immutable {

	protected Tuple() {
		this(null);
	}
	
	
	protected Tuple(Seal addressee_) {
		addressee = addressee_;
	}

	
	public final Seal publisher = my(OwnSeal.class).get().currentValue();
	public final long publicationTime = my(Clock.class).time().currentValue();
	
	public final Seal addressee;
	
}
