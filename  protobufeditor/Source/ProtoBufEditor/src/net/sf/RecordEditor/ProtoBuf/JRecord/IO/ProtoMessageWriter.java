package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.IO.AbstractLineWriter;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;

public class ProtoMessageWriter extends AbstractLineWriter {

	OutputStream out;
	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void open(OutputStream outputStream) throws IOException {
		out = new BufferedOutputStream(outputStream, Consts.IO_BUFFER_SIZE);

	}

	@Override
	public void write(AbstractLine line) throws IOException {
		((ProtoLine) line).getMsg().writeTo(out);
	}

}
