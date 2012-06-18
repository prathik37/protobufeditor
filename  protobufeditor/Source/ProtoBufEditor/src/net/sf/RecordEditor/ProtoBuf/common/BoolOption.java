package net.sf.RecordEditor.ProtoBuf.common;

import net.sf.RecordEditor.utils.common.ProgramOptions.BoolOpt;

public class BoolOption extends BoolOpt {
	private boolean def;
	public BoolOption(String value, Boolean def) {
		super(value);
		this.def = def;
	}
	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.utils.common.ProgramOptions.BoolOpt#isDefaultTrue()
	 */
	@Override
	protected boolean isDefaultTrue() {
		return def;
	}

}
