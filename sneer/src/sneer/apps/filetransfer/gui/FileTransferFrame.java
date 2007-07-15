package sneer.apps.filetransfer.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import sneer.apps.filetransfer.FilePart;
import wheel.io.ui.CancelledByUser;
import wheel.lang.Omnivore;
import wheel.reactive.Signal;

public class FileTransferFrame extends JFrame {
	
	private Hashtable<String,File> _directoryMap = new Hashtable<String,File>();

	public FileTransferFrame(Signal<String> otherGuysNick, final Signal<FilePart> fileInput) {

		//Fix: should ask if user wants do download the file

		initComponents();
		
		_otherGuysNick = otherGuysNick;
		_otherGuysNick.addReceiver(new Omnivore<String>() { @Override public void consume(String nick) {
			setTitle(nick);
		}});

		fileInput.addReceiver(filePartReceiver());

		setVisible(true);
	}

	private Omnivore<FilePart> filePartReceiver() {
		return new Omnivore<FilePart>() { @Override public void consume(FilePart filePart) {
			File directory = directoryFor(filePart); 
			if (directory == null) {
				System.out.println("Stop sending this file. I dont want it."); //Fix Stop sending.
				return;
			}
			
			System.out.println("Received filepart: " + filePart);
			updateProgressBar(filePart._offset,filePart._filesize);
			try {
				String filename = directory.getPath() + File.separator + filePart._filename;
				RandomAccessFile raf = new RandomAccessFile(filename,"rws");
				raf.seek(filePart._offset);
				raf.write(filePart._content,0,filePart._content.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace(); //Fix: Treat properly
			} catch (IOException e) {
				e.printStackTrace(); //Fix: Treat properly
			}
			updateProgressBar(filePart._offset+filePart._content.length,filePart._filesize);
		}};
	}
	
	private File directoryFor(FilePart filePart) {
		if (filePart._offset != 0) //Not the first one.
			return _directoryMap.get(filePart._filename);

		File directory;
		try {
			directory = chooseTargetDirectory("Saving " + filePart._filename);
		} catch (CancelledByUser e) {
			return null;
		}
		_directoryMap.put(filePart._filename, directory); //Fix What is two different contacts are sending files with the same name? use another key.
		return directory;
	}
	
	private File chooseTargetDirectory(String dialogTitle) throws CancelledByUser {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle(dialogTitle);
		
		while (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			throw new CancelledByUser();
		
		return fc.getSelectedFile();
	}
	
	public void updateProgressBar(long value, long total){
		int current = (int)((value*100)/total);
		progress.setValue(current);
	}

	private final Signal<String> _otherGuysNick;

	private final JProgressBar progress = new JProgressBar();

	private void initComponents() {
		setLayout(new BorderLayout());
		add(progress, BorderLayout.CENTER);
		setSize(100, 50);
		progress.setStringPainted(true);
		progress.setValue(0);
	}

	private static final long serialVersionUID = 1L;
}
