package net.sf.RecordEditor.ProtoBuf.re.Test.ams;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;



import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Common.FieldDetail;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.LayoutDetail;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.IO.AbstractLineReader;

import net.sf.JRecord.IO.CobolIoProvider;
import net.sf.JRecord.Numeric.Convert;
import net.sf.RecordEditor.ProtoBuf.re.Test.TestConst;
import net.sf.RecordEditor.ProtoBuf.re.Test.ams.AmsLocation.Locations;


public class WriteLocations {

 
    /**
     * Example of LineReader / LineWrite classes
     */
    private WriteLocations(String fname) {
        super();
        String installDir     =  TestConst.INPUT_DIR;
        String locationFile      = installDir + fname + ".txt";
        String locationFileOut   = TestConst.OUTPUT_DIR + fname + ".bin";
        String copybookName   = TestConst.COBOL_DIR + "AmsLocation.cbl";

        int lineNum = 0;

        AbstractLine<LayoutDetail> locationRecord;

        try {
            int fileStructure = Constants.IO_DEFAULT;
            CobolIoProvider ioProvider = CobolIoProvider.getInstance();
            AbstractLineReader<LayoutDetail> reader  = ioProvider.getLineReader(
                   fileStructure, Convert.FMT_INTEL,
                    CopybookLoader.SPLIT_NONE, copybookName, locationFile
            );
            FileOutputStream out = new FileOutputStream(locationFileOut);

            Writer w = new FileWriter(locationFileOut + ".html");
            int fldNum = 0;
            
            
            FieldDetail brandField = reader.getLayout().getField(0, fldNum++);
            FieldDetail locationNoField = reader.getLayout().getField(0, fldNum++);
            FieldDetail locationTypeField = reader.getLayout().getField(0, fldNum++);
            FieldDetail locationNameField = reader.getLayout().getField(0, fldNum++);
            FieldDetail addr1NameField = reader.getLayout().getField(0, fldNum++);
            FieldDetail addr2NameField = reader.getLayout().getField(0, fldNum++);
            FieldDetail addr3NameField = reader.getLayout().getField(0, fldNum++);

            FieldDetail postcodeField     = reader.getLayout().getField(0, fldNum++);
            FieldDetail stateField   = reader.getLayout().getField(0, fldNum++);
            FieldDetail activeField   = reader.getLayout().getField(0, fldNum++);
            
            Locations.Builder bld;
            Locations aLocation;
            
            //HtmlGenerator generator = new HtmlGenerator(w);

            while ((locationRecord = reader.read()) != null) {
                lineNum += 1;
                 bld =  Locations.newBuilder()
                				.setBrandId(locationRecord.getFieldValue(brandField).asString())
                				.setLocNbr(locationRecord.getFieldValue(locationNoField).asString())
                 				.setLocType(locationRecord.getFieldValue(locationTypeField).asString())
                				.setLocName(locationRecord.getFieldValue(locationNameField).asString())
                				.setLocAddrLn1(locationRecord.getFieldValue(addr1NameField).asString())
                 				.setLocAddrLn2(locationRecord.getFieldValue(addr2NameField).asString())
                 				.setLocAddrLn3(locationRecord.getFieldValue(addr3NameField).asString())
 
                				.setLocPostcode(locationRecord.getFieldValue(postcodeField).asString())
                				.setLocState(locationRecord.getFieldValue(stateField).asString())
                				.setLocActvInd(locationRecord.getFieldValue(activeField).asString())

                 				;
               aLocation = bld.build();
               
               //HtmlFormat.print(aLocation, generator);
               aLocation.writeDelimitedTo(out);
  
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
		
		new WriteLocations("Ams_LocDownload_20041228");
		new WriteLocations("Ams_LocDownload_20041228_Extract2");
		new WriteLocations("Ams_LocDownload_20041228_Extract");
		new WriteLocations("Ams_LocDownload_20041228");
	}

}
