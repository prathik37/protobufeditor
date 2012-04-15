package net.sf.RecordEditor.zProtoBuf.zTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.FieldDetail;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.IO.CobolIoProvider;
import net.sf.JRecord.Numeric.Convert;
import net.sf.RecordEditor.ProtoBuf.re.Test.Sales2.StoreSales;



import com.google.protobuf.AbstractMessage;


public class BuildSalesList {
	   private String installDir     =  "/home/bm/Programs/JRecord/SampleFiles/";
	    private String salesFile      = installDir + "DTAR020_Sorted.bin";

	    private String copybookName   = "/home/bm/Programs/JRecord/CopyBook/Cobol/" + "DTAR020.cbl";
	    
	    private List<AbstractMessage> list = new ArrayList<AbstractMessage>();
	    
	
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
	    @SuppressWarnings("unchecked")
		public List<AbstractMessage> getSalesList() {

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

	            System.out.println("Written: " + lineNum );

	         } catch (Exception e) {
	            System.out.println("~~> " + lineNum + " " + e.getMessage());
	            System.out.println();

	            e.printStackTrace();
	        }
	         
	         return list;
	    }
	    
	    private void storeChanged() throws IOException {
	    	 deptChanged();
	    	 
	 		if (lastStore >= 0) {
	 			aSale = bldStore.build();
	 			list.add(aSale);

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

}
