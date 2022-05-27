package model;

import java.lang.reflect.Field;
import java.util.Map;

public class Lesson {
    private int unit;
    private double grade;

    public Lesson() {
    }

    public Lesson(int unit, double grade) {
        this.unit = unit;
        this.grade = grade;
    }

    public Lesson(Map<String, String> map) {
        map.forEach((key, value) -> {
            try {
                Field field = this.getClass().getDeclaredField(key);
                String type = field.getType().getTypeName();
                Object convertedValue;
                if (type.equals("int")) convertedValue = Integer.parseInt(value);
                else convertedValue = Double.parseDouble(value);
                field.set(this,convertedValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "\n\t{" +
                "\n\t\t \"unit\" : \"" + unit +
                "\",\n\t\t \"grade\" : \"" + grade +
                "\"\n\t}";
    }
}
