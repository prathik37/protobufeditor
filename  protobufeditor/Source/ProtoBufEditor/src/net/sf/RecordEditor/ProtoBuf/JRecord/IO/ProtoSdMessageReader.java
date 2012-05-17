package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import com.google.protobuf.ByteString;
import com.google.protobuf.DynamicMessage;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.UnknownFieldSet.Field;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Utils;

public class ProtoSdMessageReader extends AbstractLineReader<ProtoLayoutDef>
implements ProtoSelfDescribingReader {

	private static final FileDescriptor fileDesc = Utils.getFileDescriptor();


	private ProtoLine data;
	private FileDescriptorSet fileset = null;


	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#open(java.io.InputStream, net.sf.JRecord.Details.AbstractLayoutDetails)
	 */
	@Override
	public void open(InputStream inputStream, ProtoLayoutDef layout)
			throws IOException, RecordException {
		InputStream in = new BufferedInputStream(inputStream, Consts.IO_BUFFER_SIZE);

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

						FileDescriptorSet.Builder fsBuilder = FileDescriptorSet.newBuilder()
																.mergeFrom(byteStr);

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

			data = new ProtoLine(layout, layout.getPrimaryMsgBuilder().mergeFrom(in));
		} else {
			data = new ProtoLine(layout, layout.getPrimaryMsgBuilder().mergeFrom(in));
		}

		super.setLayout(layout);
		in.close();
	}

	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#read()
	 */
	@Override
	public AbstractLine<ProtoLayoutDef> read() throws IOException {

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
