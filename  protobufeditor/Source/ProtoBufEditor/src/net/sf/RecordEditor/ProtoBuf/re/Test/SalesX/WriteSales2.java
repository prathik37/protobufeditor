package net.sf.RecordEditor.ProtoBuf.re.Test.SalesX;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.FieldDetail;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.IO.AbstractLineReader;

import net.sf.JRecord.IO.CobolIoProvider;
import net.sf.JRecord.Numeric.Convert;
import net.sf.RecordEditor.ProtoBuf.re.Test.Sales2.StoreSales;


//
//   Program to write a Test File.
//   It uses a version of cb2xml used in the RecordEditor
//   to Read a Cobol File (the file also comes in the RecordEditor).
//   



public class WriteSales2 {

    private String installDir     =  "/home/bm/Programs/JRecord/SampleFiles/";
    private String salesFile      = installDir + "DTAR020_Sorted.bin";
    private String salesFileOut   = "/home/bm/Work/Temp/ProtoBuffers/" + "protoStoreSales.bin";
    private String copybookName   = "/home/bm/Programs/JRecord/CopyBook/Cobol/" + "DTAR020.cbl";
    
    FileOutputStream out;

    Writer w ;
    
    //HtmlGenerator generator;


    StoreSales.Store.Builder bldStore = null;
    StoreSales.Deptartment.Builder bldDept = null;
    StoreSales.Product.Builder bldProd = null;
    StoreSales.Store aSale = null;
    
    int lastStore = -1;
    int lastDept = -1;
    int store, dept;

    /**
     * Example of LineReader / LineWrite classes
     */
    private WriteSales2() {
        super();

        int lineNum = 0;

        AbstractLine saleRecord;

        try {
            int fileStructure = Constants.IO_FIXED_LENGTH;
            CobolIoProvider ioProvider = CobolIoProvider.getInstance();
            AbstractLineReader reader  = ioProvider.getLineReader(
                   fileStructure, Convert.FMT_MAINFRAME,
                    CopybookLoader.SPLIT_NONE, copybookName, salesFile
            );
            int fldNum = 0;
            
            
            FieldDetail keycodeField = reader.getLayout().getField(0, fldNum++);
            FieldDetail storeField = reader.getLayout().getField(0, fldNum++);
            FieldDetail dateField = reader.getLayout().getField(0, fldNum++);
            FieldDetail deptField = reader.getLayout().getField(0, fldNum++);

            FieldDetail qtyField     = reader.getLayout().getField(0, fldNum++);
            FieldDetail salesField   = reader.getLayout().getField(0, fldNum++);
            
            out = new FileOutputStream(salesFileOut);

            w = new FileWriter(salesFileOut + ".html");
            
            //generator = new HtmlGenerator(w);


            while ((saleRecord = reader.read()) != null) {
            	store = saleRecord.getFieldValue(storeField).asInt();
            	dept = saleRecord.getFieldValue(deptField).asInt();
            	
            	if (store != lastStore) {
            		storeChanged();
            	} else  	if (dept != lastDept || store != lastStore) {
            		 deptChanged() ;
             	}

                lineNum += 1;
                BigDecimal price = saleRecord.getFieldValue(salesField).asBigDecimal().multiply(
                		BigDecimal.valueOf(1000));
                bldProd =  StoreSales.Product.newBuilder()
                				.setKeycode(saleRecord.getFieldValue(keycodeField).asInt())
                		//		.setStore(saleRecord.getFieldValue(storeField).asInt())
                 		//		.setDepartment(saleRecord.getFieldValue(deptField).asInt())
                				.setSaleDate(saleRecord.getFieldValue(dateField).asInt())
                				.setQuantity(saleRecord.getFieldValue(qtyField).asInt())
                				.setPrice(price.longValue());
                bldDept.addProduct(bldProd);
                lastStore = store;
                lastDept = dept;
           }

            storeChanged();
			
			
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
    
    private void storeChanged() throws IOException {
    	 deptChanged();
    	 
 		if (lastStore >= 0) {
 			aSale = bldStore.build();
			//HtmlFormat.print(aSale, generator);
			aSale.writeDelimitedTo(out);
		}
		bldStore = StoreSales.Store.newBuilder();
		bldStore	.setStore(store)
						.setName("Store: " + store);
    }
    
    private void deptChanged() {
  		if (lastDept >= 0) {
   			bldStore.addDepartment(bldDept);
		}
		bldDept = StoreSales.Deptartment.newBuilder();
		bldDept	.setDepartment(dept)
						.setName("Department: " + dept);

    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new WriteSales2();
	}

}
