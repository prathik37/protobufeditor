package net.sf.RecordEditor.ProtoBuf.re.Test.Sales;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Asale {
	private Sales.sale saleProto;

	/**
	 * @return the saleProto
	 */
	public final Sales.sale getSaleProto() {
		return saleProto;
	}

	/**
	 * @param saleProto the saleProto to set
	 */
	public final void setSaleProto(Sales.sale saleProto) {
		this.saleProto = saleProto;
	}

	/**
	 * @return
	 * @see net.sf.RecordEditor.ProtoBuf.re.Test.Sales.Sales.sale#getDepartment()
	 */
	public int getDepartment() {
		return saleProto.getDepartment();
	}

	/**
	 * @return
	 * @see net.sf.RecordEditor.ProtoBuf.re.Test.Sales.Sales.sale#getKeycode()
	 */
	public int getKeycode() {
		return saleProto.getKeycode();
	}

	/**
	 * @return
	 * @see net.sf.RecordEditor.ProtoBuf.re.Test.Sales.Sales.sale#getPrice()
	 */
	public BigDecimal getPrice() {

		return new BigDecimal(saleProto.getPrice()).setScale(-3);
	}

	/**
	 * @return
	 * @see net.sf.RecordEditor.ProtoBuf.re.Test.Sales.Sales.sale#getQuantity()
	 */
	public int getQuantity() {
		return saleProto.getQuantity();
	}

	/**
	 * @return
	 * @see net.sf.RecordEditor.ProtoBuf.re.Test.Sales.Sales.sale#getSaleDate()
	 */
	public Calendar getSaleDate() {

		int d = saleProto.getSaleDate();
		return new GregorianCalendar(d / 10000, (d / 100) % 100, d % 100);
	}

	
	/**
	 * @return
	 * @see net.sf.RecordEditor.ProtoBuf.re.Test.Sales.Sales.sale#getStore()
	 */
	public int getStore() {
		return saleProto.getStore();
	}

	/**
	 * @return
	 * @see com.google.protobuf.AbstractMessage#hashCode()
	 */
	public int hashCode() {
		return saleProto.hashCode();
	}
	
	
}
