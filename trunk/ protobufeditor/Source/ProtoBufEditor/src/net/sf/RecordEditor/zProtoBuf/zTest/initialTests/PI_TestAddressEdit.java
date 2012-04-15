package net.sf.RecordEditor.zProtoBuf.zTest.initialTests;

import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.RecordEditor.ProtoBuf.PackageInterface;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;


public class PI_TestAddressEdit {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		//DynamicMessage msg = new DynamicMessage();
		
		//DynamicMessage
		FileInputStream in = new FileInputStream("/home/bm/Download/protobuf-2.2.0/examples/addressbook.protocomp");
		FileInputStream indata = new FileInputStream("/home/bm/Work/Temp/ProtoBuffers/ProtoTest_Address1.bin");

		//DescriptorProtos.
		FileDescriptorSet dp = FileDescriptorSet.parseFrom(in);
		ProtoIOProvider.register();
				
		System.out.println("~~ " + dp.getFileCount() );
		for (int i = 0; i < dp.getFileCount(); i++) {
			System.out.println("    === " + dp.getFile(i).getName());
		}
		
		//FileDescriptorProto proto = FileDescriptorProto.parseFrom(bb);
		FileDescriptor fd = FileDescriptor.buildFrom(dp.getFile(0), new FileDescriptor[] {});
		List<Descriptor> msgTypes = fd.getMessageTypes();
		//System.out.println(" ~~> " +  msgTypes.size() + " " + fd.getName() + " " + fd.getMessageTypes().size());
		for (Descriptor d : msgTypes ) {
			System.out.println(" --> " + d.getFullName() + " " + d.getName() + " " + d.getIndex() + " ");
		}
		

		ArrayList<AbstractLine<ProtoLayoutDef>> lines = new ArrayList<AbstractLine<ProtoLayoutDef>>(1);
		ProtoLayoutDef layout = new ProtoLayoutDef(fd, Constants.IO_PROTO_DELIMITED);
		
		
		DynamicMessage.Builder bld = DynamicMessage
															.newBuilder(fd.findMessageTypeByName("AddressBook"))
															.mergeFrom(indata);
        indata.close();
        
        PackageInterface.editProtoBufferBuilder(bld);
    
	}


}
