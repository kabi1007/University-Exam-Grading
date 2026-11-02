import java.util.ArrayList;

/** INHERITANCE: Student IS-A Person. */
public class Student extends Person {
    private ArrayList<String> courseCodes = new ArrayList<>();

    public Student(String id, String name) {
        super(id, name);
    }

    public void register(String courseCode) {
        if (!courseCodes.contains(courseCode)) {
            courseCodes.add(courseCode);
        }
    }

    public boolean isRegistered(String courseCode) {
        return courseCodes.contains(courseCode);
    }

    public ArrayList<String> getCourseCodes() { return courseCodes; }

    @Override
    public String getRole() { return "Student"; }   // method overriding
}
