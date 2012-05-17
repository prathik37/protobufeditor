/**
 *
 */
package net.sf.RecordEditor.ProtoBuf;

import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.ProtoBuf.re.display.ProtoLayoutSelection;
import net.sf.RecordEditor.diff.Menu;
import net.sf.RecordEditor.re.openFile.AbstractLayoutSelectCreator;
import net.sf.RecordEditor.re.util.ReIOProvider;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.screenManager.ReMainFrame;

/**
 * @author Bruce Martin
 *
 */
public class CompareProtoLayout {

	static {
        Common.OPTIONS.xsltAvailable.set(false);

        ReIOProvider.register();
        ProtoIOProvider.register();
	}

	public final static void newMenu() {
		 new Menu(new AbstractLayoutSelectCreator<ProtoLayoutSelection>() {
			/**
			 * @see net.sf.RecordEditor.re.openFile.AbstractLayoutSelectCreator#create()
			 */
			@Override
			public ProtoLayoutSelection create() {
				return new ProtoLayoutSelection();
//
//				ret.setLoadFromFile(true);
//				return ret;
			}
		 }, Consts.RECENT_FILES);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		 new ReMainFrame("Proto Buffer Compare", "", "pCmp");
		 newMenu();
	}

}
