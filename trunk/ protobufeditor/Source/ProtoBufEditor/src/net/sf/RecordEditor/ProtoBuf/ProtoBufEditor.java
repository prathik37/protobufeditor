/*
 * @Author Bruce Martin
 * Created on 12/01/2007
 *
 * Purpose:
 *
 * Changes
 * # Version 0.61 Bruce Martin 2007/04/14
 *   - Refactoring changes from moving classes to new packages, and
 *     creation of JRecord
 *   - Name changes
 *   - Better file exists checking and error messages
 */
package net.sf.RecordEditor.ProtoBuf;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JEditorPane;
import javax.swing.JTabbedPane;


import net.sf.JRecord.IO.AbstractLineIOProvider;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ConstClass;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.ProtoBuf.re.display.ProtoLayoutSelection;
import net.sf.RecordEditor.ProtoBuf.re.display.ShowProtoAction;
import net.sf.RecordEditor.edit.EditRec;

import net.sf.RecordEditor.edit.display.Action.HightlightMissingFields;
import net.sf.RecordEditor.edit.display.Action.VisibilityAction;
import net.sf.RecordEditor.edit.open.OpenFile;
import net.sf.RecordEditor.editProperties.EditOptions;
import net.sf.RecordEditor.editProperties.EditPropertiesPnl;

import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.common.Parameters;
import net.sf.RecordEditor.utils.common.ReActionHandler;
import net.sf.RecordEditor.utils.edit.ParseArgs;
import net.sf.RecordEditor.utils.edit.ReIOProvider;
import net.sf.RecordEditor.utils.screenManager.ReFrame;


/**
 * This class will Edit a file with a File-Layout (instead of using a DB copybook) 
 *
 * @author Bruce Martin
 *
 */
@SuppressWarnings("serial")
public class ProtoBufEditor extends EditRec {

	private static final String COMPC_SCREEN_DESC 
		= "<h1>protoc Run options</h1>"
		+ "<p>This screen lets you set the protoc Command and its extra options"
		;
	private static String[][]  READER_OPTIONS = {
		{Consts.DEFAULT_PROTO_DEFINITION_READER, "The default Proto reader (standard/compiled) proto files"},
		{Consts.DEFAULT_PROTO_FILE_STRUCTURE, "The default Proto Message Reader"},
	};

	private static final Object[][] PROTOC_OPTS = new Object[ConstClass.NUMBER_OF_PROTOC_OPTIONS + 1][];

	static {
		PROTOC_OPTS[0] = new Object[] {ConstClass.VAR_PROTOBUF_COMPILE, "protoc command", null, EditPropertiesPnl.FLD_TEXT, null};
		
		for (int i = 0; i < ConstClass.NUMBER_OF_PROTOC_OPTIONS; i++) {
			PROTOC_OPTS[i+1] = new Object[] {
						ConstClass.VAR_PROTOBUF_COMPILE_OPTS + (i), 
						"User supplied protoc option " + i,
						null,
						EditPropertiesPnl.FLD_TEXT,
						null
			};
		}
	};
	
	private String[] loaders = {Consts.COPYBOOK_PROTO, Consts.COPYBOOK_COMPILED_PROTO}; 
	private ComboBoxModel[] models = {
			new DefaultComboBoxModel(loaders),
			new DefaultComboBoxModel(ProtoIOProvider.getNames()),
	};
	
	
    /**
	 * Creating the File & record selection screen
	 *
	 * @param pInFile File to be read (optional)
	 * @param pInitialRow initial Row (optional)
	 * @param pInterfaceToCopyBooks interface to copybooks
	 */
	public ProtoBufEditor(final String pInFile,
	        	      final int pInitialRow) {
		this(pInFile, pInitialRow, ReIOProvider.getInstance());
	}

	/**
	 * Creating the File & record selection screen
	 *
	 * @param pInFile File to be read (optional)
	 * @param pInitialRow initial Row (optional)
	 * @param pIoProvider ioProvider to use when creating
	 *        lines
	 * @param pInterfaceToCopyBooks interface to copybooks
	 */
    public ProtoBufEditor(final String pInFile,
     	   final int pInitialRow,
    	   final AbstractLineIOProvider pIoProvider) {
        super(false, "Protocol Buffer Editor", null);

        ProtoIOProvider.register();
        EditOptions.setDefaultDetails(READER_OPTIONS, models);
        //BaseDisplay.registerTableEditor(ArrayDetails.class, new ArrayRender(), new ArrayTableEditor());
        
        OpenFile open = new OpenFile(pInFile, pInitialRow, pIoProvider,
                null, null, Parameters.getApplicationDirectory() + Consts.RECENT_FILES,
                Common.HELP_COBOL_EDITOR, new ProtoLayoutSelection());

        super.getEditMenu().addSeparator();
        super.getEditMenu().add(addAction(new VisibilityAction()));
        super.getEditMenu().add(new HightlightMissingFields() );

        this.setOpenFileWindow(open, 
//        		new AbstractAction("File Copy Menu") {
//
//					/**
//					 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//					 */
//					@Override
//					public void actionPerformed(ActionEvent e) {
//						CopyFileLayout.newMenu();			
//					}
//        		},
        		null,
				new AbstractAction("Compare Menu") {
		
					/**
					 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
					 */
					@Override
					public void actionPerformed(ActionEvent e) {
						CompareProtoLayout.newMenu();			
					}
				},
				false
		);
        
        super.getViewMenu().addSeparator();
        super.getViewMenu().add(newAction(ReActionHandler.SHOW_INVALID_ACTIONS));
        super.getViewMenu().add(addAction(new ShowProtoAction()));
    }


	@Override
	protected void editProperties(boolean includeJdbc, boolean includeWizardOptions) {

		EditOptions editOpts = new EditOptions(false, includeJdbc, includeWizardOptions, false);
		JTabbedPane protoPane = new JTabbedPane();
		protoPane.add(
				"protoc options",				
				new EditPropertiesPnl(editOpts.getParams(), COMPC_SCREEN_DESC, PROTOC_OPTS)
		);
		editOpts.add("ProtoBuf", protoPane);
		editOpts.displayScreen();
	}

	@Override
	protected void showAbout() {
		ReFrame aboutFrame = new ReFrame("About", null, null);
		JEditorPane aboutText = new JEditorPane("text/html",
				"The <b>Protocol Buffers Editor</b> is an editor for Protocol Buffers binary "
			  + "data files. It is built on top of the <b>RecordEditor</b><br><br><pre>"
			  +	" <br><b>Author:</b><br><br> "
			  + "\t<b>Bruce Martin</b>: Main author<br><br>"
			  + " <b>Websites:</b><br><br> " 
			  + "\t<b>RecordEditor          :</b> http://record-editor.sourceforge.net<br>"
			  + "\t<b>Protocol buffer Editor:</b> http://code.google.com/p/protobufeditor/"		  
			  + "</pre><br>"
		);
		
		aboutFrame.getContentPane().add(aboutText);
		aboutFrame.pack();
		aboutFrame.setVisible(true);
	}
	/**
	 * Edit a record oriented file
	 * @param pgmArgs program arguments
	 */
	public static void main(final String[] pgmArgs) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

			    ParseArgs args = new ParseArgs(pgmArgs);

			    new ProtoBufEditor(args.getDfltFile(), args.getInitialRow(), ProtoIOProvider.getInstance());
			}
		});
	}
}
