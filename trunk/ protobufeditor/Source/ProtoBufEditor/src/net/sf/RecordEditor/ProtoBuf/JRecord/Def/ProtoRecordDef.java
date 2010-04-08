package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.util.List;


import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLayoutDetails;
import net.sf.JRecord.Details.AbstractRecordDetail;
import net.sf.JRecord.Details.BasicRecordDetail;
import net.sf.JRecord.Types.Type;

public class ProtoRecordDef 
extends BasicRecordDetail<ProtoFieldDef, ProtoRecordDef, ProtoChildDefinition>
implements AbstractRecordDetail<ProtoFieldDef> {

	private Descriptor protoDesc;

	protected ProtoRecordDef(Descriptor descriptor,  ProtoFieldDef[] newFields) {
		protoDesc = descriptor;
		fields = newFields;
		fieldCount = fields.length;
	}

	public ProtoRecordDef(Descriptor descriptor) {
		protoDesc = descriptor;
		List<FieldDescriptor> protoFields = protoDesc.getFields();
		int i;
		int count = 0;
		int type;

		for (FieldDescriptor fd : protoFields) {
			if (! isChild(fd)) {
				count += 1;
			}
		}

		fields = new ProtoFieldDef[count];
		i = 0;
		for (FieldDescriptor fd : protoFields) {
			if (! isChild(fd)) {
				type = Type.ftProtoField;
				if (fd.isRepeated()) {
					type = Type.ftArrayField;
				} else if (fd.getType() == com.google.protobuf.Descriptors.FieldDescriptor.Type.ENUM) {
					type = Type.ftComboItemField;
				}
				fields[i++] = new ProtoFieldDef(i, type, "",0, "",fd);
			}
		}
		fieldCount = count;
	}

	@Override
	public void addField(ProtoFieldDef field) {
		throw new ProtoRecordException("Can not add fields");
	}

	@Override
	public String getDelimiter() {
		return "";
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

	@Override
	public ProtoFieldDef getSelectionField() {
		return null;
	}

	@Override
	public String getSelectionValue() {
		return null;
	}

	@Override
	public int[] getWidths() {
		return null;
	}

	@Override
	public boolean isBinary(int fldNum) {
		return false;
	}

	@Override
	public void setSelectionField(ProtoFieldDef newSelectionField) {
	}
	
	/**
	 * @return the protoDesc
	 */
	public final Descriptor getProtoDesc() {
		return protoDesc;
	}

	protected void setChildRecords(AbstractLayoutDetails<ProtoFieldDef, ProtoRecordDef> layout) {
		List<FieldDescriptor> protoFields = protoDesc.getFields();
		int i, j;
		int count = 0;
		for (FieldDescriptor fd : protoFields) {
			if (isChild(fd)) {
				count += 1;
			}
		}

		childRecords = new ProtoChildDefinition[count];
		i = 0;
		for (FieldDescriptor fd : protoFields) {
			if (isChild(fd)) {
				for (j = 0; j < layout.getRecordCount(); j++) {
//					System.out.println(" ==> " + fd.getFullName() + " > "
//							+ fd.getMessageType().getFullName() + " > "
//							+ layout.getRecord(j).getProtoDesc().getFullName());
					if (fd.getMessageType().equals(layout.getRecord(j).getProtoDesc())) {
						childRecords[i] = new ProtoChildDefinition(fd, layout.getRecord(j), j, i);
						i += 1;
						break;
					}
				}
			}
		}
	}
	
	private boolean isChild(FieldDescriptor fd) {
		return fd.getJavaType().equals(JavaType.MESSAGE);
	}
}
