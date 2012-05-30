package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.io.UnsupportedEncodingException;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;

public class Utils {

	public static FileDescriptor getFileDescriptor() {
		 java.lang.String descriptorData =
		      "\n\017SdMessage.proto\022\010tutorial\"\036\n\005SdMsg\022\025\n\013" +
		      "proto_files\030\240\215\006 \002(\014B\026\n\024com.example.tutor" +
		      "ial"
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

}
