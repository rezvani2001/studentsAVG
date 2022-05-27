import model.Student;
import pipeline.Queue;

import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    /**
     * the queue of students witches waiting to be written to file
     */
    public Queue<Student> students;

    /**
     * base folder to hold records
     */
    private final String basePath;

    public Writer() {
        this.students = new Queue<>();
        this.basePath = "results/";
    }


    /**
     * writes given record into it`s own file
     *
     * @param student the record we want to write
     */
    public void writeDown(Student student) throws IOException {
        FileWriter writer = new FileWriter(basePath + student.getId() + ".txt");

        writer.write(student.toString());
        writer.flush();
    }
}
