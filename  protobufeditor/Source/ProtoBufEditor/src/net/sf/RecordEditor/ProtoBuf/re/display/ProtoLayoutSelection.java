package net.sf.RecordEditor.ProtoBuf.re.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.Log.AbsSSLogger;
import net.sf.JRecord.Log.TextLog;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ConstClass;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoDelimitedByteReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoSelfDescribingReader;
import net.sf.RecordEditor.ProtoBuf.common.Const;
import net.sf.RecordEditor.ProtoBuf.common.Utils;
import net.sf.RecordEditor.ProtoBuf.summary.ProtoSummary;
import net.sf.RecordEditor.ProtoBuf.summary.ProtoSummaryStore;
import net.sf.RecordEditor.re.openFile.AbstractLayoutSelection;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.common.StreamUtil;
import net.sf.RecordEditor.utils.lang.LangConversion;
import net.sf.RecordEditor.utils.params.BoolOpt;
import net.sf.RecordEditor.utils.params.Parameters;
import net.sf.RecordEditor.utils.swing.BasePanel;
import net.sf.RecordEditor.utils.swing.FileChooser;
import net.sf.RecordEditor.utils.swing.SwingUtils;
import net.sf.RecordEditor.utils.swing.Combo.ComboOption;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;

public class ProtoLayoutSelection
   extends AbstractLayoutSelection
implements ProtoLayoutActionInterface {

	private static final String SEPERATOR = ",,";

	public static final String NULL_STR = "$$Empty$$";

	private static final BoolOpt CHECK_NEW_PROTO = new BoolOpt(Const.CHECK_PREVIOUS_PROTO, true);
	private static final String NO_PROTO_FILE = LangConversion.convert("You must enter a Proto Definition File");

	private FileChooser  protoDefinitionFile;
	private FileChooser  protoImportDir;


	private JComboBox fileStructure; // = new JComboBox(STRUCTURE_OPTIONS);
	private JComboBox loaderOptions; // = new JComboBox(COPYBOOK_LOADER);
	private JComboBox protoFile;
	private JComboBox messageName;
	private JTextField fileField;

	private JTextArea message = null;
	private JButton protoSearchBtn = SwingUtils.newButton("Proto Search", Common.getRecordIcon(Common.ID_SEARCH_ICON));


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

	private FocusAdapter focuslistner = new FocusAdapter() {
    	public void focusLost(FocusEvent e) {
    		protoFileChanged();
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

 	    protoImportDir.setText(Parameters.getString(ConstClass.VAR_PROTO_IMBED_DIR));

	    if (layoutFile != null)  {
	    	protoDefinitionFile = layoutFile;

		    pnl.addLine("Output File Structure", fileStructure);
	    } else {
	    	protoDefinitionFile.setText(Common.OPTIONS.DEFAULT_COPYBOOK_DIRECTORY.get());

		    pnl.addLine("File Structure", fileStructure);

		    //loaderOptions.setSelectedIndex(0);
	    	pnl.addLine("Type of Definition", loaderOptions, protoSearchBtn);
	    	//pnl.addComponent("Split Copybook", splitOption);

	    	pnl.setGap(BasePanel.GAP0);
			pnl.addLine("Proto Definition", protoDefinitionFile, protoDefinitionFile.getChooseFileButton());
			pnl.addLine("Proto Import Directory", protoImportDir, protoImportDir.getChooseFileButton());
		    pnl.setGap(BasePanel.GAP0);

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


	    protoDefinitionFile.addFcFocusListener(focuslistner);
	    protoImportDir.addFcFocusListener(focuslistner);

		setEditable();

		protoSearchBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String s = fileField.getText();
				File f;
				if ("".equals(s)) {
					setMessageTxt("You must Enter a filename");
				} else if (! (f = new File(s)).exists()) {
					setMessageTxt("File: {0} does not exist", s);
				} else if (f.isDirectory()) {
					setMessageTxt("File: {0} is a directory, it should be a protocol buffers file", s);
				} else {
					byte[] b = getFileBytes();
					if (b == null) {
						setMessageTxt("No data to check");
					} else {
						new ProtoSearchPnl(b, s, ProtoLayoutSelection.this);
					}
				}
			}
		});
	}



    /**
     * set editable
     */
    private void setEditable() {

    }


    /* (non-Javadoc)
	 * @see net.sf.RecordEditor.re.openFile.AbstractLayoutSelection#notifyFileNameChanged(java.lang.String)
	 */
	@Override
	public void notifyFileNameChanged(String newFileName) {
		fileChangedImp();

		int fileStruc = ((ComboOption) fileStructure.getSelectedItem()).index;

		switch (fileStruc) {
		case Constants.IO_PROTO_DELIMITED:
		case Constants.IO_PROTO_SINGLE_MESSAGE:
			byte[] b = getFileBytes();
			if (b != null) {
				ProtoSummaryStore.getInstance().lookupFileDesc(b, this);
			}
		}

	}

	private byte[] getFileBytes() {
		byte[] b = null;
		int fileStruc = ((ComboOption) fileStructure.getSelectedItem()).index;

		String s = fileField.getText();
		File f = new File(s);

		if (f.exists() && f.length() > 0) {
			switch (fileStruc) {
			case Constants.IO_PROTO_DELIMITED:
				ProtoDelimitedByteReader r = new ProtoDelimitedByteReader();
				try {
					r.open(s);
					b = r.read();

					r.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Constants.IO_PROTO_SD_SINGLE_MESSAGE:
			case Constants.IO_PROTO_SINGLE_MESSAGE:
				if (f.length() < 3000000) {
					try {
						b = StreamUtil.read(new FileInputStream(f), (int) f.length());
					} catch (Exception ex) {
					}
				}
			}
		}
		return b;
	}


	public final void fileChanged() {
    	if (listnersActive) {
    		fileChangedImp();
    	}
    }


	private void fileChangedImp() {

		String s = fileField.getText();
		File f = new File(s);
		int type = Constants.IO_PROTO_SINGLE_MESSAGE;

		if (f.exists() && f.length() > 0) {
			ProtoDelimitedByteReader r = new ProtoDelimitedByteReader();
			try {
				int i = 0;
				byte[] b, bd;

				Descriptor desc = Utils.getFileDescriptor().getMessageTypes().get(0);
				r.open(s);
				if ((b = r.read()) != null) {
					DynamicMessage.newBuilder(desc).mergeFrom(b);
					bd = b;
					while ((b = r.read()) != null && i++ < 200) {
						DynamicMessage.newBuilder(desc).mergeFrom(b);
					}

					type = Constants.IO_PROTO_DELIMITED;
					try {
						FileDescriptorSet.Builder bld = FileDescriptorSet.newBuilder().mergeFrom(bd);
						if (bld.getFileCount() > 0) {
							type = Constants.IO_PROTO_SD_DELIMITED;
						}
					} catch (Exception e) {

					}
				}


			} catch (IOException e) {
				if (f.length() < 3000000) {
					try {
						FileDescriptorSet fd = getFileDescriptionSet(Constants.IO_PROTO_SD_SINGLE_MESSAGE, s);
						if ( Utils.isFileDescriptorSet(fd) ) {
							type = Constants.IO_PROTO_SD_SINGLE_MESSAGE;
						}
					} catch (Exception ex) {
					}
				}
			} finally {
//				System.out.println("Read ProtoFile");
				try {
					r.close();
				} catch (Exception e) {
				}
			}

			boolean oldListnersActive = listnersActive;
			try {
				int fileStruc = ((ComboOption) fileStructure.getSelectedItem()).index;
				listnersActive = false;

				if (type == Constants.IO_PROTO_SINGLE_MESSAGE) {
					if (fileStruc != Constants.IO_PROTO_SINGLE_MESSAGE
					&&  fileStruc != Constants.IO_PROTO_SD_SINGLE_MESSAGE) {
						fileStructure.setSelectedIndex(ConstClass.getFileStructureIndex(type));
					}
				} else if (type == Constants.IO_PROTO_DELIMITED) {
					if (fileStruc != Constants.IO_PROTO_DELIMITED
					   &&  fileStruc != Constants.IO_PROTO_SD_DELIMITED) {
						fileStructure.setSelectedIndex(ConstClass.getFileStructureIndex(type));
					}
				} else {
					fileStructure.setSelectedIndex(ConstClass.getFileStructureIndex(type));
					setFile(-1);
				}
			} finally {
				listnersActive = oldListnersActive || listnersActive;
			}

		}
    }

    private void protoFileChanged() {

		if (listnersActive) {
			setProtoType();
			setFile(-1);
			ProtoSummaryStore.getInstance().addProtoDirectory(protoImportDir.getText());
		}
    }

    private void setProtoType() {

		String s = protoDefinitionFile.getText();
		File f = new File(s);

		if (f.exists() && f.length() > 0) {
			int type = ConstClass.COPYBOOK_PROTO;

			if ( Utils.isFileDescriptorSet(s) ) {
				type = ConstClass.COPYBOOK_COMPILED_PROTO;
			}

			listnersActive = false;
			try {
				loaderOptions.setSelectedIndex(ConstClass.getLoaderIndex(type));
			} finally {
				listnersActive = true;
			}
		}
    }

    private void setFile(int idx) {

    	String lName = protoDefinitionFile.getText();
    	File f;
    	if (("".equals(lName) || (!(f = new File(lName)).exists()) || f.isDirectory())
    	&& (! isSelfDescribing()))  {
    		return;
    	}
    	protoFile.removeActionListener(msgListner);
    	protoFile.removeAllItems();
    	FileDescriptorSet dp = getProtoDesc();

     	//System.out.println("Checking file " + (dp != null));
    	if (dp != null && dp.getFileCount() > 0) {
    		for (int i = 0; i < dp.getFileCount(); i++) {
    			protoFile.addItem(dp.getFile(i).getName());
    		}

    		if (idx < 0) {
    			HashSet<String> usedRecords = new HashSet<String>();
    			FileDescriptor[] fileDescs = new FileDescriptor[dp.getFileCount()];
    			List<Descriptor> msgTypes;

    			ProtoSummaryStore.getInstance().addFileDescriptor(protoDefinitionFile.getText(), dp);
    			idx = 0;
    			Utils.addMessages(dp, usedRecords, fileDescs);

    			for (int i = 0; i < dp.getFileCount(); i++) {
    				if (fileDescs[i] != null) {
						//System.out.println(" -- " + fileDescs[i].getName());
    					msgTypes = fileDescs[i].getMessageTypes();
	    				for (Descriptor d : msgTypes ) {
	    					if (! usedRecords.contains(d.getFullName())) {
	    						idx = i;
	    						break;
	    					}
	    				}

    				}
    			}
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
			int index = 0;


			HashSet<String> usedRecords = new HashSet<String>();

			Utils.addMessages(dp, usedRecords, new FileDescriptor[dp.getFileCount()]);
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
    		Common.logMsg(AbsSSLogger.ERROR, "Error Setting up Messages:", e.getMessage(), e);
    		e.printStackTrace();
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
      			message.setText(NO_PROTO_FILE);
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
    	return Utils.compileProto(layoutName, protoImportDir.getText(), message);
    }


	@Override
	public String getLayoutName() {
		String s = NULL_STR;
		if (messageName.getSelectedItem() != null) {
			s = getStr(messageName.getSelectedItem().toString());
		}
		return    getStr(protoDefinitionFile.getText())
        + SEPERATOR + loaderOptions.getSelectedIndex()
        + SEPERATOR + fileStructure.getSelectedIndex()
//        + SEPERATOR + splitOption.getSelectedIndex()
//        + SEPERATOR + numericFormat.getSelectedIndex()
    	+ SEPERATOR + protoFile.getSelectedIndex()
    	+ SEPERATOR + messageName.getSelectedIndex()
 //   	+ SEPERATOR + quote.getSelectedIndex()
		+ SEPERATOR + s;
	}

	private String getStr(String s) {
		if (s == null || "".equals(s)) {
			s = NULL_STR;
		}

		return s;
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
	            message.setText(NO_PROTO_FILE);
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
            message.setText(LangConversion.convert("You must enter a valid Proto File:") + "\n" + e.getMessage());
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
		   dp =  Utils.getFileDescriptor(layoutName);
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

	private boolean isSelfDescribing() {
		boolean ret = false;
		int fileStruc = ((ComboOption) fileStructure.getSelectedItem()).index;
		switch (fileStruc) {
		case Constants.IO_PROTO_SD_DELIMITED:
		case Constants.IO_PROTO_SD_SINGLE_MESSAGE:
			ret = true;
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
        	ProtoSummaryStore str = ProtoSummaryStore.getInstance();
        	String lname = getStringTok(t);
        	try {
	        	ProtoSummary.Proto p = str.lookupProtoSummary(lname);
	        	if (p == null) {
	        		FileDescriptorSet fd = Utils.getDescriptor(lname, new TextLog());
	        		p = str.addFileDescriptor(lname, fd);
	        	}
	        	setupFields();

	        	listnersActive = false;
	        	protoDefinitionFile.removeFocusListener(focuslistner);

	        	if (p != null) {
	        		fileChangedImp();
	        		byte[] b = getFileBytes();
					if (b != null) {
						DynamicMessage.Builder builder = DynamicMessage.newBuilder(ProtoSummaryStore.desc);

						if (CHECK_NEW_PROTO.isSelected()) {
							if (str.checkFileDesc(b, p, builder.mergeFrom(b).getUnknownFields(), this)) {
								return false;
							} else if (str.lookupFileDesc(b, this)) {
								return false;
							}
						}
	        		}
	        	}
        	} catch (Exception ex) {
                ex.printStackTrace();
        	}
            protoDefinitionFile.setText(lname);

            loader = getIntToken(t);
            fileStructure.setSelectedIndex(getIntToken(t));
            loaderOptions.setSelectedIndex(loader);

            setFile(getIntToken(t));
            //protoFile.setSelectedIndex(getIntToken(t));
            messageName.setSelectedIndex(getIntToken(t));
//            quote.setSelectedIndex(getIntToken(t));
            messageName.setSelectedItem(getStringTok(t));

            fileChanged();
			setFile(-1);
			setEditable();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	listnersActive = true;
        	if (! contains(protoDefinitionFile.getFocusListeners(), focuslistner)) {
        		protoDefinitionFile.addFocusListener(focuslistner);
        	}
        }
        setEditable();

		return false;
	}

	private boolean contains(FocusListener[] a, FocusListener l) {
		if (a != null) {
			for (FocusListener item : a) {
				if (item == l) {
					return true;
				}
			}
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.ProtoBuf.summary.FileDescriptionCallback#setSchema(java.lang.String, int, int)
	 */
	@Override
	public void setSchema(String schemaFile, int fileNo, int msgNo) {


		try {
			protoDefinitionFile.removeFocusListener(focuslistner);
			protoDefinitionFile.setText(schemaFile);
			setProtoType();
			setFile(fileNo);
			messageName.setSelectedIndex(msgNo);
		} finally {
			protoDefinitionFile.addFocusListener(focuslistner);
		}
	}

	private String getStringTok(StringTokenizer tok) {

		String s = tok.nextToken();
		if (s == null || NULL_STR.equals(s)) {
			s = "";
		}
		return s;
	}
	private void setupFields() {

		if (loaderOptions == null) {
			protoDefinitionFile   = new FileChooser(true, "Choose Layout");
			protoImportDir = new FileChooser(true, "Imbed Directory");
			protoImportDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

     		fileStructure = new JComboBox(ConstClass.getStructureOptions());
    		loaderOptions = new JComboBox(ConstClass.getCopybookLoaders());

    		protoFile = new JComboBox();
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

    private void setMessageTxt(String s) {
    	message.setText(LangConversion.convert(s));
    }


    private void setMessageTxt(String s, String param) {
    	message.setText(LangConversion.convert(s, param));
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
