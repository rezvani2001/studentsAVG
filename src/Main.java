import Loader.Loader;
import model.Status;
import model.Student;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    /**
     * instance of {@link Loader} class
     */
    static Loader loader;

    /**
     * the {@link ThreadPoolExecutor} to hold and manage threads resources and running time
     */
    static ThreadPoolExecutor threadPool;

    /**
     * the state of the application which is always READY but the time witch there is nothing to be calculated
     * basically tells writer the job is done
     */
    static Status applicationStatus;

    /**
     * the state of writer
     * witch will be READY when ever starts on a thread
     * and wil be DONE if there is noting to write
     */
    static Status writerStatus;

    /**
     * instance of {@link Writer} class
     */
    static Writer writer;

    /**
     * the initializer and core thread
     */
    public static void main(String[] args) throws FileNotFoundException {
        applicationStatus = Status.READY;

        // the time that thread started to work
        long startingTime = System.currentTimeMillis();
        // initializes the threadPool with a fix thread pool of 8
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        // initializes loader for farther use
        loader = new Loader("input.json");
        // initializes writer for farther use
        writer = new Writer();

        // starts reading tier on a new thread
        threadPool.submit(Main::startReadingTier);
        // starts grabbing loaded students and calculating tier on a new thread
        threadPool.submit(Main::grabStudent);
        // starts writing tier on a new thread
        threadPool.submit(Main::startWritingFiles);

        System.out.println("");

        // makes sure to shut down the app when there is no thread left running on threadPool
        while (!applicationStatus.equals(Status.DONE) || !writerStatus.equals(Status.DONE)) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        // prints total run time of the main thread
        System.out.println("total execution time: " + (System.currentTimeMillis() - startingTime));
        // shuts down the threadPool to end the program
        threadPool.shutdown();
    }


    /**
     * a runnable methode witch will execute reading till the last student
     * and then prints total reading time
     */
    private static void startReadingTier() {
        long startingTime = System.currentTimeMillis();
        while (loader.getStatus().equals(Status.READY)) {
            loader.load();
        }
        System.out.println("reading time: " + (System.currentTimeMillis() - startingTime));
    }

    /**
     * constantly checks for any student in loader result queue, and then will pass them to calculation tier
     */
    private static void grabStudent() {
        while (loader.getStatus().equals(Status.READY) || loader.students.size > 0) {
            if (loader.students.size > 0)
                threadPool.submit(() -> calculateAverage(loader.students.pop()));
            else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        applicationStatus = Status.DONE;
    }


    /**
     * calculates summation of grades and units then calculates avg of them
     * then pushes the student to writing queue to be picked and written
     *
     * @param student the student witch is in the front line
     */
    private static void calculateAverage(Student student) {
        AtomicInteger units = new AtomicInteger();
        AtomicReference<Double> grades = new AtomicReference<>((double) 0);
        student.getLessons().forEach(lesson -> {
            units.addAndGet(lesson.getUnit());
            grades.updateAndGet(v -> v + lesson.getGrade());
        });

        student.setAvg(grades.get() / units.get());
        writer.students.push(student);
    }


    /**
     * writing tier,
     * constantly checks for students queue to grab em and print them into their own file
     * then prints total writing time
     */
    private static void startWritingFiles() {
        writerStatus = Status.READY;
        long startingTime = System.currentTimeMillis();

        while (applicationStatus.equals(Status.READY) || writer.students.size > 0) {
            try {
                if (writer.students.size > 0) {
                    Student student = writer.students.pop();
                    threadPool.submit(() -> {
                        try {
                            writer.writeDown(student);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("writing time:" + (System.currentTimeMillis() - startingTime));
        writerStatus = Status.DONE;
    }
}
