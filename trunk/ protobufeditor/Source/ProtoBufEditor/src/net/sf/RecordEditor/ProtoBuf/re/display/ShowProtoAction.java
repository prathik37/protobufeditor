package net.sf.RecordEditor.ProtoBuf.re.display;

import java.awt.event.ActionEvent;

import net.sf.JRecord.Details.AbstractLayoutDetails;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.re.display.AbstractFileDisplay;
import net.sf.RecordEditor.re.display.DisplayBuilderFactory;
import net.sf.RecordEditor.re.display.IDisplayFrame;
import net.sf.RecordEditor.re.file.FileView;
import net.sf.RecordEditor.re.tree.LineNodeChild;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.fileStorage.DataStoreStd;
import net.sf.RecordEditor.utils.lang.ReAbstractAction;
import net.sf.RecordEditor.utils.screenManager.AbstractActiveScreenAction;
import net.sf.RecordEditor.utils.screenManager.ReFrame;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

@SuppressWarnings("serial")
public class ShowProtoAction extends ReAbstractAction implements AbstractActiveScreenAction {


	/**
	 * @param creator
	 */
	public ShowProtoAction() {
		super("Show Proto Definition");
		checkActionEnabled();
	}

	/**
	 * @see net.sf.RecordEditor.utils.screenManager.AbstractActiveScreenAction#checkActionEnabled()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void checkActionEnabled() {
		ReFrame actionHandler = ReFrame.getActiveFrame();

//		System.out.println("Show Proto Available: " + (actionHandler != null)
//				+ " " + (actionHandler instanceof AbstractFileDisplay)
//				+ " " + (actionHandler instanceof DisplayFrame
//						  && (((DisplayFrame) actionHandler).getActiveDisplay() instanceof AbstractFileDisplay)));
		super.setEnabled(actionHandler != null
					&&  (  actionHandler instanceof AbstractFileDisplay
					  ||  (actionHandler instanceof IDisplayFrame
						&& (((IDisplayFrame) actionHandler).getActiveDisplay() instanceof AbstractFileDisplay))));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ReFrame actionHandler = ReFrame.getActiveFrame();
		if (actionHandler instanceof AbstractFileDisplay) {
			displayProto(((AbstractFileDisplay) actionHandler).getFileView().getLayout());
		} else if (actionHandler instanceof IDisplayFrame
			&& (((IDisplayFrame) actionHandler).getActiveDisplay() instanceof AbstractFileDisplay)) {
			displayProto(((IDisplayFrame) actionHandler).getActiveDisplay().getFileView().getLayout());
		}
	}

	private void displayProto(AbstractLayoutDetails layout) {

		if (layout instanceof ProtoLayoutDef) {
			ProtoLayoutDef l = (ProtoLayoutDef) layout;
			FileDescriptorSet fds = ProtoHelper.getFileDescriptorSet(l.getFileDesc());

			ProtoLayoutDef layoutDef = new ProtoLayoutDef(
							FileDescriptorSet.getDescriptor().getFile(),
							Common.IO_PROTO_SINGLE_MESSAGE,
							true);
			layoutDef.setPrimaryMessageIndex(0);

			DataStoreStd<AbstractLine> list = DataStoreStd.newStore(layoutDef);

			list.add(new ProtoLine(layoutDef, fds.toBuilder()));

			FileView view = new FileView(list, null, null, true);

//			DisplayBuilder
//				.newLineTreeChild(null, view, new LineNodeChild("Proto Definition", view), true, 0)
//					.expandTree("FieldDescriptorProto");
			DisplayBuilderFactory.getInstance()
					.newLineTreeChildScreen(
							DisplayBuilderFactory.ST_LINE_TREE_CHILD_EXPAND_PROTO,
							null, view, new LineNodeChild("Proto Definition", view), true, 0);
		}
	}
}
