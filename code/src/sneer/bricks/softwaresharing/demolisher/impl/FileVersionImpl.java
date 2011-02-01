package sneer.bricks.softwaresharing.demolisher.impl;

import sneer.bricks.softwaresharing.FileVersion;
import sneer.foundation.lang.Functor;

class FileVersionImpl implements FileVersion {

	private final String _relativePath;
	private final byte[] _contents;
	private final long _lastModified;
	private final Status _status;
	private final Functor<String, byte[]> _currentContentsFinder;

	FileVersionImpl(String path, byte[] contents, Functor<String, byte[]> currentContentsFinder, long lastModified, boolean isCurrent) {
		_relativePath = path;
		_contents = contents;
		_currentContentsFinder = currentContentsFinder;
		_lastModified = lastModified;
		_status = isCurrent ? Status.CURRENT : Status.DIFFERENT;
	}

	@Override
	public byte[] contents() {
		return _contents;
	}

	@Override
	public byte[] contentsInCurrentVersion() {
		return _currentContentsFinder.evaluate(_relativePath);
	}

	@Override
	public String relativePath() {
		return _relativePath;
	}

	@Override
	public Status status() {
		return _status;
	}

	@Override
	public long lastModified() {
		return _lastModified;
	}

}
