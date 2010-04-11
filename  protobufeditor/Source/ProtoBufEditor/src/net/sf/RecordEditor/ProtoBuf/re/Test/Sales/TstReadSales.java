package net.sf.RecordEditor.ProtoBuf.re.Test.Sales;

import java.io.FileInputStream;

import java.io.FileWriter;

import java.io.Writer;

import java.util.List;

import com.google.protobuf.DynamicMessage;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;



public class TstReadSales {

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
		DynamicMessage msg ;
		
		Writer w = new FileWriter("/home/bm/Work/Temp/ProtoBuffers/sales.xml");
		

        int i = 0;
        //XmlGenerator generator = new XmlGenerator(w);
        //generator.print("<message>");


        while (indata.available() > 0) {
           i += 1;
            bld =  DynamicMessage.newBuilder(fd.getMessageTypes().get(0));
            bld.mergeDelimitedFrom(indata);
            				
           msg = bld.build();
           
           //generator.print("<sale>");
           //XmlFormat.print(msg, generator);
           //generator.print("</sale>");

       }


        indata.close();
        System.out.println("Written: " + i );
        //generator.print("</message>");

        w.close();
	}

}
