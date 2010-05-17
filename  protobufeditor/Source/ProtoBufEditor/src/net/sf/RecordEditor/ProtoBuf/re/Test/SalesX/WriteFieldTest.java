package net.sf.RecordEditor.ProtoBuf.re.Test.SalesX;

import java.io.FileOutputStream;

import com.google.protobuf.ByteString;

import net.sf.RecordEditor.ProtoBuf.re.Test.TestConst;

public class WriteFieldTest {
	

    private String salesFileOut   = TestConst.OUTPUT_DIR + "protoFieldTest.bin";
    private String salesFileOut2   = TestConst.OUTPUT_DIR + "protoFieldTestA.bin";


    
    public void writeFile1() {
    	FieldTest.TestFields.Builder rec;
    	int iVal, tt;
    	long lVal;
    	byte[] bytes = {0,0,0,0,0};
    	int j = 0;
    	int k = 1;

    	FieldTest.SaleType[] saleTypes = {
    			FieldTest.SaleType.RETURN, FieldTest.SaleType.OTHER, FieldTest.SaleType.SALE
    	};
    	
    	try {
	    	FileOutputStream out = new FileOutputStream(salesFileOut);
	    	for (int i = 0; i < 120; i++) {
	    		rec = FieldTest.TestFields.newBuilder();
	    		iVal = i * 10000;
	    		lVal = i * 100000000;
	    		tt = i / 3;
	    		bytes[4] = (byte) i;
	    		rec.setF01(iVal);
	    		rec.setF02(iVal);
	    		rec.setF03(iVal);
	    		rec.setF04(iVal);
	    		rec.setF05(iVal);
	    		rec.setF06(lVal);
	    		rec.setF07(lVal);
	    		rec.setF08(lVal);
	    		rec.setF09(lVal);
	    		rec.setF10(lVal);
	    		rec.setF11(iVal);
	    		rec.setF12(lVal);
	    		rec.setF13(tt * 3 == i );
	    		rec.setF15(lVal + "");
	    		
	    		rec.setF17(saleTypes[j++]);
	    		if (j > 2) j = 0;
	    		
	    		if (k < 3) {
	    			rec.setF14(tt * 3 != i );
	    			rec.setF16(lVal + " optional");
	    			rec.setF18(saleTypes[k++]);
	    			rec.setF20(ByteString.copyFrom(bytes));
	    		} else {
	    			k = 0;
	    			rec.clearF18();
	    		}
	    		
	    		rec.setF19(ByteString.copyFrom(bytes));
	    		rec.build().writeDelimitedTo(out);
	    	}
	    	out.close();
	   	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void writeFile2() {
    	FieldTest.TestFields.Builder rec;
    	int iVal, tt;
    	long lVal;
    	byte[] bytes = {0,0,0,0,0};
    	int j = 0;
    	int k = 1;

    	FieldTest.SaleType[] saleTypes = {
    			FieldTest.SaleType.RETURN, FieldTest.SaleType.OTHER, FieldTest.SaleType.SALE
    	};
    	
    	try {
	    	FileOutputStream out = new FileOutputStream(salesFileOut2);
	    	for (int i = 0; i < 120; i++) {
	    		rec = FieldTest.TestFields.newBuilder();
	    		iVal = i * 10000;
	    		lVal = i;
	    		lVal *= 100000000;
	    		tt = i / 3;
	    		bytes[4] = (byte) i;
	    		rec.setF01(iVal);
	    		rec.setF02(iVal);
	    		rec.setF03(iVal);
	    		rec.setF04(iVal);
	    		rec.setF05(iVal);
	    		rec.setF06(lVal);
	    		rec.setF07(lVal);
	    		rec.setF08(lVal);
	    		rec.setF09(lVal);
	    		rec.setF10(lVal);
	    		rec.setF11(iVal);
	    		rec.setF12(lVal);
	    		rec.setF13(tt * 3 == i );
	    		rec.setF15(lVal + "");
	    		
	    		rec.setF17(saleTypes[j++]);
	    		if (j > 2) j = 0;
	    		
	    		if (k < 3) {
	    			rec.setF14(tt * 3 != i );
	    			rec.setF16(lVal + " optional");
	    			rec.setF18(saleTypes[k++]);
	    			rec.setF20(ByteString.copyFrom(bytes));
	    		} else {
	    			k = 0;
	    			rec.clearF18();
	    		}
	    		
	    		rec.setF19(ByteString.copyFrom(bytes));
	    		rec.build().writeDelimitedTo(out);
	    	}
	    	for (int i = 0; i < 120; i++) {
	    		rec = FieldTest.TestFields.newBuilder();

	    		lVal = i;
	    		lVal *= 100000000;
	    		iVal = (int) lVal;
	    		tt = i / 3;
	    		bytes[4] = (byte) i;
	    		rec.setF01(iVal);
	    		rec.setF02(iVal);
	    		rec.setF03(iVal);
	    		rec.setF04(iVal);
	    		rec.setF05(iVal);
	    		rec.setF06(lVal);
	    		rec.setF07(lVal);
	    		rec.setF08(lVal);
	    		rec.setF09(lVal);
	    		rec.setF10(lVal);
	    		rec.setF11(iVal);
	    		rec.setF12(lVal);
	    		rec.setF13(tt * 3 == i );
	    		rec.setF15(lVal + "");
	    		
	    		rec.setF17(saleTypes[j++]);
	    		if (j > 2) j = 0;
	    		
	    		if (k < 3) {
	    			rec.setF14(tt * 3 != i );
	    			rec.setF16(lVal + " optional");
	    			rec.setF18(saleTypes[k++]);
	    			rec.setF20(ByteString.copyFrom(bytes));
	    		} else {
	    			k = 0;
	    			rec.clearF18();
	    		}
	    		
	    		rec.setF19(ByteString.copyFrom(bytes));
	    		rec.build().writeDelimitedTo(out);
	    	}
	    	out.close();
	   	} catch (Exception e) {
			e.printStackTrace();
		}
    }

    
    public static void main(String[] args) {
		
    	WriteFieldTest tf = new WriteFieldTest();
    	
    	tf.writeFile1();
    	tf.writeFile2();
	}
}
