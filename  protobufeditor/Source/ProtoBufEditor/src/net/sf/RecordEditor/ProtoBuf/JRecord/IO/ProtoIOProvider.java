package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.LineProvider;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.IO.AbstractLineWriter;
import net.sf.JRecord.IO.BasicIoProvider;
import net.sf.JRecord.IO.LineIOProvider;
import net.sf.JRecord.IO.LineReaderWrapper;
import net.sf.JRecord.IO.LineWriterWrapper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLineProvider;

public class ProtoIOProvider extends BasicIoProvider {

	private static int[] keys = {
		Constants.IO_PROTO_SINGLE_MESSAGE, Constants.IO_PROTO_DELIMITED,
		Constants.IO_PROTO_SD_SINGLE_MESSAGE, Constants.IO_PROTO_SD_DELIMITED};
	private static String[] names = {
		"ProtoBuffer Single Message", "ProtoBuffer Delimited Messages",
		"ProtoBuffer Self Describing Message", "ProtoBuffer Self Describing Delimited"
	};
	private static String[] externalNames = {
		"ProtoBuf_Message", "ProtoBuf_Delimited",
		"ProtoBuf_SD_Message",  "ProtoBuf_SD_Delimited"};

	private static ProtoLineProvider lineProvider = new ProtoLineProvider();

	private static  ProtoIOProvider instance = new ProtoIOProvider();
	private static boolean toRegister = true;

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Common.AbstractManager#getManagerName()
	 */
	@Override
	public String getManagerName() {
		return "ProtoBuffers_IO_Provider";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public LineProvider getLineProvider(int fileStructure) {
		return lineProvider;
	}


	@SuppressWarnings("rawtypes")
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
		break;
		case(Constants.IO_PROTO_SD_DELIMITED):
			reader = new ProtoSdDelimitedReader();
		break;
		case(Constants.IO_PROTO_SD_SINGLE_MESSAGE):
			reader = new ProtoSdMessageReader();
		break;
		}
		return reader;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public AbstractLineReader getLineReader(int fileStructure) {
		return getLineReader(fileStructure, lineProvider);
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.IO.AbstractLineIOProvider#getLineWriter(int)
	 */
	@Override
	public AbstractLineWriter getLineWriter(int fileStructure) {
		return getLineWriter(fileStructure, null);
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.IO.AbstractLineIOProvider#getLineWriter(int, java.lang.String)
	 */
	@Override
	public AbstractLineWriter getLineWriter(int fileStructure, String charset) {
		AbstractLineWriter writer = null;
		switch (fileStructure) {
		case(Constants.IO_PROTO_DELIMITED):
			writer = new LineWriterWrapper(
					new ProtoDelimitedByteWriter()
			);
		break;
		case(Constants.IO_PROTO_SINGLE_MESSAGE):
			writer = new ProtoMessageWriter();
		break;
		case(Constants.IO_PROTO_SD_DELIMITED):
			writer = new ProtoSdDelimitedWriter();
		break;
		case(Constants.IO_PROTO_SD_SINGLE_MESSAGE):
			//writer = new ProtoMessageWriter();
		break;
		}
		return writer;
	}


	@Override
	public String getStructureName(int fileStructure) {
    	for (int i = 0; i < keys.length && keys[i] != Constants.NULL_INTEGER; i++) {
    		if (keys[i] == fileStructure) {
    			//System.out.println("--> " + fileStructure + " " + keys[i] + " " + externalNames[i]);
    			return externalNames[i];
    		}
    	}
    	return "";
	}


	@Override
	public String getStructureNameForIndex(int index) {
		return externalNames[index];
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
