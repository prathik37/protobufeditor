package net.sf.RecordEditor.zProtoBuf.zTest.BuildData;

import java.io.FileInputStream;
import java.io.IOException;

import com.example.tutorial.SdMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

public class TestSDread1 {

	public static void main(String[] args) throws IOException {
		String filename = "/home/bm/Work/Temp/ProtoBuffers/ProtoTest_Address1_SD.bin";
		FileInputStream in = new FileInputStream(filename);
		SdMessage.SdMsg.Builder builder = SdMessage.SdMsg.newBuilder();
		
		builder.mergeFrom(in);
		in.close();
		FileDescriptorSet.Builder fsBuilder = FileDescriptorSet.newBuilder().mergeFrom(builder.getProtoFiles());
		
		System.out.println(" --> " + fsBuilder.getFileCount()
				+ " " + fsBuilder.getFile(0).getName()
				+ " " + fsBuilder.getFile(1).getName()
		);
	}
}
