package net.sf.RecordEditor.zProtoBuf.zTest.BuildData;

import java.io.FileInputStream;
import java.io.IOException;

public class TestZZ {

	public static void main(String[] args) throws IOException {
		String filename = "/home/bm/Work/ProtoBuffers/SdMessage.protocomp";
		FileInputStream in = new FileInputStream(filename);
		byte[] bytes = new byte[in.available()];
		in.read(bytes);
		
		System.out.println(new String(bytes, "ISO-8859-1"));
	}
}
