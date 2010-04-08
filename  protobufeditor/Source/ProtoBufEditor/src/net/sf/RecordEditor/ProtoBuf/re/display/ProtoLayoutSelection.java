package net.sf.RecordEditor.ProtoBuf.re.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;

import net.sf.JRecord.Log.AbsSSLogger;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ConstClass;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.common.Parameters;
import net.sf.RecordEditor.utils.openFile.AbstractLayoutSelection;
import net.sf.RecordEditor.utils.swing.BasePanel;
import net.sf.RecordEditor.utils.swing.FileChooser;

public class ProtoLayoutSelection extends AbstractLayoutSelection<ProtoLayoutDef>  {
	
	private static final String SEPERATOR = ",,";
	
	private static final String PROTO_COMPILE = "protoc";
	private static final String PROTO_COMPILE_OUTPUT = "--descriptor_set_out=";
	
	

	private FileChooser  protoDefinitionFile;


	private JComboBox fileStructure; // = new JComboBox(STRUCTURE_OPTIONS);
	private JComboBox loaderOptions; // = new JComboBox(COPYBOOK_LOADER);
	private JComboBox protoFile;
	private JComboBox messageName;
	
	
//    private JComboBox fieldSeparator;
//    private JComboBox quote;
	private JTextArea message = null;

//	private int mode = MODE_NORMAL;

	
	ActionListener fileListner = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			setFile(0);
			setEditable();
		}
	};

	ActionListener msgListner = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			setMsgCombo();
			setEditable();
		}
	};

 


	@Override
	public void addLayoutSelection(BasePanel pnl, JTextField file, JPanel goPanel,
			JButton layoutCreate1, JButton layoutCreate2) {
		addLayoutSelection(pnl, goPanel, layoutCreate1, layoutCreate2, null);
	}

	/**
	 * Add Selection with external layout file
	 * @param pnl panel to add fields to
	 * @param goPanel go panel to use
	 * @param layoutFile layout file
	 */
	public void addLayoutSelection(BasePanel pnl, JPanel goPanel, 	FileChooser layoutFile) {
		
		addLayoutSelection(pnl, goPanel, null, null, layoutFile);
	}

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

 	    protoDefinitionFile.setText(Common.DEFAULT_COPYBOOK_DIRECTORY);

	    if (layoutFile != null)  {
	    	//loaderOptions.setSelectedIndex(0);
	    	protoDefinitionFile = layoutFile;
	    	
		    pnl.addComponent("Output File Structure", fileStructure);
//		    pnl.addComponent("Output Numeric Format", numericFormat);

	    } else {
		    pnl.addComponent("File Structure", fileStructure);
		    
		    //loaderOptions.setSelectedIndex(0);
	    	pnl.addComponent("Type of Definition", loaderOptions);
	    	//pnl.addComponent("Split Copybook", splitOption);
	    	
	    	pnl.setGap(BasePanel.GAP0);
			pnl.addComponent("Proto Definition", protoDefinitionFile, protoDefinitionFile.getChooseFileButton());
		    pnl.setGap(BasePanel.GAP0);
		    
//		    pnl.addComponent("Numeric Format", numericFormat);
	    
		    pnl.addComponent("Proto File", protoFile);
		    pnl.addComponent("Primary Message", messageName);
//		    pnl.addComponent("Quote", quote);
		    pnl.setGap(BasePanel.GAP1);
			pnl.addComponent("", null, goPanel);
			if (goPanel != null) {
				pnl.setHeight(Math.max(BasePanel.NORMAL_HEIGHT * 3, goPanel.getPreferredSize().getHeight()));
			}
	    }
		
//	    } else {
//		    pnl.setGap(BasePanel.GAP1);
//	    }

	    loaderOptions.addActionListener(fileListner);
	    protoDefinitionFile.addFcFocusListener(new FocusAdapter() {
        	public void focusLost(FocusEvent e) {
        		 setFile(0);
           	}
        } );

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

    	System.out.println("Checking file " + (dp != null));
    	if (dp != null && dp.getFileCount() > 0) {
    		for (int i = 0; i < dp.getFileCount(); i++) {
    			protoFile.addItem(dp.getFile(i).getName());
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
    		protoFile.setSelectedIndex(0);
    		setupMsgCombo(dp, protoFile.getSelectedIndex());
    	}
    }

    private void setupMsgCombo(FileDescriptorSet dp, int idx) {
    	
    	messageName.removeAllItems();
    	
     	try {
	    	FileDescriptor fd = FileDescriptor.buildFrom(dp.getFile(idx), new FileDescriptor[] {});
			List<Descriptor> msgTypes = fd.getMessageTypes();
			int index =0;


			HashSet<Descriptor> usedRecords = new HashSet<Descriptor>();
			System.out.println("Setting Messages for index=" + idx + " Count=" + msgTypes.size());
			for (Descriptor d : msgTypes ) {
				messageName.addItem(d.getName());
				List<FieldDescriptor> fldList = d.getFields();
				for (FieldDescriptor field : fldList) {
					if (field.getJavaType().equals(JavaType.MESSAGE)) {
						usedRecords.add(field.getMessageType());
					}
				}	
			}
			for (Descriptor d : msgTypes ) {
				if (! usedRecords.contains(d)) {
					messageName.setSelectedIndex(index);
					break;
				}
			
				index += 1;
			}

    	} catch (Exception e) {
    		Common.logMsg("Error Setting up Messages: " + e.getMessage(), e);
		}
    }

    private FileDescriptorSet getProtoDesc() {
        int protoType;
        String layoutName = protoDefinitionFile.getText();
        FileDescriptorSet dp = null;
    
        protoType =  ((ConstClass.Option) loaderOptions.getSelectedItem()).index;
  	
        try {     
           	 if (layoutName == null ||  "".equals(layoutName)) {
            //copybookFile.requestFocus();
           		 message.setText("You must enter a Record Layout name ");
           	 } else if (protoType == ConstClass.COPYBOOK_PROTO) {
           		 dp = compileProto(layoutName);
           	 } else if (protoType == ConstClass.COPYBOOK_COMPILED_PROTO) {
             	 dp =getFileDescriptor(layoutName);
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
    	String dir = "";
    	String compiledFile;
    	String s, cmd;
   	
    	if (pos > 0) {
    		lastPart = layoutName.substring(pos + 1);
    		dir = "--proto_path=" + layoutName.substring(0, pos);
    	}
    	
    	compiledFile = Parameters.getPropertiesDirectory() 
		+ File.separator + "TempWork" + File.separator
		+ lastPart + "comp";
    	
    	cmd = PROTO_COMPILE 
    		+" " + dir
			+ " " + PROTO_COMPILE_OUTPUT 
			+ " " +compiledFile
			+ " " + layoutName +""
			;
    	String[] cmdAndArgs = {
    			  PROTO_COMPILE 
    			, layoutName
        		, dir
    			, PROTO_COMPILE_OUTPUT + compiledFile
     	};
    	
    	//System.out.println(cmd);
 
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
        
        fileStruc = ((ConstClass.Option) fileStructure.getSelectedItem()).index;
        copybooktype =  ((ConstClass.Option) loaderOptions.getSelectedItem()).index;

        try {
	        if (layoutName == null ||  "".equals(layoutName)) {
	            protoDefinitionFile.requestFocus();
	            message.setText("You must enter a proto file name ");
	            
	       } else if (	copybooktype == ConstClass.COPYBOOK_COMPILED_PROTO
	    		   		||	copybooktype == ConstClass.COPYBOOK_PROTO) {
	    	   if (copybooktype == ConstClass.COPYBOOK_PROTO) {
	    		   dp = compileProto(layoutName);
	    	   } else {
	    		   dp = getFileDescriptor(layoutName);
	    	   }

	    	   int index = protoFile.getSelectedIndex();
	    	   FileDescriptor fd = FileDescriptor.buildFrom(dp.getFile(index), new FileDescriptor[] {});
	    	   ret =  new ProtoLayoutDef(fd, fileStruc);

	    	   ret.setPrimaryMessageIndex(messageName.getSelectedIndex());
	        }
        } catch (Exception e) {
            protoDefinitionFile.requestFocus();
            message.setText("You must enter a valid Record Layout name:\n" + e.getMessage());
            e.printStackTrace();
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
            protoDefinitionFile.setText(t.nextToken());
            
            loader = getIntToken(t);
            loaderOptions.setSelectedIndex(loader);
            fileStructure.setSelectedIndex(getIntToken(t));

            setFile(getIntToken(t));
            //protoFile.setSelectedIndex(getIntToken(t));
            messageName.setSelectedIndex(getIntToken(t));
//            quote.setSelectedIndex(getIntToken(t));
            messageName.setSelectedItem(t.nextToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setEditable();

		return false;
	}
	
	private void setupFields() {

		if (loaderOptions == null) {
			protoDefinitionFile   = new FileChooser("Choose Layout");
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
	 * @see net.sf.RecordEditor.utils.openFile.AbstractLayoutSelection#getDataBaseNames()
	 */
	@Override
	public String[] getDataBaseNames() {
		return null;
	}


	/**
	 * @see net.sf.RecordEditor.utils.openFile.AbstractLayoutSelection#setDatabaseIdx(int)
	 */
	@Override
	public void setDatabaseIdx(int idx) {
		
	}

	/**
	 * @see net.sf.RecordEditor.utils.openFile.AbstractLayoutSelection#getDatabaseIdx()
	 */
	@Override
	public int getDatabaseIdx() {
		return 0;
	}

	/**
	 * @see net.sf.RecordEditor.utils.openFile.AbstractLayoutSelection#getDatabaseName()
	 */
	@Override
	public String getDatabaseName() {
		return null;
	}

	/**
	 * @see net.sf.RecordEditor.utils.openFile.AbstractLayoutSelection#formatLayoutName(java.lang.String)
	 */
	@Override
	public String formatLayoutName(String layoutName) {
		return Common.DEFAULT_COPYBOOK_DIRECTORY + layoutName;
	}


	/**
	 * @return the copybookFile
	 */
	public final FileChooser getCopybookFile() {
		return protoDefinitionFile;
	}
}
