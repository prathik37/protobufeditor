package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.IOException;
import java.io.OutputStream;

import net.sf.JRecord.Details.AbstractLayoutDetails;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.IO.LineWriterWrapper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;

public class ProtoSdDelimitedWriter extends LineWriterWrapper {

	private ProtoDelimitedByteWriter writer;
	
	private boolean toInit = true;
	
	public ProtoSdDelimitedWriter() {
		this(new ProtoDelimitedByteWriter());
	}
	
	private ProtoSdDelimitedWriter(ProtoDelimitedByteWriter byteWriter) {
		super(byteWriter);
		
		writer = byteWriter;
	}
	
	@Override
	public void open(OutputStream outputStream) throws IOException {
		super.open(outputStream);
		
		toInit = true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setLayout(AbstractLayoutDetails layout) throws IOException {

		super.setLayout(layout);
		
		if (toInit && layout instanceof ProtoLayoutDef) {
			writer.write(
				ProtoHelper.getFileDescriptorSet(
					((ProtoLayoutDef) layout).getFileDesc()
				).toByteArray()
			);
			toInit = false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(AbstractLine line) throws IOException {
		
		setLayout(line.getLayout());

		super.write(line);
		toInit = false;
	}

	
}
