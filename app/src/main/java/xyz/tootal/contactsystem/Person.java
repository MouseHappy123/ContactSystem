package xyz.tootal.contactsystem;

public class Person {
    private String name;
    private int number;

    public Person(String name, int number){
        setName(name);
        setNumber(number);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}