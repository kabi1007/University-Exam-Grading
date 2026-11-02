/** ABSTRACTION: an abstract class - we never create a plain Person, only a Student. */
public abstract class Person {
    private String id;      // ENCAPSULATION: private fields, access through methods
    private String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public abstract String getRole();   // abstract method: subclass must define it

    @Override
    public String toString() {
        return getRole() + " " + id + " - " + name;
    }
}
