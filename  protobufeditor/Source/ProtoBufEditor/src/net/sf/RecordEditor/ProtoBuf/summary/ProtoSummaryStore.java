package net.sf.RecordEditor.ProtoBuf.summary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.JRecord.Log.AbsSSLogger;
import net.sf.JRecord.Log.TextLog;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.ProtoBuf.common.Const;
import net.sf.RecordEditor.ProtoBuf.common.Utils;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.params.AParameterChangeListner;
import net.sf.RecordEditor.utils.params.BoolOpt;
import net.sf.RecordEditor.utils.params.Parameters;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.WireFormat;
import com.google.protobuf.WireFormat.JavaType;


/**
 *
 * Purpose:
 *     To analyze proto definition files and work out which proto definition files describe a protocol buffer
 *   Message File. While it is not possible to definitively work out which proto file belongs to a
 *   protocol message, you can eliminate most proto definitions.
 *
 * @author Bruce Martin
 *
 * License GPL 2 (or 3 or later)
 *
 */
public class ProtoSummaryStore implements AParameterChangeListner {
	private static final String SUMMARY_DIR = Parameters.getPropertiesDirectoryWithFinalSlash() + "ProtoSummary/";
	private static final String SUMMARY_FILE = SUMMARY_DIR + "ProtoSummary.bin";
	private static final String PROTO_DIR_FILE = SUMMARY_DIR + "ProtoDir.txt";

	private static final int NUMER_OF_FIELDS_TO_CHECK = 8;

	private static final FileDescriptor fileDesc = Utils.getFileDescriptor();
	public  static final Descriptor desc = fileDesc.getMessageTypes().get(0);

	public static final ProtoSummaryStore instance = new ProtoSummaryStore();

	private static final BoolOpt DO_SEARCH = new BoolOpt(Const.USE_EXTENDED_LOOKUP, true);

	private ArrayList<ProtoSummary.Proto> summaries = new ArrayList<ProtoSummary.Proto>(500);
	private HashMap<String, ProtoSummary.Proto> summaryMap;

	private HashSet<String> protoDirs = new HashSet<String>();

	private TextLog txtLog = new TextLog();
	private boolean toInit = true;
	private boolean changed = false;

	private String lastDir = Parameters.getFileName(Parameters.COPYBOOK_DIRECTORY);

	private ProtoSummaryStore() {
		Parameters.addParameterChangeListner(this);
	}

	public void init() {
		File sumFile = new File(SUMMARY_FILE);
		File protoDirFile = new File(PROTO_DIR_FILE);
		if (sumFile.exists()) {
			BufferedInputStream is = null;
			try {
				ProtoSummary.Proto.Builder b = ProtoSummary.Proto.newBuilder();

				is = new BufferedInputStream(new FileInputStream(sumFile));
				while (b.mergeDelimitedFrom(is)) {
					addProtoSummary(b);
					b = ProtoSummary.Proto.newBuilder();
				}
				is.close();
			} catch (Exception e) {
				Common.logMsg("Error Loading Proto Summary file:", e);
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		summaryMap = new HashMap<String, ProtoSummary.Proto>(summaries.size() * 4 / 3 + 10);
		for (ProtoSummary.Proto p : summaries) {
			summaryMap.put(getKey(p), p);
		}

		if (protoDirFile.exists()) {
			BufferedReader r = null;
			try {
				r = new BufferedReader(new FileReader(protoDirFile));
				String s;
				while ((s = r.readLine()) != null) {
					addProtoDir(s, true);
				}
				r.close();
			} catch (IOException e) {
				Common.logMsg("Error Processing Proto Directory file", e);
				e.printStackTrace();
			} finally {
				if (r != null) {
					try {
						r.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		instance.toInit = false;

		if (changed) {
			saveSummary();
		}
	}

	private void addProtoSummary(ProtoSummary.Proto.Builder b) {
		File protoFile = new File(b.getFilename());

		if (protoFile.exists()) {
			if (b.getFilename().indexOf("DTAR020") > 0) {
				System.out.println("*");
			}
			if (protoFile.lastModified() > b.getChangeDate()) {
				try {
					if ((new File(b.getFilename())).exists()) {
						ProtoSummary.Proto p = getProtoSummary(b.getFilename());
						if (p != null) {
							summaries.add(p);
						}
					}
				} catch (Exception e) {
					Common.logMsg(AbsSSLogger.ERROR, "Error Retrieving proto definition:", b.getFilename() , e);
				}
			} else {
				summaries.add(b.build());
			}
		}
	}

	/**
	 * Add a directory to the list of monitored Proto source directories
	 * @param dir directory to look for Proto files in.
	 */
	public final void addProtoDirectory(String dir) {

		File dirFile;
		if (dir != null && ! "".equals(dir) && ! protoDirs.contains(dir)
		&& (dirFile = new File(dir)).exists()
		&& (dirFile).isDirectory()) {
			new Thread(new loadDir(dir)).start();
		}
	}

	/**
	 * public (for testing purposes), use addProtoDirectory instead
	 * @param dir directory
	 */
	public final void addProtoDir(String dir) {
		addProtoDir(dir, true);
	}

	private final void addProtoDir(String dir, boolean addDir) {
		if (dir != null
		&& ! protoDirs.contains(dir)) {
			checkProtoDir(dir, addDir);
		}
	}


	public final void checkProtoDirs() {

		for (String d : protoDirs) {
			checkProtoDir(d, false);
		}
	}


	private final void checkProtoDir(String dir, boolean addDir) {
		File dirFile = new File(dir);
		if (dirFile.exists()
		&&  dirFile.isDirectory()) {
			File[] files = dirFile.listFiles();
			String filename;
			boolean found = false;

			for (File file : files) {
				filename = file.getAbsolutePath();
				String key = getKey(filename);
				if (file.isDirectory()) {
					addProtoDir(filename, false);
				} else if (summaryMap.containsKey(key)
					   &&  file.lastModified() <= summaryMap.get(key).getChangeDate()) {
					found = true;
				} else if (filename != null
						&& file.length() < 100000
						&& filename.toLowerCase().indexOf(".p") > 0
						&& (! filename.endsWith("~")) && (! filename.endsWith("py"))
						&& (! filename.toLowerCase().endsWith(".bak"))) {
//					System.out.println();
//					System.out.print(" ### " + filename);
					try {
						ProtoSummary.Proto p = getProtoSummary(filename);

						if (p != null) {
							summaries.add(p);
							summaryMap.put(key, p);
							changed = true;
							found = true;
						}
					} catch (Exception e) {
						System.out.println("===> " + e.getMessage() + " " + e.getClass().getName() + " " + filename);
					}
				}
			}
			if (found && addDir) {
				protoDirs.add(dir);
			}
		}
	}


	private String getKey(ProtoSummary.Proto p) {
		return getKey(p.getFilename());
	}
	private String getKey(String filename) {
		filename = (new File(filename)).getAbsolutePath();
		if (File.separator.equals("\\")) {
			return filename.toLowerCase();
		}
		return filename;
	}

	private ProtoSummary.Proto getProtoSummary(String filename)  {
		FileDescriptorSet fd = Utils.getDescriptor(filename, txtLog);

		if (fd != null) {
			return toProtoSummary(fd, new File(filename));
		}
		return null;
	}

	public final ProtoSummary.Proto toProtoSummary(FileDescriptorSet fd, File file) {
		ProtoSummary.Proto.Builder b = ProtoSummary.Proto.newBuilder();
		ProtoSummary.File.Builder fb;
		ProtoSummary.Msg.Builder mb;
		ProtoSummary.Field.Builder fldBld;

		FileDescriptor fdp, fdp2;
		List<Descriptor> ldp, ldp2;
		List<FieldDescriptor> lfp;

		HashSet<String> usedRecords = new HashSet<String>();
		FileDescriptor[] fileDescs = new FileDescriptor[fd.getFileCount()];

		int l1, l2;


		b.setChangeDate(file.lastModified());
		b.setFilename(file.getAbsolutePath());

		Utils.addMessages(fd, usedRecords, fileDescs);

		for (int i = 0; i < fd.getFileCount(); i++) {
			fb = ProtoSummary.File.newBuilder();

			try {
				fdp = fileDescs[i];
				ldp = fdp.getMessageTypes();
				for (int i1 = 0; i1 < ldp.size(); i1++) {
					Descriptor d = ldp.get(i1);
					mb = ProtoSummary.Msg.newBuilder();
					lfp = d.getFields();
					System.out.println();
					if (! usedRecords.contains(d.getFullName())) {
						fb.addMessagesToCheck(i1);
						System.out.print(" --> " + i1 + " " + d.getFullName() + " " + fb.getMessagesCount());
					}
					l1 = 0;
					l2 = 0;

					for (FieldDescriptor f : lfp) {


						fldBld = ProtoSummary.Field.newBuilder();

						fldBld.setType(f.getLiteType().getWireType());
						fldBld.setTag(f.getNumber());

						//System.out.print(" >> " + f.getLiteType().getWireType() + " " + f.getJavaType()
						//		+ " " + f.getType());
						if (f.getLiteType().getJavaType().equals(JavaType.MESSAGE)) {
							//System.out.print(" ++ ");
							boolean search = true;
							for (int j = 0; j < ldp.size(); j++) {
								if (ldp.get(j) == f.getMessageType()) {
									fldBld.setMsgNo(j);
									search = false;
									break;
								}
							}
							if (search) {
								for (int k = 0; k < fd.getFileCount(); k++) {
									if (k != i) {
										fdp2 = fileDescs[k];
										ldp2 = fdp2.getMessageTypes();
										for (int j = 0; j < ldp2.size(); j++) {
											if (ldp2.get(j) == f.getMessageType()) {
												fldBld.setFileNo(k);
												fldBld.setMsgNo(j);
												break;
											}
										}
									}
								}
							}
						}
						if (f.isRequired() || lfp.size() == 1) {
							mb.addFields(fldBld);
							if (l1++ >= NUMER_OF_FIELDS_TO_CHECK) {
								break;
							}
						} else if (l2++  < NUMER_OF_FIELDS_TO_CHECK) {
							mb.addOptionalFields(fldBld);
						}
					}
					System.out.println(" ~~ " + i1 + " " + d.getFullName() + " " + fb.getMessagesCount());
					fb.addMessages(mb);
				}

				b.addFiles(fb);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return b.build();
	}

	private void saveProtoDirectories() {
		File summaryDirFile = new File(PROTO_DIR_FILE);
		File tmpSummaryDirFile = new File(PROTO_DIR_FILE + ".tmp");

		Utils.checkDir(Parameters.getPropertiesDirectoryWithFinalSlash());
		Utils.checkDir(SUMMARY_DIR);
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(tmpSummaryDirFile));
			for (String s : protoDirs) {
				w.write(s);
				w.newLine();
			}
			w.close();
			if ((! summaryDirFile.exists()) || summaryDirFile.delete()) {
				tmpSummaryDirFile.renameTo(summaryDirFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveSummary() {

		BufferedOutputStream os = null;
		File summaryFile = new File(SUMMARY_FILE);
		File tmpSummaryFile = new File(SUMMARY_FILE + ".tmp");

		Utils.checkDir(Parameters.getPropertiesDirectoryWithFinalSlash());
		Utils.checkDir(SUMMARY_DIR);
		try {
			os = new BufferedOutputStream(new FileOutputStream(tmpSummaryFile));
			for (ProtoSummary.Proto p : summaries) {
				p.writeDelimitedTo(os);
			}
			os.close();
			if ((! summaryFile.exists()) || summaryFile.delete()) {
				tmpSummaryFile.renameTo(summaryFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ProtoSummary.Proto addFileDescriptor(String filename, FileDescriptorSet fd) {
		File file = new File(filename);
		ProtoSummary.Proto p = toProtoSummary(fd, file);
		if (p != null) {
			String key = getKey(p);
			if (! summaryMap.containsKey(key)) {
				summaries.add(p);
				summaryMap.put(key, p);

				String path = file.getParent();

				if (! protoDirs.contains(path)) {
					protoDirs.add(path);

					new Thread(new loadDir(path)).start();
				}
			}
		}
		return p;
	}

	public boolean lookupFileDesc(byte[] bytes, FileDescriptionCallback callback) {

		if (DO_SEARCH.isSelected()) {
			try {

				DynamicMessage.Builder builder = DynamicMessage.newBuilder(desc);
				UnknownFieldSet unknownFields;

				builder.mergeFrom(bytes);
				unknownFields = builder.getUnknownFields();

				for (ProtoSummary.Proto p : summaries) {
					try {
						if (checkFileDesc(bytes, p, unknownFields, callback)) {
							return true;
						}
					} catch (Exception e) {
						System.out.println("Error: " + e.getClass().getName() + " " + e.getMessage());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void lookupAllFileDesc(byte[] bytes, FileDescriptionCallback callback) {

		try {
			DynamicMessage.Builder builder = DynamicMessage.newBuilder(desc);
			UnknownFieldSet unknownFields;

			builder.mergeFrom(bytes);
			unknownFields = builder.getUnknownFields();

			for (ProtoSummary.Proto p : summaries) {
				try {
					checkFileDesc(bytes, p, unknownFields, callback);
				} catch (Exception e) {
					System.out.println("Error: " + e.getClass().getName() + " " + e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public final boolean checkFileDesc(
			byte[] bytes,
			ProtoSummary.Proto p,
			UnknownFieldSet unknownFields,
			FileDescriptionCallback callback) {

		List<Integer> l;
		for (int i = 0; i < p.getFilesCount(); i++) {
			//System.out.println(" -- " + i);
			l = p.getFiles(i).getMessagesToCheckList();
			for (int msgNo : l) {
				if (checkMessage(i, msgNo, p, unknownFields)) {
					try {
						FileDescriptorSet fdf = Utils.getDescriptor(p.getFilename(), txtLog);
						@SuppressWarnings("rawtypes")
						AbstractMessage.Builder bld = DynamicMessage.newBuilder(
								ProtoHelper.getFileDescriptor(fdf, i).getMessageTypes().get(msgNo));

						//System.out.print(" Check Unknown Fields " + i + " " + msgNo
						//		+ " " + fdf.getFile(i).getMessageType(msgNo).getName());

						bld.mergeFrom(bytes);
						//System.out.print( " : "  + bld.getUnknownFields().asMap().size()
						//		+ " " + bld.getAllFields().size());

						Message mm = bld.build();
						boolean ok = checkMessage(mm);


						if (callback != null &&  ok) {
							callback.setSchema(p.getFilename(), i, msgNo);
						}
						return ok;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return false;
	}

	private boolean checkMessage(Message mm) {
		if (mm.getUnknownFields() != null && mm.getUnknownFields().asMap() != null && mm.getUnknownFields().asMap() .size() > 0) {
			return false;
		}

		Set<Descriptors.FieldDescriptor> fieldSet = mm.getAllFields().keySet();
		for (Descriptors.FieldDescriptor f : fieldSet) {
			if (f.getLiteJavaType().equals(JavaType.MESSAGE)) {
				boolean ok = true;
				//System.out.println(" ....+ " + f.getFullName() + " "+ f.isRepeated());
				if (f.isRepeated()) {
					int count = mm.getRepeatedFieldCount(f);
					for (int j = 0; ok && j < count; j++) {
						ok = checkMessage((Message) mm.getRepeatedField(f, j));
					}
				} else {
					ok = checkMessage((Message) mm.getField(f));
				}
				if (! ok) {
					return false;
				}
			} else {

				//System.out.println(" ====> " + f.getFullName() + " " + f.isRepeated() + " " + f.isRequired() + " "
				//		+ f.getType() + " " +  mm.getField(f));

			}
		}

		return true;
	}

	private boolean checkMessage(
			int fileNo, int msgNo,
			//byte[] bytes,
			ProtoSummary.Proto p,
			//FileDescriptorSet fds,
			UnknownFieldSet unknownFields) {

		ProtoSummary.Msg msg = p.getFiles(fileNo).getMessages(msgNo);
		int size = unknownFields.asMap().size();

//		System.out.println("Check Message: " + msgNo);
		if (   size <  msg.getFieldsCount()
		|| (   size >  msg.getFieldsCount() + msg.getOptionalFieldsCount()
			&& msg.getOptionalFieldsCount() < NUMER_OF_FIELDS_TO_CHECK
		    && msg.getFieldsCount() < NUMER_OF_FIELDS_TO_CHECK)) {
			return false;
		}
		UnknownFieldSet.Field fld;

		ProtoSummary.Field fldDef;
		boolean couldBeRepeated = msg.getFieldsCount() == 1;
		for (int k = 0; k < msg.getFieldsCount(); k++) {
			fldDef = msg.getFields(k);
			fld = unknownFields.getField(fldDef.getTag());
			if (fld == null || ! check(fld, fldDef, false, couldBeRepeated)) {
//				System.out.println("Check Message 1> " + msgNo + " " + k + " " + fldDef.getTag()
//						+ " " + fldDef.getType());

				return false;
			}
		}
		for (int k = 0; k < msg.getOptionalFieldsCount(); k++) {
			fldDef = msg.getOptionalFields(k);
			fld = unknownFields.getField(fldDef.getTag());
			if (fld != null && ! check(fld, fldDef, true, true)) {

//				System.out.println("Check Message 2> " + msgNo + " " + k + " " + fldDef.getTag()
//						+ " " + fldDef.getType()
//						+ " " + (fld.getVarintList().size() > 0)
//						+ " " + (fld.getFixed32List().size() > 0)
//						+ " " + (fld.getFixed64List().size() > 0)
//						+ " " + (fld.getLengthDelimitedList().size() > 0));
//
//				check(fld, fldDef, true, true);

				return false;
			}
		}


		try {
			for (int k = 0; k < msg.getFieldsCount(); k++) {
				fldDef =  msg.getFields(k);
				if (! checkMsgField(p, fileNo, fldDef, unknownFields.getField(fldDef.getTag()))) {
					return false;
				}
			}
			for (int k = 0; k < msg.getOptionalFieldsCount(); k++) {
				fldDef =  msg.getOptionalFields(k);
				if (! checkMsgField(p, fileNo, fldDef, unknownFields.getField(fldDef.getTag()))) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean checkMsgField(
			ProtoSummary.Proto p, int fileNo, ProtoSummary.Field fldDef, UnknownFieldSet.Field fld)
	throws InvalidProtocolBufferException {

		if (fld != null && fldDef.hasMsgNo() && fld.getLengthDelimitedList().size() > 0) {
			DynamicMessage.Builder builder = DynamicMessage.newBuilder(desc);
			int fno = fileNo;
			if (fldDef.hasFileNo()) {
				fno= fldDef.getFileNo();
			}

			//System.out.println(" -- ");
			//System.out.print("Checking " + fno + " " + k);
			builder.mergeFrom(fld.getLengthDelimitedList().get(0));

			System.out.println("Checking " + fldDef.getTag());
			if (! checkMessage(fno, fldDef.getMsgNo(), p, builder.getUnknownFields())) {
				System.out.println(" failed " + fno + " " + fldDef.getMsgNo()
						);
				return false;
			}
		}
		return true;
	}

	private boolean check(UnknownFieldSet.Field fld, ProtoSummary.Field f, boolean optional, boolean couldBeRepeated) {
		if (optional
		&& fld.getVarintList().size() == 0
		&& fld.getFixed32List().size() == 0
		&& fld.getFixed64List().size() == 0
		&& fld.getLengthDelimitedList().size() == 0) {
			return true;
		}

		switch (f.getType()) {
		case WireFormat.WIRETYPE_VARINT: 	return chkCount(fld.getVarintList().size(), couldBeRepeated);
		case WireFormat.WIRETYPE_FIXED32: 	return chkCount(fld.getFixed32List().size(), couldBeRepeated);
		case WireFormat.WIRETYPE_FIXED64: 	return chkCount(fld.getFixed64List().size(), couldBeRepeated);
		case WireFormat.WIRETYPE_LENGTH_DELIMITED: 	return chkCount(fld.getLengthDelimitedList().size(), couldBeRepeated);
		}
		return false;
	}

	private boolean chkCount(int count, boolean optional) {
		boolean ok = count == 1;
		if (optional) {
			ok = count > 0;
		}
		return ok;
	}


	public ProtoSummary.Proto lookupProtoSummary(String filename) {
		return summaryMap.get(getKey(filename));
	}


	/**
	 * @return the protoDirs
	 */
	public String[] getProtoDirextories() {
		System.out.println(" ^^ " + protoDirs.size());
		return protoDirs.toArray(new String[protoDirs.size()]);
	}

	/* (non-Javadoc)
	 * @see net.sf.RecordEditor.utils.common.AParameterChangeListner#notifyParamChanged()
	 */
	@Override
	public void notifyParamChanged() {

		String newDir = Parameters.getFileName(Parameters.COPYBOOK_DIRECTORY);
		if (newDir == null ||  newDir.equals(lastDir) || protoDirs.contains(newDir) ) {

		} else {
			new Thread(new loadDir(newDir)).start();

			lastDir = newDir;
		}
	}

	public static void initialise() {
		new Thread(
			(new Runnable() {

				@Override
				public void run() {
					Thread.yield();
					getInstance();
				}
			})
		).start();
	}


	/**
	 * @return the instance
	 */
	public static ProtoSummaryStore getInstance() {
		synchronized (instance.summaries) {
			if (instance.toInit) {
				instance.init();
			}
		}
		return instance;
	}


	private static class loadDir implements Runnable {
		String path;

		public loadDir(String path) {
			this.path = path;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			instance.saveProtoDirectories();
			instance.addProtoDir(path);
			instance.saveProtoDirectories();
		}
	}
}
