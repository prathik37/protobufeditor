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
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;

import net.sf.JRecord.IO.AbstractLineIOProvider;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ConstClass;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoIOProvider;
import net.sf.RecordEditor.ProtoBuf.common.Const;
import net.sf.RecordEditor.ProtoBuf.re.display.ProtoLayoutSelection;
import net.sf.RecordEditor.ProtoBuf.re.display.ShowProtoAction;
import net.sf.RecordEditor.ProtoBuf.summary.ProtoSummaryStore;
import net.sf.RecordEditor.edit.EditRec;
import net.sf.RecordEditor.edit.display.Action.HighlightMissingFields;
import net.sf.RecordEditor.edit.display.Action.VisibilityAction;
import net.sf.RecordEditor.edit.open.OpenFile;
import net.sf.RecordEditor.re.editProperties.DefaultOptModel;
import net.sf.RecordEditor.re.editProperties.EditOptions;
import net.sf.RecordEditor.re.editProperties.EditPropertiesPnl;
import net.sf.RecordEditor.re.util.ReIOProvider;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.common.ReActionHandler;
import net.sf.RecordEditor.utils.edit.ParseArgs;
import net.sf.RecordEditor.utils.lang.ReAbstractAction;
import net.sf.RecordEditor.utils.params.Parameters;
import net.sf.RecordEditor.utils.screenManager.ReFrame;
import net.sf.RecordEditor.utils.swing.ComboBoxs.EnglishStrModel;
import net.sf.RecordEditor.utils.swingx.TipsManager;



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
	private static final String LOOKUP_SCREEN_DESC
	= "<h1>Lookup options / Direcories</h1>"
	+ "<p>This screen lets you update the Proto lookup options + specify Directories"
	;
	private static String[][]  READER_OPTIONS = {
		{Consts.DEFAULT_PROTO_DEFINITION_READER, "The default Proto reader (standard/compiled) proto files"},
		{Consts.DEFAULT_PROTO_FILE_STRUCTURE,    "The default Proto Message Reader"},
	};

	private static final Object[][] PROTOC_OPTS = new Object[ConstClass.NUMBER_OF_PROTOC_OPTIONS + 1][];
	private static final Object[][] LOOKUP_OPTIONS = {
        {Const.USE_EXTENDED_LOOKUP, "Use Extended Proto Lookup", null, EditPropertiesPnl.FLD_BOOLEAN, null}, // CHECK
        {Const.CHECK_PREVIOUS_PROTO, "Check to make sure the proto on a similarly named file is the correct proto",  null, EditPropertiesPnl.FLD_BOOLEAN, "Check previous used Proto"}, // Checked

        {"", "", null, EditPropertiesPnl.FLD_EMPTY, null},

        {"DefaultFileDirectory",	"Directory where the Editor Starts in (if no file specified)", null, EditPropertiesPnl.FLD_DIR, "Directory where the Editor Starts in"},
        {Parameters.COPYBOOK_DIRECTORY, "Directory where proto files are stored", null, EditPropertiesPnl.FLD_DIR, null},
	};


	static {
        Common.OPTIONS.xsltAvailable.set(false);

        ReIOProvider.register();
        ProtoIOProvider.register();

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
	private EnglishStrModel[] models = {
			DefaultOptModel.newModel("ProtoIdlLoader", loaders),
			DefaultOptModel.newModel(ProtoIOProvider.getNames()),
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

        EditOptions.setDefaultDetails(READER_OPTIONS, models);
        //BaseDisplay.registerTableEditor(ArrayDetails.class, new ArrayRender(), new ArrayTableEditor());

        OpenFile open = new OpenFile(pInFile, pInitialRow, pIoProvider,
                null, null, Parameters.getApplicationDirectory() + Consts.RECENT_FILES,
                Const.HELP_PROTOBUF_EDITOR, new ProtoLayoutSelection());

        super.getEditMenu().addSeparator();
        super.getEditMenu().add(addAction(new VisibilityAction()));
        super.getEditMenu().add(new HighlightMissingFields() );

        this.setOpenFileWindow(
        		open,
        		null,
				new ReAbstractAction("Compare Menu") {

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

        if (Common.OPTIONS.showRecordEditorTips.isSelected() && TipsManager.tipsModulePresent()) {
        	TipsManager.startTips(this, Parameters.getSytemJarFileDirectory() + "/ProtoBufEditor_TipOfTheDay.properties",
        					  Parameters.SHOW_RECORDEDITOR_TIPS);
        }
    }


	@Override
	protected void editProperties(boolean includeJdbc, boolean includeWizardOptions) {

		EditOptions editOpts = new EditOptions(false, includeJdbc, includeWizardOptions, false);
		JTabbedPane protoPane = new JTabbedPane();
		protoPane.add(	"protoc options",
						new EditPropertiesPnl(editOpts.getParams(), COMPC_SCREEN_DESC, PROTOC_OPTS)
		);
		protoPane.add(	"Lookup / Directories",
						new EditPropertiesPnl(editOpts.getParams(), LOOKUP_SCREEN_DESC, LOOKUP_OPTIONS)
				);
		editOpts.add("ProtoBuf", protoPane);
		editOpts.displayScreen();
	}



	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.utils.screenManager.ReMainFrame#addWebsitesToHelpMenu(javax.swing.JMenu)
	 */
	@Override
	protected void addWebsitesToHelpMenu(JMenu helpMenu) {

		try {
			helpMenu.add(
				new ShowURI(
						"ProtoBuf Introduction",
						Common.formatHelpURL("ProtoBufIntro.htm").toURI()));

			super.addWebsitesToHelpMenu(helpMenu);

			helpMenu.addSeparator();
			helpMenu.add(new ShowURI(
					"ProtoBuf Editor's Web Page",
					new URI("http://code.google.com/p/protobufeditor/")));
			helpMenu.add(new ShowURI(
					"ProtoBuf Editor's Discussions",
					new URI("https://groups.google.com/forum/?fromgroups#!forum/protobufeditor-users")));
			helpMenu.addSeparator();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void showAbout() {
		ReFrame aboutFrame = new ReFrame("", "About", null, null);
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

		try {
			Common.OPTIONS.loadPoScreens.set(false);
		} catch (Exception e) {
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				ProtoSummaryStore.initialise();
			    ParseArgs args = new ParseArgs(pgmArgs);

			    new ProtoBufEditor(args.getDfltFile(), args.getInitialRow(), ProtoIOProvider.getInstance());
			}
		});
	}
}
