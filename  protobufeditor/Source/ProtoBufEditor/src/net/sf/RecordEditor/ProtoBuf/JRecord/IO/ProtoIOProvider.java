package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.LineProvider;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.IO.AbstractLineIOProvider;
import net.sf.JRecord.IO.AbstractLineWriter;
import net.sf.JRecord.IO.LineIOProvider;
import net.sf.JRecord.IO.LineReaderWrapper;
import net.sf.JRecord.IO.LineWriterWrapper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLineProvider;

public class ProtoIOProvider implements AbstractLineIOProvider {

	private static int[] keys = {Constants.IO_PROTO_SINGLE_MESSAGE, Constants.IO_PROTO_DELIMITED};
	private static String[] names = {"ProtoBuffer Message", "ProtoBuffer Delimited Messages"};
	private static String[] externalNames = {"ProtoBuf_Message", "ProtoBuf_Delimited"};
	
	private static ProtoLineProvider lineProvider = new ProtoLineProvider();
	
	private static  ProtoIOProvider instance = new ProtoIOProvider();
	private static boolean toRegister = true;
	
	@SuppressWarnings("unchecked")
	@Override
	public LineProvider getLineProvider(int fileStructure) {
		return  lineProvider;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractLineReader getLineReader(int fileStructure,
			LineProvider provider) {
		AbstractLineReader reader = null;
		switch (fileStructure) {
		case(Constants.IO_PROTO_DELIMITED): 
			reader = new LineReaderWrapper(
					provider, 
					new ProtoDelimitedByteReader()
			);
		break;
		case(Constants.IO_PROTO_SINGLE_MESSAGE): 
			reader = new ProtoMessageReader();
		}
		return reader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractLineReader getLineReader(int fileStructure) {
		return getLineReader(fileStructure, lineProvider);
	}

	@Override
	public AbstractLineWriter getLineWriter(int fileStructure) {
		AbstractLineWriter reader = null;
		switch (fileStructure) {
		case(Constants.IO_PROTO_DELIMITED): 
			reader = new LineWriterWrapper(
					new ProtoDelimitedByteWriter()
			);
		break;
		case(Constants.IO_PROTO_SINGLE_MESSAGE): 
			reader = new ProtoMessageWriter();
		break;
		}
		return reader;
	}

	@Override
	public String getStructureName(int fileStructure) {
		return externalNames[fileStructure];
	}

	@Override
	public boolean isCopyBookFileRequired(int fileStructure) {
		return false;
	}

	@Override
	public int getKey(int idx) {
		return keys[idx];
	}

	@Override
	public String getName(int idx) {
		return names[idx];
	}

	@Override
	public int getNumberOfEntries() {
		return names.length;
	}

	/**
	 * @return the names
	 */
	public static final String[] getNames() {
		return names.clone();
	}

	/**
	 * @return the instance
	 */
	public static final ProtoIOProvider getInstance() {
		return instance;
	}

	public static void register() {
		
		if (toRegister) {
			LineIOProvider.getInstance().register(instance);
			toRegister = false;
		}
	}
}
