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
import net.sf.RecordEditor.ProtoBuf.re.Test.Sales3.StoreSales6;


//
//   Program to write a Test File.
//   It uses a version of cb2xml used in the RecordEditor
//   to Read a Cobol File (the file also comes in the RecordEditor).
//   


public class WriteSales6 {

	private static final int KEYCODE_IDX = 3;
	private static final int DEPARTMENT_IDX = 2;
	private static final int STORE_IDX = 1;
	
	
    private String installDir     = TestConst.INPUT_DIR;
    private String salesFile      = installDir + "DTAR020_Sorted.bin";
    private String salesFileOut   = TestConst.OUTPUT_DIR + "protoStoreSales6.bin";
    private String copybookName   = TestConst.COBOL_DIR + "DTAR020.cbl";
    private StoreSales6.PaymentType[] payments = {
    		StoreSales6.PaymentType.CASH, StoreSales6.PaymentType.CHEQUE,
    		StoreSales6.PaymentType.CREDIT_CARD, StoreSales6.PaymentType.DEBIT_CARD,
    };
    private int paymentIdx = 0;
    
    FileOutputStream out;

    Writer w ;
    
    //HtmlGenerator generator;


    StoreSales6.Store.Builder bldStore = null;
    StoreSales6.Deptartment.Builder bldDept = null;
    StoreSales6.Product.Builder bldProd = null;
    StoreSales6.Order.Builder order = null;
    //StoreSales6.Summary.Builder deptSummary = null;
    //StoreSales6.Summary.Builder storeSummary= null;
    
    
    StoreSales6.Store aSale = null;
    
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
	private WriteSales6() {
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
            int fldNum = 0;
            StoreSales6.SaleType saleType;
            
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
            	
            	saleType = StoreSales6.SaleType.SALE;
            	if (qty ==0) {
            		saleType = StoreSales6.SaleType.OTHER;
            	} else if (qty < 0) {
            		saleType = StoreSales6.SaleType.RETURN;
            	}


                lineNum += 1;
                bldProd =  StoreSales6.Product.newBuilder()
                				.setKeycode(keycode)
                				.setSaleDate(saleRecord.getFieldValue(dateField).asInt())
                				.setQuantity(qty)
                				.setPrice(price.longValue())
                				.setSaleType(saleType);
                if (paymentIdx < payments.length) {
                	bldProd.setPaymentType(payments[paymentIdx]);
                	paymentIdx += 1;
                } else {
                	//bldProd.setPaymentType(null);
                	bldProd.clearPaymentType();
                	paymentIdx = 0;
                }
                bldDept.addProduct(bldProd);
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
		bldStore = StoreSales6.Store.newBuilder();
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
		bldDept = StoreSales6.Deptartment.newBuilder();
		bldDept	.setDepartment(dept)
						.setName("Department: " + dept);
		inc(DEPARTMENT_IDX);
    }
    
    private void keycodeChanged() {
    	if (lastKeycode >= 0) {
    		if (sumQty[KEYCODE_IDX] > 0) {
	    		order =StoreSales6.Order.newBuilder()
	    				.setKeycode(lastKeycode)
	    				.setQuantity(sumQty[KEYCODE_IDX]);
	    		bldStore.addOrder(order);
    		}
    		
    		inc(KEYCODE_IDX);
    	}
    }

    private StoreSales6.Summary.Builder buildSummary(int idx) {
    	return StoreSales6.Summary.newBuilder()
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
		
		new WriteSales6();
	}

}
