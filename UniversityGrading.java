import java.util.ArrayList;
import java.util.Scanner;

public class UniversityGrading {

    static Scanner sc = new Scanner(System.in);
    static GradingPolicy policy = new StandardPolicy();   

    static ArrayList<Course> courses = new ArrayList<>();
    static ArrayList<Student> students = new ArrayList<>();
    static ArrayList<Assessment> assessments = new ArrayList<>();
    static ArrayList<ExamSchedule> schedules = new ArrayList<>();
    static ArrayList<MarkEntry> marks = new ArrayList<>();
    static ArrayList<Grade> grades = new ArrayList<>();

    
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("demo")) {
            loadDemoData();
        }
        while (true) {
            System.out.println("\n===== University Exams & Grading =====");
            System.out.println("1. Add Course");
            System.out.println("2. Add Student");
            System.out.println("3. Create Assessment");
            System.out.println("4. Schedule Exam");
            System.out.println("5. Record Marks");
            System.out.println("6. Publish Grades");
            System.out.println("7. Generate Transcript");
            System.out.println("8. Exit");
            int choice = readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1: addCourse(); break;
                    case 2: addStudent(); break;
                    case 3: createAssessment(); break;
                    case 4: scheduleExam(); break;
                    case 5: recordMarks(); break;
                    case 6: publishGrades(); break;
                    case 7: generateTranscript(); break;
                    case 8: System.out.println("Goodbye!"); return;
                    default: System.out.println("Invalid option.");
                }
            } catch (InvalidDataException e) {         
                System.out.println("Error: " + e.getMessage());
            } finally {
                System.out.println("-- done --");           
            }
        }
    }

    static void addCourse() throws InvalidDataException {
        String code = readLine("Course code: ").toUpperCase();
        if (code.isEmpty() || findCourse(code) != null) {
            throw new InvalidDataException("Course code is empty or already exists.");
        }
        String name = readLine("Course name: ");
        int credits = readInt("Credits: ");
        String term = readLine("Term (e.g. 2025-SEM1, blank = TBD): ");
        if (credits <= 0) {
            throw new InvalidDataException("Credits must be positive.");
        }
        Course c;
        if (term.isEmpty()) {
            c = new Course(code, name, credits);
        } else {
            c = new Course(code, name, credits, term);
        }
        courses.add(c);
        System.out.println("Course added: " + c);
    }

    static void addStudent() throws InvalidDataException {
        String id = readLine("Student ID: ").toUpperCase();
        if (id.isEmpty() || findStudent(id) != null) {
            throw new InvalidDataException("Student ID is empty or already exists.");
        }
        String name = readLine("Student name: ");
        Student s = new Student(id, name);
        String line = readLine("Register courses (comma separated codes, blank for none): ");
        if (!line.isEmpty()) {
            for (String part : line.split(",")) {
                String code = part.trim().toUpperCase();
                if (findCourse(code) == null) {
                    System.out.println("  Skipped unknown course: " + code);
                } else {
                    s.register(code);
                }
            }
        }
        students.add(s);
        System.out.println("Added " + s + ". Registered: " + s.getCourseCodes());
    }

 
    static void createAssessment() throws InvalidDataException {
        Course c = findCourse(readLine("Course code: ").toUpperCase());
        if (c == null) throw new InvalidDataException("Course not found.");

        String id = readLine("Assessment ID: ").toUpperCase();
        if (id.isEmpty() || findAssessment(id) != null) {
            throw new InvalidDataException("Assessment ID is empty or already exists.");
        }
        String name = readLine("Assessment name (e.g. Midterm): ");
        double max = readDouble("Maximum marks: ");
        double weight = readDouble("Weightage % (remaining " + (100 - c.getTotalWeightage()) + "): ");
        if (max <= 0 || weight <= 0) {
            throw new InvalidDataException("Max marks and weightage must be positive.");
        }
        Assessment a = new Assessment(id, c.getCode(), name, max, weight);
        c.addAssessment(a);            // throws if the course would pass 100%
        assessments.add(a);
        System.out.println("Assessment created. Weightage used: " + c.getTotalWeightage() + "%");
    }

    static void scheduleExam() throws InvalidDataException {
        Assessment a = findAssessment(readLine("Assessment ID: ").toUpperCase());
        if (a == null) throw new InvalidDataException("Assessment not found.");

        String date = readLine("Date (YYYY-MM-DD): ");
        String time = readLine("Time (HH:MM): ");
        String venue = readLine("Venue: ");
        for (ExamSchedule e : schedules) {
            if (e.getAssessmentId().equals(a.getId())) {
                throw new InvalidDataException("This assessment is already scheduled.");
            }
            if (e.getDate().equals(date) && e.getTime().equals(time) && e.getVenue().equalsIgnoreCase(venue)) {
                throw new InvalidDataException("Clash: venue already booked at that date and time.");
            }
        }
        schedules.add(new ExamSchedule(a.getId(), date, time, venue));
        System.out.println("Exam scheduled.");
    }

  
    static void recordMarks() throws InvalidDataException {
        Student s = findStudent(readLine("Student ID: ").toUpperCase());
        if (s == null) throw new InvalidDataException("Student not found.");
        Assessment a = findAssessment(readLine("Assessment ID: ").toUpperCase());
        if (a == null) throw new InvalidDataException("Assessment not found.");

        
        if (!s.isRegistered(a.getCourseCode())) {
            throw new InvalidDataException(s.getId() + " is not registered for " + a.getCourseCode()
                    + ". Cannot record marks.");
        }
        double m = readDouble("Marks (out of " + a.getMaxMarks() + "): ");
        if (m < 0 || m > a.getMaxMarks()) {
            throw new InvalidDataException("Marks must be between 0 and " + a.getMaxMarks());
        }

        MarkEntry existing = findMark(s.getId(), a.getId());
        Grade published = findGrade(s.getId(), a.getCourseCode());

        if (existing == null) {                              
            marks.add(new MarkEntry(s.getId(), a.getId(), m));
            System.out.println("Marks recorded.");
            return;
        }
        if (published == null) {                              
            existing.setMarks(m);
            System.out.println("Marks corrected (grades not yet published).");
            return;
        }

       
        if (existing.getAttempts() >= 2) {
            throw new InvalidDataException("Not eligible: the one resit/improvement attempt is already used.");
        }
        String letter = published.getLetter();
        String type;
        if (letter.equals("F")) {
            type = "RESIT";
        } else if (letter.equals("C") || letter.equals("D")) {
            type = "IMPROVEMENT";
        } else {
            throw new InvalidDataException("Not eligible: grade " + letter + " is too high for a resit/improvement.");
        }
        double old = existing.getMarks();
        existing.setMarks(Math.max(old, m));                 
        existing.addAttempt();
        System.out.println(type + " accepted. Old: " + old + ", New: " + m + ", Counted: " + existing.getMarks());

       
        Course c = findCourse(a.getCourseCode());
        Grade updated = new Grade(s.getId(), c.getCode(), computeTotal(s, c), policy);
        saveGrade(updated);
        System.out.printf("Grade updated: %.2f -> %s%n", updated.getTotal(), updated.getLetter());
    }


    static void publishGrades() throws InvalidDataException {
        Course c = findCourse(readLine("Course code: ").toUpperCase());
        if (c == null) throw new InvalidDataException("Course not found.");
        if (c.getAssessments().isEmpty()) {
            throw new InvalidDataException("No assessments defined for this course.");
        }
        if (Math.abs(c.getTotalWeightage() - 100) > 0.0001) {
            throw new InvalidDataException("Cannot publish: weightage totals " + c.getTotalWeightage()
                    + "%, it must be 100%.");
        }

        int count = 0;
        for (Student s : students) {
            if (!s.isRegistered(c.getCode())) continue;
            try {
                double total = computeTotal(s, c);            // Rule 2
                saveGrade(new Grade(s.getId(), c.getCode(), total, policy));   // Rule 3
                count++;
            } catch (InvalidDataException e) {
                System.out.println("  Skipped " + s.getId() + ": " + e.getMessage());
            }
        }
        System.out.println(count + " grade(s) published.");
        printCourseGradeSheet(c);
        printAnalytics(c);
    }

  
    static double computeTotal(Student s, Course c) throws InvalidDataException {
        double total = 0;
        for (Assessment a : c.getAssessments()) {
            MarkEntry me = findMark(s.getId(), a.getId());
            if (me == null) {
                throw new InvalidDataException("no marks for '" + a.getName() + "'");
            }
            total += a.getWeightedScore(me.getMarks());
        }
        return total;
    }

    static void saveGrade(Grade g) {
        Grade old = findGrade(g.getStudentId(), g.getCourseCode());
        if (old != null) grades.remove(old);
        grades.add(g);
    }

   
    static void generateTranscript() throws InvalidDataException {
        Student s = findStudent(readLine("Student ID: ").toUpperCase());
        if (s == null) throw new InvalidDataException("Student not found.");

        printStudentGradeSheet(s);

        ArrayList<Grade> mine = new ArrayList<>();
        for (Grade g : grades) {
            if (g.getStudentId().equals(s.getId())) mine.add(g);
        }
        new Transcript(s, mine, courses).print();
    }

   
    static void printCourseGradeSheet(Course c) {
        System.out.println("\n---- Grade Sheet: " + c + " ----");
        System.out.printf("%-10s %-20s %-8s %-6s %-6s%n", "StudentID", "Name", "Total", "Grade", "Points");
        for (Grade g : grades) {
            if (!g.getCourseCode().equals(c.getCode())) continue;
            Student s = findStudent(g.getStudentId());
            System.out.printf("%-10s %-20s %-8.2f %-6s %-6.1f%n",
                    s.getId(), s.getName(), g.getTotal(), g.getLetter(), g.getPoints());
        }
        System.out.println("Policy: A>=90 (4.0) | B>=80 (3.0) | C>=70 (2.0) | D>=50 (1.0) | F<50 (0.0)");
    }

    
    static void printStudentGradeSheet(Student s) {
        System.out.println("\n---- Grade Sheet: " + s + " ----");
        for (String code : s.getCourseCodes()) {
            Course c = findCourse(code);
            System.out.println(c);
            for (Assessment a : c.getAssessments()) {
                MarkEntry me = findMark(s.getId(), a.getId());
                String got = (me == null) ? "-" : String.valueOf(me.getMarks());
                String when = "not scheduled";
                for (ExamSchedule e : schedules) {
                    if (e.getAssessmentId().equals(a.getId())) when = e.toString();
                }
                System.out.printf("   %-6s %-12s %6s/%-6.1f weight %-5.1f%% [%s]%n",
                        a.getId(), a.getName(), got, a.getMaxMarks(), a.getWeightage(), when);
            }
            Grade g = findGrade(s.getId(), code);
            if (g == null) {
                System.out.println("   => Grade not published");
            } else {
                System.out.printf("   => Total %.2f, Grade %s%n", g.getTotal(), g.getLetter());
            }
        }
    }

    static void printAnalytics(Course c) {
        System.out.println("\n---- Assessment Analytics: " + c.getCode() + " ----");
        System.out.printf("%-8s %-12s %-6s %-9s %-8s %-8s%n", "ID", "Name", "Count", "Average", "Highest", "Lowest");
        for (Assessment a : c.getAssessments()) {
            int count = 0;
            double sum = 0, highest = 0, lowest = 0;
            for (MarkEntry me : marks) {
                if (!me.getAssessmentId().equals(a.getId())) continue;
                if (count == 0) {
                    highest = me.getMarks();
                    lowest = me.getMarks();
                }
                count++;
                sum += me.getMarks();
                if (me.getMarks() > highest) highest = me.getMarks();
                if (me.getMarks() < lowest) lowest = me.getMarks();
            }
            if (count == 0) {
                System.out.printf("%-8s %-12s %-6d %-9s %-8s %-8s%n", a.getId(), a.getName(), 0, "-", "-", "-");
            } else {
                System.out.printf("%-8s %-12s %-6d %-9.2f %-8.2f %-8.2f%n",
                        a.getId(), a.getName(), count, sum / count, highest, lowest);
            }
        }
    }

    static Course findCourse(String code) {
        for (Course c : courses) {
            if (c.getCode().equals(code)) return c;
        }
        return null;
    }

    static Student findStudent(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    static Assessment findAssessment(String id) {
        for (Assessment a : assessments) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }

    static MarkEntry findMark(String studentId, String assessmentId) {
        for (MarkEntry m : marks) {
            if (m.getStudentId().equals(studentId) && m.getAssessmentId().equals(assessmentId)) return m;
        }
        return null;
    }

    static Grade findGrade(String studentId, String courseCode) {
        for (Grade g : grades) {
            if (g.getStudentId().equals(studentId) && g.getCourseCode().equals(courseCode)) return g;
        }
        return null;
    }

    
    static String readLine(String prompt) {
        System.out.print(prompt);
        if (!sc.hasNextLine()) {          
            System.out.println();
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }
    static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
    static void loadDemoData() {
        try {
            courses.add(new Course("CS101", "Java Programming", 4, "2025-SEM1"));
            courses.add(new Course("MA101", "Mathematics", 3, "2025-SEM1"));

            Student asha = new Student("S1", "Asha");
            asha.register("CS101");
            asha.register("MA101");
            Student ravi = new Student("S2", "Ravi");
            ravi.register("CS101");
            Student meena = new Student("S3", "Meena");
            meena.register("CS101");
            meena.register("MA101");
            students.add(asha);
            students.add(ravi);
            students.add(meena);

            addDemoAssessment("A1", "CS101", "Lab Record", 20, 20);
            addDemoAssessment("MID", "CS101", "Midterm", 50, 30);
            addDemoAssessment("FIN", "CS101", "Final", 100, 50);
            addDemoAssessment("B1", "MA101", "Quiz 1", 20, 20);
            addDemoAssessment("B2", "MA101", "End Sem", 100, 80);

            schedules.add(new ExamSchedule("MID", "2025-10-10", "10:00", "Hall1"));
            schedules.add(new ExamSchedule("FIN", "2025-11-20", "14:00", "Hall1"));
            schedules.add(new ExamSchedule("B2", "2025-11-21", "10:00", "Hall2"));

            String[][] data = {   
                {"S1", "A1", "19"}, {"S1", "MID", "45"}, {"S1", "FIN", "92"}, {"S1", "B1", "17"}, {"S1", "B2", "78"},
                {"S2", "A1", "8"},  {"S2", "MID", "20"}, {"S2", "FIN", "30"},
                {"S3", "A1", "15"}, {"S3", "MID", "35"}, {"S3", "FIN", "70"}, {"S3", "B1", "14"}
            };
            for (String[] row : data) {
                marks.add(new MarkEntry(row[0], row[1], Double.parseDouble(row[2])));
            }
            System.out.println("Demo data loaded. Try option 6 (Publish Grades) for CS101.");
        } catch (InvalidDataException e) {
            System.out.println("Demo data error: " + e.getMessage());
        }
    }

    static void addDemoAssessment(String id, String code, String name, double max, double weight)
            throws InvalidDataException {
        Assessment a = new Assessment(id, code, name, max, weight);
        findCourse(code).addAssessment(a);
        assessments.add(a);
    }
}
