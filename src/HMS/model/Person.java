package HMS.model;

public abstract class Person {

    // variables
    private final String id;
    private String name;
    private int age;

    // constructor
    protected Person(String id, String name, int age) {
        this.id = id;
        setName(name);
        setAge(age);
    }

    // getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // setters
    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    public void setAge(int age) {
        if (age < 0 || age > 120)
            throw new IllegalArgumentException("Invalid age");
        this.age = age;
    }

    // display basic info
    protected void displayBasicInfo() {
        System.out.println("ID: " + id + ", Name: " + name + ", Age: " + age);
    }
}
