/** ABSTRACTION: an interface. It says WHAT a grading policy must do, not HOW. */
public interface GradingPolicy {
    String getLetter(double total);

    double getPoints(String letter);
}
