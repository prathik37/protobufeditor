package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.utils.params.Parameters;
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
					ProtoIOProvider.getInstance().getName(i).substring(12));
		}
	};

	public static final String VAR_PROTO_IMBED_DIR = "ProtoImbedDir";

	public static final String VAR_PROTOBUF_COMPILE = "ProtoC";
	public static final String VAR_PROTOBUF_COMPILE_OPTS = "ProtoCopts.";

	public static final int NUMBER_OF_PROTOC_OPTIONS = 8;


	/**
	 * @return the cOPYBOOK_LOADER
	 */
	public static final ComboOption[] getCopybookLoaders() {
		return COPYBOOK_LOADER.clone();
	}


	public static int getLoaderIndex(int loaderId) {
		for (int i = 0; i < COPYBOOK_LOADER.length; i++) {
			if (COPYBOOK_LOADER[i].index == loaderId) {
				return i;
			}
		}
		return 0;
	}


	public static int getFileStructureIndex(int structure) {
		for (int i = 0; i < STRUCTURE_OPTIONS.length; i++) {
			if (STRUCTURE_OPTIONS[i].index == structure) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * @return the sTRUCTURE_OPTIONS
	 */
	public static final ComboOption[] getStructureOptions() {
		return STRUCTURE_OPTIONS.clone();
	}

	/**
	 * Get the protoc compile command
	 * @return protoc compile command
	 */
	public static final String getProtoC() {
		String s = Parameters.getString(VAR_PROTOBUF_COMPILE);

		if (s == null || s.equals("")) {
			s = "protoc";
		}

		return s;
	}
}
