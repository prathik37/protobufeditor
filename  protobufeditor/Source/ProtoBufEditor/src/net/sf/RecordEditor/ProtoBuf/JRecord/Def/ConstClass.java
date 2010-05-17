package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.utils.swing.Combo.ComboOption;

public class ConstClass {
	public final static byte[] EMPTY_BYTE_ARRAY = {}; 

	public static final int COPYBOOK_PROTO = 1;
	public static final int COPYBOOK_COMPILED_PROTO = 2;

	private static final ComboOption[] COPYBOOK_LOADER = {
		new ComboOption(COPYBOOK_PROTO, Consts.COPYBOOK_PROTO),
		new ComboOption(COPYBOOK_COMPILED_PROTO, Consts.COPYBOOK_COMPILED_PROTO),
	};
	private static final ComboOption[] STRUCTURE_OPTIONS;
	
	static {
		ProtoIOProvider p = ProtoIOProvider.getInstance();
		STRUCTURE_OPTIONS = new ComboOption[p.getNumberOfEntries()];
		for (int i = 0; i < STRUCTURE_OPTIONS.length; i++) {
			STRUCTURE_OPTIONS[i] = new ComboOption(ProtoIOProvider.getInstance().getKey(i), 
					ProtoIOProvider.getInstance().getName(i));
		}
	};

	
	
	/**
	 * @return the cOPYBOOK_LOADER
	 */
	public static final ComboOption[] getCopybookLoaders() {
		return COPYBOOK_LOADER.clone();
	}



	/**
	 * @return the sTRUCTURE_OPTIONS
	 */
	public static final ComboOption[] getStructureOptions() {
		return STRUCTURE_OPTIONS.clone();
	}

}
