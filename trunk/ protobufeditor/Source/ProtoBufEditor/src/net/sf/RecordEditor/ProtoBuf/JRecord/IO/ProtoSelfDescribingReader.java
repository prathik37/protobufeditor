package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;

public interface ProtoSelfDescribingReader {
	public FileDescriptorSet getFileDescriptorSet();
}
