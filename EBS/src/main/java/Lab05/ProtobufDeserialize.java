package Lab05;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import Lab05.AddressBookProtos.AddressBook;
import Lab05.AddressBookProtos.Person;

public class ProtobufDeserialize {
    private static final String addressBookPath = "./addressBook";

    // Iterates though all people in the AddressBook and prints info about them.
    static void Print(AddressBook addressBook) {
        for (Person person: addressBook.getPeopleList()) {
            System.out.println("Person ID: " + person.getId());
            System.out.println("  Name: " + person.getName());
            if (!person.getEmail().equals("")) {
                System.out.println("  E-mail address: " + person.getEmail());
            }

            for (Person.PhoneNumber phoneNumber : person.getPhonesList()) {
                switch (phoneNumber.getType()) {
                    case MOBILE:
                        System.out.print("  Mobile phone #: ");
                        break;
                    case HOME:
                        System.out.print("  Home phone #: ");
                        break;
                    case WORK:
                        System.out.print("  Work phone #: ");
                        break;
                }
                System.out.println(phoneNumber.getNumber());
            }
        }
    }

    public static void main( String[] args ) throws FileNotFoundException, IOException
    {
        AddressBook addressBook = AddressBook.parseFrom(Files.newInputStream(Paths.get(addressBookPath)));

        Print(addressBook);

    }
}