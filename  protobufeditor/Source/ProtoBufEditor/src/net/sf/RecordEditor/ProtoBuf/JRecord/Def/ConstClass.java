package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;

public class ConstClass {
	public final static byte[] EMPTY_BYTE_ARRAY = {}; 

	public static final int COPYBOOK_PROTO = 1;
	public static final int COPYBOOK_COMPILED_PROTO = 2;

	private static final Option[] COPYBOOK_LOADER = {
		new Option(COPYBOOK_PROTO, Consts.COPYBOOK_PROTO),
		new Option(COPYBOOK_COMPILED_PROTO, Consts.COPYBOOK_COMPILED_PROTO),
	};
	private static final Option[] STRUCTURE_OPTIONS;
	
	static {
		ProtoIOProvider p = ProtoIOProvider.getInstance();
		STRUCTURE_OPTIONS = new Option[p.getNumberOfEntries()];
		for (int i = 0; i < STRUCTURE_OPTIONS.length; i++) {
			STRUCTURE_OPTIONS[i] = new Option(ProtoIOProvider.getInstance().getKey(i), 
					ProtoIOProvider.getInstance().getName(i));
		}
	};

	
	
	/**
	 * @return the cOPYBOOK_LOADER
	 */
	public static final Option[] getCopybookLoaders() {
		return COPYBOOK_LOADER.clone();
	}



	/**
	 * @return the sTRUCTURE_OPTIONS
	 */
	public static final Option[] getStructureOptions() {
		return STRUCTURE_OPTIONS.clone();
	}



	public static final class Option {
		public final String string;
		public final int index;
		
		public Option(int idx,String str) {
			string = str;
			index = idx;
		}
		
		public String toString() {
			return string;
		}
	}

}
