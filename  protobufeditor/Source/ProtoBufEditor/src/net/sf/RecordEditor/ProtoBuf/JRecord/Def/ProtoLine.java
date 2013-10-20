package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.awt.Color;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.IFieldDetail;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.Common.RecordRunTimeException;
import net.sf.JRecord.Details.AbstractLayoutDetails;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.AbstractTreeDetails;
import net.sf.JRecord.Details.BaseLine;
import net.sf.JRecord.Details.NullTreeDtls;
import net.sf.JRecord.Log.AbsSSLogger;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.swing.Combo.ColorItem;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;



/**
 * implementation of LineInterface for ProtoBuffer lines.
 *
 * @author Bruce Martin
 *
 */
public class ProtoLine extends BaseLine<ProtoLayoutDef> implements AbstractLine {

//	public final static byte[] EMPTY_BYTE_ARRAY = {};
	private final static AbstractTreeDetails<ProtoRecordDef.ProtoFieldDef, ProtoRecordDef, ProtoLayoutDef, ProtoLine>
				NULL_TREE_DETAILS
					= new NullTreeDtls<ProtoRecordDef.ProtoFieldDef, ProtoRecordDef, ProtoLayoutDef, ProtoChildDefinition, ProtoLine>();
	public static final Color DESCRIPTOR_COLOR = new Color(220, 220, 255);
	public static final Color ENUM_COLOR = new Color(220, 255, 220);

	private Message.Builder bld;
	private int layoutIdx;

	private ProtoTreeDetails treeDtls = null;

	private byte[] data = null;

	public ProtoLine(ProtoLine parent, ProtoChildDefinition childDefinition, int layoutIndex, Message msg) {
		this(parent, childDefinition, -1,  layoutIndex,  msg);
		//System.out.println("--## 1");
	}

	public ProtoLine(
			ProtoLine parent,
			ProtoChildDefinition childDefinition,
			int index,
			int layoutIndex,
			Message msg) {
		this(	parent,
				childDefinition,
				index,
				layoutIndex,
				msg.newBuilderForType().mergeFrom(msg));
	}

	public ProtoLine(
			ProtoLine parent,
			ProtoChildDefinition childDefinition,
			int index,
			int layoutIndex,
			Message.Builder builder) {
		this(parent.getLayout(), layoutIndex, childDefinition);
		//System.out.println("--## 3");
		bld = builder;

		if (layout.hasChildren()) {
			treeDtls = new ProtoTreeDetails(this, childDefinition);
			getTreeDetails().setParentLine(parent, index);
		}
	}

	public ProtoLine(ProtoLayoutDef layoutDescription, int layoutIndex, Message.Builder builder) {
		this(layoutDescription, layoutIndex, (ProtoChildDefinition) null);
		//System.out.println("--## 4");
		bld = builder;
	}

	public ProtoLine(ProtoLayoutDef layoutDescription, int layoutIndex, byte[] bytes) {
		this(layoutDescription, layoutIndex, (ProtoChildDefinition) null);
		//System.out.println("--## 5");
		data = bytes;
	}

	private ProtoLine(ProtoLayoutDef layoutDescription, int layoutIndex, ProtoChildDefinition childDefinition) {
		super(layoutDescription);
		//System.out.println("--## 6");
		//layout = layoutDescription;
		layoutIdx = layoutIndex;

		if (layout.hasChildren()) {
			treeDtls = new ProtoTreeDetails(this, childDefinition);
		}
	}

	public ProtoLine(ProtoLayoutDef layoutDescription, Message.Builder builder) {
		super(layoutDescription);
		//System.out.println("--## 7");
		//layout = layoutDescription;
		bld = builder;
		layoutIdx = 0;

		for (int i = 0; i < layout.getRecordCount(); i++) {
			if (builder.getDescriptorForType().equals(layout.getRecord(i).getProtoDesc())) {
				layoutIdx = i;
				break;
			}
		}

		if (layout.hasChildren()) {
			treeDtls = new ProtoTreeDetails(this, null);
		}
	}


	@Override
	public AbstractTreeDetails<ProtoRecordDef.ProtoFieldDef, ProtoRecordDef, ProtoLayoutDef, ProtoLine> getTreeDetails() {
		if (treeDtls == null) {
			return NULL_TREE_DETAILS;
		}
		return treeDtls;
	}

	@Override
	public byte[] getData(int start, int len) {
		return null;
	}

	@Override
	public byte[] getData() {
		if (data != null) {
			return data;
		} else if (getBuilder().isInitialized()) {
			return getMsg().toByteArray();
		}
		return ConstClass.EMPTY_BYTE_ARRAY;
	}

	@Override
	public Object getField(int recordIdx, int fieldIdx) {
		if (layout.isProtoDefinition()
		&& recordIdx != layoutIdx  && fieldIdx == 0
		&& layout.getRecord(layoutIdx).getFieldCount() > 0) {
			ColorItem item = new ColorItem();
			item.data =  getField(layout.getField(layoutIdx, fieldIdx));
			item.color = Color.WHITE;
			if ("FileDescriptorProto".equalsIgnoreCase(layout.getRecord(layoutIdx).getRecordName())) {
				item.color = Color.YELLOW;
			} else if ("DescriptorProto".equalsIgnoreCase(layout.getRecord(layoutIdx).getRecordName())) {
				item.color = DESCRIPTOR_COLOR;
			} else if ("EnumDescriptorProto".equalsIgnoreCase(layout.getRecord(layoutIdx).getRecordName())) {
				item.color = ENUM_COLOR;
			}
			return item;
		} else if (fieldIdx == Constants.KEY_INDEX
		||  recordIdx != layoutIdx
		||  fieldIdx >= layout.getRecord(layoutIdx).getFieldCount()) {
			return null;
		}

		return getField(layout.getField(recordIdx, fieldIdx));
	}

	private Object getFieldProto(FieldDescriptor field, ProtoRecordDef.ProtoFieldDef reField) {

		try {
			if (field.isRepeated()) {
				return new ArrayDetails(this, reField);
			}
			if (getBuilder().hasField(field)) {
				Object value = getBuilder().getField(field);
	//			if (field.getType() == Type.ENUM && field.isOptional()) {
	//				System.out.println("--- Enum    value: " + value + " " + getBuilder().hasField(field));
	//				System.out.println("--- Adjusted value: " + ProtoHelper.getAdjustedFieldValue(value, field));
	//			}
				return ProtoHelper.getAdjustedFieldValue(value, field);
			}
		} catch (Exception e) {
			System.out.println("## Error >> " + layoutIdx + " " + layout.getRecord(layoutIdx).getRecordName() + " "
					+ bld.getDescriptorForType().getFullName());
			e.printStackTrace();

			return null;
		}

		if (field.isRequired()) {
			return Common.MISSING_REQUIRED_VALUE;
		}

		return Common.MISSING_VALUE;
	}


	@Override
	public Object getField(IFieldDetail field) {
		if (field == null ) {
			//System.out.println("Null Field");
			return null;
		} else if (field instanceof ProtoRecordDef.ProtoFieldDef) {
			return getField((ProtoRecordDef.ProtoFieldDef) field);
		}

		return null;
	}

	public Object getField(ProtoRecordDef.ProtoFieldDef field) {
		if (field == null) {
			return null;
		}
		return getFieldProto(field.getProtoField(), field);
	}

	@Override
	public byte[] getFieldBytes(int recordIdx, int fieldIdx) {
		return null;
	}

	@Override
	public String getFieldHex(int recordIdx, int fieldIdx) {
		return null;
	}

	@Override
	public String getFieldText(int recordIdx, int fieldIdx) {
		if (recordIdx != layoutIdx || fieldIdx >= layout.getRecord(layoutIdx).getFieldCount()) {
			return null;
		}
		Object o = getField(layout.getField(recordIdx, fieldIdx));
		String ret = null;
		if (o != null) {
			ret = o.toString();
		}

		return ret;
	}



	@Override
	public String getFullLine() {
		StringBuilder b = new StringBuilder();
		ProtoRecordDef recordDef = layout.getRecord(layoutIdx);
		Message.Builder build = getBuilder();
		FieldDescriptor protoFieldDesc;
		Object val;

		for (int i = 0; i < recordDef.getFieldCount(); i++) {
			protoFieldDesc = recordDef.getField(i).getProtoField();

			if (protoFieldDesc.isRepeated()) {
				String sep = "{";
				for (int j =0; j < build.getRepeatedFieldCount(protoFieldDesc); j++) {
					b.append(sep).append(
							ProtoHelper.getAdjustedFieldValue(
									build.getRepeatedField(protoFieldDesc, j),
									protoFieldDesc
									));
					sep = ", ";
				}
				b.append("}\t");
			} else if (build.hasField(protoFieldDesc)) {
				val = build.getField(protoFieldDesc);
				//if (protoFieldDesc.getType() == Type.ENUM) {
				val = ProtoHelper.getAdjustedFieldValue(val, protoFieldDesc);
				//}
				b.append(val).append('\t');
			} else {
				b.append('\t');
			}
		}
		return b.toString();
	}


	@Override
	public int getPreferredLayoutIdx() {
		return getPreferredLayoutIdxAlt();
	}

	@Override
	public int getPreferredLayoutIdxAlt() {
		return layoutIdx;
	}

	@Override
	public void replace(byte[] rec, int start, int len) {

	}

	@Override
	public void setData(String newVal) {

	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLine#setData(byte[])
	 */
	@Override
	public void setData(byte[] newVal) {

	}


	@Override
	public void setField(int recordIdx, int fieldIdx, Object val)
			throws RecordException {
		if (recordIdx == layoutIdx || fieldIdx < layout.getRecord(layoutIdx).getFieldCount()) {
			setField(layout.getField(recordIdx, fieldIdx), val);
		}
	}

	@Override
	public void setField(IFieldDetail field, Object value)
			throws RecordException {
		if (field != null && field instanceof ProtoRecordDef.ProtoFieldDef) {
			setField(((ProtoRecordDef.ProtoFieldDef) field).getProtoField(), value);
		}
	}


	public void setField(ProtoRecordDef.ProtoFieldDef field, Object value)
			throws RecordException {
		if (field != null) {
			setField(field.getProtoField(), value);
		}
	}

	@Override
	public String setFieldHex(int recordIdx, int fieldIdx, String val)
			throws RecordException {
		return null;
	}

	@Override
	public void setFieldText(int recordIdx, int fieldIdx, String value)
			throws RecordException {
		IProtoRecordDetails.FieldDetails field = layout.getField(recordIdx, fieldIdx);
		if (field != null) {
			setField(field.getProtoField(), value);
		}
	}




	private void setField(FieldDescriptor field, Object newValue) {

		if (Common.isEmpty(newValue)) {
			if  (field.isOptional()) {
				getBuilder().clearField(field);
			} else {
				throw new RecordRunTimeException("Field: {0} must be entered", field.getName());
			}
		} else {
//			if (newValue != null) {
//				System.out.println("setField: " + newValue + " "
//						+ newValue.getClass().getName()  +" " + field.getName());
//			}
			ProtoHelper.setField(getBuilder(), field, newValue);
		}
		updateParent();
	}


	protected void updateParent() {

		if (treeDtls != null && treeDtls.getParentLine() != null) {
			FieldDescriptor parentFld = treeDtls.getChildDefinitionInParent().getFieldDefinition();
			Message msg;
			try {
				msg = bld.clone().build();
			} catch (Exception e) {
				Common.logMsg(
						AbsSSLogger.WARNING,
						"Error updating Message in parent (missing required fields ???):", e.getMessage(),
						null);
				return;
			}
			if (parentFld.isRepeated()) {
				if (treeDtls.getParentLine().getTreeDetails() instanceof ProtoTreeDetails) {
					ProtoTreeDetails parentDtl = (ProtoTreeDetails) treeDtls.getParentLine().getTreeDetails();

					ProtoChildDefinition def = treeDtls.getChildDefinitionInParent();
					if (parentDtl.isRebuildChildren(def.childIndex)) {
						parentDtl.rebuildRepeatedField(
								def,
								parentDtl.getLines(def.getChildIndex()));
						//		    			   if (parentDtl.isRebuildChildren(def.childIndex)) {
						//		    				   Common.logMsg("Incomplete record has prevented full updates being made", null);
						//		    			   }
					} else {
						treeDtls.getParentLine().getBuilder().setRepeatedField(
								parentFld, treeDtls.getParentIndex(), msg);
					}
				} else {
					treeDtls.getParentLine().getBuilder().setRepeatedField(
							parentFld, treeDtls.getParentIndex(), msg);
				}
			} else {
				treeDtls.getParentLine().getBuilder().setField(parentFld, msg);
			}
			treeDtls.getParentLine().updateParent();
		}
	}



	@Override
	public void setLayout(AbstractLayoutDetails newLayout) {
//		System.out.print(" --> " + layoutIdx  + " " + layout.getRecord(layoutIdx).getRecordName());

		layoutIdx = newLayout.getRecordIndex(getBuilder().getDescriptorForType().getName());
//		System.out.println("  --> " + layoutIdx + " " + newLayout.getRecord(layoutIdx).getRecordName()
//				+ " " + getBuilder().getDescriptorForType().getFullName());
		layout = (ProtoLayoutDef) newLayout;
	}


	@Override
	public void setWriteLayout(int writeLayout) {

	}


	public ProtoLine clone() {
		ProtoLine ret;

		if (treeDtls == null || treeDtls.getParentLine() == null) {
			ret = new ProtoLine(layout, layoutIdx, getBuilder().clone());
		} else {
			ret = new ProtoLine(treeDtls.getParentLine(), treeDtls.getChildDefinitionInParent(), -1, layoutIdx, getBuilder().clone());
		}
		return ret;
	}

	public final Message getMsg() {
		return getBuilder().clone().build();
	}

	public final Message.Builder getBuilder() {
		if (data != null || bld == null) {
			synchronized (data) {
				if (data != null || bld == null) {
					try {
						bld = DynamicMessage.newBuilder(layout.getRecord(layoutIdx).getProtoDesc());

						if (data != null) {
							bld.mergeFrom(data, layout.getRegistry());
							data = null;
						} else {
							ProtoHelper.initBuilder(bld, layout.getRecord(layoutIdx).getProtoDesc());
						}
					} catch (Exception e) {
						String s ="Error with data: " + e.getMessage();
						Common.logMsg(AbsSSLogger.WARNING, s, null);
						System.out.println(s);
					}
				}
			}
		}
		return bld;
	}

	/**
	 * @param bld the bld to set
	 */
	public final void setBuilder(Message.Builder build) {
		this.bld = build;
		if (build != null) {
			data = null;
		}

		updateParent();
	}

	/**
	 * @see net.sf.JRecord.Details.TreeDetails#isError()
	 */
	@Override
	public boolean isError() {
		return ! getBuilder().isInitialized();
	}

	/**
	 * @see net.sf.JRecord.Details.AbstractLine#getNewDataLine()
	 */
	@Override
	public ProtoLine getNewDataLine() {
		return clone();
	}


}
