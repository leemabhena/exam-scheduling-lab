// We are the sole authors of the work in this repository.

import structure5.*;

public class Student {

  /* name of the student */
  protected String name;
  
  /* courses taken by the student*/
  protected Vector<String> courseList;

  /** Constructor for students
    * @param n a String n that will be the name of the student
    * @param schedule a Vector<String> where each string is a course the student is taking
    * @post the student is created
    */
  public Student(String n, Vector<String> schedule) {
    name = n;
    courseList = schedule;
  }

  /** gets the name of the student
    * @pre the student exists
    * @post the name of the student is returned
    * @return returns the name of the student
    */
  public String getName() {
    return name;
  }

  /** gets the courses the student is taking
    * @pre the student exists
    * @post the courses the student is taking has been returned
    * @return returns the Vector<String> of courses that the student is taking
    */
  public Vector<String> getCourses() {
    return courseList;
  }
}
