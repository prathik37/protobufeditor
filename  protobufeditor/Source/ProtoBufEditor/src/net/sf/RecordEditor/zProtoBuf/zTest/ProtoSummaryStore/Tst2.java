package net.sf.RecordEditor.zProtoBuf.zTest.ProtoSummaryStore;


import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;

import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoDelimitedByteReader;
import net.sf.RecordEditor.ProtoBuf.common.Utils;
import net.sf.RecordEditor.ProtoBuf.summary.FileDescriptionCallback;
import net.sf.RecordEditor.ProtoBuf.summary.ProtoSummary;
import net.sf.RecordEditor.ProtoBuf.summary.ProtoSummaryStore;
import junit.framework.TestCase;

public class Tst2 extends TestCase {

	private static final FileDescriptor fileDesc = Utils.getFileDescriptor();

	private static final  String PROTO_DIR = "C:\\Users\\mum\\.RecordEditor\\ProtoBuf\\CopyBook";
	private static final  String SAMPLE_DIR = "C:\\Users\\mum\\.RecordEditor\\ProtoBuf\\SampleFiles";

	private static final  String[] PROTOS = {
		"DTAR020.proto", "Sales.proto", "Ams_Location.proto",
		"StoreSales3.proto", "StoreSales3.proto", "StoreSales3.proto", "StoreSales3.proto",
		"StoreSales5.proto", "StoreSales6.proto", "StoreSales7.proto",
	};

	private static final  String[] FILES = {
		"DTAR020_tst1.bin", "protoSales.bin", "Ams_LocDownload_20041228.bin",
		"protoStoreSales3.bin", "protoStoreSales3a.bin", "protoStoreSales3b.bin", "protoStoreSales3c.bin",
		"protoStoreSales5.bin", "protoStoreSales6.bin", "protoStoreSales7.bin",
	};

	private static final ProtoSummaryStore str = ProtoSummaryStore.getInstance();
	private static final Descriptor desc = fileDesc.getMessageTypes().get(0);

	static {
		str.addProtoDir(PROTO_DIR);
	}



	public void testCheckFile1() {

		for (int  i =0; i < PROTOS.length; i++) {
			assertTrue(i + " " + PROTOS[i],
					check(str.lookupProtoSummary(PROTO_DIR + "\\" + PROTOS[i]),
								  SAMPLE_DIR + "//" + FILES[i]));
		}
	}


	public void testCheckFile2() {

		for (int  i =0; i < PROTOS.length; i++) {
			assertTrue(i + " " + PROTOS[i] + "comp",
					check(str.lookupProtoSummary(PROTO_DIR + "\\" + PROTOS[i] + "comp"),
								  SAMPLE_DIR + "//" + FILES[i] ));
		}
	}

	public void testCheckFile3() {

		for (int  i =0; i < PROTOS.length; i++) {
			for (int  j =0; j < PROTOS.length; j++) {
				if (PROTOS[i].equals(PROTOS[j])
				|| (PROTOS[i].startsWith("StoreSales") && PROTOS[j].startsWith("StoreSales"))
				|| (PROTOS[j].startsWith("DTAR020") && PROTOS[i].startsWith("Sales"))
				|| (PROTOS[i].startsWith("DTAR020") && PROTOS[j].startsWith("Sales")) ) {
				} else {
					assertFalse(i + " " + j + " " + PROTOS[i] + " " + PROTOS[j] + " ~~",
							check(str.lookupProtoSummary(
									PROTO_DIR + "\\" + PROTOS[j] + "comp"),
									SAMPLE_DIR + "//" + FILES[i] ));
				}
			}
		}
	}

	public void testCheckFile4() {

		System.out.println();
		System.out.println();
		System.out.println();
		find(	0,
				str.lookupProtoSummary(PROTO_DIR + "\\" + PROTOS[0]),
				PROTO_DIR + "\\" + PROTOS[1],
				SAMPLE_DIR + "\\" + FILES[0] );
		find(	1,
				str.lookupProtoSummary(PROTO_DIR + "\\" + PROTOS[0]),
				PROTO_DIR + "\\" + PROTOS[1],
				SAMPLE_DIR + "\\" + FILES[1] );


		for (int  i = 2; i < PROTOS.length; i++) {
			find(	i,
					str.lookupProtoSummary(PROTO_DIR + "\\" + PROTOS[i]),
					null,
					SAMPLE_DIR + "\\" + FILES[i] );
		}
	}

	public void testCheckFile5() {
		boolean ok = check(	str.lookupProtoSummary(PROTO_DIR + "\\" + PROTOS[PROTOS.length - 4]),
				SAMPLE_DIR + "\\" + FILES[PROTOS.length - 2] );
		System.out.println(" $$$ " + ok);
	}

	public void testCheckFile6() {


		find(	PROTOS.length - 2,
				str.lookupProtoSummary(PROTO_DIR + "\\" + PROTOS[PROTOS.length - 2]),
				null,
				SAMPLE_DIR + "\\" + FILES[PROTOS.length - 2] );
		find(	121,
				str.lookupProtoSummary(PROTO_DIR + "\\ProtoSummary.proto"),
				null,
				"C:\\Users\\mum\\RecordEditor_HSQL\\ProtoSummary\\ProtoSummary.bin" );
	}


	private boolean check(ProtoSummary.Proto p, String filename) {
		byte[] bytes;
		ProtoDelimitedByteReader r = new ProtoDelimitedByteReader();
		try {
			r.open(filename);
			if ((bytes = r.read()) != null) {
				DynamicMessage.Builder builder = DynamicMessage.newBuilder(desc).mergeFrom(bytes);

				r.close();

				return str.checkFileDesc(bytes, p, builder.getUnknownFields(), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean find(int id, ProtoSummary.Proto p, String protoname2, String filename) {
		byte[] bytes;
		ProtoDelimitedByteReader r = new ProtoDelimitedByteReader();
		try {
			r.open(filename);
			if ((bytes = r.read()) != null) {
				r.close();

				Base callback = new callback1(id, p.getFilename(), filename);
				if (protoname2 !=  null) {
					callback = new callback2(id, p.getFilename(), protoname2, filename);
				}
				str.lookupFileDesc(bytes, callback);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}



	private abstract class Base implements FileDescriptionCallback {
		public boolean called = false;
	}

	private class callback1 extends Base {
		final String filename, protoname;
		final int id;

		public callback1(int id, String protoname, String filename) {
			super();
			this.protoname = protoname;
			this.filename = filename;
			this.id = id;
		}

		public void setSchema(String schemaFile, int fileNo, int msgNo) {
			System.out.println("Found: " + id + " " + filename + ": " + protoname + " " + schemaFile
					+ "  ~~  " +  protoname.equals(schemaFile));
			assertEquals("Callback " + id, protoname, schemaFile);
		}
	}


	private class callback2 extends Base  {
		final String filename, protoname, protoname2;
		final int id;

		public callback2(int id, String protoname, String protoname2, String filename) {
			super();
			this.protoname = protoname;
			this.protoname2 = protoname2;
			this.filename = filename;
			this.id = id;
		}

		public void setSchema(String schemaFile, int fileNo, int msgNo) {
			if ( !protoname2.equals(schemaFile)) {
				assertEquals("Callback " + id, protoname, schemaFile);
			}
		}
	}
}