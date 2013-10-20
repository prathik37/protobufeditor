package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.JRecord.ByteIO.AbstractByteReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;

import com.google.protobuf.CodedInputStream;

public class ProtoDelimitedByteReader extends  AbstractByteReader {

	private InputStream in;
	private CodedInputStream coded;


	@Override
	public void open(InputStream inputStream) throws IOException {
		in = new BufferedInputStream(inputStream, Consts.IO_BUFFER_SIZE);
		coded = CodedInputStream.newInstance(in);
	}


	@Override
	public void close() throws IOException {
		in.close();
	}


	@Override
	public byte[] read() throws IOException {
		if (coded == null) {
			throw new IOException("Reader has not been opened !!!");
		}
		if (coded.isAtEnd()) {
			return null;
		}

		coded.resetSizeCounter();
		return coded.readBytes().toByteArray();
	}
}
