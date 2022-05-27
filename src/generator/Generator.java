package generator;

import model.Lesson;
import model.Status;
import model.Student;
import pipeline.Queue;

import java.io.*;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Generator {
    static int studentCount = 1000;
    static int lessonCount = 30;
    static FileWriter writer;
    static ThreadPoolExecutor threadPool;
    static Random rand;
    static String[] nameList;
    static Queue<Student> studentQueue;
    static Status status;
    static int sw = 0;

    public static void main(String... args) throws IOException {
        long startTime = System.currentTimeMillis();
        writer = new FileWriter("input.json");
        writer.write("[\n");
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        rand = new Random();

        readNameList();

        studentQueue = new Queue<>();
        status = Status.READY;

        threadPool.submit(Generator::generate);
        threadPool.submit(Generator::lookForWrite);

        // makes sure to shut down the app when there is no thread left running on threadPool
        while (threadPool.getActiveCount() + threadPool.getQueue().size() != 0) {
            System.out.println(threadPool.getActiveCount() + "    " + threadPool.getQueue().size());
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        writer.write("]");
        writer.flush();

        System.out.println("total generation time: " + (System.currentTimeMillis() - startTime));

        // shuts down the threadPool to end the program
        threadPool.shutdown();
    }

    public static void generate() {
        for (int i = 0; i < studentCount; i++) {
            studentQueue.push(addLesson(generateStudent()));
        }
        status = Status.DONE;
    }

    public static void readNameList() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("nameList.txt"));
        nameList = new String[100];
        for (int i = 0; i < 100; i++) {
            nameList[i] = reader.readLine();
        }
    }

    public static Student addLesson(Student student) {
        for (int i = 0; i < lessonCount; i++) {
            student.setLessons(generateLesson());
        }

        return student;
    }

    public static Student generateStudent() {
        return new Student(UUID.randomUUID(), nameList[rand.nextInt(100)] + " " + nameList[rand.nextInt(100)]);
    }

    public static Lesson generateLesson() {
        return new Lesson(rand.nextInt(3) + 1, rand.nextInt(81) / 4.0);
    }

    public static void lookForWrite() {
        while (status.equals(Status.READY) || studentQueue.size > 0) {
            if (studentQueue.size > 0) {
                try {
                    write(studentQueue.pop());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void write(Student student) throws IOException {
        if (sw == 1)
            writer.write(",\n");
        else sw = 1;
        writer.write(student.toString());
        writer.flush();
    }
}
