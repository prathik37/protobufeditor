package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class ZCopyProtoSdMessageReader extends AbstractLineReader<ProtoLayoutDef>
implements ProtoSelfDescribingReader {

	private static final FileDescriptor fileDesc = getFileDescriptor();


	private ProtoLine data;
	private FileDescriptorSet fileset = null;


	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#open(java.io.InputStream, net.sf.JRecord.Details.AbstractLayoutDetails)
	 */
	@Override
	public void open(InputStream inputStream, ProtoLayoutDef layout)
			throws IOException, RecordException {
		InputStream in = new BufferedInputStream(inputStream, Consts.IO_BUFFER_SIZE);
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

			ByteString byteString = (ByteString) builder.getField(desc.findFieldByNumber(1));

			FileDescriptorSet.Builder fsBuilder = FileDescriptorSet.newBuilder().mergeFrom(byteString);
			System.out.println(" --> " + fsBuilder.getFileCount()
					+ " " + fsBuilder.getFile(0).getName()
					+ " " + fsBuilder.getFile(1).getName()
			);
			fileset = fsBuilder.build();

			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e);
			}
			try {
				layout = new ProtoLayoutDef(
						ProtoHelper.getFileDescriptor(fileset, 0),
						Constants.IO_PROTO_SD_SINGLE_MESSAGE);
			} catch (DescriptorValidationException e) {
				e.printStackTrace();
				throw new IOException(e);
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

	private static FileDescriptor getFileDescriptor() {
		 java.lang.String descriptorData =
			      "\n\017SdMessage.proto\022\010tutorial\"\034\n\005SdMsg\022\023\n\013" +
			      "proto_files\030\001 \002(\014B\026\n\024com.example.tutoria" +
			      "l"
			    ;
	      final byte[] descriptorBytes;
	      try {
	        descriptorBytes = descriptorData.toString().getBytes("ISO-8859-1");
	      } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException(
	          "Standard encoding ISO-8859-1 not supported by JVM.", e);
	      }

	      FileDescriptorProto proto;
	      try {
	        proto = FileDescriptorProto.parseFrom(descriptorBytes);
	      } catch (InvalidProtocolBufferException e) {
	        throw new IllegalArgumentException(
	          "Failed to parse protocol buffer descriptor for generated code.", e);
	      }


	      final FileDescriptor result;
	      try {
	        result = FileDescriptor.buildFrom(proto, new com.google.protobuf.Descriptors.FileDescriptor[] {});
	      } catch (DescriptorValidationException e) {
	        throw new IllegalArgumentException(
	          "Invalid embedded descriptor for \"" + proto.getName() + "\".", e);
	      }

	      return result;
	}

	@Override
	public FileDescriptorSet getFileDescriptorSet() {

		return fileset;
	}


}
