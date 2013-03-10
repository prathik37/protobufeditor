package net.sf.RecordEditor.ProtoBuf.re.display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import net.sf.JRecord.Log.TextLog;
import net.sf.RecordEditor.ProtoBuf.common.Const;
import net.sf.RecordEditor.ProtoBuf.common.Utils;
import net.sf.RecordEditor.ProtoBuf.summary.FileDescriptionCallback;
import net.sf.RecordEditor.ProtoBuf.summary.ProtoSummaryStore;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.lang.LangConversion;
import net.sf.RecordEditor.utils.screenManager.ReFrame;
import net.sf.RecordEditor.utils.swing.BaseHelpPanel;
import net.sf.RecordEditor.utils.swing.BasePanel;
import net.sf.RecordEditor.utils.swing.FileChooser;
import net.sf.RecordEditor.utils.swing.SwingUtils;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

@SuppressWarnings("serial")
public class ProtoSearchPnl extends BaseHelpPanel {

    private static final String[] DIR_COL_HEADINGS = LangConversion.convertColHeading(
			"ProtoSearch - Directories to be searched",
			new String[] {"Proto Directory"});
    private static final String[] PROTO_COL_HEADINGS = LangConversion.convertColHeading(
			"ProtoSearch - Matching Proto definition files",
			new String[] {"Proto Filename", "File Name", "Message Name"});
    private static final int MAX_TABLE_WIDTH = 70 * SwingUtils.ONE_CHAR_TABLE_CELL_WIDTH;

    private static final String PANEL_DESCRIPTION =
              "<h2>Proto Search</h2>"
            + "<p>This screen lets you search for a proto definition file that matches<br/>"
            + "a Protocol Buffers data file. You can add extra directories to be searched<br/>"
            + "(these will be permanently added to the search directories)</p>"
            + "<p>Double click on the proto file in the bottom table to select it.</p>"
            ;

    private ReFrame frame;
    private final ProtoMdl protoMdl = new ProtoMdl();
    private final JTextField filenameTxt = new JTextField();
    private JTable dirTbl;
    private final FileChooser dir1 = new FileChooser();
    private final FileChooser dir2 = new FileChooser();
    private final JButton runBtn = new JButton("search");
    private final JCheckBox keepOpenChk = new JCheckBox();
    private final JTable protoTbl = new JTable(protoMdl);
    private ProtoDetails[] protos = {};
    private ArrayList<ProtoDetails> tmpProtos = new ArrayList<ProtoDetails>();

    private final byte[] protoMsg;
    private final ProtoLayoutActionInterface protoAction;


    public ProtoSearchPnl(byte[] protoMsg, String filename, ProtoLayoutActionInterface action) {

        this.protoMsg = protoMsg;
        this.filenameTxt.setText(filename);
        this.protoAction = action;

        init_100_Setup();
        init_200_LayoutScreen();
        init_300_Finalise();
    }


    private void init_100_Setup() {
        ProtoSummaryStore protoSum = ProtoSummaryStore.getInstance();
        String[] pD = protoSum.getProtoDirextories();
        String[][] pDirs = new String[pD.length][];

        for (int i = 0; i < pD.length; i++) {
            pDirs[i] = new String[] {pD[i]};
        }

        dirTbl = new JTable(pDirs, DIR_COL_HEADINGS);
        keepOpenChk.setSelected(false);
        dir1.setText("");
        dir2.setText("");

        protoTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }


    private void init_200_LayoutScreen() {

        this.addComponent(1, 5, SwingUtils.TIP_HEIGHT, BasePanel.GAP1,
                BasePanel.FULL, BasePanel.FULL,
                new JEditorPane("text/html",
                		LangConversion.convertId(LangConversion.ST_MESSAGE, "ProtoBuf_ProtoSearch",PANEL_DESCRIPTION)));

        addLine("Standard Directories:", null);
        addComponent(1, 5,
                SwingUtils.calculateTableHeight(dirTbl.getRowCount(), SwingUtils.TABLE_ROW_HEIGHT * 8),
                BasePanel.GAP0,
                BasePanel.FULL, BasePanel.FULL,
                new JScrollPane(dirTbl));

        setGap(GAP1);

        addLine("Proto Directory 1", dir1, dir1.getChooseFileButton());
        addLine("Proto Directory 2", dir2, dir2.getChooseFileButton());

        setGap(GAP1);
        addLine("File", this.filenameTxt);

        setGap(GAP1);

        addLine("Keep open", keepOpenChk, runBtn);

        setGap(GAP1);

        addComponent(1, 5, SwingUtils.calculateTableHeight(8, SwingUtils.TABLE_ROW_HEIGHT * 9),
                BasePanel.GAP,
                BasePanel.FULL, BasePanel.FULL,
                protoTbl);
    }


    private void init_300_Finalise() {

    	setHelpURL(Common.formatHelpURL(Const.HELP_PROTOSEARCH));
        search();

        filenameTxt.setEditable(false);
        runBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                search();
                Common.calcColumnWidths(protoTbl, 0, MAX_TABLE_WIDTH);
            }
        });

        protoTbl.addMouseListener(new MouseAdapter() {

            /* (non-Javadoc)
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getClickCount()) {
                case 2:
                case 3:
                    int row = protoTbl.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < protos.length) {
                        ProtoDetails p = protos[row];
                        protoAction.setSchema(p.names[0], p.fileNo, p.msgNo);
                        protoAction.loadFile(false);
                        frame.setVisible(keepOpenChk.isSelected());
                    }
                }
            }

        });

        frame = new ReFrame("", "Proto Search", null);
        frame.setDefaultCloseOperation(ReFrame.DISPOSE_ON_CLOSE);
        frame.addMainComponent(this);
        frame.setVisible(true);
        Common.calcColumnWidths(protoTbl, 0, MAX_TABLE_WIDTH);

    }


    private void search() {
        ProtoSummaryStore str = ProtoSummaryStore.getInstance();
        addDir(str, dir1);
        addDir(str, dir2);

        tmpProtos.clear();
        str.checkProtoDirs();
        str.lookupAllFileDesc(protoMsg, new FileDescriptionCallback() {

            @Override
            public void setSchema(String schemaFile, int fileNo, int msgNo) {
                FileDescriptorSet fd = Utils.getDescriptor(schemaFile, new TextLog());
                String 	s = "",
                        t = "";
                if (fd != null) {
                    try {
                        s = fd.getFile(fileNo).getName();
                        t = fd.getFile(fileNo).getMessageType(msgNo).getName();
                    } catch (Exception e) {

                    }
                }
                tmpProtos.add(new ProtoDetails(schemaFile, s, t, fileNo, msgNo));
            }
        });

        protos = new ProtoDetails[tmpProtos.size()];
        protos = tmpProtos.toArray(protos);
        protoMdl.fireTableDataChanged();
    }

    private void addDir(ProtoSummaryStore str, JTextField dir) {
        String s = dir.getText();
        if (! "".equals(s)) {
            str.addProtoDir(s);
        }
    }


    private class ProtoMdl extends AbstractTableModel {


        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getRowCount()
         */
        @Override
        public int getRowCount() {
            return protos.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return protos[rowIndex].names[columnIndex];
        }

        /* (non-Javadoc)
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        @Override
        public int getColumnCount() {
            return PROTO_COL_HEADINGS.length;
        }


        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnName(int)
         */
        @Override
        public String getColumnName(int column) {
            return PROTO_COL_HEADINGS[column];
        }
    }

    private class ProtoDetails {
        public final String[] names = new String[3];
        public final int fileNo, msgNo;


        public ProtoDetails(String protoName, String fileName, String msgName,
                int fileNo, int msgNo) {
            super();
            this.names[0] = protoName;
            this.names[1] = fileName;
            this.names[2] = msgName;
            this.fileNo = fileNo;
            this.msgNo = msgNo;
        }



    }
}
