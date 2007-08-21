package sneer.apps.metoo;


public interface MeTooPacket {
	
	public static final int APP_LIST_REQUEST = 0;
	public static final int APP_LIST_RESPONSE = 1;
	public static final int APP_INSTALL_REQUEST = 2;
	public static final int APP_INSTALL_RESPONSE = 3;
	public static final int APP_FILE_PART = 4;
	
	public int type();
 
}
