package net.sf.RecordEditor.ProtoBuf.re.Test.SalesX;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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


public class WriteSales5 {

	private static final int KEYCODE_IDX = 3;
	private static final int DEPARTMENT_IDX = 2;
	private static final int STORE_IDX = 1;
	
	
    private String installDir     = TestConst.INPUT_DIR;
    private String salesFile      = installDir + "DTAR020_Sorted.bin";
    private String salesFileOut   = TestConst.OUTPUT_DIR + "protoStoreSales5.bin";
    private String copybookName   = TestConst.COBOL_DIR + "DTAR020.cbl";
    
    FileOutputStream out;

    Writer w ;
    
    //HtmlGenerator generator;


    StoreSales5.Store.Builder bldStore = null;
    StoreSales5.Deptartment.Builder bldDept = null;
    StoreSales5.Product.Builder bldProd = null;
    StoreSales5.Order.Builder order = null;
    //StoreSales5.Summary.Builder deptSummary = null;
    //StoreSales5.Summary.Builder storeSummary= null;
    
    
    StoreSales5.Store aSale = null;
    
    int lastStore = -1;
    int lastDept = -1;
    int lastKeycode = -1;
    int store, dept, qty, keycode;
    BigDecimal price;
    
    int[] sumQty = {0, 0, 0, 0};
    int[] sumCount = {0, 0, 0, 0};
    long[] sumPrice = {0, 0, 0, 0};

    /**
     * Example of LineReader / LineWrite classes
     */
    @SuppressWarnings("unchecked")
	private WriteSales5() {
        super();

       
        StoreSales5.Product.SaleType saleType;
        int lineNum = 0;

        AbstractLine<LayoutDetail> saleRecord;

        try {
            int fileStructure = Constants.IO_FIXED_LENGTH;
            CobolIoProvider ioProvider = CobolIoProvider.getInstance();
            AbstractLineReader<LayoutDetail> reader  = ioProvider.getLineReader(
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
            	keycode = saleRecord.getFieldValue(keycodeField).asInt();
            	qty = saleRecord.getFieldValue(qtyField).asInt();
                price = saleRecord.getFieldValue(salesField).asBigDecimal().multiply(
                		BigDecimal.valueOf(1000));
           	
            	if (store != lastStore) {
            		storeChanged();
            	} else  	if (dept != lastDept) {
            		 deptChanged() ;
             	} else  	if (keycode != lastKeycode) {
             		keycodeChanged() ;
             	}

            	saleType = StoreSales5.Product.SaleType.SALE;
            	if (qty ==0) {
            		saleType = StoreSales5.Product.SaleType.OTHER;
            	} else if (qty < 0) {
            		saleType = StoreSales5.Product.SaleType.RETURN;
            	}
            	
                lineNum += 1;
                bldProd		.setKeycode(keycode)
                				.addSaleDate(saleRecord.getFieldValue(dateField).asInt())
                				.addQuantity(qty)
                				.addPrice(price.longValue())
                				.addSaleType(saleType);
                 sumCount[KEYCODE_IDX] += 1;
                sumQty[KEYCODE_IDX] += qty;
                sumPrice[KEYCODE_IDX] += price.longValue();
                
                lastStore = store;
                lastDept = dept;
                lastKeycode = keycode;
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
 			bldStore.setSummary(buildSummary(STORE_IDX));
 			aSale = bldStore.build();
			//HtmlFormat.print(aSale, generator);
			aSale.writeDelimitedTo(out);
		}
		bldStore = StoreSales5.Store.newBuilder();
		bldStore	.setStore(store)
						.setName("Store: " + store);
		
		inc(STORE_IDX);
    }
    
    private void deptChanged() {
    	keycodeChanged();
  		if (lastDept >= 0) {
  			bldDept.setSummary(buildSummary(DEPARTMENT_IDX));
   			bldStore.addDepartment(bldDept);
		}
		bldDept = StoreSales5.Deptartment.newBuilder();
		bldDept	.setDepartment(dept)
						.setName("Department: " + dept);
		inc(DEPARTMENT_IDX);
    }
    
    private void keycodeChanged() {
    	if (lastKeycode >= 0) {
    		if (sumQty[KEYCODE_IDX] > 0) {
	    		order =StoreSales5.Order.newBuilder()
	    				.setKeycode(lastKeycode)
	    				.setQuantity(sumQty[KEYCODE_IDX]);
	    		bldStore.addOrder(order);
    		}
            bldDept.addProduct(bldProd);

    		inc(KEYCODE_IDX);
    	}
    	bldProd =  StoreSales5.Product.newBuilder();
    }

    private StoreSales5.Summary.Builder buildSummary(int idx) {
    	return StoreSales5.Summary.newBuilder()
    		.setCount(sumCount[idx])
    		.setPrice(sumPrice[idx])
    		.setQuantity(sumQty[idx]);
    }
    
    private void inc(int idx) {
    	
  		sumQty[idx - 1] += sumQty[idx];
		sumPrice[idx - 1] += sumPrice[idx];
		sumCount[idx - 1] += sumCount[idx];
		
		sumQty[idx] = 0;
		sumPrice[idx] = 0;
		sumCount[idx] = 0;
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		new WriteSales5();
	}

}
