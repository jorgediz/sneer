package dfcsantos.tracks.folder.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.FolderConfig;
import dfcsantos.tracks.folder.TracksFolderKeeper;

class TracksFolderKeeperImpl implements TracksFolderKeeper {

	private final Register<File> _ownTracksFolder = my(Signals.class).newRegister(defaultOwnTracksFolder());
	private final Register<File> _peerTracksFolder = my(Signals.class).newRegister(defaultPeerTracksFolder());

	private File defaultOwnTracksFolder() {
		File result = new File(my(FolderConfig.class).storageFolder().get() ,"media/own_tracks");
		result.mkdirs();
		return result;
	}

	@Override
	public Signal<File> ownTracksFolder() {
		return _ownTracksFolder.output();
	}

	@Override
	public void setOwnTracksFolder(File ownTracksFolder) {
		_ownTracksFolder.setter().consume(ownTracksFolder);
	}

	private File defaultPeerTracksFolder() {
		File result = new File(my(FolderConfig.class).storageFolder().get() ,"media/peer_tracks");
		result.mkdirs();
		return result;
	}

	@Override
	public Signal<File> peerTracksFolder() {
		return _peerTracksFolder.output();
	}

	@Override
	public void setPeerTracksFolder(File ownTracksFolder) {
		_peerTracksFolder.setter().consume(ownTracksFolder);
	}

}
