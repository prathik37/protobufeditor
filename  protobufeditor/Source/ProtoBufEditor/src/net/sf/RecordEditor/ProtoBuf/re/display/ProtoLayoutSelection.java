package net.sf.RecordEditor.ProtoBuf.re.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.Log.AbsSSLogger;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ConstClass;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoSelfDescribingReader;
import net.sf.RecordEditor.re.openFile.AbstractLayoutSelection;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.common.Parameters;
import net.sf.RecordEditor.utils.swing.BasePanel;
import net.sf.RecordEditor.utils.swing.FileChooser;
import net.sf.RecordEditor.utils.swing.Combo.ComboOption;

public class ProtoLayoutSelection extends AbstractLayoutSelection<ProtoLayoutDef>  {
	
	private static final String SEPERATOR = ",,";
	
	private static final String PROTO_COMPILE_OUTPUT = "--descriptor_set_out=";
	
	

	private FileChooser  protoDefinitionFile;
	private FileChooser  protoImportDir;


	private JComboBox fileStructure; // = new JComboBox(STRUCTURE_OPTIONS);
	private JComboBox loaderOptions; // = new JComboBox(COPYBOOK_LOADER);
	private JComboBox protoFile;
	private JComboBox messageName;
	private JTextField fileField;
	
//    private JComboBox fieldSeparator;
//    private JComboBox quote;
	private JTextArea message = null;

//	private int mode = MODE_NORMAL;
	
	private boolean listnersActive = true;

	
	ActionListener fileListner = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (listnersActive) {
				setFile(-1);
				setEditable();
			}
		}
	};

	ActionListener msgListner = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (listnersActive) {
				setMsgCombo();
				setEditable();
			}
		}
	};

 


	@Override
	public void addLayoutSelection(BasePanel pnl, JTextField file, JPanel goPanel,
			JButton layoutCreate1, JButton layoutCreate2) {
		addLayoutSelection(pnl, goPanel, layoutCreate1, layoutCreate2, null);
		fileField = file;
	}

//	/**
//	 * Add Selection with external layout file
//	 * @param pnl panel to add fields to
//	 * @param goPanel go panel to use
//	 * @param layoutFile layout file
//	 */
//	public void addLayoutSelection(BasePanel pnl, JPanel goPanel, 	FileChooser layoutFile) {
//		
//		addLayoutSelection(pnl, goPanel, null, null, layoutFile);
//	}

	/**
	 * Add layout Selection to panel
	 * @param pnl panel to be updated
	 * @param goPanel panel holding go button
	 * @param layoutCreate1 layout create button1
	 * @param layoutCreate2  layout create button2
	 * @param layoutFile layout file
	 */
	private void addLayoutSelection(BasePanel pnl, JPanel goPanel,
			JButton layoutCreate1, JButton layoutCreate2, FileChooser layoutFile) {
  	    setupFields();

 	    protoDefinitionFile.setText(Common.OPTIONS.DEFAULT_COPYBOOK_DIRECTORY.get());
 	    protoImportDir.setText(Parameters.getString(ConstClass.VAR_PROTO_IMBED_DIR));
 	   

	    if (layoutFile != null)  {
	    	protoDefinitionFile = layoutFile;
	    	
		    pnl.addLine("Output File Structure", fileStructure);
	    } else {
		    pnl.addLine("File Structure", fileStructure);
		    
		    //loaderOptions.setSelectedIndex(0);
	    	pnl.addLine("Type of Definition", loaderOptions);
	    	//pnl.addComponent("Split Copybook", splitOption);
	    	
	    	pnl.setGap(BasePanel.GAP0);
			pnl.addLine("Proto Definition", protoDefinitionFile, protoDefinitionFile.getChooseFileButton());
			pnl.addLine("Proto Import Directory", protoImportDir, protoImportDir.getChooseFileButton());
		    pnl.setGap(BasePanel.GAP0);
		    
//		    pnl.addComponent("Numeric Format", numericFormat);
	    
		    pnl.addLine("Proto File", protoFile);
		    pnl.addLine("Primary Message", messageName);
//		    pnl.addComponent("Quote", quote);
		    pnl.setGap(BasePanel.GAP1);
			pnl.addLine("", null, goPanel);
			if (goPanel != null) {
				pnl.setHeight(Math.max(BasePanel.NORMAL_HEIGHT * 3, goPanel.getPreferredSize().getHeight()));
			}
	    }
		
//	    } else {
//		    pnl.setGap(BasePanel.GAP1);
//	    }

	    //System.out.println("Setting listners ");
	    fileStructure.addActionListener(fileListner);
	    loaderOptions.addActionListener(fileListner);
	    
	    FocusAdapter focuslistner = new FocusAdapter() {
        	public void focusLost(FocusEvent e) {
        		if (listnersActive) {
        			setFile(-1);
        		}
           	}
        };
	    protoDefinitionFile.addFcFocusListener(focuslistner);
	    protoImportDir.addFcFocusListener(focuslistner);

		setEditable();
	}
	

    
    /**
     * set editable
     */
    private void setEditable() {
  
    }

             
    private void setFile(int idx) {
    	
    	protoFile.removeActionListener(msgListner);
    	protoFile.removeAllItems();
    	FileDescriptorSet dp = getProtoDesc();
    	
     	//System.out.println("Checking file " + (dp != null));
    	if (dp != null && dp.getFileCount() > 0) {
    		for (int i = 0; i < dp.getFileCount(); i++) {
    			protoFile.addItem(dp.getFile(i).getName());
    		}
    		
    		//TODO set message
    		//TODO set message

    		if (idx < 0) {
    			HashSet<String> usedRecords = new HashSet<String>();
    			FileDescriptor[] fileDescs = new FileDescriptor[dp.getFileCount()];
    			List<Descriptor> msgTypes;
    			
    			idx = 0;
    			addMessages(dp, usedRecords, fileDescs);

    			for (int i = 0; i < dp.getFileCount(); i++) {
    				if (fileDescs[i] != null) {
						//System.out.println(" -- " + fileDescs[i].getName());
    					msgTypes = fileDescs[i].getMessageTypes();
	    				for (Descriptor d : msgTypes ) {
	    					if (! usedRecords.contains(d.getFullName())) {
	    						idx = i;
	    						//System.out.println("    ~~~~> " + d.getFullName());
	    						break;
	    					//} else {
	    					//	System.out.println("    --> " + d.getFullName());
	    					}
	    				}
	    				
    				}
	   				//System.out.println();
    			}

//    			for (int i = 0; i < dp.getFileCount(); i++) {
//    				if (dp.getFile(i).getMessageTypeCount() > 0) {
//    					idx = i;
//    					break;
//    				}
//    			}
    		}
    		
    		protoFile.setSelectedIndex(idx);
    		setupMsgCombo(dp, idx);
    		
    		if (dp.getFileCount() > 1) {
    			protoFile.addActionListener(msgListner);
    		}
    	}
    }

    
    private void setMsgCombo() {
    	

    	FileDescriptorSet dp = getProtoDesc();

    	if (dp != null && dp.getFileCount() > 0) {
    		//protoFile.setSelectedIndex(0);
    		setupMsgCombo(dp, protoFile.getSelectedIndex());
    	}
    }

    private void setupMsgCombo(FileDescriptorSet dp, int idx) {
    	
    	messageName.removeAllItems();
    	
     	try {
  	    	FileDescriptor fd = ProtoHelper.getFileDescriptor(dp, idx);
			List<Descriptor> msgTypes = fd.getMessageTypes();
			int index =0;


			HashSet<String> usedRecords = new HashSet<String>();
			
			addMessages(dp, usedRecords, new FileDescriptor[dp.getFileCount()]);
			for (Descriptor d : msgTypes ) {
				messageName.addItem(d.getName());			
			}
						
			for (Descriptor d : msgTypes ) {
				if (! usedRecords.contains(d.getFullName())) {
					messageName.setSelectedIndex(index);
					break;
				}
			
				index += 1;
			}

    	} catch (Exception e) {
    		Common.logMsg("Error Setting up Messages: " + e.getMessage(), e);
    		e.printStackTrace();
		}
    }
    
    
    private void addMessages(FileDescriptorSet dp, HashSet<String> usedRecords, FileDescriptor[] fileDescs) {
		List<Descriptor> msgTypes;

		for (int i = 0; i < dp.getFileCount(); i++) {
			try {
				fileDescs[i] = ProtoHelper.getFileDescriptor(dp, i);
				msgTypes = fileDescs[i].getMessageTypes();

				for (Descriptor d : msgTypes ) {
					List<FieldDescriptor> fldList = d.getFields();
					for (FieldDescriptor field : fldList) {
						if (field.getJavaType().equals(JavaType.MESSAGE)) {
							usedRecords.add(field.getMessageType().getFullName());
						}
					}	
				}
			} catch (Exception e) {
			}
		}
    }


    private FileDescriptorSet getProtoDesc() {
        int protoType;
        String layoutName = protoDefinitionFile.getText();
        FileDescriptorSet dp = null;
    
        protoType =  ((ComboOption) loaderOptions.getSelectedItem()).index;
  	
        try {
      		 if (fileField != null) {
       			 dp  = getFileDescriptionSet((
       					 (ComboOption) fileStructure.getSelectedItem()).index, 
       					 fileField.getText());
       		 } 
      		 
      		 if (dp != null) {
      		 } else if (layoutName == null ||  "".equals(layoutName)) {
            //copybookFile.requestFocus();
           		 message.setText("You must enter a Record Layout name ");
           	 } else {
           		dp = getFileDescriptorSet(protoType, layoutName);
           	 }
       	} catch (Exception e) {
        	 System.out.println("Error Layout: " + layoutName);
			 e.printStackTrace();
		}

        return dp;
    }

    private FileDescriptorSet compileProto(String layoutName) throws IOException {
    	int c;
    	StringBuilder oBuf = new  StringBuilder();
    	int pos = Math.max(
    			layoutName.lastIndexOf("\\"),
    			layoutName.lastIndexOf("/"));
    	String lastPart = layoutName;

    	String compiledFile;
    	String s, cmd, imbed;
   	
    	
     	
 
    	ArrayList<String> cmdOptList = new ArrayList<String>(15);
    	StringBuilder cmdBld = new StringBuilder();
    	String[] cmdAndArgs;
    	
    	cmdOptList.add(ConstClass.getProtoC());
    	cmdOptList.add(layoutName);
    	if (pos > 0) {
    		lastPart = layoutName.substring(pos + 1);
    		cmdOptList.add("--proto_path=" + layoutName.substring(0, pos));
    	}
    	
    	imbed = protoImportDir.getText();
    	if (! "".equals(imbed)) {
    		cmdOptList.add("--proto_path=" + imbed);
    		if (! imbed.equals(Parameters.getString(ConstClass.VAR_PROTO_IMBED_DIR))) {
    			Parameters.setProperty(ConstClass.VAR_PROTO_IMBED_DIR, imbed);
    			Parameters.writeProperties();
    		}
    	}
    	
    	compiledFile = Parameters.getPropertiesDirectory() 
					+ File.separator + "TempWork" + File.separator
					+ lastPart + "comp";
    	cmdOptList.add( "--include_imports");
    	cmdOptList.add(PROTO_COMPILE_OUTPUT + compiledFile);
    	
    	for (int i = 0 ; i < ConstClass.NUMBER_OF_PROTOC_OPTIONS; i++) {
    		s = Parameters.getString(ConstClass.VAR_PROTOBUF_COMPILE_OPTS + i);
    		if (s != null && ! "".equals(s)) {
    			cmdOptList.add(s);
    		}
    	}
    	
    	
    	cmdAndArgs = new String[cmdOptList.size()];
    	cmdAndArgs = cmdOptList.toArray(cmdAndArgs);
    	for (int i = 0; i < cmdAndArgs.length; i++) {
    		cmdBld.append(cmdAndArgs[i]).append(' ');
    	}
    	cmd = cmdBld.toString();
    	
    	try {
    		File file = new File(compiledFile);
    		if (file.exists()) {
    			file.delete();
    		}
    	} catch (Exception e) {
		}
    	   	
    	Process child = Runtime.getRuntime().exec(cmdAndArgs);

		InputStream in = child.getErrorStream();
		
		while ((c = in.read()) >= 0) {
			oBuf.append((char)c);
		}
		in.close();
		child.destroy();
		s = oBuf.toString();
		
		if (s != null && !"".equals(s)) {
			Common.logMsg(AbsSSLogger.WARNING, "Messages Compiling proto file \n\n"
					+cmd + "\n\n"+s, null);
//		} else {
//			Common.logMsg(AbsSSLogger.WARNING, cmd, null);
		}
		
   		File file = new File(compiledFile);
   		if (! file.exists()) {
   			message.setText("Error Compiling proto file:\n" + s);
   			throw new RuntimeException("Error Compiling proto");
   		}
 		
    	return getFileDescriptor(compiledFile);
    }
    
    private FileDescriptorSet getFileDescriptor(String inFileName) throws IOException {
    	FileDescriptorSet ret = null;
    	FileInputStream infile = new FileInputStream(inFileName);
    	try {
    		ret = FileDescriptorSet.parseFrom(infile); 
		} finally {
			infile.close();
		}
		
		return ret;
    }

	@Override
	public String getLayoutName() {
		return    protoDefinitionFile.getText()
        + SEPERATOR + loaderOptions.getSelectedIndex()
        + SEPERATOR + fileStructure.getSelectedIndex()
//        + SEPERATOR + splitOption.getSelectedIndex()
//        + SEPERATOR + numericFormat.getSelectedIndex()
    	+ SEPERATOR + protoFile.getSelectedIndex()
    	+ SEPERATOR + messageName.getSelectedIndex()
 //   	+ SEPERATOR + quote.getSelectedIndex()
		+ SEPERATOR + messageName.getSelectedItem();
	}

	/**
	 * Get Layout details
	 * @return record layout
	 */
	@Override
	public final ProtoLayoutDef getRecordLayout(String fileName) {
		ProtoLayoutDef ret = null;
        int fileStruc, copybooktype;
        String layoutName = protoDefinitionFile.getText();
        FileDescriptorSet dp;
                
        fileStruc = ((ComboOption) fileStructure.getSelectedItem()).index;
        copybooktype =  ((ComboOption) loaderOptions.getSelectedItem()).index;

        try {
    	   ProtoLayoutDef layout = getLayoutFromFile(fileStruc, fileName);
    	   if (layout != null) {
    		   return layout;
    	   }
    	   
	       if (layoutName == null ||  "".equals(layoutName)) {
	            protoDefinitionFile.requestFocus();
	            message.setText("You must enter a proto file name ");
	       } else {
	    	   if (	copybooktype == ConstClass.COPYBOOK_COMPILED_PROTO
	    		||	copybooktype == ConstClass.COPYBOOK_PROTO) {
		    	   dp = getFileDescriptorSet(copybooktype, layoutName);
	
		    	   int index = protoFile.getSelectedIndex();
		    	   FileDescriptor fd = ProtoHelper.getFileDescriptor(dp, index);
		    	   ret =  new ProtoLayoutDef(fd, fileStruc);
		    	   
		    	   ret.setPrimaryMessageIndex(messageName.getSelectedIndex());
	    	   }
	        }
        } catch (Exception e) {
            protoDefinitionFile.requestFocus();
            message.setText("You must enter a valid Record Layout name:\n" + e.getMessage());
            e.printStackTrace();
        } 

        return ret;
	}
	
	private FileDescriptorSet getFileDescriptorSet(int copybooktype, String layoutName)
	throws IOException {
		FileDescriptorSet dp = null;
		
 	   if (copybooktype == ConstClass.COPYBOOK_PROTO) {
		   dp = compileProto(layoutName);
	   } else if (copybooktype == ConstClass.COPYBOOK_COMPILED_PROTO){
		   dp = getFileDescriptor(layoutName);
	   }

 	   return dp;
	}
	
	private ProtoLayoutDef getLayoutFromFile(int fileStruc, String fileName) 
	throws IOException, RecordException {
		ProtoLayoutDef ret = null;
		AbstractLineReader reader = getReader(fileStruc, fileName);
		if (reader != null) {
			ret = (ProtoLayoutDef) reader.getLayout();
			
			ret.setPrimaryMessageIndex(messageName.getSelectedIndex());
		}
		
		return ret;
	}

	
	private FileDescriptorSet getFileDescriptionSet(int fileStruc, String fileName) 
	throws IOException, RecordException {
		FileDescriptorSet ret = null;
		AbstractLineReader reader = getReader(fileStruc, fileName);
		if (reader != null && (reader instanceof ProtoSelfDescribingReader)) {
			ret = ((ProtoSelfDescribingReader) reader).getFileDescriptorSet();
		}
		
		return ret;
	}

	private AbstractLineReader getReader(int fileStruc, String fileName) 
	throws IOException, RecordException {
		
		
	   AbstractLineReader ret = null;
		
//	   System.out.println(" FileStructure --> " + fileStruc);
 	   if ((fileStruc == Constants.IO_PROTO_SD_DELIMITED
 		|| fileStruc >= Constants.IO_PROTO_SD_SINGLE_MESSAGE)
	    && ! "".equals(fileName) && (new File(fileName)).exists()) {
		   ret = ProtoIOProvider.getInstance().getLineReader(fileStruc);
		   ret.open(fileName);
		   ret.close();
	   }

 	   return ret;
	}

	/**
	 * Get Layout details
	 * @param layoutName record layout name
	 * @return record layout
	 */
	@Override
	public final ProtoLayoutDef getRecordLayout(String layoutName, String fileName) {
		setLayoutName(layoutName);
		return getRecordLayout(fileName);
	}

    
	@Override
	public void reload() {

	}

	@Override
	public boolean setLayoutName(String layoutName) {
        StringTokenizer t = new StringTokenizer(layoutName, SEPERATOR);

        try {
        	int loader;
        	setupFields();
        	
        	listnersActive = false;
            protoDefinitionFile.setText(t.nextToken());
            
            loader = getIntToken(t);
            fileStructure.setSelectedIndex(getIntToken(t));
            loaderOptions.setSelectedIndex(loader);

            setFile(getIntToken(t));
            //protoFile.setSelectedIndex(getIntToken(t));
            messageName.setSelectedIndex(getIntToken(t));
//            quote.setSelectedIndex(getIntToken(t));
            messageName.setSelectedItem(t.nextToken());
            
			setFile(-1);
			setEditable();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	listnersActive = true;
        }
        setEditable();

		return false;
	}
	
	private void setupFields() {

		if (loaderOptions == null) {
			protoDefinitionFile   = new FileChooser(true, "Choose Layout");
			protoImportDir = new FileChooser(true, "Imbed Directory");
			protoImportDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
     		fileStructure = new JComboBox(ConstClass.getStructureOptions());
    		loaderOptions = new JComboBox(ConstClass.getCopybookLoaders());

    		protoFile = new JComboBox();
//        	quote          = new JComboBox(Common.QUOTE_LIST);
        	messageName = new JComboBox();
        	
        	setCombo(fileStructure, Consts.DEFAULT_PROTO_FILE_STRUCTURE);
        	setCombo(loaderOptions, Consts.DEFAULT_PROTO_DEFINITION_READER);
		}
	}

	
	private void setCombo(JComboBox combo, String key) {
		String value = Parameters.getString(key);
		
       	if (value != null && ! "".equals(value)) {
    		for (int i = 0; i < combo.getItemCount(); i++) {
    			if (value.equals(combo.getItemAt(i).toString())) {
    				combo.setSelectedIndex(i);
    				break;
    			}
    		}
    	}

	}
	
	
    /**
     * get the next integer Token
     * @param t StringTokenizer
     * @return next token as an integer
     */
    private int getIntToken(StringTokenizer t) {
        int ret = 0;
        String s = t.nextToken();

        try { 
            ret = Integer.parseInt(s);
        } catch (Exception e) {
        }

        return ret;
    }


	/**
	 * @param message the message to set
	 */
	public void setMessage(JTextArea message) {
		this.message = message;
	}


	/**
	 * @see net.sf.RecordEditor.re.openFile.AbstractLayoutSelection#getDataBaseNames()
	 */
	@Override
	public String[] getDataBaseNames() {
		return null;
	}


	/**
	 * @see net.sf.RecordEditor.re.openFile.AbstractLayoutSelection#setDatabaseIdx(int)
	 */
	@Override
	public void setDatabaseIdx(int idx) {
		
	}

	/**
	 * @see net.sf.RecordEditor.re.openFile.AbstractLayoutSelection#getDatabaseIdx()
	 */
	@Override
	public int getDatabaseIdx() {
		return 0;
	}

	/**
	 * @see net.sf.RecordEditor.re.openFile.AbstractLayoutSelection#getDatabaseName()
	 */
	@Override
	public String getDatabaseName() {
		return null;
	}

	/**
	 * @see net.sf.RecordEditor.re.openFile.AbstractLayoutSelection#formatLayoutName(java.lang.String)
	 */
	@Override
	public String formatLayoutName(String layoutName) {
		return Common.OPTIONS.DEFAULT_COPYBOOK_DIRECTORY.get() + layoutName;
	}


	/**
	 * @return the copybookFile
	 */
	public final FileChooser getCopybookFile() {
		return protoDefinitionFile;
	}
	

	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.re.openFile.AbstractLayoutSelection#isFileBasedLayout()
	 */
	@Override
	public boolean isFileBasedLayout() {
		return true;
	}
}
