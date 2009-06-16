package sneer.bricks.pulp.tuples.tests;

import static sneer.foundation.commons.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.Consumer;
import sneer.bricks.pulp.threads.mocks.ThreadsMock;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.brickness.testsupport.BrickTest;
import sneer.foundation.brickness.testsupport.Contribute;
import sneer.foundation.commons.lang.ByRef;

public class TupleSpaceResponsivenessTest extends BrickTest {

	@Contribute private final ThreadsMock _threads = new ThreadsMock();
	
	private final TupleSpace _subject = my(TupleSpace.class);
	
	@Test (timeout = 1000)
	public void test() {
		final ByRef<Boolean> wasPublished = ByRef.newInstance(false);
		Consumer<TestTuple> refToAvoidGc = new Consumer<TestTuple>() { @Override public void consume(TestTuple value) {
			wasPublished.value = true;
		}};
		_subject.addSubscription(TestTuple.class, refToAvoidGc);

		final TestTuple tuple = new TestTuple(42);
		_subject.publish(tuple);
		
		assertFalse(wasPublished.value);
		_threads.stepper(0).step();
		assertTrue(wasPublished.value);
	}
	
}

