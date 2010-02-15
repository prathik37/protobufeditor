package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import com.google.protobuf.DynamicMessage;

import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.LineProvider;

public class ProtoLineProvider implements LineProvider<ProtoLayoutDef> {

	/**
	 * @see net.sf.JRecord.Details.LineProvider#getLine(net.sf.JRecord.Details.AbstractLayoutDetails, byte[])
	 */
	@Override
	public AbstractLine<ProtoLayoutDef> getLine(ProtoLayoutDef recordDescription,
			byte[] lineBytes) {
		return new ProtoLine(recordDescription, recordDescription.getPrimaryMessageIndex(), lineBytes);
	}

	/**
	 * @see net.sf.JRecord.Details.LineProvider#getLine(net.sf.JRecord.Details.AbstractLayoutDetails, java.lang.String)
	 */
	@Override
	public AbstractLine<ProtoLayoutDef> getLine(ProtoLayoutDef recordDescription,
			String linesText) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.LineProvider#getLine(net.sf.JRecord.Details.AbstractLayoutDetails)
	 */
	@Override
	public AbstractLine<ProtoLayoutDef> getLine(ProtoLayoutDef recordDescription) {
		int recordId = recordDescription.getPrimaryMessageIndex();
		return new ProtoLine(recordDescription, recordId, 
				DynamicMessage.newBuilder(recordDescription.getRecord(recordId).getProtoDesc()));
	}
//
//	public static void register() {
//		ProtoLineProvider provider = new ProtoLineProvider();
//		
//		
//		LineIOProvider.getInstance().getLineProviderManager().register(Constants.IO_PROTO_DELIMITED, provider);
//		LineIOProvider.getInstance().getLineProviderManager().register(Constants.IO_PROTO_SINGLE_MESSAGE, provider);
//	}
}
