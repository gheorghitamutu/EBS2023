package Lab05;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import Lab05.AddressBookProtos.AddressBook;
import Lab05.AddressBookProtos.Person;

public class ProtobufSerialize
{
    private static final String addressBookPath = "./addressBook";

    static Person PromptForAddress(BufferedReader stdin) throws IOException {

        Person.Builder person = Person.newBuilder();

        System.out.print("Enter person ID: ");
        person.setId(Integer.parseInt(stdin.readLine()));

        System.out.print("Enter name: ");
        person.setName(stdin.readLine());

        System.out.print("Enter email address (blank for none): ");
        String email = stdin.readLine();
        if (email.length() > 0) {
            person.setEmail(email);
        }

        while (true) {
            System.out.print("Enter a phone number (or leave blank to finish): ");
            String number = stdin.readLine();
            if (number.length() == 0) {
                break;
            }

            Person.PhoneNumber.Builder phoneNumber =
                    Person.PhoneNumber.newBuilder().setNumber(number);

            System.out.print("Is this a mobile, home, or work phone? ");
            String type = stdin.readLine();
            switch (type) {
                case "mobile":
                    phoneNumber.setType(Person.PhoneType.MOBILE);
                    break;
                case "home":
                    phoneNumber.setType(Person.PhoneType.HOME);
                    break;
                case "work":
                    phoneNumber.setType(Person.PhoneType.WORK);
                    break;
                default:
                    System.out.println("Unknown phone type.  Using default.");
                    break;
            }

            person.addPhones(phoneNumber);
        }

        return person.build();
    }

    public static void main( String[] args ) throws IOException
    {
        AddressBook.Builder addressBook = AddressBook.newBuilder();

        // Read the existing address book.
        // The "addressbook" file should already exist at the provided path - e.g., to create touch /path/to/addressbook
        try {
            addressBook.mergeFrom(new FileInputStream(addressBookPath));
        } catch (FileNotFoundException e) {
            System.out.println(addressBookPath + ": File not found.");
        }

        // Add an address.
        addressBook.addPeople(
                PromptForAddress(new BufferedReader(new InputStreamReader(System.in))
                ));

        // Write the new address book back to disk.
        FileOutputStream output = new FileOutputStream(addressBookPath);
        addressBook.build().writeTo(output);
        output.close();

    }
}