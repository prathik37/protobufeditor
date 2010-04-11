package net.sf.RecordEditor.zProtoBuf.zTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors.DescriptorValidationException;

import junit.framework.TestCase;

public class TestDelimitedByteReader extends TestCase {
    private String salesFileOut   = "/home/bm/Work/Temp/ProtoBuffers/" + "zrProtoStoreSales.bin";
    
    private List<AbstractMessage> msgs;
    
  
    
    public void testReader() throws IOException, RecordException, DescriptorValidationException {
    	int times = 1000;
    	writeFile(times);
    	
    	AbstractLineReader reader = ProtoIOProvider.getInstance().getLineReader(Constants.IO_PROTO_DELIMITED);
    	reader.open(salesFileOut, Util.getLayout());
    	
    	for (int i =0; i < times ; i++) {
    		readCheck(reader);
    	}
    	reader.close();
    }
  
    
    public void testReader1() throws IOException, RecordException, DescriptorValidationException {
    	writeFile(1);
    	
    	AbstractLineReader reader = ProtoIOProvider.getInstance().getLineReader(Constants.IO_PROTO_DELIMITED);
    	reader.open(salesFileOut, Util.getLayout());
    	readCheck(reader);
    	reader.close();
    }
    	
    private void writeFile(int times) throws IOException {
    	msgs = (new BuildSalesList()).getSalesList();
    	FileOutputStream output = new FileOutputStream(salesFileOut);
  	
    	for (int i =0; i < times; i++) {
	    	for (AbstractMessage msg : msgs) {
	    		msg.writeDelimitedTo(output);
	    	}
    	}
    	
    	output.close();
    }
     
    private void readCheck(AbstractLineReader reader)  throws IOException {
    	assertTrue("Error in Read Tests, see output", Util.readCheck(reader, msgs));
    }
}
