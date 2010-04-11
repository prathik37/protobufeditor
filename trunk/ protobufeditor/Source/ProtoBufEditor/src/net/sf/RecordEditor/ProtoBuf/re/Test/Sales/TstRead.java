package net.sf.RecordEditor.ProtoBuf.re.Test.Sales;

import java.io.FileInputStream;

import java.io.FileWriter;

import java.io.Writer;
import java.util.List;

import com.google.protobuf.DynamicMessage;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;


public class TstRead {

	private static void printFileDescriptor(FileDescriptorSet dp) {
		int j;
		
		System.out.println("~~ " + dp.getFileCount() );
		
		for (int i = 0; i < dp.getFileCount(); i++) {
			System.out.println();
			System.out.println("    === " + dp.getFile(i).getName());
			for (j = 0; j < dp.getFile(i).getMessageTypeCount(); j++) {
				printMessage(dp.getFile(i).getMessageType(j), 0);
			}
		}
	}
	
	private static void printMessage(DescriptorProto pd, int level) {
		for (int i = 0; i < level; i++) {
			System.out.print("\t");
		}
		System.out.println("\t ---- " + pd.getName() + " " + pd.getFieldCount());
		
		for (DescriptorProto nm : pd.getNestedTypeList()) {
			printMessage(nm, level + 1);
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		//DynamicMessage msg = new DynamicMessage();
		
		//DynamicMessage
		FileInputStream in = new FileInputStream("/home/bm/Download/protobuf-2.2.0/examples/addressbook.protocomp");
		FileInputStream indata = new FileInputStream("/home/bm/Work/Temp/ProtoBuffers/ProtoTest_Address1.bin");
//		byte[] tb = new byte[1];
//		String s;
		byte[] b = new byte[in.available()];
		in.read(b);
//		byte[] bb = ("\n\021addressbook.proto\022\010tutorial\"\332\001\n\006Person" +
//	      "\022\014\n\004name\030\001 \002(\t\022\n\n\002id\030\002 \002(\005\022\r\n\005email\030\003 \001(" +
//	      "\t\022+\n\005phone\030\004 \003(\0132\034.tutorial.Person.Phone" +
//	      "Number\032M\n\013PhoneNumber\022\016\n\006number\030\001 \002(\t\022.\n" +
//	      "\004type\030\002 \001(\0162\032.tutorial.Person.PhoneType:" +
//	      "\004HOME\"+\n\tPhoneType\022\n\n\006MOBILE\020\000\022\010\n\004HOME\020\001" +
//	      "\022\010\n\004WORK\020\002\"/\n\013AddressBook\022 \n\006person\030\001 \003(" +
//	      "\0132\020.tutorial.PersonB)\n\024com.example.tutor" +
//	      "ialB\021AddressBookProtos").getBytes("ISO-8859-1");
		
		System.out.println();

		in = new FileInputStream("/home/bm/Download/protobuf-2.2.0/examples/addressbook.protocomp");
		//DescriptorProtos.
		FileDescriptorSet dp = FileDescriptorSet.parseFrom(in);
		
		printFileDescriptor(dp);
		
		//FileDescriptorProto proto = FileDescriptorProto.parseFrom(bb);
		FileDescriptor fd = FileDescriptor.buildFrom(dp.getFile(0), new FileDescriptor[] {});
		List<Descriptor> msgTypes = fd.getMessageTypes();
		//System.out.println(" ~~> " +  msgTypes.size() + " " + fd.getName() + " " + fd.getMessageTypes().size());
		for (Descriptor d : msgTypes ) {
			System.out.println(" --> " + d.getFullName() + " " + d.getName() + " " + d.getIndex() + " ");
		}
		
		DynamicMessage msg = DynamicMessage.parseFrom(fd.findMessageTypeByName("AddressBook"), indata);
		
		Writer w = new FileWriter("/home/bm/Work/Temp/ProtoBuffers/ProtoTest_Address1.xml");
		
		//XmlFormat.print(msg, w);
		
		w.close();
	}

}
