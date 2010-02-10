package dfcsantos.tracks.sharing.playingtracks.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.tracks.sharing.playingtracks.client.PlayingTrackClient;
import dfcsantos.tracks.sharing.playingtracks.keeper.PlayingTrackKeeper;
import dfcsantos.tracks.sharing.playingtracks.server.PlayingTrackServer;
import dfcsantos.wusic.Wusic;

public class PlayingTrackTest extends BrickTest {

	@Bind private final Wusic _wusic = mock(Wusic.class);
	private final Register<Track> _playingTrack = my(Signals.class).newRegister(null);

	private Contact _localContact;
	private PlayingTrackKeeper _remoteKeeper;

	@Test
	public void playingTrackBroadcast() {
		checking(new Expectations() {{
			oneOf(_wusic).playingTrack(); will(returnValue(_playingTrack.output()));
		}});

		my(PlayingTrackServer.class);

		Environment remote = newTestEnvironment(my(TupleSpace.class), my(Clock.class));
		configureStorageFolder(remote);

		final Seal localSeal = my(Seals.class).ownSeal();
		Environments.runWith(remote, new Closure() { @Override public void run() {
			_localContact = my(Contacts.class).produceContact("local");
			my(Seals.class).put("local", localSeal);
			_remoteKeeper = my(PlayingTrackKeeper.class);
			my(PlayingTrackClient.class);
		}});

		testPlayingTrack("track1");
		testPlayingTrack("track2");
		testPlayingTrack("track2");
		testPlayingTrack("track3");
		testPlayingTrack("");
		testPlayingTrack("track4");

		testNullPlayingTrack();

		crash(remote);
	}

	private void testPlayingTrack(String trackName) {
		setPlayingTrack(trackName.isEmpty() ? "" : trackName + ".mp3");
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		assertEquals(trackName, playingTrackReceivedFromLocal());
	}

	private void testNullPlayingTrack() {
		my(Clock.class).advanceTime(1);
		_playingTrack.setter().consume(null);
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		assertEquals("", playingTrackReceivedFromLocal());
	}

	private String playingTrackReceivedFromLocal() {
		return _remoteKeeper.playingTrack(_localContact).currentValue();
	}

	private void setPlayingTrack(String trackName) {
		_playingTrack.setter().consume(my(Tracks.class).newTrack(new File(trackName)));
	}

	private void configureStorageFolder(Environment remote) {
		Environments.runWith(remote, new Closure() { @Override public void run() {
			my(FolderConfig.class).storageFolder().set(newTmpFile("remote"));
		}});
	}

	private void crash(Environment remote) {
		Environments.runWith(remote, new Closure() { @Override public void run() {
			my(Threads.class).crashAllThreads();
		}});
	}

}