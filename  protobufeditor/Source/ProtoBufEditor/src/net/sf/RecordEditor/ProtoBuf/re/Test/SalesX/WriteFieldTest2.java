package net.sf.RecordEditor.ProtoBuf.re.Test.SalesX;

import java.io.FileOutputStream;

import com.google.protobuf.ByteString;

import net.sf.RecordEditor.ProtoBuf.re.Test.TestConst;

public class WriteFieldTest2 {
	

    private String salesFileOut   = TestConst.OUTPUT_DIR + "protoFieldTest2.bin";


    
    public WriteFieldTest2() {
    	FieldTest2.TestFields2.Builder rec;
    	int iVal, tt;
    	long lVal;
    	byte[] bytes = {0,0,0,0,0};
    	int j = 0;
    	int k = 1;
    	

    	FieldTest2.SaleType[] saleTypes = {
    			FieldTest2.SaleType.RETURN, FieldTest2.SaleType.OTHER, FieldTest2.SaleType.SALE
    	};
    	
    	try {
	    	FileOutputStream out = new FileOutputStream(salesFileOut);
	    	for (int i = 0; i < 120; i++) {
	    		rec = FieldTest2.TestFields2.newBuilder();
	    		
	    		for (int ii = 0; ii < k; ii++) {
		    		iVal = i * 10000 + ii;
		    		lVal = i * 100000000 + ii;
		    		tt = i / 3 + ii;
		    		bytes[4] = (byte) (i + ii);
		    		rec.addF01(iVal);
		    		rec.addF02(iVal);
		    		rec.addF03(iVal);
		    		rec.addF04(iVal);
		    		rec.addF05(iVal);
		    		rec.addF06(lVal);
		    		rec.addF07(lVal);
		    		rec.addF08(lVal);
		    		rec.addF09(lVal);
		    		rec.addF10(lVal);
		    		rec.addF11(iVal);
		    		rec.addF12(lVal);
		    		rec.addF13(tt * 3 == i );
		    		rec.addF15(lVal + "");
		    		
		    		rec.addF17(saleTypes[j++]);
		    		if (j > 2) j = 0;
		    		
			    		
		    		rec.addF19(ByteString.copyFrom(bytes));
	    		}
	    		k += 1;
	    		if (k > 7) k = 1;
	    		rec.build().writeDelimitedTo(out);
	    	}
	    	out.close();
	   	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
		
		new WriteFieldTest2();
	}
}
