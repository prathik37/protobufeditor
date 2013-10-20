package net.sf.RecordEditor.ProtoBuf;

import java.io.IOException;
import java.io.InputStream;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoDelimitedByteReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.re.display.DisplayBuilderFactory;
import net.sf.RecordEditor.re.file.FileView;
import net.sf.RecordEditor.re.tree.LineNodeChild;
import net.sf.RecordEditor.re.tree.TreeParserXml;
import net.sf.RecordEditor.utils.edit.ParseArgs;
import net.sf.RecordEditor.utils.fileStorage.DataStore;
import net.sf.RecordEditor.utils.fileStorage.DataStoreStd;

import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Message;

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

				ProtoLayoutDef layout
			    		= new ProtoLayoutDef(
			    					builder.getDescriptorForType().getFile(),
			    					Constants.IO_PROTO_SINGLE_MESSAGE);
			    DataStoreStd<AbstractLine> lines
			    		= DataStoreStd.newStore(layout, 1);
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
				    ProtoLayoutDef layout
				    		= new ProtoLayoutDef(
				    					fileDescrtiption,
				    					Constants.IO_PROTO_DELIMITED);
					    DataStoreStd<AbstractLine> lines
				    		= DataStoreStd.newStore(layout, 1);
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

	private static void processFile(DataStore<AbstractLine> lines,
			ProtoLayoutDef layoutDtls,
			boolean pBrowse)  {

		ParseArgs args = new ParseArgs(NULL_ARGS);
		FileView file = new FileView(lines, null, null, false);

	    new ProtoBufEditor(args.getDfltFile(), args.getInitialRow(), ProtoIOProvider.getInstance());


		if (layoutDtls.hasChildren()) {
			DisplayBuilderFactory.newLineTreeChild(null, file, new LineNodeChild("File", file), true, 0);
		} else if (layoutDtls.isXml()) {
			DisplayBuilderFactory.newLineTree(null, file, TreeParserXml.getInstance(), true, 1);
		} else {
			DisplayBuilderFactory.newLineList(null, layoutDtls, file, file);
		}
	}

}
