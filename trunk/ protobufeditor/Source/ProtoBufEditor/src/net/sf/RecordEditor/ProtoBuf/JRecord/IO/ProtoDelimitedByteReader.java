package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.protobuf.CodedInputStream;

import net.sf.JRecord.ByteIO.AbstractByteReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;

public class ProtoDelimitedByteReader extends AbstractByteReader {

	private InputStream in;
	private CodedInputStream coded;


	@Override
	public void open(InputStream inputStream) throws IOException {
		in = new BufferedInputStream(inputStream, Consts.IO_BUFFER_SIZE);
		coded = CodedInputStream.newInstance(in);
	}
	
	
	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public byte[] read() throws IOException {
		if (coded == null) {
			throw new IOException("Reader has not been opened !!!");
		}
		if (coded.isAtEnd()) {
			return null;
		}
		return coded.readBytes().toByteArray();
	}

//
//	  /**
//	   * Reads a varint from the input one byte at a time, so that it does not
//	   * read any bytes after the end of the varint.  If you simply wrapped the
//	   * stream in a CodedInputStream and used {@link #readRawVarint32(InputStream)}
//	   * then you would probably end up reading past the end of the varint since
//	   * CodedInputStream buffers its input.
//	   */
//	  private static int readRawVarint32(final InputStream input) throws IOException {
//	    int result = 0;
//	    int offset = 0;
//	    for (; offset < 32; offset += 7) {
//	      final int b = input.read();
//	      if (b == -1) {
//	        throw truncatedMessage();
//	      }
//	      result |= (b & 0x7f) << offset;
//	      if ((b & 0x80) == 0) {
//	        return result;
//	      }
//	    }
//	    // Keep reading up to 64 bits.
//	    for (; offset < 64; offset += 7) {
//	      final int b = input.read();
//	      if (b == -1) {
//	        throw truncatedMessage();
//	      }
//	      if ((b & 0x80) == 0) {
//	        return result;
//	      }
//	    }
//	    throw  new InvalidProtocolBufferException(
//	      "CodedInputStream encountered a malformed varint.");
//	 }
//
//	 private static InvalidProtocolBufferException truncatedMessage() {
//		    return new InvalidProtocolBufferException(
//		      "While parsing a protocol message, the input ended unexpectedly " +
//		      "in the middle of a field.  This could mean either than the " +
//		      "input has been truncated or that an embedded message " +
//		      "misreported its own length.");
//	}
}
