package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import com.google.protobuf.Descriptors.FieldDescriptor;

import net.sf.JRecord.Common.FieldDetail;

public class ProtoFieldDef extends FieldDetail {

	private FieldDescriptor protoField;
	public ProtoFieldDef(int pPosition, int type, 
			String font, int format, String paramater,
			FieldDescriptor protoFieldDef) {
		super(protoFieldDef.getName(), protoFieldDef.getFullName(), type, 0, font, format, paramater);
		setPosOnly(pPosition);
		protoField = protoFieldDef;
	}

	/**
	 * @return the protoField
	 */
	public final FieldDescriptor getProtoField() {
		return protoField;
	}

}
