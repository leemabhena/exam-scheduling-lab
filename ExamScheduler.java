// We are the sole authors of the work in this repository.

import structure5.*;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Arrays;

/**
 * Class ExamScheduler creates exam slots for students such that
 * the student have to take at most 1 course per day.
 * NOTE: we implemented extension 1 and 2
 */
public class ExamScheduler {

  /* list of all courses offered */
  protected Vector<String> courses;

  /* list of all the students */
  protected Vector<Student> students;

  /* graph of all the courses offered linked by the schedule of each student */
  protected GraphListUndirected<String, Integer> graph;

  /* schedule is a table of course and its time slot */
  protected Hashtable<String, Integer> schedule;

  /**
   * Constructor that initializes the exam scheduler fields.
   */
  public ExamScheduler() {
    courses = new Vector<String>();
    students = new Vector<Student>();
    graph = new GraphListUndirected<String, Integer>();
    schedule = new Hashtable<>();
  }

  /**
   * Function to add course to courses vector.
   * @param course the course to add.
   * @pre course is a String
   * @post course is added to courses list.
   */
  private void addCourse(String course) {
    courses.add(course); // add course
  }

  /**
   * Saves a student to a student list
   * @param s Student object to save to students list
   * @pre s is a Student object
   * @post s is added to student list
   */
  private void addStudent(Student s) {
    students.add(s); // add student
  }

  /**
   * Function to read input from file and parse it.
   * @pre should pass in a file "java ExamScheduler < file.txt"
   * @post students and courses list are populated with data from file.
   */
  public void addInfoFromFile() {
    Scanner sc = new Scanner(System.in);
    while (sc.hasNextLine()) {
      Vector<String> c = new Vector<>(); // list of courses student is taking
      // first line is name and next 4 courses ADD them to c
      String name = sc.nextLine();
      c.add(sc.nextLine());
      c.add(sc.nextLine());
      c.add(sc.nextLine());
      c.add(sc.nextLine());
      // create a student object and save it
      Student st = new Student(name, c);
      addStudent(st);
      // add the student courses to the courses list
      for (int i = 0; i < c.size(); i++) {
        if (!courses.contains(c.get(i))) { // avoid duplicates
          addCourse(c.get(i));
        }
      }
    }
  }

  /**
   * Function that creates the graph.
   * @post graph is set up
   */
  public void setUpGraph() {
    // create graph of each student's courses
    for (int x = 0; x < students.size(); x++) {
      Vector<String> c = students.get(x).getCourses(); // each student courses
      // add all vertices
      for (int y = 0; y < c.size(); y++) {
        if (!graph.contains(c.get(y))) { // avoid duplicates
          graph.add(c.get(y));
        }
      }
      // add edges between vertices
      for (int i = 0; i < c.size(); i++) {
        for (int j = 0; j < i; j++) {
          if (graph.containsEdge(c.get(i), c.get(j))) { // update weight
            graph.getEdge(c.get(i), c.get(j)).setLabel(graph.getEdge(c.get(i), c.get(j)).label() + 1);
          } else {
            graph.addEdge(c.get(i), c.get(j), 1); // set weight to 1
          }
        }
      }
    }
  }

  /**
   * Function that creates schedules
   * NOTE: This answers the first part of the extension.
   * @return a String of slots ordered with courses ordered
   * @pre graph should have been set up
   * @post returns a String of slots ordered with courses ordered
   */
  public String createSchedule() {
    Integer slotNumber = 1; // start at slot 1
    Vector<String> tempCourse = courses;
    Vector<Vector<String>> internalResult = new Vector<Vector<String>>(); // stores course per slot
    while (tempCourse.size() > 0) { // for all courses
      internalResult.add(new Vector<String>()); // initialize elems of internal result
      internalResult.get(slotNumber - 1).add(tempCourse.get(0)); // add the first course
      schedule.put(tempCourse.get(0), slotNumber); // add the slot n num to table
      Vector<String> neighborList = new Vector<>(); // create a list of all neighbors
      Iterator<String> nIterator = graph.neighbors(tempCourse.get(0));
      while (nIterator.hasNext()) {
        neighborList.add(nIterator.next());
      } // create schedule unordered
      helperAddCourseToSlot(internalResult, slotNumber, neighborList, tempCourse);
      for (int i = 0; i < internalResult.get(slotNumber - 1).size(); i++) {
        graph.remove(internalResult.get(slotNumber - 1).get(i));
      } // remove the added course from graph and courses
      tempCourse.remove(0);
      slotNumber++;
    }

    return orderCoursesToPrint(internalResult);
  }

  /**
   * Function that creates sorted String.
   * @param internalResult the vector to create String from
   * @return the string of courses in sorted order
   * @post he string of courses in sorted order
   */
  private String orderCoursesToPrint(Vector<Vector<String>> internalResult) {
    String tResult = "EXTENSION 1: Prints Courses in each slot in ALPHABETICAL ORDER\n"; // string to return
    for (int i = 0; i < internalResult.size(); i++) { // order the slots
      tResult += "Slot " + (i + 1) + ": ";
      sortCourses(internalResult.get(i));
      for (int j = 0; j < internalResult.get(i).size(); j++) {
        tResult += internalResult.get(i).get(j) + " ";
      }
      tResult += "\n";
    }
    return tResult;
  }

  /**
   * Function that creates slots
   * @param internalResult - vector of slots
   * @param slotNumber     - the slotNumber
   * @param neighborList   - a list of all the neighbors
   * @param tempCourse     - the current course we checking
   * @post creates a schedule
   * @pre graph exists
   */
  private void helperAddCourseToSlot(Vector<Vector<String>> internalResult, Integer slotNumber,
      Vector<String> neighborList, Vector<String> tempCourse) {
    // check if a course is a neighbor
    Iterator<String> gIterator = graph.iterator();
    while (gIterator.hasNext()) {
      String temp = gIterator.next(); // current course
      Boolean addFlag = true;
      for (int i = 0; i < internalResult.get(slotNumber - 1).size(); i++) {
        if (this.isThereAnEdge(internalResult.get(slotNumber - 1).get(i), temp)) {
          addFlag = false; // if course already in slot is a neighbor to the current dont add it
        }
      }
      if (!neighborList.contains(temp) && temp != tempCourse.get(0) && addFlag) {
        internalResult.get(slotNumber - 1).add(temp); // add slot list of slots
        schedule.put(temp, slotNumber); // add course and slot to table
        tempCourse.remove(temp); // remove it when done
      }
    }
  }

  /**
   * Function that checks if two courses are neighbors
   * @param x is course
   * @param y is a course
   * @return true iff x and y are neighbors else false
   * @pre x and y are vertices in graph
   * @post true iff x and y are neighbors else false
   */
  protected Boolean isThereAnEdge(String x, String y) {
    if (graph.contains(x) && graph.contains(y)) { // x and y should be vertices
      return graph.containsEdge(x, y);
    }
    return false;
  }

  /**
   * Function that prints students in alphabetical order and the time slots
   * NOTE: This answers second part of the extension.
   * @post prints student exam slots with their name in alphabetical order
   */
  public void printScheduleStudent() {
    System.out.println("EXTENSION 2: Students in alphabetical order and courses per slot");
    Student[] sortedStudentArr = sortStudents(students); // arr of sorted students
    for (int i = 0; i < sortedStudentArr.length; i++) {
      String name = sortedStudentArr[i].getName();
      Vector<String> studCourses = sortedStudentArr[i].getCourses();
      String[] courseArr = new String[4]; // student courses
      String toPrint = name + " \n"; // string to print
      for (int j = 0; j < studCourses.size(); j++) { // copy students courses in order to sort them
        Integer slotNum = schedule.get(studCourses.get(j));
        courseArr[j] = String.valueOf(slotNum) + ": " + studCourses.get(j);
      }
      Arrays.sort(courseArr); // sort the student arr
      for (String course : courseArr) { // add courses to string to print
        toPrint += "Slot " + course + "\n";
      }
      System.out.println(toPrint);
    }
  }

  /**
   * Functions that orders students by name
   * @param st vector of all students
   * @return sorted array of students
   * @post returns sorted array of students
   */
  protected Student[] sortStudents(Vector<Student> st) {
    Student[] studentArr = new Student[st.size()]; // copy students into array
    for (int i = 0; i < st.size(); i++) {
      studentArr[i] = st.get(i);
    }
    Arrays.sort(studentArr, (Student student1, Student student2) -> { // sort array by name
      return student1.getName().compareTo(student2.getName());
    });
    return studentArr;
  }

  /**
   * Function to sort students courses
   * @param c Vector of courses to sort
   * @return sorted Vector of courses
   * @post returns a sorted vector
   */
  protected Vector<String> sortCourses(Vector<String> c) {
    String[] cArr = new String[c.size()]; // copy vector into array
    c.copyInto(cArr);
    Arrays.sort(cArr); // sort the array
    for (int i = 0; i < c.size(); i++) {
      c.set(i, cArr[i]); // build a sorted vector
    }
    return c;
  }

  /**
   * Function that execute, creates and print the schedule
   * @param args - command line arguments
   */
  public static void main(String[] args) {
    // create exam scheduler
    ExamScheduler eScheduler = new ExamScheduler();
    // read from file
    eScheduler.addInfoFromFile();
    // set up the graph
    eScheduler.setUpGraph();
    // print courses in alphabetical order, EXTENSION 1
    System.out.println(eScheduler.createSchedule());
    // print slot per student, EXTENSION 2
    eScheduler.printScheduleStudent();
  }
}
