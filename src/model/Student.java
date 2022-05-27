package model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Student {
    private UUID id;
    private String name;
    private List<Lesson> lessons;
    private double avg;

    public Student() {
    }

    public Student(Map<String, String> map, List<Lesson> lessons) {
        map.forEach((key, value) -> {
            try {
                Field field = this.getClass().getDeclaredField(key);
                Class<?> type = field.getType();
                Object convertedValue;
                if (type.equals(UUID.class)) convertedValue = UUID.fromString(value);
                else convertedValue = value;
                field.set(this, convertedValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.lessons = lessons;
    }

    public Student(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.lessons = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(Lesson lesson) {
        this.lessons.add(lesson);
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    @Override
    public String toString() {
        return "{\n\t" +
                " \"id\" : \"" + id +
                "\",\n\t \"name\" : \"" + name +
                "\",\n\t \"lessons\" : " + lessons.toString() +
                ((avg == 0.0)? "": ",\n\t \"avg\" : " + avg )
                +
                "\n}";
    }
}
