package online.padev.kariti.entity;

public class School {
    Integer school_id;
    String name;

    public School(Integer school_id, String name) {
        this.school_id = school_id;
        this.name = name;
    }

    public Integer getSchool_id() {
        return school_id;
    }

    public String getName() {
        return name;
    }
}
