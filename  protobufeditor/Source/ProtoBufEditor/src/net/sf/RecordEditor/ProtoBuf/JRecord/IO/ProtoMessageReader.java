package net.sf.RecordEditor.ProtoBuf.JRecord.IO;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.JRecord.Common.RecordException;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.Consts;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLayoutDef;
import net.sf.RecordEditor.ProtoBuf.JRecord.Def.ProtoLine;

public class ProtoMessageReader extends AbstractLineReader<ProtoLayoutDef>{

	private ProtoLine data;


	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#open(java.io.InputStream, net.sf.JRecord.Details.AbstractLayoutDetails)
	 */
	@Override
	public void open(InputStream inputStream, ProtoLayoutDef layout)
			throws IOException, RecordException {

		InputStream in = new BufferedInputStream(inputStream, Consts.IO_BUFFER_SIZE);
		super.setLayout(layout);

		if (in.available() <= 0) {
			data = null;
		} else {
			data = new ProtoLine(layout, layout.getPrimaryMsgBuilder().mergeFrom(in, layout.getRegistry()));
		}
		in.close();
	}

	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#read()
	 */
	@Override
	public ProtoLine read() throws IOException {

		ProtoLine ret =  data;
		data = null;
		return ret;
	}

	/**
	 * @see net.sf.JRecord.IO.AbstractLineReader#close()
	 */
	@Override
	public void close() throws IOException {

	}


//	private InputStream in;
//	private ProtoLayoutDef layoutDef;
//	private boolean eof = true;
//
//	/**
//	 * @see net.sf.JRecord.IO.AbstractLineReader#close()
//	 */
//	@Override
//	public void close() throws IOException {
//		eof = true;
//		in.close();
//	}
//
//	/**
//	 * @see net.sf.JRecord.IO.AbstractLineReader#open(java.io.InputStream, net.sf.JRecord.Details.AbstractLayoutDetails)
//	 */
//	@Override
//	public void open(InputStream inputStream, ProtoLayoutDef layout)
//			throws IOException, RecordException {
//		in = new BufferedInputStream(inputStream, Consts.IO_BUFFER_SIZE);
//		layoutDef = layout;
//		//System.out.println("ProtoMessageReader available: " + in.available() );
//		eof = in.available() <= 0;
//	}
//
//	/**
//	 * @see net.sf.JRecord.IO.AbstractLineReader#read()
//	 */
//	@Override
//	public AbstractLine read() throws IOException {
//		if (eof) {
//			return null;
//		}
//		eof = true;
//		return new ProtoLine(layoutDef, layoutDef.getPrimaryMsgBuilder().mergeFrom(in));
//	}


}
