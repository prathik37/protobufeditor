package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import net.sf.JRecord.Details.AbstractChildDetails;

import com.google.protobuf.Descriptors.FieldDescriptor;

public class ProtoChildDefinition implements AbstractChildDetails<ProtoRecordDef> {


	private FieldDescriptor fieldDefinition;
	private ProtoRecordDef recordDefinition;
	public final int recordIndex;
	public int childIndex;

	public ProtoChildDefinition(FieldDescriptor fieldDef,
			ProtoRecordDef recordDef, int recordIdx, int childIdx) {

		fieldDefinition = fieldDef;
		recordDefinition = recordDef;
		recordIndex = recordIdx;
		childIndex= childIdx;
	}

	/**
	 * @see net.sf.JRecord.Details.AbstractChildDetails#getChildRecord()
	 */
	@Override
	public ProtoRecordDef getChildRecord() {
		return recordDefinition;
	}

	/**
	 * @see net.sf.JRecord.Details.AbstractChildDetails#getName()
	 */
	@Override
	public String getName() {
		return fieldDefinition.getName();
	}

	/**
	 * @see net.sf.JRecord.Details.AbstractChildDetails#isRepeated()
	 */
	@Override
	public boolean isRepeated() {
		return fieldDefinition.isRepeated();
	}

	/**
	 * @see net.sf.JRecord.Details.AbstractChildDetails#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return fieldDefinition.isRequired();
	}

	public FieldDescriptor getFieldDefinition() {
		return fieldDefinition;
	}


	/**
	 * @return the recordIndex
	 */
	public final int getRecordIndex() {
		return recordIndex;
	}

	/**
	 * @return the childIndex
	 */
	public final int getChildIndex() {
		return childIndex;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return fieldDefinition.getName();
	}

	public boolean isMap() {
		return false;
	}
}
