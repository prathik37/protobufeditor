package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.math.BigInteger;


import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

import net.sf.JRecord.Common.Conversion;
import net.sf.JRecord.Common.FieldDetail;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.Details.AbstractFieldValue;

import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.AbstractTreeDetails;
import net.sf.JRecord.Details.NullTreeDtls;
import net.sf.JRecord.Details.FieldValue;

import net.sf.JRecord.Details.LineProvider;
import net.sf.JRecord.Log.AbsSSLogger;
import net.sf.RecordEditor.utils.common.Common;
/**
 * implementation of LineInterface for ProtoBuffer lines.
 * 
 * @author Bruce Martin
 *
 */
public class ProtoLine implements AbstractLine<ProtoLayoutDef> {

//	public final static byte[] EMPTY_BYTE_ARRAY = {}; 
	private final static AbstractTreeDetails<ProtoFieldDef, ProtoRecordDef, ProtoLayoutDef, ProtoLine>
				NULL_TREE_DETAILS 
					= new NullTreeDtls<ProtoFieldDef, ProtoRecordDef, ProtoLayoutDef, ProtoChildDefinition, ProtoLine>();
	private Message.Builder bld;
	private ProtoLayoutDef layout;
	private int layoutIdx;

	private ProtoTreeDetails treeDtls = null;

	private byte[] data = null;
	
	public ProtoLine(ProtoLine parent, ProtoChildDefinition childDefinition, int layoutIndex, Message msg) {
		this(parent, childDefinition, -1,  layoutIndex,  msg);
	}
	
	public ProtoLine(
			ProtoLine parent, 
			ProtoChildDefinition childDefinition, 
			int index,
			int layoutIndex, 
			Message msg) {
		this(parent, 
				childDefinition, 
				index,
				layoutIndex, msg.newBuilderForType().mergeFrom(msg));
	}

	public ProtoLine(
			ProtoLine parent, 
			ProtoChildDefinition childDefinition, 
			int index,
			int layoutIndex, 
			Message.Builder builder) {
		this(parent.getLayout(), layoutIndex, childDefinition);
		bld = builder;

		if (layout.hasChildren()) {
			getTreeDetails().setParentLine(parent, index);
		}
	}

	public ProtoLine(ProtoLayoutDef layoutDescription, int layoutIndex, Message.Builder builder) {
		this(layoutDescription, layoutIndex, (ProtoChildDefinition) null);
		bld = builder;
	}
	
	public ProtoLine(ProtoLayoutDef layoutDescription, int layoutIndex, byte[] bytes) {
		this(layoutDescription, layoutIndex, (ProtoChildDefinition) null);
		
		data = bytes;
	}
	
	private ProtoLine(ProtoLayoutDef layoutDescription, int layoutIndex, ProtoChildDefinition childDefinition) {
		layout = layoutDescription;
		layoutIdx = layoutIndex;
		
		if (layout.hasChildren()) {
			treeDtls = new ProtoTreeDetails(this, childDefinition);
		}
	}

	public ProtoLine(ProtoLayoutDef layoutDescription, Message.Builder builder) {
		layout = layoutDescription;
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public AbstractTreeDetails<ProtoFieldDef, ProtoRecordDef, ProtoLayoutDef, ProtoLine> getTreeDetails() {
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
		if (recordIdx != layoutIdx || fieldIdx >= layout.getRecord(layoutIdx).getFieldCount()) {
			return null;
		}
		return getField(layout.getField(recordIdx, fieldIdx));
	}

	@Override
	public Object getField(FieldDetail field) {
		if (field == null ) {
			System.out.println("Null Field");
			return null;
		} else if (field instanceof ProtoFieldDef) {
			return getField((ProtoFieldDef) field);
		}
		
		return null;
	}

	public Object getField(ProtoFieldDef field) {
		if (field == null) {
			return null;
		}
		return getField(field.getProtoField());
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
		Object o = getField(layout.getField(recordIdx, fieldIdx));
		String ret = null;
		if (o != null) {
			ret = o.toString();
		}
		
		return ret;
	}


    @Override
	public AbstractFieldValue getFieldValue(FieldDetail field) {
		return new FieldValue(this, field);
	}



	@Override
	public AbstractFieldValue getFieldValue(int recordIdx, int fieldIdx) {
		return new FieldValue(this, recordIdx, fieldIdx);
	}



	@Override
	public AbstractFieldValue getFieldValue(String fieldName) {
		return  getFieldValue(layout.getFieldFromName(fieldName));
	}

	@Override
	public String getFullLine() {
		StringBuilder b = new StringBuilder();
		ProtoRecordDef recordDef = layout.getRecord(layoutIdx);
		Message.Builder build = getBuilder();
		FieldDescriptor protoFieldDesc;
		
		for (int i = 0; i < recordDef.getFieldCount(); i++) {
			protoFieldDesc = recordDef.getField(i).getProtoField();
		
			if (build.hasField(protoFieldDesc)) {
				b.append(build.getField(protoFieldDesc)).append('\t');
			} else {
				b.append('\t');
			}
		}
		return b.toString();
	}

	@Override
	public ProtoLayoutDef getLayout() {
		return layout;
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

	@Override
	public void setField(String fieldName, Object value) throws RecordException {

		setField(layout.getFieldFromName(fieldName), value);
	}

	@Override
	public void setField(int recordIdx, int fieldIdx, Object val)
			throws RecordException {
		setField(layout.getField(recordIdx, fieldIdx), val);
	}

	@Override
	public void setField(FieldDetail field, Object value)
			throws RecordException {
		if (field != null && field instanceof ProtoFieldDef) {
			setField(((ProtoFieldDef) field).getProtoField(), value);
		}
	}


	public void setField(ProtoFieldDef field, Object value)
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
		ProtoFieldDef field = layout.getField(recordIdx, fieldIdx);
		if (field != null) {
			setField(field.getProtoField(), value);
		}
	}

	private Object getField(FieldDescriptor field) {
//		System.out.println();
//		System.out.print(" --->> " + (field == null));
//		System.out.print(" ---> " +  getBuilder().getDescriptorForType().getFullName());
//		System.out.print(" ---> " + layoutIdx + "  >> " +  layout.getRecord(this.layoutIdx).getProtoDesc().getFullName());
		Object value =  getBuilder().getField(field);
		Object ret = value;
//		System.out.print(" ->" +  getBuilder().getDescriptorForType().getFullName() + " ~ ");
//		System.out.print(field.getFullName() + " : " + field.getType() + " :: >" + value.toString() + "<  " );
//		System.out.println( value.getClass().getName() + " (( ");
	    switch (field.getType()) {
           case INT32:
           case INT64:
           case SINT32:
           case SINT64:
           case SFIXED32:
           case SFIXED64:
           case FLOAT:
           case DOUBLE:
           case BOOL:
           case STRING:  
        	  break;

           case UINT32:
           case FIXED32:
               ret = unsignedToLong(((Integer) value).intValue());
               break;

           case UINT64:
           case FIXED64:
               ret = unsignedToNumber(((Long) value).longValue());
               break;

           case BYTES: {
        	   byte[] bytes = (byte[]) value;
               ret = Conversion.getDecimal(bytes , 0, bytes.length);
               break;
           }

           case ENUM: {
               ret = ((EnumValueDescriptor) value).getName();
               break;
           }

           case MESSAGE:
           case GROUP:
               ret = null;
               break;
        }
	    return ret;
	}





	private void setField(FieldDescriptor field, Object newValue) {
		Object value = newValue;

		long l;
		if (newValue == null && field.isOptional()) {
			getBuilder().setField(field, value);
		} else {
			switch (field.getType()) {
			case INT32:
			case SINT32:
			case SFIXED32:
				if (! (newValue instanceof Integer)) {
					value = Integer.valueOf(getNumber(newValue).intValue());
				}
				break;

			case INT64:
			case SINT64:
			case SFIXED64:
				if (! (newValue instanceof Long)) {
					value = Long.valueOf(getNumber(newValue).longValue());
				}
				break;

			case UINT32:
			case FIXED32:
				l = getNumber(newValue).longValue();
				if (l < 0) {
					throw new RuntimeException("must be >= 0");
				}
				value = Integer.valueOf((int) l);
				break;

			case UINT64:
			case FIXED64:
				l = getNumber(newValue).longValue();
				if (l < 0) {
					throw new RuntimeException("must be >= 0");
				}
				value = Long.valueOf(l);
				break;

			case FLOAT:
				if (! (newValue instanceof Float)) {
					value = Float.valueOf(getNumber(newValue).floatValue());
				}
				break;

			case DOUBLE:
				if (! (newValue instanceof Double)) {
					value = Double.valueOf(getNumber(newValue).doubleValue());
				}
				break;

			case BOOL:
				if (! (newValue instanceof Boolean)) {
					value = Boolean.FALSE;
					if (newValue != null) {
						String s = newValue.toString().toLowerCase();
						if ("yes".equals(s) || "true".equals(s) || "y".equals(s)) {
							value = Boolean.TRUE;
						}
					}
				}
				break;

			case STRING:
				if (newValue == null) {
					value ="";
				} else if (! (newValue instanceof String)) {
					value = newValue.toString();
				}
				break;

			case BYTES:
				byte[] b; 
				if (newValue == null) {
					b = ConstClass.EMPTY_BYTE_ARRAY;
				} else {
					String s = newValue.toString();
					b = new byte[s.length() / 2];
					for (int i = 0; i < b.length; i++) {
						b[i] = Byte.parseByte(s.substring(i * 2, i * 2 + 1));
					}
				}
				value = b;

				break;

			case ENUM: {
				if (newValue == null) {
					throw new RuntimeException("An enum must have a value");
				}
				Descriptors.EnumDescriptor enumType = field.getEnumType();

				boolean isNum = false;
				int number = 0;
				if (newValue instanceof Number) {
					isNum =true;
					number = ((Number) newValue).intValue();
				} else {
					try {
						number = Integer.parseInt(newValue.toString());
					} catch (Exception e) {
					}
				}
				if (isNum) {
					value = enumType.findValueByNumber(number);
					if (value == null) {
						throw new RuntimeException("Enum type \""
								+ enumType.getFullName()
								+ "\" has no value with number "
								+ number + ".");
					}
				} else {
					String id = newValue.toString();
					value = enumType.findValueByName(id);
					if (value == null) {
						throw new RuntimeException("Enum type \""
								+ enumType.getFullName()
								+ "\" has no value named \""
								+ id + "\".");
					}
				}

				break;
			}

			case MESSAGE:
			case GROUP:
				throw new RuntimeException("Can't get here.");
			}
			bld = getBuilder().setField(field, value);

			updateParent();
		}
	}


	private void updateParent() {

		if (treeDtls != null && treeDtls.getParentLine() != null) {
			FieldDescriptor parentFld = treeDtls.getChildDefinitionInParent().getFieldDefinition();
			Message msg;
			try {
				msg = bld.clone().build();
			} catch (Exception e) {
				Common.logMsg(
						AbsSSLogger.WARNING, 
						"Error updating Message in parent (missing required fields ???): " + e.getMessage(), 
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




	private Number getNumber(Object val) {
		if (val instanceof Number) {
			return (Number) val;
		} else if (val == null) {
			return Integer.valueOf(0);
		} else {
			return new BigInteger(val.toString());
		}
	}
    /**
     * Convert an unsigned 32-bit integer to a string.
     */
    private static Long unsignedToLong(int value) {
        if (value >= 0) {
            return Long.valueOf(value);
        } else {
            return Long.valueOf((value) & 0x00000000FFFFFFFFL);
        }
    }

    /**
     * Convert an unsigned 64-bit integer to a string.
     */
    private static Number unsignedToNumber(long value) {
        if (value >= 0) {
            return Long.valueOf(value);
        } else { 
            // Pull off the most-significant bit so that BigInteger doesn't
            // think
            // the number is negative, then set it again using setBit().
            return BigInteger.valueOf(value & 0x7FFFFFFFFFFFFFFFL).setBit(63);
        }
    }


	@Override
	public void setLayout(ProtoLayoutDef newLayout) {
		layoutIdx = newLayout.getRecordIndex(layout.getRecord(layoutIdx).getRecordName());
		layout = newLayout;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setLineProvider(LineProvider lineProvider) {
		// TODO Auto-generated method stub

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
				try {
					bld = DynamicMessage.newBuilder(layout.getRecord(layoutIdx).getProtoDesc());
					
					if (data != null) {
						bld.mergeFrom(data);
						data = null;
					}
				} catch (Exception e) {
					String s ="Error with data: " + e.getMessage();
					Common.logMsg(AbsSSLogger.WARNING, s, null);
					System.out.println(s);
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
}
