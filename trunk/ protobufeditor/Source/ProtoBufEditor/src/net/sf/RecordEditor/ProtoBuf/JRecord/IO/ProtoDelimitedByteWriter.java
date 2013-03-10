package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.JRecord.ByteIO.AbstractByteWriter;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ConstClass;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;

import com.google.protobuf.CodedOutputStream;

public class ProtoDelimitedByteWriter extends AbstractByteWriter {

	private OutputStream out;
	private CodedOutputStream codedStream = null;
	
	@Override
	public void open(OutputStream outputStream) throws IOException {
		out = new BufferedOutputStream(outputStream, Consts.IO_BUFFER_SIZE);
		codedStream = CodedOutputStream.newInstance(outputStream);
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		if (bytes != ConstClass.EMPTY_BYTE_ARRAY) {
			codedStream.writeRawVarint32(bytes.length);
			codedStream.writeRawBytes(bytes);
			codedStream.flush();
		}
	}

}
