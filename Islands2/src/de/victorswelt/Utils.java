package de.victorswelt;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Utils {
	public static Font FONT_HEADER  = new Font(Font.SANS_SERIF, Font.BOLD, 24),
					   FONT_DEFAULT = new Font(Font.SANS_SERIF, Font.PLAIN, 14),
					   FONT_MONO    = new Font(Font.MONOSPACED, Font.PLAIN, 14);
	
	
	private Utils() {}
	
	
	public static boolean checkCollision(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		if(y1 > y2 + h2) return false;
		if(y2 > y1 + h1) return false;
		if(x1 > x2 + w2) return false;
		if(x2 > x1 + w1) return false;

		return true;
	}
	
	public static int decodeVarNum(DataInputStream dis) throws IOException, NumberFormatException {
		int ret = 0;
		int bytePosition = 0;
		
		while(true) {
			int in = dis.readByte();
			ret |= (in & 0x7F) << bytePosition * 7;
			bytePosition++;
			
			if(bytePosition>9)
				throw new NumberFormatException("VarNum too big");
			
			// the input does not have any more bytes following
			if((in & 0x80) == 0)
				break;
		}
		
		return ret;
	}
	
	public static void encodeVarInt(DataOutputStream dos, int number) throws IOException, NumberFormatException {
		if(number<0)
			throw new NumberFormatException("VarNum is negative");
		
		while(true) {
            if ((number & 0xFFFFFF80) == 0) {
                dos.writeByte((int) number);
                return;
            }
			
			// 
			dos.writeByte(((int) (number & 0x7F)) | 0x80);
			
			// shift the number to the right
			number >>>= 7;
		}
	}
	
	public static void createPacket(DataOutputStream dos, byte packetType, String content) throws NumberFormatException, IOException {
		dos.writeByte(packetType);
		Utils.encodeVarInt(dos, content.length());
		dos.writeBytes(content);
	}
}
