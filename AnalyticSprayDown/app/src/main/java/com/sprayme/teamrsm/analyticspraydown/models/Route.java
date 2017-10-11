package com.sprayme.teamrsm.analyticspraydown.models;

/**
 * Created by climbak on 10/4/17.
 */

public class Route {
    private Long id;
    private String name;
    private RouteType type;
    private String gradeStr;
    private Grade grade;
    private Float stars;
    private Integer pitches;
    private String url;

    public Route(Long id, String name, String type, String difficulty, Float stars, Integer pitches, String url){
        this.id = id;
        this.name = name;

        GradeType gradetype = GradeType.RouteYosemite;
        if (type.equalsIgnoreCase("Sport")) {
            this.type = RouteType.Sport;
            gradetype = GradeType.RouteYosemite;
        }
        else if (type.equalsIgnoreCase("Trad")) {
            this.type = RouteType.Trad;
            gradetype = GradeType.RouteYosemite;
        }
        else if (type.equalsIgnoreCase("Boulder")) {
            this.type = RouteType.Boulder;
            gradetype = GradeType.BoulderHueco;
        }
        else if (type.equalsIgnoreCase("Ice")) {
            this.type = RouteType.Ice;
            gradetype = GradeType.Ice;
        }
        else if (type.equalsIgnoreCase("Aid")) {
            this.type = RouteType.Aid;
            gradetype = GradeType.Aid;
        }
        else if (type.equalsIgnoreCase("Mixed")) {
            this.type = RouteType.Mixed;
            gradetype = GradeType.Other;
        }
        else {
            this.type = RouteType.Unknown;
            gradetype = GradeType.Other;
        }

        // todo map the grade string to the grade value
        this.gradeStr = difficulty;
        this.grade = new Grade(gradeStr, gradetype);
        this.stars = stars;
        this.pitches = pitches;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RouteType getType() {
        return type;
    }

    public Float getStars() {
        return stars;
    }

    public Integer getPitches() {
        return pitches;
    }

    public String getUrl() {
        return url;
    }

    public Grade getGrade() {
        return grade;
    }
}
