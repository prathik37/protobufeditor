package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

import net.sf.JRecord.Common.FieldDetail;
import net.sf.RecordEditor.utils.swing.Combo.ComboModelSupplier;


public class ProtoFieldDef extends FieldDetail implements ComboModelSupplier {

	private FieldDescriptor protoField;
	private String[] enumSymbols = null; 

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
				enumSymbols = new String[list.size() + 1];
				enumSymbols[0] = "";
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

//	private static class EnumComboModel  implements ComboBoxModel {
//		private FieldDescriptor protoField;
//		private int currIdx = 0;
//		private int firstDataRow = 1;
//		private EventListenerList listenerList = new EventListenerList();
//		private List<EnumValueDescriptor> list;
//		
//		public EnumComboModel(FieldDescriptor protoFieldDef) {
//			protoField = protoFieldDef;
//			
//			list = protoField.getEnumType().getValues();
//			if (protoFieldDef.isRequired()) {
//				firstDataRow = 0;
//			}
//		}
//		
//
//		/**
//		 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
//		 */
//		public void setSelectedItem(Object arg0) {
//
//			if (Common.isEmpty(arg0)) {
//				System.out.println("Empty Combo ... ");
//				currIdx = 0;
//				tellOfUpdates();
//			} else {
//				try {
//					currIdx = protoField.getEnumType().findValueByName(arg0.toString()).getIndex()
//							+ firstDataRow;
//		
//				    tellOfUpdates();
//				} catch (Exception e) {
//					currIdx = 0;
//					tellOfUpdates();
//				}
//			}  
//			System.out.println("## Set ComboItem -->" + arg0 + "<-- " + currIdx);
//		}
//
//
//		/**
//		 * @see javax.swing.ComboBoxModel#getSelectedItem()
//		 */
//		public Object getSelectedItem() {
//			return getElementAt(currIdx); 
//		}
//
//		/**
//		 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
//		 */
//		public void addListDataListener(ListDataListener l) {
//
//		    listenerList.add(ListDataListener.class, l);
//		}
//
//
//		/**
//		 * Set the current index
//		 *
//		 * @see idx new current index
//		 */
//		public void setIndex(int idx) {
//			currIdx = idx;
//		}
//
//
//		/* (non-Javadoc)
//		 * @see javax.swing.ListModel#getElementAt(int)
//		 */
//		@Override
//		public Object getElementAt(int index) {
//			if (index < firstDataRow) {
//				return "";
//			}
//			return list.get(index - firstDataRow).getName();
//		}
//
//
//		/* (non-Javadoc)
//		 * @see javax.swing.ListModel#getSize()
//		 */
//		@Override
//		public int getSize() {
//			return list.size() + firstDataRow;
//		}
//
//
//		/**
//		 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
//		 */
//		public void removeListDataListener(ListDataListener l) {
//			listenerList.remove(ListDataListener.class, l);
//		}
//
//
//		/**
//		 * Tell all listners of updates
//		 */
//		private void tellOfUpdates() {
//
//			Object[] listeners = listenerList.getListenerList();
//
//			if (listeners.length > 0) {
//				ListDataEvent le = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, currIdx);
//
//				for (int i = listeners.length - 1; i >= 0; i -= 1) {
//					if (listeners[i] == ListDataListener.class) {
//						((ListDataListener) listeners[i + 1]).contentsChanged(le);
//					}
//	            }
//	        }
//		}
//	}
}
