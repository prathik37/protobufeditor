package net.sf.RecordEditor.ProtoBuf.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.text.JTextComponent;

import net.sf.JRecord.Log.AbsSSLogger;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ConstClass;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoHelper;
import net.sf.RecordEditor.utils.common.Common;
import net.sf.RecordEditor.utils.lang.LangConversion;
import net.sf.RecordEditor.utils.params.Parameters;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;

public class Utils {
	private static final String MESSAGES_COMPILING_PROTO_FILE = LangConversion.convert("Messages Compiling proto file");
	private static final String ERROR_COMPILING_PROTO_FILE =  LangConversion.convert("Error Compiling proto file:");
	private static final String PROTO_COMPILE_OUTPUT = "--descriptor_set_out=";

	public static FileDescriptor getFileDescriptor() {
		 java.lang.String descriptorData =
		      "\n\017SdMessage.proto\022\010tutorial\"\036\n\005SdMsg\022\025\n\013" +
		      "proto_files\030\240\215\006 \002(\014B\026\n\024com.example.tutor" +
		      "ial"
			    ;
	      final byte[] descriptorBytes;
	      try {
	        descriptorBytes = descriptorData.toString().getBytes("ISO-8859-1");
	      } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException(
	          "Standard encoding ISO-8859-1 not supported by JVM.", e);
	      }

	      FileDescriptorProto proto;
	      try {
	        proto = FileDescriptorProto.parseFrom(descriptorBytes);
	      } catch (InvalidProtocolBufferException e) {
	        throw new IllegalArgumentException(
	          "Failed to parse protocol buffer descriptor for generated code.", e);
	      }


	      final FileDescriptor result;
	      try {
	        result = FileDescriptor.buildFrom(proto, new com.google.protobuf.Descriptors.FileDescriptor[] {});
	      } catch (DescriptorValidationException e) {
	        throw new IllegalArgumentException(
	          "Invalid embedded descriptor for \"" + proto.getName() + "\".", e);
	      }

	      return result;
	}

	public static FileDescriptorSet getDescriptor(String inFileName, AbsSSLogger log)  {
		FileDescriptorSet fd = null;

		try {
			fd = getFileDescriptor(inFileName);
		} catch (IOException e) {
		}

		if (fd == null || ! isFileDescriptorSet(fd)) {
			try {
				fd = compileProto(inFileName, "", null, log);
			} catch (IOException e) {
			}
		}
		return fd;
	}

    public static FileDescriptorSet compileProto(String layoutName, String importDir, JTextComponent msgTxt) throws IOException {
    	return compileProto(layoutName, importDir, msgTxt, Common.getLogger());
	}

    public static FileDescriptorSet compileProto(String layoutName, String importDir, JTextComponent msgTxt, AbsSSLogger log) throws IOException {
   	int c;
    	StringBuilder oBuf = new  StringBuilder();
    	int pos = Math.max(
    			layoutName.lastIndexOf("\\"),
    			layoutName.lastIndexOf("/"));
    	String lastPart = layoutName;

    	String compiledFile;
    	String s, cmd, imbed;




    	ArrayList<String> cmdOptList = new ArrayList<String>(15);
    	StringBuilder cmdBld = new StringBuilder();
    	String[] cmdAndArgs;

    	cmdOptList.add(ConstClass.getProtoC());
    	cmdOptList.add(layoutName);
    	if (pos > 0) {
    		lastPart = layoutName.substring(pos + 1);
    		cmdOptList.add("--proto_path=" + layoutName.substring(0, pos));
    	}

    	imbed = importDir;
    	if (! "".equals(imbed)) {
    		cmdOptList.add("--proto_path=" + imbed);
    		if (! imbed.equals(Parameters.getString(ConstClass.VAR_PROTO_IMBED_DIR))) {
    			Parameters.setProperty(ConstClass.VAR_PROTO_IMBED_DIR, imbed);
    			Parameters.writeProperties();
    		}
    	}

    	checkDir(Parameters.getPropertiesDirectoryWithFinalSlash());
    	checkDir(Parameters.getPropertiesDirectoryWithFinalSlash() + "TempWork");
    	compiledFile = Parameters.getPropertiesDirectoryWithFinalSlash()
    				+ "TempWork" + File.separator
					+ lastPart + "comp";
    	cmdOptList.add( "--include_imports");
    	cmdOptList.add(PROTO_COMPILE_OUTPUT + compiledFile);

    	for (int i = 0 ; i < ConstClass.NUMBER_OF_PROTOC_OPTIONS; i++) {
    		s = Parameters.getString(ConstClass.VAR_PROTOBUF_COMPILE_OPTS + i);
    		if (s != null && ! "".equals(s)) {
    			cmdOptList.add(s);
    		}
    	}


    	cmdAndArgs = new String[cmdOptList.size()];
    	cmdAndArgs = cmdOptList.toArray(cmdAndArgs);
    	for (int i = 0; i < cmdAndArgs.length; i++) {
    		cmdBld.append(cmdAndArgs[i]).append(' ');
    	}
    	cmd = cmdBld.toString();

    	try {
    		File file = new File(compiledFile);
    		if (file.exists()) {
    			file.delete();
    		}
    	} catch (Exception e) {
		}

    	Process child = Runtime.getRuntime().exec(cmdAndArgs);

		InputStream in = child.getErrorStream();

		while ((c = in.read()) >= 0) {
			oBuf.append((char)c);
		}
		in.close();
		child.destroy();
		s = oBuf.toString();

		if (s != null && !"".equals(s)) {
			log.logMsg(AbsSSLogger.WARNING,
					MESSAGES_COMPILING_PROTO_FILE + "\n\n" + cmd + "\n\n"+s);
//		} else {
//			Common.logMsg(AbsSSLogger.WARNING, cmd, null);
		}

   		File file = new File(compiledFile);
   		if (! file.exists()) {
   			if (msgTxt != null) {
   				msgTxt.setText(ERROR_COMPILING_PROTO_FILE + "\n" + s);
   			}
   			throw new RuntimeException(ERROR_COMPILING_PROTO_FILE + " " + s);
   		}

    	return getFileDescriptor(compiledFile);
    }


	public static void checkDir(String s) {
		File f = new File(s);
		if (! f.exists()) {
			f.mkdir();
		}
	}

	public static boolean isFileDescriptorSet(String inFileName)  {
		try {
			FileDescriptorSet fds = getFileDescriptor(inFileName);
			return fds != null && fds.getFileCount() > 0;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean isFileDescriptorSet(FileDescriptorSet fds) {
		return fds != null && fds.getFileCount() > 0;
	}

    public static FileDescriptorSet getFileDescriptor(String inFileName) throws IOException {
    	FileDescriptorSet ret = null;
    	FileInputStream infile = new FileInputStream(inFileName);
    	try {
    		ret = FileDescriptorSet.parseFrom(infile);
		} finally {
			infile.close();
		}

		return ret;
    }


    public static void addMessages(FileDescriptorSet dp, HashSet<String> usedRecords, FileDescriptor[] fileDescs) {
		List<Descriptor> msgTypes;

		for (int i = 0; i < dp.getFileCount(); i++) {
			try {
				fileDescs[i] = ProtoHelper.getFileDescriptor(dp, i);
				msgTypes = fileDescs[i].getMessageTypes();

				for (Descriptor d : msgTypes ) {
					addFields(usedRecords, d.getFields());
					addFields(usedRecords, d.getExtensions());
				}

				addFields(usedRecords, fileDescs[i].getExtensions());
			} catch (Exception e) {
			}
		}
    }

    private static void addFields(HashSet<String> usedRecords, List<FieldDescriptor> fldList) {

		for (FieldDescriptor field : fldList) {
			if (field.getJavaType().equals(JavaType.MESSAGE)) {
				usedRecords.add(field.getMessageType().getFullName());
			}
		}
    }
}
