package net.sf.RecordEditor.ProtoBuf.re.display;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

import net.sf.JRecord.Details.AbstractLayoutDetails;
import net.sf.JRecord.Details.AbstractLine;

import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;
import net.sf.RecordEditor.edit.display.LineTreeChild;
import net.sf.RecordEditor.re.file.FileView;
import net.sf.RecordEditor.re.script.AbstractFileDisplay;
import net.sf.RecordEditor.re.tree.LineNodeChild;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.fileStorage.DataStoreStd;
import net.sf.RecordEditor.utils.screenManager.AbstractActiveScreenAction;
import net.sf.RecordEditor.utils.screenManager.ReFrame;

@SuppressWarnings("serial")
public class ShowProtoAction extends AbstractAction implements AbstractActiveScreenAction {


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
	@Override
	public void checkActionEnabled() {
		ReFrame actionHandler = ReFrame.getActiveFrame();

		super.setEnabled(actionHandler != null && actionHandler instanceof AbstractFileDisplay);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent arg0) {
		ReFrame actionHandler = ReFrame.getActiveFrame();
		if (actionHandler instanceof AbstractFileDisplay) {
			AbstractLayoutDetails layout = ((AbstractFileDisplay) actionHandler).getFileView().getLayout();
			if (layout instanceof ProtoLayoutDef) {
				ProtoLayoutDef l = (ProtoLayoutDef) layout;
				FileDescriptorSet fds = ProtoHelper.getFileDescriptorSet(l.getFileDesc());
				
				ProtoLayoutDef layoutDef = new ProtoLayoutDef(
								FileDescriptorSet.getDescriptor().getFile(), 
								Common.IO_PROTO_SINGLE_MESSAGE,
								true);
				layoutDef.setPrimaryMessageIndex(0);
				
				DataStoreStd<AbstractLine<ProtoLayoutDef>> list = new DataStoreStd<AbstractLine<ProtoLayoutDef>>(layoutDef);
				
				list.add(new ProtoLine(layoutDef, fds.toBuilder()));
				
				FileView<ProtoLayoutDef> view = new FileView(list, null, null, true); 
				
				new LineTreeChild(view, new LineNodeChild("Proto Definition", view), true, 0)
					.expandTree("FieldDescriptorProto");

			}
		}
	}
}
