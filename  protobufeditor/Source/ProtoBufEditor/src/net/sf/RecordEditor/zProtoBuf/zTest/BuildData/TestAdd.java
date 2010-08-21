package net.sf.RecordEditor.zProtoBuf.zTest.BuildData;

import java.io.FileOutputStream;

import com.example.tutorial.AddressBookProtos.AddressBook;
import com.example.tutorial.AddressBookProtos.Person;
import com.example.tutorial.AddressBookProtos.Person.PhoneType;


public class TestAdd {

	static String filename = "/home/bm/Work/Temp/ProtoBuffers/ProtoTest_Address1.bin";
	static PhoneType[] type = {null, Person.PhoneType.MOBILE, Person.PhoneType.HOME, Person.PhoneType.WORK};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		AddressBook.Builder addressBook = AddressBook.newBuilder();
		Person p;
//		byte[] b;
//		int temp;
		
		for (int i = 1; i <50; i++) {
			Person.Builder person = Person.newBuilder();
			person.setId(1000 + i);
			person.setName("name_" + i);
			person.setEmail("me_" + i + "@yahoo.com.au");
			for (int j=1; j < 4; j++) {
				Person.PhoneNumber.Builder phoneNumber =
			        Person.PhoneNumber.newBuilder().setNumber(((10 + j)*10000 + i) + "");
				phoneNumber.setType(type[j]);
				
				person.addPhone(phoneNumber);
			}
			p = person.build();
			addressBook.addPerson(p);
			
//			b= p.toByteArray();
//			System.out.print(" -- " + i);
//			for (int j = 0; j < b.length; j++) {
//				temp = b[j];
//				if (temp < 0) {
//					temp += 128;
//				}
//				System.out.print(" " + temp);
//			}
//			System.out.println();
//			System.out.println(" -- " + i + " " + new String(b));
//			System.out.println();
		}

	    FileOutputStream output = new FileOutputStream(filename);
	    addressBook.build().writeTo(output);

	    output.close();

	}

}
