package net.sf.RecordEditor.ProtoBuf.re.Test.SalesX;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
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

//
//   Program to write a Test File.
//   It uses a version of cb2xml used in the RecordEditor
//   to Read a Cobol File (the file also comes in the RecordEditor).
//   


public class WriteSales {

    private String installDir     =  TestConst.INPUT_DIR;
    private String salesFile      = installDir + "DTAR020.bin";
    private String salesFileOut   = TestConst.OUTPUT_DIR + "protoSales11.bin";
    private String copybookName   = TestConst.COBOL_DIR + "DTAR020.cbl";
    
    private Sales11.PaymentType[] payments = {
    		Sales11.PaymentType.CASH, Sales11.PaymentType.CHEQUE,
    		Sales11.PaymentType.CREDIT_CARD, Sales11.PaymentType.DEBIT_CARD,
    };

    /**
     * Example of LineReader / LineWrite classes
     */
    private WriteSales() {
        super();

        int lineNum = 0;
        Sales11.SaleType saleType;
        int qty;
        int paymentIdx = 0;
        String ss;

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
            
            Sales11.sale11.Builder bld;
            Sales11.sale11 aSale;
            
            //HtmlGenerator generator = new HtmlGenerator(w);

            while ((saleRecord = reader.read()) != null) {
                lineNum += 1;
                BigDecimal price = saleRecord.getFieldValue(salesField).asBigDecimal().multiply(
                		BigDecimal.valueOf(1000));
              	saleType = Sales11.SaleType.SALE;
              	
              	qty = saleRecord.getFieldValue(qtyField).asInt();
            	if (qty ==0) {
            		saleType = Sales11.SaleType.OTHER;
            	} else if (qty < 0) {
            		saleType = Sales11.SaleType.RETURN;
            	}
                bld =  Sales11.sale11.newBuilder()
                				.setKeycode(saleRecord.getFieldValue(keycodeField).asInt())
                				.setStore(saleRecord.getFieldValue(storeField).asInt())
                 				.setDepartment(saleRecord.getFieldValue(deptField).asInt())
                				.setSaleDate(saleRecord.getFieldValue(dateField).asInt())
                				.setQuantity(qty)
                				.setPrice(price.longValue());
                
                bld.setSaleType(saleType);
                
                ss = "";
                for (int j = 0; j < paymentIdx * 3 / 2; j++) {
                	bld.addStrArray(ss);
                	ss = ss + " " + j;
                }
                
                if (paymentIdx != 0) {
                	bld.setPriceFloat(saleRecord.getFieldValue(salesField).asFloat());
                }
                if (paymentIdx < payments.length) {
                	bld.setPaymentType(payments[paymentIdx]);
                	paymentIdx += 1;
                	bld.setPriceDouble(saleRecord.getFieldValue(salesField).asDouble());
                } else {
                	//bldProd.setPaymentType(null);
                	bld.clearPaymentType();
                	paymentIdx = 0;
                }

               aSale = bld.build();
               
               //HtmlFormat.print(aSale, generator);
               aSale.writeDelimitedTo(out);
  
           }

            reader.close();
            out.close();
            System.out.println("Written: " + lineNum );
            //generator.print("</body></html>");
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
