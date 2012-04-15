package net.sf.RecordEditor.zProtoBuf.zTest.initialTests;

import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.edit.display.LineTreeChild;
import net.sf.RecordEditor.re.file.FileView;
import net.sf.RecordEditor.re.tree.LineNodeChild;
import net.sf.RecordEditor.utils.fileStorage.DataStore;
import net.sf.RecordEditor.utils.fileStorage.DataStoreStd;
import net.sf.RecordEditor.utils.screenManager.ReMainFrame;

import com.google.protobuf.DynamicMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;


public class TestAddressEdit {
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
		

		ProtoLayoutDef layout = new ProtoLayoutDef(fd, Constants.IO_PROTO_DELIMITED);
		DataStore<AbstractLine<ProtoLayoutDef>> lines = new DataStoreStd<AbstractLine<ProtoLayoutDef>>(layout, 1);
		
		
		DynamicMessage.Builder bld = DynamicMessage
															.newBuilder(fd.findMessageTypeByName("AddressBook"))
															.mergeFrom(indata);
        indata.close();
        
        lines.add(new ProtoLine(layout, bld));
       
        FileView file = new FileView(lines, null, null, false);
        file.setAllowMultipleRecords(false);
         
        new ReMainFrame("Proto Edit", "", "");
        //new LineList(layout, file, file);
        new LineTreeChild(file, new LineNodeChild("File", file), true, 0);
	}


}
