/**
 *
 */
package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.util.ArrayList;
import java.util.List;

import net.sf.JRecord.Details.AbstractChildDetails;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.TreeDetails;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.lang.LangConversion;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

/**
 * @author bm
 *
 */
public class ProtoTreeDetails
extends TreeDetails<ProtoRecordDef.ProtoFieldDef, ProtoRecordDef, ProtoLayoutDef, ProtoChildDefinition, ProtoLine> {

	private ProtoRecordDef recordDef;
	private boolean[] rebuildChildren;


	/**
	 *
	 */
	public ProtoTreeDetails(ProtoLine parentLine, ProtoChildDefinition childDefinition) {
		super.convertNullToArrayList = false;

		line = parentLine;
		definitionInParent = childDefinition;

		ProtoLayoutDef layout = line.getLayout();

		recordDef = layout.getRecord(line.getPreferredLayoutIdx());
		rebuildChildren = new boolean[recordDef.getChildRecordCount()];
		for (int i = 0; i < recordDef.getChildRecordCount(); i++) {
			super.addChildDefinition(recordDef.getChildRecord(i), null);
			rebuildChildren[i] = false;
		}
	}

	/**
	 * @see net.sf.JRecord.Details.TreeDetails#hasLines(int)
	 */
	@Override
	public boolean hasLines(int idx) {
		boolean ret = super.hasLines(idx);

		if (! ret) {
			FieldDescriptor fd = getChildDetails(idx).getFieldDefinition();

			if (fd.isRepeated()) {
				ret =line.getBuilder().getRepeatedFieldCount(fd) > 0;
			} else {
				ret = line.getBuilder().hasField(fd);
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.ChildLines#getLines(int)
	 */
	@Override
	public List<ProtoLine> getLines(int idx) {

		if (super.list.get(idx) == null) {
			ProtoChildDefinition childDef = getChildDetails(idx);
			ArrayList<ProtoLine> lines;

			if (hasLines(idx)) {
				FieldDescriptor fieldDef = childDef.getFieldDefinition();
				Message.Builder bld = line.getBuilder();

				if (fieldDef.isRepeated()) {
					int count = bld.getRepeatedFieldCount(fieldDef);
					lines = new ArrayList<ProtoLine>(Math.min(1, count));

					for (int i = 0; i < count; i++) {
						Message m =(Message) bld.getRepeatedField(fieldDef, i);
						lines.add(new ProtoLine(
								line, childDef, i,
								childDef.getRecordIndex(),
								m));
					}
				} else {
					lines = new ArrayList<ProtoLine>(1);
					lines.add(new ProtoLine(
							line, childDef,
							childDef.getRecordIndex(),
							(Message) bld.getField(fieldDef)));
				}
			} else {
				lines = new ArrayList<ProtoLine>();
			}
			super.list.set(idx, lines);
		}

		return super.getLines(idx);
	}

	/**
	 * @see net.sf.JRecord.Details.AbstractTreeDetails#addChild(net.sf.JRecord.Details.AbstractLine)
	 */
	@Override
	public void addChild(ProtoLine newLine, int location) {
		super.addChild(newLine, location);
		ProtoLine l = newLine;
		int pos = location;
		if (pos < 0) {
			pos = getLines(newLine.getTreeDetails().getChildDefinitionInParent().getChildIndex()).size() - 1;
		}
		ProtoChildDefinition childD = (ProtoChildDefinition) newLine.getTreeDetails().getChildDefinitionInParent();

		List<ProtoLine> childList = getLines(childD.childIndex);

		l.getTreeDetails().setParentLine(line, pos);

		rebuildRepeatedField(childD, childList);
	}


	@Override
	public <childDtls extends AbstractChildDetails<ProtoRecordDef>> ProtoLine addChild(childDtls childDefition, int location) {
		ProtoLine ret = null;

		if (childDefition != null && childDefition instanceof ProtoChildDefinition) {
			ProtoChildDefinition childDef = (ProtoChildDefinition) childDefition;
			if (super.childDescription.get(childDef.childIndex) == childDef) {
				ProtoLayoutDef layout = line.getLayout();
				List<ProtoLine> lineList = getLines(childDef.childIndex);

				//DynamicMessage.newBuilder(layout.getRecord(childDef.recordIndex).getProtoDesc());
				ret = new ProtoLine(
						line, childDef, location, childDef.recordIndex,
						ProtoHelper.getBuilder(layout.getRecord(childDef.recordIndex).getProtoDesc())
//						layout.getRecord(childDef.recordIndex).getProtoDesc().toProto().toBuilder()
				);
				//		layout, layout.getRecord(childDef.recordIndex).getProtoDesc().toProto().toBuilder());
				ret.getTreeDetails().setParentLine(line, location);

				if (location < 0 || location >= lineList.size()) {
					lineList.add(ret);
					ret.getTreeDetails().setParentLine(line, lineList.size() - 1);
				} else {
					lineList.add(location, ret);
				}

				rebuildRepeatedField(childDef, lineList);
			}
		}

		return ret;
	}
	/**
	 * @see net.sf.JRecord.Details.AbstractLine#removeChild(net.sf.JRecord.Details.AbstractLine)
	 */
	@Override
	public void removeChild(AbstractLine child) {
		if (super.line != child.getTreeDetails().getParentLine()) {
			System.out.println("->> " + line.getFullLine());
			System.out.println("->> " + child.getTreeDetails().getParentLine());
			Common.logMsgRaw(
					  LangConversion.convert("Could not remove child, it is not present:")
					+ " \n" + child.getFullLine()  + "\n", null);
			return;
		}

		if (! (child instanceof ProtoLine)) {
			Common.logMsg("Could not remove child, it is not a Protocol Buffer Line: ", null);
			return;
		}

		ProtoLine childLine = (ProtoLine) child;
		ProtoChildDefinition childD = childLine.getTreeDetails().getChildDefinitionInParent();
		//AbstractLineNode node= (AbstractLineNode) child.getTreeDetails().getTreeNode();
		List<ProtoLine> childList = getLines(childD.childIndex);
		if (childD.isRepeated()) {
			if (childList.remove(child)) {
				rebuildRepeatedField(childD,  childList);
			}
		} else if (! childD.isRequired()) {
			if (childList.remove(child)) {
				line.getBuilder().clearField(childD.getFieldDefinition());
			}
		} else {
			Common.logMsgRaw(
					LangConversion.convert("Could not remove child, it is Required:")
						+ "\n"+ child.getFullLine() + "\n",
					null);
		}
	}



	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.TreeDetails#removeChildren(net.sf.JRecord.Details.AbstractChildDetails)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean removeChildren(AbstractChildDetails childDef) {
		boolean ret = false;
		if (childDef instanceof ProtoChildDefinition) {
			line.getBuilder().clearField(((ProtoChildDefinition) childDef).getFieldDefinition());
			super.removeChildren(childDef);
			ret = true;
		}

		return ret;
	}


	/**
	 * Rebuild  a repeated field if required
	 * @param childDefinition
	 * @param childList
	 */
	public void rebuildRepeatedField(ProtoChildDefinition childDefinition, List<ProtoLine> childList) {
		try {
			ProtoLine c;
			Message.Builder build = line.getBuilder().clone();

			rebuildChildren[childDefinition.childIndex] = true;
			build.clearField(childDefinition.getFieldDefinition());
			for (int i = 0; i < childList.size(); i++) {
				c = childList.get(i);
				build.addRepeatedField(childDefinition.getFieldDefinition(), c.getMsg());
				c.getTreeDetails().setParentIndex(i);
			}

			line.setBuilder(build);
			rebuildChildren[childDefinition.childIndex] = false;
		} catch (Exception e) {
			System.out.println("Child Rebuild failed: " + e.getMessage());
		}
	}

	/**
	 * Check if field repeated child needs to be rebuilt
	 * @param childIdx child index
	 * @return wether rebuild is required
	 */
	public final boolean isRebuildChildren(int childIdx) {
		return rebuildChildren[childIdx];
	}

//	/**
//	 * @see net.sf.JRecord.Details.TreeDetails#isError()
//	 */
//	@Override
//	public boolean isError() {
//		if (error || ! line.getBuilder().isInitialized()) {
//			return true;
//		}
//
//		boolean ret = false;
//		for (int i = 0; i< rebuildChildren.length; i++) {
//			if (rebuildChildren[i]) {
//				ret = true;
//				break;
//			}
//		}
//
//		return ret;
//	}

}
