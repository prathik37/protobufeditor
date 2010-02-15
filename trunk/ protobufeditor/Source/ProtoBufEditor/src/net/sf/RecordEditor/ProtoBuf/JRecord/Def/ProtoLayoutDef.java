/**
 * 
 */
package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLayoutDetails;
import net.sf.JRecord.Details.BasicLayout;
import net.sf.JRecord.Details.RecordDecider;


/**
 * @author bm
 *
 */
public class ProtoLayoutDef 
extends BasicLayout<ProtoFieldDef, ProtoRecordDef> {
//implements AbstractLayoutDetails<ProtoFieldDef, ProtoRecordDef> {

	private static final byte[] EMPTY_BYTE_ARRAY = {};

	private HashMap<String, ProtoFieldDef> fieldNameMap = null;
	private int fileStructure;
	private FileDescriptor fileDesc;
	private int primaryMessageIdx = 0;

	private  ProtoLayoutDef(FileDescriptor fileDescription, int fileFormat, 
			ArrayList<ProtoRecordDef> recordList) {
		fileDesc = fileDescription;
		fileStructure = fileFormat;

		records = new ProtoRecordDef[recordList.size()];
		records = recordList.toArray(records);
		setupChildRecords();
	}

	/**
	 * 
	 */
	public ProtoLayoutDef(FileDescriptor fileDescription, int fileFormat) {
		fileDesc = fileDescription;
		fileStructure = fileFormat;

		ArrayList<ProtoRecordDef> recs = new ArrayList<ProtoRecordDef>();
		
		addTRecords(recs, fileDesc.getMessageTypes());
		
		records = new ProtoRecordDef[recs.size()];
		
		records = recs.toArray(records);
		
		setupChildRecords();
	}
	
	
	private void addTRecords(ArrayList<ProtoRecordDef> recs, List<Descriptor> msgs) {
		
		if (msgs == null) {
			return;
		}
		
		for (Descriptor d : msgs) {
			recs.add(new ProtoRecordDef(d));
		}

		for (Descriptor d : msgs) {
			addTRecords(recs, d.getNestedTypes());
		}

	}

	/* (non-Javadoc)
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#addRecord(net.sf.JRecord.Details.AbstractRecordDetail)
	 */
	@Override
	public void addRecord(ProtoRecordDef record) {
		throw new ProtoRecordException("You can not add records to this layouts");
	}

	/**
	 * @see net.sf.JRecord.Details.AbstractLineDetails#getAdjField(int, int)
	 */
    public ProtoFieldDef getAdjField(int layoutIdx, int fieldIdx) {
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
	 * @see net.sf.JRecord.Details.AbstractLayoutDetails#getField(byte[], int, net.sf.JRecord.Common.FieldDetail)
	 */
	@Override
	public Object getField(byte[] record, int type, ProtoFieldDef field) {
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
	public ProtoFieldDef getFieldFromName(String fieldName) {
		ProtoFieldDef ret = null;
    	String key = fieldName.toUpperCase();

    	if (fieldNameMap == null) {
    		int i, j, size;
    		ProtoFieldDef fld;

    		size = 0;
    		for (i = 0; i < records.length; i++) {
    		    size += records[i].getFieldCount();
    		}
 
    		fieldNameMap = new HashMap<String, ProtoFieldDef>(size);

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
		return records[primaryMessageIdx].getChildRecordCount() > 0;
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
		for (int i = 0; i < records.length; i++) {
			records[i].setChildRecords(this);
		}
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
	@SuppressWarnings("unchecked")
	public final AbstractMessage.Builder getPrimaryMsgBuilder() {
		return DynamicMessage.newBuilder(records[primaryMessageIdx].getProtoDesc()); 
	}


	/**
	 * @see net.sf.JRecord.Details.BasicLayout#getNewLayout(java.util.ArrayList)
	 */
	@Override
	protected AbstractLayoutDetails<ProtoFieldDef, ProtoRecordDef> getNewLayout(
			ArrayList<ProtoRecordDef> recordList) {
		ProtoRecordDef[] recs = new ProtoRecordDef[recordList.size()];
		recs = recordList.toArray(recs);
		return new ProtoLayoutDef(fileDesc, fileStructure, recordList);
	}


	/**
	 * @see net.sf.JRecord.Details.BasicLayout#getNewRecord(net.sf.JRecord.Details.AbstractRecordDetail, java.util.ArrayList)
	 */
	@Override
	protected ProtoRecordDef getNewRecord(ProtoRecordDef record,
			ArrayList<ProtoFieldDef> fields) {
		ProtoFieldDef[] flds = new ProtoFieldDef[fields.size()];
		//ProtoRecordDef ret;

		flds = fields.toArray(flds);
		
		return new ProtoRecordDef(record.getProtoDesc(), flds);
	}
}
