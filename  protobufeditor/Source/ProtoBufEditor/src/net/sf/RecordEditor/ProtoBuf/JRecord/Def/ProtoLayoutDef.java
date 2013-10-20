/**
 *
 */
package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.IFieldDetail;
import net.sf.JRecord.Details.AbstractLayoutDetails;
import net.sf.JRecord.Details.AbstractRecordDetail.FieldDetails;
import net.sf.JRecord.Details.BasicLayout;
import net.sf.JRecord.Details.Options;
import net.sf.JRecord.Details.RecordDecider;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage.Builder;


/**
 * @author bm
 *
 */
public class ProtoLayoutDef
extends BasicLayout<ProtoRecordDef> {
//implements AbstractLayoutDetails<ProtoFieldDef, ProtoRecordDef> {

	private static final byte[] EMPTY_BYTE_ARRAY = {};

	private HashMap<String, ProtoRecordDef.ProtoFieldDef> fieldNameMap = null;
	private int fileStructure;
	private FileDescriptor fileDesc;
	private int primaryMessageIdx = 0;

	private boolean protoDefinition = false;

	private final ExtensionRegistry registry = ExtensionRegistry.newInstance();


	private  ProtoLayoutDef(FileDescriptor fileDescription, int fileFormat,
			List<ProtoRecordDef> recordList) {
		fileDesc = fileDescription;
		fileStructure = fileFormat;

		records = new ProtoRecordDef[recordList.size()];
		records = recordList.toArray(records);
		setupChildRecords();
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.BasicLayout#getField(int, int)
	 */
	@Override
	public ProtoRecordDef.ProtoFieldDef getField(int layoutIdx, int fieldIdx) {
		return records[layoutIdx].getField(fieldIdx);
	}

	/**
	 *
	 */
	public ProtoLayoutDef(FileDescriptor fileDescription, int fileFormat) {
		this(fileDescription, fileFormat, false);
	}

	/**
	 *
	 */
	public ProtoLayoutDef(FileDescriptor fileDescription, int fileFormat, boolean protoDef) {
		fileDesc = fileDescription;
		fileStructure = fileFormat;

		ArrayList<ProtoRecordDef> recs = new ArrayList<ProtoRecordDef>();

		protoDefinition = protoDef;

		addTRecords(recs, fileDesc, protoDef);


		records = new ProtoRecordDef[recs.size()];

		records = recs.toArray(records);

		setupChildRecords();
	}

	//int ind = 1;
	//String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";

	private void addTRecords(ArrayList<ProtoRecordDef> recs, FileDescriptor fileDesc, boolean highlightProto) {
		System.out.println();
		System.out.println("File Descriptor: " + fileDesc.getName());
		addTRecords(recs, fileDesc.getMessageTypes(), highlightProto);

		List<FileDescriptor> depList = fileDesc.getDependencies();

		System.out.println(" Adding dependencies ... ");
		for (FileDescriptor dep : depList) {
			addTRecords(recs, dep, highlightProto);
		}
	}

	private void addTRecords(ArrayList<ProtoRecordDef> recs, List<Descriptor> msgs, boolean highlightProto) {
		//ind += 1;

		if (msgs == null) {
			return;
		}

		for (Descriptor d : msgs) {
//			System.out.println(tabs.substring(tabs.length() - ind)
//					+ d.getFullName()
//					);
			recs.add(new ProtoRecordDef(d, highlightProto));
		}

		for (Descriptor d : msgs) {
			addTRecords(recs, d.getNestedTypes(), highlightProto);
		}
//		System.out.println();
//
//		ind -=1;
	}


//	/* (non-Javadoc)
//	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#addRecord(net.sf.JRecord.Details.AbstractRecordDetail)
//	 */
//	@Override
//	public void addRecord(ProtoRecordDef record) {
//		throw new ProtoRecordException("You can not add records to this layouts");
//	}


	/**
	 * @see net.sf.JRecord.Details.AbstractLineDetails#getAdjField(int, int)
	 */
    public ProtoRecordDef.ProtoFieldDef getAdjField(int layoutIdx, int fieldIdx) {
        return getRecord(layoutIdx)
                       .getField(getAdjFieldNumber(layoutIdx, fieldIdx));
    }


    /**
	 * @see net.sf.JRecord.Details.AbstractLineDetails#getAdjFieldNumber(int, int)
	 */
    public int getAdjFieldNumber(int recordIndex, int inColumn) {
	    return inColumn;
    }

	/**
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getDecider()
	 */
	@Override
	public RecordDecider getDecider() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getDelimiter()
	 */
	@Override
	public String getDelimiter() {
		return "";
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getDelimiterBytes()
	 */
	@Override
	public byte[] getDelimiterBytes() {
		return EMPTY_BYTE_ARRAY;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getDescription()
	 */
	@Override
	public String getDescription() {
		return "";
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getEolString()
	 */
	@Override
	public String getEolString() {
		return System.getProperty("line.separator");
	}



	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getField(byte[], int, net.sf.JRecord.Common.IFieldDetail)
	 */
	@Override
	public Object getField(byte[] record, int type, IFieldDetail field) {
		return null;
	}


	/**
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getFieldDescriptions(int, int)
	 */
	@Override
	public String[] getFieldDescriptions(int layoutIdx, int columnsToSkip) {

		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getFieldFromName(java.lang.String)
	 */
	@Override
	public ProtoRecordDef.ProtoFieldDef getFieldFromName(String fieldName) {
		ProtoRecordDef.ProtoFieldDef ret = null;
    	String key = fieldName.toUpperCase();

    	if (fieldNameMap == null) {
    		int i, j, size;
    		ProtoRecordDef.ProtoFieldDef fld;

    		size = 0;
    		for (i = 0; i < records.length; i++) {
    		    size += records[i].getFieldCount();
    		}

    		fieldNameMap = new HashMap<String, ProtoRecordDef.ProtoFieldDef>(size);

    		for (i = 0; i < records.length; i++) {
    			//FieldDetail[] flds = records[i].getFields();
    			for (j = 0; j < records[i].getFieldCount(); j++) {
    			    fld = records[i].getField(j);
    				fieldNameMap.put(fld.getName().toUpperCase(), fld);
    			}
    		}
     	}

    	if (fieldNameMap.containsKey(key)) {
    		ret = fieldNameMap.get(key);
    	}

    	return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getFileStructure()
	 */
	@Override
	public int getFileStructure() {
		return fileStructure;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getFontName()
	 */
	@Override
	public String getFontName() {
		return "";
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getLayoutName()
	 */
	@Override
	public String getLayoutName() {
		return fileDesc.getName();
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getLayoutType()
	 */
	@Override
	public int getLayoutType() {
		return Constants.rtProtoRecord;
	}


	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getRecordCount()
	 */
	@Override
	public int getRecordCount() {
		return records.length;
	}


	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getRecordSep()
	 */
	@Override
	public byte[] getRecordSep() {
		return Constants.SYSTEM_EOL_BYTES;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getUnAdjFieldNumber(int, int)
	 */
	@Override
	public int getUnAdjFieldNumber(int recordIndex, int inColumn) {
		return inColumn;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#hasTreeStructure()
	 */
	@Override
	public boolean hasTreeStructure() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#isAllowChildren()
	 */
	@Override
	public boolean hasChildren() {
		//if (primaryMessageIdx < 0 || primaryMessageIdx >= records.length)
		return primaryMessageIdx >= 0 && primaryMessageIdx < records.length
			&& records[primaryMessageIdx].getChildRecordCount() > 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#isBinary()
	 */
	@Override
	public boolean isBinary() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#isBinCSV()
	 */
	@Override
	public boolean isBinCSV() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#isBuildLayout()
	 */
	@Override
	public boolean isBuildLayout() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#isOkToAddAttributes()
	 */
	@Override
	public boolean isOkToAddAttributes() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#isXml()
	 */
	@Override
	public boolean isXml() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#setAllowChildren(boolean)
	 */
	@Override
	public void setAllowChildren(boolean allowChildren) {

	}

	/**
	 * set Child records
	 */
	private void setupChildRecords() {

		addExtensions(fileDesc.getExtensions());
		for (int i = 0; i < records.length; i++) {
			records[i].setChildRecords(this);
		}
	}

	public FileDescriptor getFileDesc() {
		return fileDesc;
	}

	/**
	 * @param primaryMessageIndex the primaryMessage to set
	 */
	public final void setPrimaryMessageIndex(int primaryMessageIndex) {
		this.primaryMessageIdx = primaryMessageIndex;
	}

	/**
	 * @return the primaryMessage
	 */
	public final int getPrimaryMessageIndex() {
		return primaryMessageIdx;
	}

	/**
	 * @return the primaryMessage
	 */
	@SuppressWarnings("rawtypes")
	public final AbstractMessage.Builder getPrimaryMsgBuilder() {
		return ProtoHelper.getBuilder(records[primaryMessageIdx].getProtoDesc());
	}


	/**
	 * @see net.sf.JRecord.Details.BasicLayout#getNewLayout(java.util.ArrayList)
	 */
	@Override
	protected AbstractLayoutDetails getNewLayout(ArrayList<ProtoRecordDef> recordList) {
		ProtoLayoutDef ret =  new ProtoLayoutDef(fileDesc, fileStructure, recordList);

		ret.setupChildRecords();
		return ret;
	}


	/**
	 * @see net.sf.JRecord.Details.BasicLayout#getNewRecord(net.sf.JRecord.Details.AbstractRecordDetail, java.util.ArrayList)
	 */
	@Override
	protected ProtoRecordDef getNewRecord(ProtoRecordDef record,
			ArrayList<? extends FieldDetails> fields) {
		ProtoRecordDef.ProtoFieldDef[] flds = new ProtoRecordDef.ProtoFieldDef[fields.size()];
		//ProtoRecordDef ret;

		flds = fields.toArray(flds);

		return new ProtoRecordDef(record.getProtoDesc(), flds);
	}

	/**
	 *
	 * @return wether there is a proto deffinition
	 */
	public boolean isProtoDefinition() {
		return protoDefinition;
	}


	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.BasicLayout#getOption(int)
	 */
	@Override
	public int getOption(int option) {
		int ret;
		if (option == Options.OPT_SINGLE_RECORD_FILE) {
			ret = Options.NO;
			if (fileStructure == Constants.IO_PROTO_SD_SINGLE_MESSAGE
			||  fileStructure == Constants.IO_PROTO_SINGLE_MESSAGE) {
				ret = Options.YES;
			}
		} else {
			ret = super.getOption(option);
		}
		return ret;
	}

	/**
	 * @return the registry
	 */
	public final ExtensionRegistry getRegistry() {
		return registry;
	}

	public final void addExtensions(List<FieldDescriptor> externalFields) {
		for (FieldDescriptor field : externalFields) {
			int recIdx = getRecordIndex(field.getContainingType().getName());

			if (recIdx >= 0) {
				ProtoRecordDef rec = getRecord(recIdx);
				if (ProtoRecordDef.isChild(field)) {
					Builder newBuilder = DynamicMessage.newBuilder(field.getMessageType());
					ProtoHelper.initBuilder(newBuilder, field.getMessageType());
					registry.add(field, newBuilder.build());

					rec.addChildRecord(this, field);
				} else {
					registry.add(field);

					rec.addField(field);
				}
			}
		}

	}
}
