package net.sf.RecordEditor.zProtoBuf.zTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.AbstractTreeDetails;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;

public class Util {

    public static boolean readCheck(AbstractLineReader reader, List<AbstractMessage> msgs)  throws IOException {
 
    	int i = 0;
    	ProtoLine l;
    	ProtoLayoutDef layout;
    	boolean ok = true;
    	for (AbstractMessage msg : msgs) {
    		l = (ProtoLine) reader.read();
    		i += 1;
    		
    		layout = l.getLayout();
    		int idx = layout.getPrimaryMessageIndex();
    		
    		System.out.println(idx + " " +layout.getRecord(idx).getRecordName() + " " + l.getField(idx, 0));
    		
    		ok = ok && Util.check(msg, l, idx);
    	}
    	
    	return ok;
    }

	   
    public static boolean check(AbstractMessage msg, ProtoLine l, int idx) {
    	boolean ok = true;
    	ProtoLayoutDef layout = l.getLayout();
    	AbstractTreeDetails tree = l.getTreeDetails();
    	AbstractMessage cmsg;
    	Object o;
    	boolean firstError = true;
    	
		for (int j = 0; j < layout.getRecord(idx).getFieldCount(); j++) {
			o = msg.getField(msg.getDescriptorForType().findFieldByName(layout.getRecord(idx).getField(j).getName()));
		
			if (! l.getField(idx, j).equals(o)) {
				if (firstError) {
					firstError = false;
					System.out.println();
					System.out.println("Error in " + layout.getRecord(idx).getRecordName());
				}
				System.out.println(idx + " " +layout.getRecord(idx).getField(j).getName()
						+ "\t" + l.getField(idx, j)
						+ "\t" + o
						+ "\t" + l.getField(idx, j).equals(o));
				ok = false;
			}
		}
		
		int k;
		for (int i =0 ; i < tree.getChildCount(); i++) {		
			List<AbstractLine> lines = tree.getLines(i);
			int lidx;
			String name;
			
			k = 0;
			for (AbstractLine line : lines) {
				FieldDescriptor desc = msg.getDescriptorForType().findFieldByName(tree.getChildName(i));
				lidx = line.getPreferredLayoutIdx();
				if (desc.isRepeated()) {
					o = msg.getRepeatedField(desc, k);
				} else {
					o = msg.getField(desc);
				}
				if (o instanceof AbstractMessage) {
					ok = ok && check((AbstractMessage) o, (ProtoLine) line, lidx);
				} else {
					ok = false;
					System.out.println("Not a message: " + desc.getName() + " (" + k + ")  " + o);
				}
				k += 1;
			}
		}

		return ok;
    }

    
    
    public static ProtoLayoutDef getLayout() throws IOException, DescriptorValidationException{
    	FileInputStream in = new FileInputStream("/home/bm/Work/ProtoBuffers/StoreSales.protocomp");
    	FileDescriptorSet dp = FileDescriptorSet.parseFrom(in);
		FileDescriptor fd = FileDescriptor.buildFrom(dp.getFile(0), new FileDescriptor[] {});
		List<Descriptor> msgTypes = fd.getMessageTypes();

		ProtoLayoutDef layout = new ProtoLayoutDef(fd, 0);
		layout.setPrimaryMessageIndex(layout.getRecordIndex("store"));
		return layout;
    }

}
