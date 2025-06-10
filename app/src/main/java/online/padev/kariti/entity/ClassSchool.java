package online.padev.kariti.entity;

import java.io.Serializable;

public class ClassSchool implements Serializable {
    private Integer class_id;
    private String name;
    private String school_id;

    public ClassSchool(Integer class_id, String name) {
        this.class_id = class_id;
        this.name = name;
    }

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id) {
        this.class_id = class_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }
}
