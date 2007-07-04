package sneer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Language {
	private static final String MSGSTR = "msgstr \"";

	private static final String MSGID = "msgid \"";

	private static final String MARK_START = "translate("; // important! change this when the method is refatored.

	private static final String MARK_END = ")";

	private static final String TRANSLATION_FILENAME = "Translation";

	private static final Language instance = new Language();

	public Hashtable<String, String> translationMap = new Hashtable<String, String>();

	private Language() {
			loadTranslationTemplate();
	}

	public static void load(String language, String country) {
		try {
			instance.loadTranslation(language,country);
		} catch (Exception ioe) {
			System.err.println("Could not find Translation file for " + language + "/" + country + " . Please generate it. Sneer still works normally without it.");
		}
	}

	public static String translate(String key) { // important! change the MARK_START constant when the method is refatored.
		String result = instance.translationMap.get(key);
		if ((result == null) || (result.isEmpty()))
			result = key;
		return result;
	}

	public static String translate(String key, Object... args) {
		return String.format(translate(key), args);
	}
	
	public static void reset(){
		instance.loadTranslationTemplate();
	}

	public void loadTranslationTemplate() {
		try {
			InputStream stream = this.getClass().getResourceAsStream("/" + TRANSLATION_FILENAME + ".pot");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			parseTranslation(reader);
		} catch (Exception anything) {
			System.err.println("Could not find Translation file. Please generate it. Sneer still works normally without it.");
		}
	}

	private void parseTranslation(BufferedReader reader) throws IOException {
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(MSGID)) {
				String msgid = line.substring(MSGID.length(), line.length() - 1);
				String nextLine = reader.readLine();
				String msgstr = nextLine.substring(MSGSTR.length(), nextLine.length() - 1);
				translationMap.put(msgid, msgstr);
			}
		}
	}

	private void loadTranslation(String language, String country) throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(TRANSLATION_FILENAME + "_" + language + "_" + country + ".po");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		parseTranslation(reader);
	}

	// Fix: the code below is responsible for translation file creation... *maybe* should be moved to an utility class

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Language");
		frame.setResizable(false);
		Container content = frame.getContentPane();
		content.setBackground(Color.white);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		JButton button1 = new JButton(" Create Template File (.pot)");
		button1.setAlignmentX(Component.CENTER_ALIGNMENT);
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTemplateFile();
			}
		});
		JButton button2 = new JButton(" Create New Language file (.po)");
		button2.setAlignmentX(Component.CENTER_ALIGNMENT);
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createLanguageFile();
			}
		});
		JButton button3 = new JButton(" Create Merged Language file (.po.merge)");
		button3.setAlignmentX(Component.CENTER_ALIGNMENT);
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				merge();
			}
		});

		content.add(button1);
		content.add(button2);
		content.add(button3);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private static void createLanguageFile() { //Refactor: low priority. lots of redundant code in this class... unify...
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Choose the Sources Directory");
		int returnVal = chooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dirFile = chooser.getSelectedFile();
			String language = JOptionPane.showInputDialog("What language? (Example: pt )");
			String country = JOptionPane.showInputDialog("What country? (Example: BR )");
			InputStream streamIn;
			OutputStream streamOut;
			try {
				streamIn = new FileInputStream(dirFile.getAbsolutePath() + File.separator + TRANSLATION_FILENAME + ".pot");
				streamOut = new FileOutputStream(dirFile.getAbsolutePath() + File.separator + TRANSLATION_FILENAME + "_" + language + "_" + country + ".po");
				BufferedReader reader = new BufferedReader(new InputStreamReader(streamIn));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(streamOut));
				String line = "";
				while ((line = reader.readLine()) != null) {
					writer.write(line + "\r\n");
				}
				writer.close();
				reader.close();
				JOptionPane.showMessageDialog(null, "Done!");
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Could not find Translation.pot file. Do you generated it?");
			} catch (IOException unexpected) {
				JOptionPane.showMessageDialog(null, "Unexpected IO problem?");
			}
		}
	}

	private static void createTemplateFile() { //Refactor: low priority. lots of redundant code in this class... unify...
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Choose the Sources Directory");
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dirFile = chooser.getSelectedFile();
			extractFromDirectory(dirFile);
			JOptionPane.showMessageDialog(null, "Done!");
		}
	}

	private static void extractFromDirectory(File dirFile) {
		File targetFile = new File(dirFile.getPath() + File.separator + "Translation.pot");
		generateTranslation(dirFile, targetFile);
	}

	private static void generateTranslation(File dirFile, File targetFile) {
		List<String> generatedMsgids = new Vector<String>();
		List<File> files = new Vector<File>();
		listJavaFiles(files, dirFile);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
			for (File currentFile : files) {
				if (!currentFile.getName().equals("Language.java")) {
					List<ExtractedString> extractedList = new Vector<ExtractedString>();
					extractStringsFromFile(dirFile, extractedList, currentFile);
					for (ExtractedString current : extractedList) {
						if (!generatedMsgids.contains(current.getExtracted())) {
							writer.write(appendedMsgdIds(extractedList, current.getExtracted()));
							writer.write(MSGID + current.getExtracted() + "\"\r\n");
							writer.write("msgstr \"\"\r\n\r\n");
							generatedMsgids.add(current.getExtracted());
						}
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unexpected IO problem!");
		}

	}

	private static String appendedMsgdIds(List<ExtractedString> extractedList, String extracted) {
		String result = "";
		for (ExtractedString current : extractedList) {
			if (current.getExtracted().equals(extracted)) {
				result += "#: " + current.getFilename() + ":" + current.getLineNumber() + "\r\n";
			}
		}
		return result;
	}

	private static void listJavaFiles(List<File> files, File fileDir) {
		File[] fileList = fileDir.listFiles();
		for (File currentFile : fileList) {
			if (currentFile.isDirectory()) {
				listJavaFiles(files, currentFile);
			} else {
				if (currentFile.getName().endsWith(".java"))
					files.add(currentFile);
			}
		}
	}

	private static void extractStringsFromFile(File rootDir, List<ExtractedString> extractedList, File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String source = asString(reader);
			int offset = source.indexOf(MARK_START);
			while (offset > -1) {
				int pointer = offset + MARK_START.length();
				if (!Character.isJavaIdentifierPart(source.charAt(offset - 1))) {// make
					// make sure it does not match another method. ie.: Somethingtranslate(
					StringBuffer extracted = new StringBuffer();
					boolean insideBlockString = false;
					while (pointer < source.length()) {
						if (source.substring(pointer).startsWith(MARK_END) && (!insideBlockString))
							break;
						char currentChar = source.charAt(pointer);
						if (currentChar == '\"')
							insideBlockString = !insideBlockString;
						else if (insideBlockString)
							extracted.append(currentChar);
						pointer++;
					}
					String name = file.getPath().substring(rootDir.getAbsolutePath().length() + 1) + File.separator + file.getName();
					ExtractedString extractedString = new ExtractedString(name, 0, extracted.toString()); //Implement: low priority. fill line number. 
					extractedList.add(extractedString);
				}
				offset = source.indexOf(MARK_START, pointer);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unexpected IO problem!");
		}
	}

	private static String asString(BufferedReader reader) throws IOException {
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			buffer.append(line + "\n");
		}
		return buffer.toString();
	}

	private static class ExtractedString {
		private String _filename;

		private int _lineNumber;

		private String _extracted;

		public ExtractedString(String filename, int lineNumber, String extracted) {
			_filename = filename;
			_lineNumber = lineNumber;
			_extracted = extracted;
		}

		public void setFilename(String filename) {
			_filename = filename;
		}

		public String getFilename() {
			return _filename;
		}

		public void setLineNumber(int lineNumber) {
			_lineNumber = lineNumber;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public void setExtracted(String extracted) {
			_extracted = extracted;
		}

		public String getExtracted() {
			return _extracted;
		}

	}

	private static void merge() { //Refactor: low priority. lots of redundant code in this class... unify...
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Choose the Sources Directory");
		int returnVal = chooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File dirFile = chooser.getSelectedFile();
			String language = JOptionPane.showInputDialog("What language? (Example: pt )");
			String country = JOptionPane.showInputDialog("What country? (Example: BR )");

			try {
				InputStream templateStreamIn = new FileInputStream(dirFile.getAbsolutePath() + File.separator + TRANSLATION_FILENAME + ".pot");
				InputStream languageStreamIn = new FileInputStream(dirFile.getAbsolutePath() + File.separator + TRANSLATION_FILENAME + "_" + language + "_" + country + ".po");
				OutputStream streamOut = new FileOutputStream(dirFile.getAbsolutePath() + File.separator + TRANSLATION_FILENAME + "_" + language + "_" + country + ".po.merge");

				List<String> msgids = new ArrayList<String>();
				List<String> lines = new ArrayList<String>();
				BufferedReader languageReader = new BufferedReader(new InputStreamReader(languageStreamIn));
				String line = "";
				while ((line = languageReader.readLine()) != null) {
					lines.add(line);
					if (line.startsWith(MSGID))
						msgids.add(line);
				}
				languageReader.close();

				BufferedReader templateReader = new BufferedReader(new InputStreamReader(templateStreamIn));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(streamOut));
				for (String l : lines)
					writer.write(l + "\r\n");
				line = "";
				while ((line = templateReader.readLine()) != null) {
					if (line.startsWith(MSGID)) {
						if (!msgids.contains(line)) {
							writer.write(line + "\r\n");
							writer.write(templateReader.readLine() + "\r\n\r\n");
						}
					}
				}
				writer.close();
				templateReader.close();
				JOptionPane.showMessageDialog(null, "Done!");
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Could not find Translation file. Do you generated it?");
			} catch (IOException unexpected) {
				JOptionPane.showMessageDialog(null, "Unexpected IO problem!");
			}
		}
	}

}
