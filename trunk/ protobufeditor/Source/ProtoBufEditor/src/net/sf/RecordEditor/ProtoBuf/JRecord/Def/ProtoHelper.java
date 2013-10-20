package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.sf.JRecord.Common.Conversion;
import net.sf.JRecord.Common.RecordRunTimeException;
import net.sf.JRecord.CsvParser.BasicCsvLineParser;
import net.sf.JRecord.CsvParser.CsvDefinition;
import net.sf.JRecord.CsvParser.StandardCsvLineParser;
import net.sf.RecordEditor.utils.common.Common;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;


public class ProtoHelper {
	private static final String MUST_BE_POSITIVE = "must be >= 0";
	public final static byte[] EMPTY_BYTE_ARRAY = {};
	public final static ByteString EMPTY_BYTE_STRING = ByteString.copyFrom(EMPTY_BYTE_ARRAY);


	public static Object getAdjustedFieldValue(Object value, FieldDescriptor field) {
		Object ret = value;

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
        	   //byte[] bytes = (byte[]) value;
        	   ByteString bs = (ByteString) value;
        	   byte[] bytes = new byte[bs.size()];
        	   bs.copyTo(bytes, 0);
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





	public static void setField(Message.Builder builder, FieldDescriptor field, Object newValue) {
		Object value = newValue;

		//System.out.println("## 1 " + newValue + " " + field.isOptional() + " " +  field.isRepeated());
		if (newValue == null && (field.isOptional() || field.isRepeated())) {
			builder.clearField(field);
		} else if (field.isRepeated()) {
			if (value instanceof String) {

				Object[] element;

				String s = dropFromStart(value.toString(), "[");
				if (s.endsWith("]")) {
					s = s.substring(0, s.length() - 1);
				}

				if (field.getType() == Type.STRING) {
					StandardCsvLineParser p = new StandardCsvLineParser();
					ArrayList<String> nl = new ArrayList<String>();
					int i =0;
					CsvDefinition csvDef = new CsvDefinition(",", "'");
					String rf = p.getField2(i++, s, csvDef);
					while (rf != null) {
						nl.add(rf);
						rf = p.getField2(i++, s, csvDef);
					}
					element = new String[nl.size()];
					element = nl.toArray(element);
				} else {
					String[] strElements = (BasicCsvLineParser.getInstance()).split(s, new CsvDefinition(",", ""), 0);

					element = new Object[strElements.length];

					for (int i =0; i < strElements.length; i++) {
						strElements[i] = dropFromStart(strElements[i], " ");

						element[i] = adjustSingleValueForUpdate(field, strElements[i], true);
					}
				}

				Object hold = builder.getField(field);
				try {
					builder.clearField(field);

					for (int i =0; i < element.length; i++) {
						builder.addRepeatedField(field, element[i]);
					}
				} catch (Exception e) {
					builder.setField(field, hold);
					throw new RuntimeException(e.getMessage());
				}
			} else {
				builder.setField(field, value);
			}
		} else {
			value = adjustSingleValueForUpdate(field, newValue, false);
//			System.out.println("## 2 " + value + " " + field.isOptional() + " " +  field.isRepeated()
//					+ " " + (value == null)
//					+ " " + (value == null && (field.isOptional() || field.isRepeated()))
//					+ " " + field.getName()
//					+ " " + builder.getDescriptorForType().getName());

			if (value == null && (field.isOptional() || field.isRepeated())) {
				//System.out.println("## 3 Clear ");
				builder.clearField(field);
			} else {
				builder.setField(field, value);
			}
		}
	}

	private static String dropFromStart(String s, String prefix) {
		if (s.startsWith(prefix)) {
			s = s.substring(1);
		}

		return s;
	}


	public static Object adjustSingleValueForUpdate(FieldDescriptor field, Object newValue,
			boolean mustBeValid) {;
		Object value = newValue;
		long l;

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
				throw new RecordRunTimeException(MUST_BE_POSITIVE);
			}
			value = Integer.valueOf((int) l);
			break;

		case UINT64:
		case FIXED64:
			l = getNumber(newValue).longValue();
			if (l < 0) {
				throw new RecordRunTimeException(MUST_BE_POSITIVE);
			}
			value = Long.valueOf(l);
			break;

		case FLOAT:
			if (newValue != null && ! (newValue instanceof Float)) {
				value = Float.valueOf(newValue.toString());
			}
			break;

		case DOUBLE:
			if (! (newValue != null && newValue instanceof Double)) {
				value = Double.valueOf(newValue.toString());
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
			if (Common.isEmpty(newValue)) {
				value ="";
			} else if (! (newValue instanceof String)) {
				value = newValue.toString();
			}
			break;

		case BYTES:
			if (Common.isEmpty(newValue)) {
				value = EMPTY_BYTE_STRING;
			} else {
				byte[] b;
				String s = newValue.toString();

				int ii;
				b = new byte[s.length() / 2];
				for (int i = 0; i < b.length; i++) {
					ii = Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
					if (ii > 127) {
						ii= ii - 256;
					}
					b[i] = (byte) ii;
				}
				value = ByteString.copyFrom(b);
			}

			break;

		case ENUM: {
			if (newValue == null) {
				throw new RecordRunTimeException("An enum must have a value");
			} else if ("".equals(newValue)) {
				value = null;
			} else {
				Descriptors.EnumDescriptor enumType = field.getEnumType();

				boolean isNum = false;
				int number = 0;
				if (newValue instanceof Number) {
					isNum =true;
					number = ((Number) newValue).intValue();
				} else {
					try {
						number = Integer.parseInt(newValue.toString());
						isNum =true;
					} catch (Exception e) {
					}
				}
				if (isNum) {
					value = enumType.findValueByNumber(number);
					if (value == null) {
						throw new RecordRunTimeException(
								"Enum type \"{0}\" has no value with number {1}.",
								new Object[] {enumType.getFullName(), number});
					}
				} else {
					String id = newValue.toString();
					value = enumType.findValueByName(id);
					if (value == null && (field.isRequired() || mustBeValid)) {
						throw new RecordRunTimeException(
									"Enum type \"{0}\" has no value named \"{1}\".",
									new Object[] { enumType.getName(), id});
					}
				}
			}

			break;
		}

		case MESSAGE:
		case GROUP:
			throw new RuntimeException("Can't get here.");
		}

		return value;
	}


	private static Number getNumber(Object val) {
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



	public static Object getDefaultValue(FieldDescriptor field) {;
		Object value = null;

		switch (field.getType()) {
		case INT32:
		case SINT32:
		case SFIXED32:
		case UINT32:
		case FIXED32:
			value = Integer.valueOf(0);
			break;

		case INT64:
		case SINT64:
		case SFIXED64:
		case UINT64:
		case FIXED64:
			value = Long.valueOf(0);
			break;

		case FLOAT:
			value = Float.valueOf(0);
			break;

		case DOUBLE:
			value = Double.valueOf(0);

			break;

		case BOOL:
			value = Boolean.FALSE;
			break;

		case STRING:
			value ="";
			break;

		case BYTES:
			value = EMPTY_BYTE_STRING;
			break;

		case ENUM: {
			Descriptors.EnumDescriptor enumType = field.getEnumType();

			value = enumType.findValueByNumber(0);

			break;
		}

		case MESSAGE:
		case GROUP:
		}

		return value;
	}

	/**
	 * Get initialized builder for a descriptor
	 * @param desc record description
	 * @return requested builder
	 */
	@SuppressWarnings("rawtypes")
	public static AbstractMessage.Builder getBuilder(Descriptor desc) {
		DynamicMessage.Builder ret = DynamicMessage.newBuilder(desc);

		initBuilder(ret, desc);

		return ret;
	}

	public static void initBuilder(Message.Builder bld, Descriptor desc) {
		Object o;
		List<FieldDescriptor> fields = desc.getFields();
		for (FieldDescriptor field : fields) {
			if (field.isRequired() && ! field.isRepeated()) {
				o = getDefaultValue(field);
				if (o != null) {
					bld.setField(field, o);
				}
			}
		}
	}


    public static FileDescriptor getFileDescriptor(FileDescriptorSet dp, int idx)
    throws DescriptorValidationException {
 		FileDescriptor[] dependencies = {};

 		idx = Math.max(0, idx);
 		if (dp.getFile(idx).getDependencyCount() > 0 ) {
 			int i, j;
 			String depName;
 			dependencies = new FileDescriptor[dp.getFile(idx).getDependencyCount()];
 			for (i = 0; i < dependencies.length; i++) {
 				depName = dp.getFile(idx).getDependency(i);
 				for (j = 0; j < dp.getFileCount(); j++) {
 					if (depName.equals(dp.getFile(j).getName())) {
 						dependencies[i] = getFileDescriptor(dp, j);
 					}
 				}
 			}
 		}

    	return FileDescriptor.buildFrom(dp.getFile(idx), dependencies);
    }

    public static FileDescriptorSet getFileDescriptorSet(FileDescriptor descriptor) {
    	FileDescriptorSet.Builder bld = FileDescriptorSet.newBuilder();

    	addDescriptors(bld, descriptor);

    	return bld.build();
    }


    private static void addDescriptors(FileDescriptorSet.Builder bld, FileDescriptor descriptor) {

    	if (descriptor != null ) {
	     	List<FileDescriptor> depencies = descriptor.getDependencies();

	   		bld.addFile(descriptor.toProto());

	    	for (FileDescriptor dep : depencies) {
	    		addDescriptors(bld, dep);
	    	}
	   	}
    }

}
