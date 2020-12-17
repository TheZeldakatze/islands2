package de.victorswelt;

import java.io.DataInputStream;
import java.io.IOException;

public class NetworkPacketReader {
	public byte type;
	public int length;
	public String content;
	
	public void read(DataInputStream data_in) throws IOException {
		type = data_in.readByte();
		length = Utils.decodeVarNum(data_in);
		byte buffer[] = new byte[length];
		data_in.readFully(buffer);
		content = new String(buffer);
	}
}
