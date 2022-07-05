package com.omni.taipeiarsdk.model.mission;

import java.io.Serializable;

public class Question implements Serializable {

    private String style;
    private String type;
    private String title;
    private String image;
    private String video;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
    private String tip;

    public String getStyle() {
        return style;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getVideo() {
        return video;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getAnswer() {
        return answer;
    }

    public String getTip() {
        return tip;
    }
}
