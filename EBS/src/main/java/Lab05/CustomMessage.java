package Lab05;

import java.util.ArrayList;

public class CustomMessage {

    private String name;
    private ArrayList<String> phone_numbers;

    CustomMessage() {
        phone_numbers = new ArrayList<String>();
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_name() {
        return this.name;
    }

    public void set_phone_numbers(ArrayList<String> numbers) {
        phone_numbers.addAll(numbers);
    }

    public int get_phone_numbers_number() {
        return phone_numbers.size();
    }

    public String get_phone_number(int i) {
        if (i < phone_numbers.size()) {
            return phone_numbers.get(i);
        }
        else {
            return "Invalid query";
        }

    }

}
