package sneer.bricks.pulp.reactive.tests;

import static basis.environments.Environments.my;

import org.junit.Test;

import basis.lang.Closure;
import basis.lang.Predicate;
import basis.util.concurrent.Latch;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.SetRegister;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class SignalUtilsTest extends BrickTestBase {
	
	@Test (timeout = 2000)
	public void waitForExistingElementWithPredicate() {
		SetRegister<String> setRegister = my(CollectionSignals.class).newSetRegister();
		setRegister.add("foo");
		my(SignalUtils.class).waitForElement(setRegister.output(), new Predicate<String>() { @Override public boolean evaluate(String value) {
			return value.equals("foo");
		}});
		
	}

	@Test (timeout = 2000)
	public void waitForNewElementWithPredicate() {
		final Latch latch1 = new Latch();
		final Latch latch2 = new Latch();

		final SetRegister<String> setRegister = my(CollectionSignals.class).newSetRegister();
		setRegister.add("one");
		
		my(Threads.class).startDaemon("SignalUtils Test", new Closure() { @Override public void run() {
			my(SignalUtils.class).waitForElement(setRegister.output(), new Predicate<String>() { @Override public boolean evaluate(String value) {
				latch1.open();
				return value.equals("two");
			}});
			latch2.open();
		}});
		
		latch1.waitTillOpen();
		setRegister.add("two");
		latch2.waitTillOpen();
	}

}
