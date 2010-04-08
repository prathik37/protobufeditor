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

import net.sf.JRecord.IO.AbstractLineIOProvider;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ArrayDetails;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.ProtoBuf.re.display.ProtoLayoutSelection;
import net.sf.RecordEditor.edit.EditRec;
import net.sf.RecordEditor.edit.OpenFile;
import net.sf.RecordEditor.edit.display.BaseDisplay;
import net.sf.RecordEditor.edit.display.Action.VisibilityAction;
import net.sf.RecordEditor.edit.display.array.ArrayInterface;
import net.sf.RecordEditor.edit.display.array.ArrayRender;
import net.sf.RecordEditor.edit.display.array.ArrayTableEditor;
import net.sf.RecordEditor.editProperties.EditOptions;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.common.Parameters;
import net.sf.RecordEditor.utils.common.ReActionHandler;
import net.sf.RecordEditor.utils.edit.ParseArgs;
import net.sf.RecordEditor.utils.edit.ReIOProvider;


/**
 * This class will Edit a file with a File-Layout (instead of using a DB copybook) 
 *
 * @author Bruce Martin
 *
 */
public class ProtoBufEditor extends EditRec {

	private  String[][]  options = {
		{Consts.DEFAULT_PROTO_DEFINITION_READER, "The default Proto reader (standard/compiled) proto files"},
		{Consts.DEFAULT_PROTO_FILE_STRUCTURE, "The default Proto Message Reader"},
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
        super(false, "Protocol Buffer Editor");

        ProtoIOProvider.register();
        EditOptions.setDefaultDetails(options, models);
        //BaseDisplay.registerTableEditor(ArrayDetails.class, new ArrayRender(), new ArrayTableEditor());
        
        OpenFile open = new OpenFile(pInFile, pInitialRow, pIoProvider,
                null, null, Parameters.getApplicationDirectory() + Consts.RECENT_FILES,
                Common.HELP_COBOL_EDITOR, new ProtoLayoutSelection());

        super.getEditMenu().addSeparator();
        super.getEditMenu().add(addAction(new VisibilityAction()));

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
    }


	/**
	 * Edit a record oriented file
	 * @param pgmArgs program arguments
	 */
	public static void main(final String[] pgmArgs) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//JFrame.setDefaultLookAndFeelDecorated(true);
			    ParseArgs args = new ParseArgs(pgmArgs);

			    new ProtoBufEditor(args.getDfltFile(), args.getInitialRow(), ProtoIOProvider.getInstance());
			        	//new CopyBookDbReader());
			}
		});
	}
}
