package net.sf.RecordEditor.ProtoBuf.JRecord.Def;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sf.JRecord.Details.AbstractLine;
import net.sf.RecordEditor.utils.lang.LangConversion;
import net.sf.RecordEditor.utils.swing.CheckBoxTableRender;
import net.sf.RecordEditor.utils.swing.SwingUtils;
import net.sf.RecordEditor.utils.swing.Combo.ComboItemEditor;
import net.sf.RecordEditor.utils.swing.Combo.ComboItemRender;
import net.sf.RecordEditor.utils.swing.array.ArrayInterface;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class ArrayDetails implements ArrayInterface {
	@SuppressWarnings("rawtypes")
	public List array;

	private ProtoLine line;
	private ProtoRecordDef.ProtoFieldDef fieldDef;
	private FieldDescriptor fieldDesc;

	/**
	 * @param array
	 * @param line
	 * @param fieldDefinition
	 */
	public ArrayDetails(ProtoLine line, ProtoRecordDef.ProtoFieldDef fieldDefinition) {
		//this.array = line.getBuilder().getField(fieldDesc);
		this.line = line;
		this.fieldDef = fieldDefinition;
		this.fieldDesc = fieldDefinition.getProtoField();

		retrieveArray();
	}

	/**
	 * {@link net.sf.RecordEditor.utils.swing.array.ArrayInterface#retrieveArray()}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void retrieveArray() {
		Message.Builder bld = line.getBuilder();

		Object list = bld.getField(fieldDesc);

		if (list instanceof List) {
			array = (List) list;
		} else if (fieldDesc.isRepeated()) {

			int count = bld.getRepeatedFieldCount(fieldDesc);

			array = new ArrayList(count);
			for (int i = 0; i < count; i++) {
				array.add(bld.getRepeatedField(fieldDesc, i));
			}
		} else {
			array = new ArrayList(2);
			array.add(bld.getField(fieldDesc));
		}
	}


	/* (non-Javadoc)
	 * @see  net.sf.RecordEditor.edit.display.array.ArrayInterface#getText()
	 */
	@Override
	public String toString() {
		StringBuilder b;
		String s;

		switch(fieldDesc.getType()) {
		case BYTES:
		case ENUM:
			b = new StringBuilder("[");
			s = "";
			for (int i = 0; i < array.size(); i++) {
				b.append(s).append(ProtoHelper.getAdjustedFieldValue(array.get(i), fieldDesc));
				s = ", ";
			}
			b.append("]");
			return b.toString();
		case STRING:
			b = new StringBuilder("[");
			s = "'";
			for (int i = 0; i < array.size(); i++) {
				b.append(s).append(array.get(i)).append("'");
				s = ",'";
			}
			b.append("]");
			return b.toString();

//		case BOOL:
//			b = new StringBuilder("[");
//			s = "";
//			for (int i = 0; i < array.size(); i++) {
//				b.append(s).append(((EnumValueDescriptor) array.get(i)).getName());
//				s = ", ";
//			}
//			b.append("]");
//			return b.toString();
		}
		return array.toString();
	}


	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.edit.display.array.ArrayInterface.ArrayInterface#add(int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void add(int index, Object value) {
		array.add(index, checkObject(value, null));
		flush();
	}

	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.edit.display.array.ArrayInterface.ArrayInterface#add(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean add(Object value) {
		boolean ret =  array.add(checkObject(value, null));
		flush();

		return ret;
	}

	private Object checkObject(Object newValue, Object oldValue) {
		Object ret = oldValue;
		Object o = newValue;
		Object temp;
		String msg;

		if (oldValue == null) {
			ret = ProtoHelper.getDefaultValue(fieldDesc);
		}

		temp = checkObj(o);
		while (o != null && temp instanceof Exception) {
			msg = ((Exception) temp).getMessage();
	        o =  JOptionPane.showInputDialog(null,
	        		msg,
	                LangConversion.convert(LangConversion.ST_MESSAGE, "Conversion Error"),
	                JOptionPane.ERROR_MESSAGE,
	                null, null, o);

	        if (o != null) {
	        	temp = checkObj(o);
	        }
		}

        if (o != null && ! (temp instanceof Exception)) {
        	ret = temp;
        }

		return ret;
	}

	private Object checkObj(Object o) {
		Object ret = null;

		try {
			ret = ProtoHelper.adjustSingleValueForUpdate(fieldDesc, o, false);
		} catch (Exception e) {
			ret = e;
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.edit.display.array.ArrayInterface.ArrayInterface#get(int)
	 */
	@Override
	public Object get(int index, int column) {
		return ProtoHelper.getAdjustedFieldValue(array.get(index), fieldDesc);
	}

	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.edit.display.array.ArrayInterface.ArrayInterface#remove(int)
	 */
	@Override
	public Object remove(int index) {
		Object ret =  array.remove(index);
		flush();
		return ret;
	}

	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.edit.display.array.ArrayInterface#set(int, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object set(int index, int column, Object value) {
		Object ret = null;
		Object val = checkObject(value, array.get(index));
		if (val == null  ||  ! val.equals(array.get(index))) {
			ret =  array.set(index, val);
			line.getBuilder().setRepeatedField(fieldDesc, index, val);
			line.updateParent();
		}
		return ret;
	}

	/**
	 * @return size
	 * @see java.util.List#size()
	 */
	@Override
	public int size() {
		return array.size();
	}



	/**
	 * @see  net.sf.RecordEditor.utils.swing.array.ArrayInterface#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 1;
	}


	/**
	 * @see net.sf.RecordEditor.utils.swing.array.ArrayInterface#getLine()
	 */
	@Override
	public AbstractLine getLine() {
		return line;
	}

	/**
	 * @see   net.sf.RecordEditor.utils.swing.array.ArrayInterface#flush()
	 */
	@Override
	public void flush() {
		line.getBuilder().setField(fieldDesc, array);
		line.updateParent();
	}

	public Object getReturn() {
		return array;
	}

	/**
     * Get Table Cell Render
     * <b>Note:</b> you should always return a new Editor rather than a
     * the same editor each time
     *
     * @param fld field being displayed
     *
     * @return Table Cell Render to be used to display the field
     */
	@Override
    public  TableCellRenderer getTableCellRenderer() {
    	TableCellRenderer ret = null;
    	switch (fieldDesc.getType()) {
    	case ENUM: ret = new ComboItemRender(fieldDef.getComboModel()); 	break;
    	case BOOL: ret = new CheckBoxTableRender(); 									break;
    	}
    	return ret;
    }

    /**
     * Get Table Cell Editor
     *
     * @param fld field being displayed
     *
     * @return Table Cell Editor to be used to edit the field
     */
    @Override
    public TableCellEditor getTableCellEditor() {
    	TableCellEditor ret = null;
       	switch (fieldDesc.getType()) {
    	case ENUM: ret = new ComboItemEditor(fieldDef.getComboModel()); 	break;
    	case BOOL: ret = new DefaultCellEditor(new CheckBoxTableRender());	break;
    	}
    	return ret;

    }



    /**
     * Get the normal height of a field
     *
     * @return field height
     */
    @Override
    public  int getFieldHeight() {
    	int ret = - 1;
       	switch (fieldDesc.getType()) {
    	case ENUM: ret = SwingUtils.COMBO_TABLE_ROW_HEIGHT; 	break;
    	//case BOOL: ret = new DefaultCellEditor(new CheckBoxTableRender());	break;
    	}
    	return ret;

    }

}
