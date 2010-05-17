package net.sf.RecordEditor.ProtoBuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Message;
import com.google.protobuf.Descriptors.FileDescriptor;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoDelimitedByteReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.edit.display.LineList;
import net.sf.RecordEditor.edit.display.LineTree;
import net.sf.RecordEditor.edit.display.LineTreeChild;
import net.sf.RecordEditor.edit.file.FileView;
import net.sf.RecordEditor.edit.tree.LineNodeChild;
import net.sf.RecordEditor.edit.tree.TreeParserXml;
import net.sf.RecordEditor.utils.edit.ParseArgs;

/**
 * This class is a programmers interface to the ProtoBuffers Editor
 * 
 * @author bruce Martin
 *
 */
public class PackageInterface {

	private static String[] NULL_ARGS = {};
	
	static {
		ProtoIOProvider.register();
	}
	
	/**
	 * method to let the user edit a ProtoBuffer Message
	 * 
	 * @param msg message to be edited by the user.
	 */
	public static void viewProtoBufferMessage(final Message msg) {
		editProtoBufferBuilder(msg.toBuilder());
	}
	
	/**
	 * method to let the user edit a ProtoBuffer builder
	 * @param builder to edit
	 */
	public static void editProtoBufferBuilder(final Message.Builder builder) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

			    ArrayList<AbstractLine<ProtoLayoutDef>> lines 
			    		= new ArrayList<AbstractLine<ProtoLayoutDef>>(1);
			    ProtoLayoutDef layout
			    		= new ProtoLayoutDef(
			    					builder.getDescriptorForType().getFile(), 
			    					Constants.IO_PROTO_SINGLE_MESSAGE);
			    int index = Math.max(0, layout.getRecordIndex(builder.getDescriptorForType().getName()));
			    
			    layout.setPrimaryMessageIndex(index);
			    lines.add(new ProtoLine(layout, builder));
			    
			    processFile(lines, layout, false);
			}
		});

	}
	
	public static void viewDelimitedProtoMessage(
			final FileDescriptor fileDescrtiption, final String messageName, final InputStream in) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				try {
				    byte[] bytes;
				    ArrayList<AbstractLine<ProtoLayoutDef>> lines 
				    		= new ArrayList<AbstractLine<ProtoLayoutDef>>(1);
				    ProtoLayoutDef layout
				    		= new ProtoLayoutDef(
				    					fileDescrtiption, 
				    					Constants.IO_PROTO_DELIMITED);
					ProtoDelimitedByteReader reader = new ProtoDelimitedByteReader();
					reader.open(in);
	
					int index = layout.getRecordIndex(messageName);
					
					layout.setPrimaryMessageIndex(index);
			        while ((bytes = reader.read()) != null) {
			            lines.add(new ProtoLine(layout, index, bytes));
			       }
	
			        in.close();
				    
				    processFile(lines, layout, false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}
	
	private static void processFile(List<AbstractLine<ProtoLayoutDef>> lines,
			ProtoLayoutDef layoutDtls,
			boolean pBrowse)  {
		
		ParseArgs args = new ParseArgs(NULL_ARGS);
		FileView<ProtoLayoutDef> file = new FileView<ProtoLayoutDef>(lines, null, null, false);
		
	    new ProtoBufEditor(args.getDfltFile(), args.getInitialRow(), ProtoIOProvider.getInstance());
	

		if (layoutDtls.hasChildren()) {
			new LineTreeChild(file, new LineNodeChild("File", file), true, 0);
		} else if (layoutDtls.isXml()) {
			new LineTree(file, TreeParserXml.getInstance(), true, 1);
		} else {
			new LineList(layoutDtls, file, file);
		}
	}

}
