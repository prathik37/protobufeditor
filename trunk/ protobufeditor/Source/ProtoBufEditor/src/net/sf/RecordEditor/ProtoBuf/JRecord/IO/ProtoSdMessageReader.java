package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.ProtoBuf.common.Utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet.Builder;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.UnknownFieldSet.Field;

public class ProtoSdMessageReader extends AbstractLineReader<ProtoLayoutDef>
implements ProtoSelfDescribingReader {

	private static final FileDescriptor fileDesc = Utils.getFileDescriptor();
	private static final int MIN_BUFFER_SIZE = 256 * 256 * 8;
	private static final int MAX_BUFFER_SIZE = MIN_BUFFER_SIZE * 16;


	private ProtoLine data;
	private FileDescriptorSet fileset = null;


	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#open(java.io.InputStream, net.sf.JRecord.Details.AbstractLayoutDetails)
	 */
	@Override
	public void open(InputStream inputStream, ProtoLayoutDef layout)
			throws IOException, RecordException {
		int bufSize = Math.min(MAX_BUFFER_SIZE, Math.max(inputStream.available(), MIN_BUFFER_SIZE));
		InputStream in = new BufferedInputStream(inputStream, bufSize);

		fileset = null;

		if (in.available() <= 0) {
			data = null;
		} else if (layout == null) {
			//byte[] bytes = new byte[in.available() + 1];
			try {
				in.mark(in.available());

				Descriptor desc = fileDesc.getMessageTypes().get(0);
				DynamicMessage.Builder builder = DynamicMessage.newBuilder(desc);
				builder.mergeFrom(in);

				in.reset();

				Field descField = null;
				int minCode = Integer.MAX_VALUE;
				for (Integer code :builder.getUnknownFields().asMap().keySet()) {
					if (minCode > code.intValue()) {
						minCode = code.intValue();
						descField = builder.getUnknownFields().getField(code);
					}
				}

				if (descField == null) {
					throw new IOException("Error: No File Descriptor field");
				} else {
					List<ByteString> list = descField.getLengthDelimitedList();
					if (list.size() != 1) {
						throw new IOException("Error: No File Descriptor field - to many objects");
					} else {
						ByteString byteStr = list.get(0);

						FileDescriptorSet.Builder fsBuilder;

						fsBuilder = FileDescriptorSet.newBuilder().mergeFrom(byteStr);

						fileset = fsBuilder.build();

						layout = new ProtoLayoutDef(
								ProtoHelper.getFileDescriptor(fileset, 0),
								Constants.IO_PROTO_SD_SINGLE_MESSAGE);
					}
				}
			} catch (IOException ioe) {
				throw ioe;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException("Error getting file descriptor",e);
			}

//			data = new ProtoLine(layout, layout.getPrimaryMsgBuilder().mergeFrom(in, layout.getRegistry()));
		}
		data = new ProtoLine(layout, layout.getPrimaryMsgBuilder().mergeFrom(in, layout.getRegistry()));

		super.setLayout(layout);
		in.close();
	}

	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#read()
	 */
	@Override
	public ProtoLine read() throws IOException {

		ProtoLine ret =  data;
		data = null;
		return ret;
	}

	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#close()
	 */
	@Override
	public void close() throws IOException {

	}


	@Override
	public FileDescriptorSet getFileDescriptorSet() {

		return fileset;
	}


}
