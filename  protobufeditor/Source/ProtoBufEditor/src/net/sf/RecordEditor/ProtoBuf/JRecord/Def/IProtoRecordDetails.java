package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import javax.swing.ComboBoxModel;

import net.sf.JRecord.Details.AbstractRecordDetail;
import net.sf.RecordEditor.utils.swing.Combo.ComboModelSupplier;

import com.google.protobuf.Descriptors.FieldDescriptor;

public interface IProtoRecordDetails extends AbstractRecordDetail {

	public interface FieldDetails extends  AbstractRecordDetail.FieldDetails, ComboModelSupplier {


		/* (non-Javadoc)
		 * @see net.sf.RecordEditor.ProtoBuf.JRecord.Def.ComboModelSupplier#getComboModel()
		 */
		public ComboBoxModel getComboModel();

		/**
		 * @return the protoField
		 */
		public  FieldDescriptor getProtoField();

	}

}
