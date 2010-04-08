package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.IO.AbstractLineWriter;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;

public class ProtoMessageWriter extends AbstractLineWriter {

	private OutputStream out;
	private int count = 0; 
	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void open(OutputStream outputStream) throws IOException {
		out = new BufferedOutputStream(outputStream, Consts.IO_BUFFER_SIZE);
		count = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(AbstractLine line) throws IOException {
		switch (count++) {
		case 0:
			((ProtoLine) line).getMsg().writeTo(out);
			break;
		case 1:
			JOptionPane.showMessageDialog(null, "Only the first Message (line) will be written (when using a ProtoBuffer Message writer)");
		}
	}

}
