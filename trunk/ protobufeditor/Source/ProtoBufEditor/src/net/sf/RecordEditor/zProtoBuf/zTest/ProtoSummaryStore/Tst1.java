package net.sf.RecordEditor.zProtoBuf.zTest.ProtoSummaryStore;

import java.io.File;
import java.io.FileInputStream;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;

import net.sf.JRecord.Log.TextLog;
import net.sf.RecordEditor.ProtoBuf.JRecord.IO.ProtoDelimitedByteReader;
import net.sf.RecordEditor.ProtoBuf.common.Utils;
import net.sf.RecordEditor.ProtoBuf.summary.ProtoSummary;
import net.sf.RecordEditor.ProtoBuf.summary.ProtoSummaryStore;
import net.sf.RecordEditor.utils.common.StreamUtil;
import junit.framework.TestCase;

public class Tst1 extends TestCase {

	private static final FileDescriptor fileDesc = Utils.getFileDescriptor();

	private static final  String PROTO_DIR = "C:\\Users\\mum\\.RecordEditor\\ProtoBuf\\CopyBook";
	private static final  String SAMPLE_DIR = "C:\\Users\\mum\\.RecordEditor\\ProtoBuf\\SampleFiles";

	private static final  String PROTO_SUMMARY_PROTO_FILE = "C:\\Users\\mum\\Bruce\\Work\\RecordEditor\\ProtoBufEditor\\Protos\\ProtoSummary.proto";
	private static final  String DTAR020_PROTO_FILE = PROTO_DIR + "\\DTAR020.protocomp";
	private static final  String STORE_PROTO_FILE = PROTO_DIR + "\\Sales.proto";
	private static final  String STORE3_PROTO_FILE = PROTO_DIR + "\\StoreSales3.protocomp";

	private static final  String ADDRESS_BOOK_PROTO_FILE = PROTO_DIR + "\\addressbook.protocomp";


	private static final  String PROTO_SUMMARY_FILE =  "C:\\Users\\mum\\RecordEditor_HSQL\\ProtoSummary\\ProtoSummary.bin";
	private static final  String DTAR020_FILE = SAMPLE_DIR + "\\DTAR020_tst1.bin";
	private static final  String STORE_FILE = SAMPLE_DIR + "\\protoSales.bin";
	private static final  String STORE3_FILE = SAMPLE_DIR + "\\protoStoreSales3.bin";

	private static final  String PROTOTEST_FILE = SAMPLE_DIR + "\\ProtoTest_Address1.bin";

	private static final ProtoSummaryStore str = ProtoSummaryStore.getInstance();
	private static final Descriptor desc = fileDesc.getMessageTypes().get(0);

	static {
		str.addProtoDir(PROTO_DIR);
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}


	public void testCheckFile1() {

		assertTrue("Summary File: correct proto", check(str.lookupProtoSummary(PROTO_SUMMARY_PROTO_FILE), PROTO_SUMMARY_FILE, null));
		assertTrue("DTAR020 File: correct proto", check(str.lookupProtoSummary(DTAR020_PROTO_FILE), DTAR020_FILE, null));
		assertTrue("Store File: correct proto", check(str.lookupProtoSummary(STORE_PROTO_FILE), STORE_FILE, null));
		assertTrue("Store3 File: correct proto", check(str.lookupProtoSummary(STORE3_PROTO_FILE), STORE3_FILE, STORE3_PROTO_FILE));
		System.out.println();
		System.out.println();
	}

	public void testCheckFile2() {
		assertFalse("Store3 File: correct proto", check(str.lookupProtoSummary(DTAR020_PROTO_FILE), STORE3_FILE, null));

		assertFalse("DTAR020 File: incorrect proto", check(str.lookupProtoSummary(STORE3_PROTO_FILE), DTAR020_FILE, null));
//		check(str.lookupProtoSummary(STORE3_PROTO_FILE), DTAR020_FILE);
	}


	public void testCheckFile3() {
		String inFileName = "C:\\Users\\mum/.RecordEditor/ProtoBuf/CopyBook/addressbook.protocomp";
		//FileDescriptorSet fd = Utils.getDescriptor(inFileName, new TextLog());
		//str.toProtoSummary(fd, new File(inFileName));
		//str.addFileDescriptor(inFileName, fd);
		assertTrue("Summary File: correct proto", checkSM(str.lookupProtoSummary(ADDRESS_BOOK_PROTO_FILE), PROTOTEST_FILE , null));
	}



	private boolean check(ProtoSummary.Proto p, String filename, String fname) {
		byte[] bytes;
		ProtoDelimitedByteReader r = new ProtoDelimitedByteReader();
		try {
			r.open(filename);
			if ((bytes = r.read()) != null) {
				DynamicMessage.Builder builder = DynamicMessage.newBuilder(desc).mergeFrom(bytes);

				r.close();


				return str.checkFileDesc(
						bytes, p,
						builder.getUnknownFields(), null);
			}
		} catch (Exception e) {
			System.out.println();
			System.out.println("^^ " + fname);
			System.out.println();
			e.printStackTrace();
		}
		return false;
	}


	private boolean checkSM(ProtoSummary.Proto p, String filename, String fname) {
		byte[] bytes;
//		ProtoDelimitedByteReader r = new ProtoDelimitedByteReader();
		FileInputStream in = null;
		try {

			in = new FileInputStream(filename);

			byte[] b = StreamUtil.read(in, (int) (new File(filename).length()));
			in.close();


			DynamicMessage.Builder builder = DynamicMessage.newBuilder(desc).mergeFrom(b);

			return str.checkFileDesc(
					b, p,
					builder.getUnknownFields(), null);

		} catch (Exception e) {
			System.out.println();
			System.out.println("^^ " + fname);
			System.out.println();
			e.printStackTrace();

			try {
				in.close();
			} catch (Exception ee) {

			}
		}
		return false;
	}
}