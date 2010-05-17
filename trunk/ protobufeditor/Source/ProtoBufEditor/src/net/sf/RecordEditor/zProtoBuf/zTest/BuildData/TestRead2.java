package net.sf.RecordEditor.zProtoBuf.zTest.BuildData;
import java.io.FileInputStream;
import java.io.IOException;

import com.example.tutorial.AddressBookProtos.Person;


public class TestRead2 {

	public static void main(String[] args) throws IOException {
		FileInputStream in = new FileInputStream("/home/bm/Work/Temp/ProtoBuffers/ProtoTest_Address2.bin");
		int i = 1;
		Person p;
		do {
			p = Person.parseDelimitedFrom(in);
			
			System.out.print(i++ + ") " + in.available() + " " +p.getId() + "\t" + p.getName() + "\t" + p.getPhoneCount());
			if (p.getPhoneCount() > 0) {
				System.out.print("\t" + p.getPhone(0).getNumber() + "\t" + p.getPhone(0).getType());
			}
			System.out.println();
		} while (in.available() > 0);
	}
}
