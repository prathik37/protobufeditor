package net.sf.RecordEditor.zProtoBuf.zTest.BuildData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.example.tutorial.SdMessage;
import com.google.protobuf.ByteString;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.UnknownFieldSet.Field;

public class TestSDread3 {

	public static FileDescriptor getDescriptor() {
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
	
	
	public static void main(String[] args) throws IOException {
		String filename = "/home/bm/Work/Temp/ProtoBuffers/ProtoTest_Address1_SD.bin";
		FileInputStream in = new FileInputStream(filename);
		//SdMessage.SdMsg.Builder builder = SdMessage.SdMsg.newBuilder();
		
		//getDescriptor();
		Descriptor desc = getDescriptor().getMessageTypes().get(0);
		DynamicMessage.Builder builder = DynamicMessage.newBuilder(desc);
		builder.mergeFrom(in);
		in.close();
//		ByteString b = (ByteString) builder.getField(desc.findFieldByNumber(1));
//		
//		
//		FileDescriptorSet.Builder fsBuilder = FileDescriptorSet.newBuilder().mergeFrom(b);
//		
//		System.out.println(" --> " + fsBuilder.getFileCount()
//				+ " " + fsBuilder.getFile(0).getName()
//				+ " " + fsBuilder.getFile(1).getName()
//		);
		
		int i = Integer.MAX_VALUE;
		Field descField = null;
//		for (FieldDescriptor fd : builder.getAllFields().keySet()) {
//			System.out.println(" --> " + fd.getName() + " > " + fd.getNumber()
//					+ " : " + fd.getType() + " " + fd.getJavaType()
//					+ " " + builder.getField(fd)
//					);
//			if (i > fd.getNumber()) {
//				i = fd.getNumber();
//				descField = fd;
//			}
//		}
		
		for (Integer ii :builder.getUnknownFields().asMap().keySet()) {
			System.out.println(" --> " 
				+ " " + ii + " "+ builder.getUnknownFields().getField(ii).getLengthDelimitedList()
			);
			
			if (i > ii.intValue()) {
				i = ii.intValue();
				descField = builder.getUnknownFields().getField(ii);
			}
		}

		
		if (descField != null) {
			Object o = descField.getLengthDelimitedList().get(0);
			System.out.println(" --> " + o.getClass());
			if (o instanceof ByteString) {
				FileDescriptorSet.Builder fsBuilder = FileDescriptorSet.newBuilder()
														.mergeFrom((ByteString)o);
				
				System.out.println(" --> " + fsBuilder.getFileCount()
						+ " " + fsBuilder.getFile(0).getName()
						+ " " + fsBuilder.getFile(1).getName()
				);
				
			
			
				SdMessage.getDescriptor();
				

				
				for (FieldDescriptor fd : builder.getAllFields().keySet()) {
					System.out.println(" --> " + fd.getName() + " > " + fd.getNumber()
							+ " : " + fd.getType() + " " + fd.getJavaType()
							+ " " + builder.getField(fd)
							);
				}
			}
		}
	}
	
}
