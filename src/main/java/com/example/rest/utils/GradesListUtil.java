package com.example.rest.utils;

import com.example.rest.models.Grade;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kamil Walkowiak
 */
public abstract class GradesListUtil {
    public static List<Grade> getGradesByNote(List<Grade> grades, double note, int direction) {
        List<Grade> result = grades;
        switch(direction) {
            case -1:
                result = result.stream().filter(grade -> grade.getNote() < note).collect(Collectors.toList());
                break;
            case 0:
                result = result.stream().filter(grade -> grade.getNote() == note).collect(Collectors.toList());
                break;
            case 1:
                result = result.stream().filter(grade -> grade.getNote() > note).collect(Collectors.toList());
                break;
            default:
                break;
        }

        return result;
    }

    public static List<Grade> getGradesByDate(List<Grade> grades, Date date) {
        return grades.stream().filter(grade -> grade.getDate().equals(date)).collect(Collectors.toList());
    }
}
