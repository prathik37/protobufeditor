package net.sf.RecordEditor.zProtoBuf.zTest.initialTests;

import java.io.FileInputStream;

import java.util.List;

import net.sf.JRecord.Details.AbstractLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.edit.display.LineList;
import net.sf.RecordEditor.re.file.FileView;
import net.sf.RecordEditor.utils.fileStorage.DataStore;
import net.sf.RecordEditor.utils.fileStorage.DataStoreStd;
import net.sf.RecordEditor.utils.screenManager.ReMainFrame;

import com.google.protobuf.DynamicMessage;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;


public class TestSalesEdit {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		//DynamicMessage msg = new DynamicMessage();
		
		//DynamicMessage
		FileInputStream in = new FileInputStream("/home/bm/Work/ProtoBuffers/sales.protocomp");
		FileInputStream indata = new FileInputStream("/home/bm/Work/Temp/ProtoBuffers/protoSales.bin");

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
		
		DynamicMessage.Builder bld;

		ProtoLayoutDef layout = new ProtoLayoutDef(fd, 0);
		DataStore<AbstractLine<ProtoLayoutDef>> lines = new DataStoreStd<AbstractLine<ProtoLayoutDef>>(layout);


		long t= System.currentTimeMillis();
        while (indata.available() > 0) {
            bld =  DynamicMessage.newBuilder(fd.getMessageTypes().get(0));
            bld.mergeDelimitedFrom(indata);
 
            lines.add(new ProtoLine(layout, 0, bld));
       }
        System.out.println("Time: " + (System.currentTimeMillis() - t));

        indata.close();
       
        FileView file = new FileView(lines, null, null, false);
         
        new ReMainFrame("Proto Edit", "", "");
        new LineList(layout, file, file);
	}


}
