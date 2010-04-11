package net.sf.RecordEditor.zProtoBuf.zTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.IO.AbstractLineWriter;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;

import junit.framework.TestCase;

public class TestDelimitedByteWriter extends TestCase {
    private String salesFileOut   = "/home/bm/Work/Temp/ProtoBuffers/" + "zrwProtoStoreSales.bin";
    
    private List<AbstractMessage> msgs;
    
    private ProtoLayoutDef layout;
    
    private FileDescriptor fd;
    
  
    public TestDelimitedByteWriter() {
    	try {
    		layout = getLayout();

    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void testReader() throws IOException, RecordException, DescriptorValidationException {
    	int times = 1000;
    	writeFile(times);
    	
    	FileInputStream reader = new FileInputStream(salesFileOut);
    	
    	for (int i =0; i < times ; i++) {
    		readCheck(reader);
    	}
    	reader.close();
    }
  
    
    public void testReader1() throws IOException, RecordException, DescriptorValidationException {
    	writeFile(1);
    	
    	FileInputStream reader = new FileInputStream(salesFileOut);
    	readCheck(reader);
    	reader.close();
    }
    	
    private void writeFile(int times) throws IOException {
    	msgs = (new BuildSalesList()).getSalesList();
    	AbstractLineWriter writer = ProtoIOProvider.getInstance().getLineWriter(Constants.IO_PROTO_DELIMITED);
    	writer.open(salesFileOut);
   	
    	for (int i =0; i < times; i++) {
	    	for (AbstractMessage msg : msgs) {
	    		writer.write(new ProtoLine(layout, msg.toBuilder()));

	    	}
    	}
    	
    	writer.close();
    }
     
    private void readCheck(InputStream in)  throws IOException {
       	int i = 0;
    	ProtoLine l;

    	boolean ok = true;
    	DynamicMessage.Builder newMessage;
    	Descriptor type = fd.findMessageTypeByName("Store");
    	for (AbstractMessage msg : msgs) {
    		newMessage = DynamicMessage.newBuilder(type);
    		newMessage.mergeDelimitedFrom(in);
    		i += 1;
    		
    		l = new ProtoLine(layout, newMessage);
    		layout = l.getLayout();
    		int idx = layout.getPrimaryMessageIndex();
    		
    		System.out.println(idx + " " +layout.getRecord(idx).getRecordName() + " " + l.getField(idx, 0));
    		
    		ok = ok && Util.check(msg, l, idx);
    	}

    	assertTrue("Error in Read Tests, see output", ok);
    }

    
   
    
    public ProtoLayoutDef getLayout() throws IOException, DescriptorValidationException{
    	FileInputStream in = new FileInputStream("/home/bm/Work/ProtoBuffers/StoreSales.protocomp");
    	FileDescriptorSet dp = FileDescriptorSet.parseFrom(in);
    	
		fd = FileDescriptor.buildFrom(dp.getFile(0), new FileDescriptor[] {});
		List<Descriptor> msgTypes = fd.getMessageTypes();

		ProtoLayoutDef layout = new ProtoLayoutDef(fd, 0);
		layout.setPrimaryMessageIndex(layout.getRecordIndex("store"));
		return layout;
    }

}
