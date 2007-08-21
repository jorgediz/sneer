package sneer.apps.metoo;
import static wheel.i18n.Language.translate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import sneer.SneerDirectories;
import sneer.apps.metoo.gui.MeTooFrame;
import sneer.apps.metoo.packet.AppFilePart;
import sneer.apps.metoo.packet.AppInstallRequest;
import sneer.apps.metoo.packet.AppListResponse;
import sneer.kernel.appmanager.AppManager;
import sneer.kernel.appmanager.AppTools;
import sneer.kernel.appmanager.SovereignApplication;
import sneer.kernel.appmanager.SovereignApplicationUID;
import sneer.kernel.business.contacts.ContactId;
import sneer.kernel.communication.Channel;
import sneer.kernel.communication.Packet;
import sneer.kernel.gui.contacts.ContactAction;
import sneer.kernel.pointofview.Contact;
import wheel.io.ui.Action;
import wheel.lang.Omnivore;
import wheel.reactive.impl.SourceImpl;
import wheel.reactive.lists.ListSignal;

//FixUrgent: File transfer is VERY insecure
public class MeToo implements SovereignApplication{
	
	private static final int FILEPART_CHUNK_SIZE = 5000;
	
	private final Channel _channel;
	private final ListSignal<SovereignApplicationUID> _publishedApps;
	
	private final File _tempDirectory = AppTools.createTempDirectory("metoo");

	private final AppManager _appManager;

	public MeToo(Channel channel, ListSignal<SovereignApplicationUID> publishedApps, AppManager appManager){
		_channel = channel;
		_publishedApps = publishedApps;
		_appManager = appManager;
		_channel.input().addReceiver(meTooPacketReceiver());
	}

	private Omnivore<Packet> meTooPacketReceiver() {
		return new Omnivore<Packet>(){
			public void consume(Packet packet) {
				MeTooPacket meTooPacket = (MeTooPacket)packet._contents;
				switch(meTooPacket.type()){
					case MeTooPacket.APP_LIST_REQUEST:
						sendAppListResponse(packet._contactId);
						break;
					case MeTooPacket.APP_INSTALL_REQUEST:
						sendAppFile(packet._contactId,(AppInstallRequest)meTooPacket);
						break;
				}
				SourceImpl<MeTooPacket> input = _inputsByContactId.get(packet._contactId);
				if (input == null) return;
				input.setter().consume(meTooPacket);
			}

		};
	}
	
	protected void sendAppFile(ContactId contactId, AppInstallRequest request) {
		File installDirectory = new File(SneerDirectories.appsDirectory(),request._installName);
		if (installDirectory.exists())
			if (installDirectory.listFiles().length>0){
				File zipFile = installDirectory.listFiles()[0]; 
				sendFile(contactId,zipFile);
			}else{
				System.out.println("file does not exist: "+installDirectory.getAbsolutePath());
			}
			else
				System.out.println("Directory does not exist: "+installDirectory.getAbsolutePath());
	}
	
	private void sendFile(final ContactId contactId, File file) {
		try {
			tryToSendFile(contactId, file);
		} catch(IOException ioe) {
			ioe.printStackTrace(); //Fix: Treat properly.
		}
	}

	private void tryToSendFile(final ContactId contactId, File file) throws IOException {
		String fileName = file.getName();
		long fileLength = file.length();
		byte[] buffer = new byte[FILEPART_CHUNK_SIZE];
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		int read;
		long offset = 0;
		while ((read = in.read(buffer)) != -1) {
			byte[] contents = new byte[read];
			System.arraycopy(buffer,0,contents,0,read);
			final AppFilePart filePart = new AppFilePart(fileName, fileLength, contents, offset);
			sendPart(contactId, filePart);
			offset += read;
		}
	}

	private void sendPart(ContactId contactId, AppFilePart filePart) {
		System.out.println("sending:"+filePart);
		_channel.output().consume(new Packet(contactId, filePart));
	}

	protected void sendAppListResponse(ContactId contactId) {
		Map<String,String> installNameAndAppUID = new Hashtable<String,String>();
		for(SovereignApplicationUID app:_publishedApps)
			installNameAndAppUID.put(app._installName, app._appUID);
		_channel.output().consume(new Packet(contactId,new AppListResponse(installNameAndAppUID)));
	}

	public List<ContactAction> contactActions() {
		return Collections.singletonList( (ContactAction)new ContactAction(){
			public void actUpon(Contact contact) {
				openMeTooFrame(contact);
			}
			public String caption() {
				return translate("Me Too");
			}
		});
	}
	
	private final Map<ContactId, MeTooFrame> _framesByContactId = new HashMap<ContactId, MeTooFrame>();
	private final Map<ContactId, SourceImpl<MeTooPacket>> _inputsByContactId = new HashMap<ContactId, SourceImpl<MeTooPacket>>();

	protected void openMeTooFrame(Contact contact) {
		if (_framesByContactId.get(contact.id()) == null){
			SourceImpl<MeTooPacket> input = new SourceImpl<MeTooPacket>(null);
			_inputsByContactId.put(contact.id(), input);
			_framesByContactId.put(contact.id(), new MeTooFrame(_channel, contact, input.output(), _tempDirectory, _appManager));
		} else {
			_framesByContactId.get(contact.id()).sendAppListRequest();
		}
		_framesByContactId.get(contact.id()).setVisible(true);
	}
	
	public String defaultName() {
		return "meetoo";
	}

	public List<Action> mainActions() {
		return null;
	}

	public int trafficPriority() {
		return 1;
	}

}
