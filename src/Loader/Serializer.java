package Loader;

import model.Lesson;
import model.Student;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * this class converts input witch is a json actually, to desired map of it
 *
 * then will parse the very object of it by using their constructor
 */
public class Serializer {

    /**
     * will separate lessons form student to be processed independently
     * then joins them
     *
     * @param input input json of the student
     * @return student representing the input json string
     */
    public static Student serialize(String input) {
        String[] array = input.split("(\n\t )*\"lessons\"(\n\t )*:");

        String[] lessonsTemplate = array[1].replace('[', ' ').replace(',', ' ').replace('{', ' ').split("}");

        List<Lesson> lessons = Arrays.stream(lessonsTemplate).limit(lessonsTemplate.length - 1).map(lesson -> lessonConverter(lesson)).collect(Collectors.toList());

        return studentConverter(array[0], lessons);
    }


    /**
     * will convert a single lesson to its object
     *
     * @param input single lesson json string
     * @return the lesson object of input
     */
    private static Lesson lessonConverter(String input) {
        return new Lesson(convertToMap(input));
    }

    /**
     * will convert the student and attach lessons to it
     *
     * @param input the json string of student
     * @param lessons the list of student`s lessons witch where parsed earlier
     * @return the student object of input json string and lessons
     */
    private static Student studentConverter(String input, List<Lesson> lessons) {
        return new Student(convertToMap(input), lessons);
    }


    /**
     * converts json to map
     *
     * @param input the json string
     * @return the input json as a map
     */
    private static Map<String, String> convertToMap(String input) {
        Pattern pattern = Pattern.compile("\"[0-9a-z A-z]+\"[\t \n]*:[\t \n]*\"[0-9a-z A-z.\\-_&|/%]+\"");
        Matcher matcher = pattern.matcher(input);

        Map<String, String> map = new HashMap<>();

        while (matcher.find()) {
            String[] strings = matcher.group().split("\" *: *\"");
            strings[0] = strings[0].split("\"")[1];
            strings[1] = strings[1].split("\"")[0];

            map.put(strings[0], strings[1]);
        }
        return map;
    }
}
