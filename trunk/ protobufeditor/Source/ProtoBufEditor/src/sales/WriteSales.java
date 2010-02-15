package sales;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;

import com.google.protobuf.HtmlFormat;
import com.google.protobuf.HtmlFormat.HtmlGenerator;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.FieldDetail;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.LayoutDetail;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.IO.StandardLineReader;

import net.sf.JRecord.IO.CobolIoProvider;
import net.sf.JRecord.Numeric.Convert;


public class WriteSales {

    private String installDir     =  "/home/bm/Programs/JRecord/SampleFiles/";
    private String salesFile      = installDir + "DTAR020.bin";
    private String salesFileOut   = "/home/bm/Work/Temp/" + "protoSales.bin";
    private String copybookName   = "/home/bm/Programs/JRecord/CopyBook/Cobol/" + "DTAR020.cbl";

    /**
     * Example of LineReader / LineWrite classes
     */
    private WriteSales() {
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

            Writer w = new FileWriter(salesFileOut + ".html");
            int fldNum = 0;
            
            
            FieldDetail keycodeField = reader.getLayout().getField(0, fldNum++);
            FieldDetail storeField = reader.getLayout().getField(0, fldNum++);
            FieldDetail dateField = reader.getLayout().getField(0, fldNum++);
            FieldDetail deptField = reader.getLayout().getField(0, fldNum++);

            FieldDetail qtyField     = reader.getLayout().getField(0, fldNum++);
            FieldDetail salesField   = reader.getLayout().getField(0, fldNum++);
            
            Sales.sale.Builder bld;
            Sales.sale aSale;
            
            HtmlGenerator generator = new HtmlGenerator(w);

            while ((saleRecord = reader.read()) != null) {
                lineNum += 1;
                BigDecimal price = saleRecord.getFieldValue(salesField).asBigDecimal().multiply(
                		BigDecimal.valueOf(1000));
                bld =  Sales.sale.newBuilder()
                				.setKeycode(saleRecord.getFieldValue(keycodeField).asInt())
                				.setStore(saleRecord.getFieldValue(storeField).asInt())
                 				.setDepartment(saleRecord.getFieldValue(deptField).asInt())
                				.setSaleDate(saleRecord.getFieldValue(dateField).asInt())
                				.setQuantity(saleRecord.getFieldValue(qtyField).asInt())
                				.setPrice(price.longValue());
               aSale = bld.build();
               
               HtmlFormat.print(aSale, generator);
               aSale.writeDelimitedTo(out);
  
           }

            reader.close();
            out.close();
            System.out.println("Written: " + lineNum );
            generator.print("</body></html>");
            w.close();
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
		
		new WriteSales();
	}

}
