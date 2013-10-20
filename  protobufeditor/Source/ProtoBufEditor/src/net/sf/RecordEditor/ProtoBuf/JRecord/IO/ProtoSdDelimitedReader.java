package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.IOException;
import java.io.InputStream;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.Details.AbstractLayoutDetails;
import net.sf.JRecord.IO.LineReaderWrapper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLineProvider;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.DescriptorValidationException;

public class ProtoSdDelimitedReader extends LineReaderWrapper
implements ProtoSelfDescribingReader {

	private ProtoDelimitedByteReader reader;
	private FileDescriptorSet fileDescriptorSet = null;

	public ProtoSdDelimitedReader() {
		this(new ProtoDelimitedByteReader());
	}


	private ProtoSdDelimitedReader(
			ProtoDelimitedByteReader byteReader) {
		super(new ProtoLineProvider(), byteReader);

		reader = byteReader;
	}



	@Override
	public void open(InputStream inputStream, AbstractLayoutDetails pLayout)
			throws IOException, RecordException {

        reader.open(inputStream);

		byte[] bytes = reader.read();

		super.setLayout(pLayout);
		if (pLayout == null) {
			FileDescriptorSet.Builder bld = FileDescriptorSet.newBuilder().mergeFrom(bytes);
			fileDescriptorSet = bld.build();

			try {
				super.setLayout(
						new ProtoLayoutDef(
								ProtoHelper.getFileDescriptor(fileDescriptorSet, 0),
								Constants.IO_PROTO_SD_DELIMITED)
				);
			} catch (DescriptorValidationException e) {
				throw new IOException("Error Building Layout: " + e.getMessage(), e);
			}
		}
	}


	public FileDescriptorSet getFileDescriptorSet() {
		return fileDescriptorSet;
	}
}
