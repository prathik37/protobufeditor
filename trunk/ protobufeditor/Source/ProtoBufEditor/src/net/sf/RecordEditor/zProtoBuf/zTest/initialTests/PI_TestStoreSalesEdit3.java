package net.sf.RecordEditor.zProtoBuf.zTest.initialTests;

import java.io.FileInputStream;


import java.util.List;

import net.sf.RecordEditor.ProtoBuf.PackageInterface;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;


public class PI_TestStoreSalesEdit3 {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		FileInputStream in = new FileInputStream("/home/bm/Work/ProtoBuffers/StoreSales3.protocomp");
		FileInputStream indata = new FileInputStream("/home/bm/Work/Temp/ProtoBuffers/protoStoreSales3.bin");


		FileDescriptorSet dp = FileDescriptorSet.parseFrom(in);
		
		ProtoIOProvider.register();
		
		System.out.println("~~ " + dp.getFileCount() );
		for (int i = 0; i < dp.getFileCount(); i++) {
			System.out.println("    === " + dp.getFile(i).getName());
		}
		

		FileDescriptor fd = FileDescriptor.buildFrom(dp.getFile(0), new FileDescriptor[] {});
//		List<Descriptor> msgTypes = fd.getMessageTypes();
//		//System.out.println(" ~~> " +  msgTypes.size() + " " + fd.getName() + " " + fd.getMessageTypes().size());
//		for (Descriptor d : msgTypes ) {
//			System.out.println(" --> " + d.getFullName() + " " + d.getName() + " " + d.getIndex() + " ");
//		}
		
		PackageInterface.viewDelimitedProtoMessage(fd, "Store", indata);

	}


}
