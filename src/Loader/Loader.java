package Loader;

import model.Status;
import model.Student;
import pipeline.Queue;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Loader {
    /**
     * works as a pipeline to make every thing easier
     */
    public Queue<Student> students;

    private final Scanner scanner;

    /**
     * will hold the state of the application
     *
     * if there is some other students to be fetched from file, value will remain READY
     * but at the end of file value changes to DONE
     */
    private Status status;

    public Loader(String fileName) throws FileNotFoundException {
        this.scanner = new Scanner(new BufferedInputStream(new FileInputStream(fileName)));
        this.students = new Queue<>();
        this.status = Status.READY;
        scanner.nextLine();
    }


    /**
     * pushes the student to queue
     */
    public void load() {
        Student student = readStudents();
        students.push(student);
    }


    /**
     * this method loads a chunk of data representing a {@link Student}
     *
     * then will pass the student data to {@link Serializer} to convert it
     * after that the student will be pushed in students queue to be used later
     *
     * @return the fetched student
     */
    private Student readStudents() {
        StringBuilder builder = new StringBuilder();
        int sw = 0;
        while (!scanner.hasNext("]")) {
            builder.append(scanner.nextLine());
            sw = 1;
        }
        builder.append(scanner.nextLine());
        if (scanner.hasNextLine())
            scanner.nextLine();

        if (sw == 0) {
            this.status = Status.DONE;
            return null;
        } else return Serializer.serialize(builder.toString());
    }

    public Status getStatus() {
        return status;
    }

}
