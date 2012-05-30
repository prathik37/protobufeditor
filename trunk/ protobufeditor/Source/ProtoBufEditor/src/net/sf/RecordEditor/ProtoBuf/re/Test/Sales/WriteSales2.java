package net.sf.RecordEditor.ProtoBuf.re.Test.Sales;

import java.io.FileOutputStream;
import java.math.BigDecimal;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.FieldDetail;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.LayoutDetail;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.IO.AbstractLineReader;

import net.sf.JRecord.IO.CobolIoProvider;
import net.sf.JRecord.Numeric.Convert;
import net.sf.RecordEditor.ProtoBuf.re.Test.TestConst;


public class WriteSales2 {

    private String installDir     =  TestConst.INPUT_DIR;
    private String salesFile      = installDir + "DTAR020_tst1.bin";
    private String salesFileOut   = TestConst.OUTPUT_DIR + "DTAR020_tst1.bin";
    private String copybookName   = TestConst.COBOL_DIR + "DTAR020.cbl";

    /**
     * Example of LineReader / LineWrite classes
     */
    private WriteSales2() {
        super();

        int lineNum = 0;

        AbstractLine<LayoutDetail> saleRecord;

        try {
            int fileStructure = Constants.IO_FIXED_LENGTH;
            CobolIoProvider ioProvider = CobolIoProvider.getInstance();
            AbstractLineReader<LayoutDetail> reader  = ioProvider.getLineReader(
                   fileStructure, Convert.FMT_MAINFRAME,
                    CopybookLoader.SPLIT_NONE, copybookName, salesFile
            );
            FileOutputStream out = new FileOutputStream(salesFileOut);

            //Writer w = new FileWriter(salesFileOut + ".html");
            int fldNum = 0;
            
            
            FieldDetail keycodeField = reader.getLayout().getField(0, fldNum++);
            FieldDetail storeField = reader.getLayout().getField(0, fldNum++);
            FieldDetail dateField = reader.getLayout().getField(0, fldNum++);
            FieldDetail deptField = reader.getLayout().getField(0, fldNum++);

            FieldDetail qtyField     = reader.getLayout().getField(0, fldNum++);
            FieldDetail salesField   = reader.getLayout().getField(0, fldNum++);
            
            DTAR020.sale.Builder bld;
            DTAR020.sale aSale;
            
            //HtmlGenerator generator = new HtmlGenerator(w);

            while ((saleRecord = reader.read()) != null) {
                lineNum += 1;
                BigDecimal price = saleRecord.getFieldValue(salesField).asBigDecimal().multiply(
                		BigDecimal.valueOf(1000));
                bld =  DTAR020.sale.newBuilder()
                				.setKeycodeNo(saleRecord.getFieldValue(keycodeField).asInt())
                				.setStoreNo(saleRecord.getFieldValue(storeField).asInt())
                 				.setDeptNo(saleRecord.getFieldValue(deptField).asInt())
                				.setDATE(saleRecord.getFieldValue(dateField).asInt())
                				.setQtySold(saleRecord.getFieldValue(qtyField).asInt())
                				.setSalePrice(price.longValue());
               aSale = bld.build();
               
               //HtmlFormat.print(aSale, generator);
               aSale.writeDelimitedTo(out);
  
           }

            reader.close();
            out.close();
            System.out.println("Written: " + lineNum );
            //generator.print("</body></html>");
            //w.close();
         } catch (Exception e) {
            System.out.println("~~> " + lineNum + " " + e.getMessage());
            System.out.println();

            e.printStackTrace();
        }
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new WriteSales2();
	}

}
