package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.FieldDetail;
import net.sf.JRecord.Details.BasicRecordDetail;
import net.sf.JRecord.Details.RecordDetail;
import net.sf.JRecord.Types.Type;
import net.sf.RecordEditor.re.jrecord.format.CellFormat;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.swing.Combo.ComboModelSupplier;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;

public class ProtoRecordDef
extends BasicRecordDetail<ProtoRecordDef.ProtoFieldDef, ProtoRecordDef, ProtoChildDefinition>
implements IProtoRecordDetails {

	private Descriptor protoDesc;

	protected ProtoRecordDef(Descriptor descriptor,  ProtoFieldDef[] newFields) {
		protoDesc = descriptor;
		fields = newFields;
		fieldCount = fields.length;
	}

	public ProtoRecordDef(Descriptor descriptor, boolean highlightProto) {
		protoDesc = descriptor;
		List<FieldDescriptor> protoFields = protoDesc.getFields();
		ArrayList<ProtoFieldDef> fieldList = new ArrayList<ProtoFieldDef>(protoFields.size());

		addFields(fieldList, protoFields,     highlightProto);

		fields = fieldList.toArray(new ProtoFieldDef[fieldList.size()]);
		fieldCount = fields.length;

		System.out.println();
		System.out.println(protoDesc.getName() + "\t" + fields.length);

//		if (extensions != null) {
//
//		}
//
//		for (FieldDescriptor fd : protoFields) {
//			if (! isChild(fd)) {
//				count += 1;
//			}
//		}
//
//		fields = new ProtoFieldDef[count];
//		i = 0;
//		for (FieldDescriptor fd : protoFields) {
//			if (! isChild(fd)) {
//				format = 0;
//				type = Type.ftProtoField;
//				System.out.print("\t" + fd.getType());
//				if (fd.isRepeated()) {
//					type = Type.ftArrayField;
//				} else if (fd.getType() == com.google.protobuf.Descriptors.FieldDescriptor.Type.ENUM) {
//					type = Type.ftComboItemField;
//				} else if (fd.getType() == com.google.protobuf.Descriptors.FieldDescriptor.Type.BOOL) {
//					type = Type.ftCheckBoxBoolean;
//				} else if (i == 0 && highlightProto && fd.getName().equalsIgnoreCase("name")) {
//					format = CellFormat.FMT_COLOR;
//				}
//				//System.out.println(" -- " + i + " " + highlightProto + " " + fd.getName() + " " + format);
//				fields[i++] = new ProtoFieldDef(i, type, "", format, "", fd);
//			}
//		}
//		System.out.println();
//		System.out.println(protoDesc.getName() + "\t" + fields.length);
//		fieldCount = count;
	}


	private void addFields(List<ProtoFieldDef> fieldList, List<FieldDescriptor> fields, boolean highlightProto) {
		int i = 1;

		if (fields != null) {
			for (FieldDescriptor fld : fields) {
				if (! isChild(fld)) {
					fieldList.add(newField(i++, highlightProto, fld));
				}
			}
		}
	}


	public void addField(FieldDescriptor fld) {

		ProtoFieldDef field = newField(fieldCount, false, fld);
	    if (fieldCount >= fields.length) {
	    	FieldDetail[] temp = fields;
	        fields = new ProtoRecordDef.ProtoFieldDef[fieldCount + 5];
	        System.arraycopy(temp, 0, fields, 0, temp.length);
	        fieldCount = temp.length;
	    }
	    field.setRecord(this);
	    fields[fieldCount] = field;
	    fieldCount += 1;
	    //numberOfFieldsAdded += 1;
	}

	private ProtoFieldDef newField(int pos, boolean highlightProto, FieldDescriptor fld) {
		int format = 0;
		int type = Type.ftProtoField;
		//System.out.print("\t" + fld.getType());
		if (fld.isRepeated()) {
			type = Type.ftArrayField;
		} else if (fld.getType() == com.google.protobuf.Descriptors.FieldDescriptor.Type.ENUM) {
			type = Type.ftComboItemField;
		} else if (fld.getType() == com.google.protobuf.Descriptors.FieldDescriptor.Type.BOOL) {
			type = Type.ftCheckBoxBoolean;
		} else if (pos == 0 && highlightProto && fld.getName().equalsIgnoreCase("name")) {
			format = CellFormat.FMT_COLOR;
		}
		//System.out.println(" -- " + i + " " + highlightProto + " " + fd.getName() + " " + format);
		return new ProtoFieldDef(pos, type, "", format, "", fld);
	}

//	@Override
//	public void addField(ProtoFieldDef field) {
//		throw new ProtoRecordException("Can not add fields");
//	}

	@Override
	public String getDelimiter() {
		return "";
	}


	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractRecordDetail#getFieldTypeName(int)
	 */
	@Override
	public String getFieldTypeName(int idx) {
		ProtoFieldDef fld = getField(idx);
		return fld.protoField.getType().name();
	}

	@Override
	public int getFieldsNumericType(int idx) {
		JavaType type = fields[idx].getProtoField().getJavaType();

		if (JavaType.DOUBLE.equals(type)
	       || JavaType.FLOAT.equals(type)
	       || JavaType.INT.equals(type)
	       || JavaType.LONG.equals(type)) {
			return Type.NT_NUMBER;
		}
	       ;
		return Type.NT_TEXT;
	}

	@Override
	public String getFontName() {
		return "";
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return -1;
	}


	@Override
	public String getQuote() {
		return null;
	}

	@Override
	public String getRecordName() {
		return protoDesc.getName();
	}

	@Override
	public int getRecordStyle() {
		return 0;
	}

	@Override
	public int getRecordType() {
		return Constants.rtProtoRecord;
	}

//	//TODO Remove this method
//	@Deprecated
//	//@Override
//	public ProtoFieldDef getSelectionField() {
//		return null;
//	}

//	@Override
//	public String getSelectionValue() {
//		return null;
//	}

	@Override
	public int[] getWidths() {
		return null;
	}

	@Override
	public boolean isBinary(int fldNum) {
		return false;
	}

//	@Override
//	public void setSelectionField(ProtoFieldDef newSelectionField) {
//	}

	/**
	 * @return the protoDesc
	 */
	public final Descriptor getProtoDesc() {
		return protoDesc;
	}

	protected void setChildRecords(ProtoLayoutDef layout) {


		List<FieldDescriptor> fields = protoDesc.getFields();
		if (fields != null) {
//			int i = 0;
			for (FieldDescriptor fd : fields) {
				if (isChild(fd)) {
					addChildRecord(layout, fd);
//					for (int j = 0; j < layout.getRecordCount(); j++) {
//						if (fd.getMessageType().equals(layout.getRecord(j).getProtoDesc())) {
//							childRecords.add(new ProtoChildDefinition(fd, layout.getRecord(j), j, i));
//							i += 1;
//							break;
//						}
//					}
				}
			}
		}

		layout.addExtensions(protoDesc.getExtensions());

		//childList = addChildRecords(protoDesc.getExtensions(), layout);
//		extensionRecords = childList.toArray(new ProtoChildDefinition[childList.size()]);


//		List<FieldDescriptor> protoFields = protoDesc.getFields();
//		int i, j;
//		int count = 0;
//		for (FieldDescriptor fd : protoFields) {
//			if (isChild(fd)) {
//				count += 1;
//			}
//		}
//
////		boolean found;
//		childRecords = new ProtoChildDefinition[count];
//		i = 0;
//		for (FieldDescriptor fd : protoFields) {
//			if (isChild(fd)) {
////				found = false;
//				for (j = 0; j < layout.getRecordCount(); j++) {
////					System.out.println(" ==> " + fd.getFullName() + " > "
////							+ fd.getMessageType().getFullName() + " > "
////							+ layout.getRecord(j).getProtoDesc().getFullName()
////							+ " " + (fd.getMessageType().equals(layout.getRecord(j).getProtoDesc())));
//					if (fd.getMessageType().equals(layout.getRecord(j).getProtoDesc())) {
////						System.out.println(" ==> " + fd.getFullName() + " > "
////								+ fd.getMessageType().getFullName() + " > "
////								+ layout.getRecord(j).getProtoDesc().getFullName()
////								+ " " + (fd.getMessageType().equals(layout.getRecord(j).getProtoDesc())));
//						childRecords[i] = new ProtoChildDefinition(fd, layout.getRecord(j), j, i);
////						found = true;
//						i += 1;
//						break;
//					}
//				}
////				if (! found) {
////					System.out.println(" --> " + fd.getFullName() + " > "
////							+ fd.getMessageType().getFullName());
////				}
//			}
//		}
	}


	protected void addChildRecord(ProtoLayoutDef layout, FieldDescriptor fd) {
		for (int j = 0; j < layout.getRecordCount(); j++) {
//			System.out.println(fd.getMessageType().getName() +  " ~ " + layout.getRecord(j).getProtoDesc().getName());
			if (fd.getMessageType().equals(layout.getRecord(j).getProtoDesc())) {
				if (childRecords == null) {
					childRecords = new ArrayList<ProtoChildDefinition>();
				}

				childRecords.add(
						new ProtoChildDefinition(fd,
								layout.getRecord(j), j,
								childRecords.size()));
				break;
			}
		}
	}

	protected static boolean isChild(FieldDescriptor fd) {
		return fd.getJavaType().equals(JavaType.MESSAGE);
	}

	public class ProtoFieldDef extends FieldDetail implements  IProtoRecordDetails.FieldDetails, ComboModelSupplier {

		private FieldDescriptor protoField;
		private Object[] enumSymbols = null;

		public ProtoFieldDef(int pPosition, int type,
				String font, int format, String paramater,
				FieldDescriptor protoFieldDef) {
			super(protoFieldDef.getName(), protoFieldDef.getFullName(), type, 0, font, format, paramater);
			setPosOnly(pPosition);
			protoField = protoFieldDef;

			if (protoField.getType()  == com.google.protobuf.Descriptors.FieldDescriptor.Type.ENUM) {
				List<EnumValueDescriptor> list = protoField.getEnumType().getValues();
				int inc = 0;
				if (protoFieldDef.isOptional()) {
					inc = 1;
					enumSymbols = new Object[list.size() + 1];
					enumSymbols[0] = Common.MISSING_VALUE;
				} else {
					enumSymbols = new String[list.size()];
				}

				for (int i = 0; i < list.size(); i++) {
					enumSymbols[i + inc] = list.get(i).getName();
				}
			}

		}

		/* (non-Javadoc)
		 * @see net.sf.RecordEditor.ProtoBuf.JRecord.Def.ComboModelSupplier#getComboModel()
		 */
		public final ComboBoxModel getComboModel() {
			if (enumSymbols == null) {
				return null;
			}
			return  new DefaultComboBoxModel(enumSymbols);
		}

		/**
		 * @return the protoField
		 */
		public final FieldDescriptor getProtoField() {
			return protoField;
		}

	}

}
