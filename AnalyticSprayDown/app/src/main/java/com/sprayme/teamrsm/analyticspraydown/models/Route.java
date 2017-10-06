package com.sprayme.teamrsm.analyticspraydown.models;

/**
 * Created by climbak on 10/4/17.
 */

public class Route {
    private String id;
    private String name;
    private RouteType type;
    private String gradeStr;
    private Grade grade;
    private String rating;
    private String pitches;
    private String url;

    public Route(String id, String name, String type, String difficulty, String rating, String pitches, String url){
        this.id = id;
        this.name = name;

        if (type.equalsIgnoreCase("Sport"))
            this.type = RouteType.Sport;
        else if (type.equalsIgnoreCase("Trad"))
            this.type = RouteType.Trad;
        else if (type.equalsIgnoreCase("Boulder"))
            this.type = RouteType.Boulder;
        else if (type.equalsIgnoreCase("Ice"))
            this.type = RouteType.Ice;
        else if (type.equalsIgnoreCase("Aid"))
            this.type = RouteType.Aid;
        else if (type.equalsIgnoreCase("Mixed"))
            this.type = RouteType.Mixed;
        else
            this.type = RouteType.Unknown;

        // todo map the grade string to the grade value
        this.gradeStr = difficulty;
        this.rating = rating;
        this.pitches = pitches;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RouteType getType() {
        return type;
    }

    public String getRating() {
        return rating;
    }

    public String getPitches() {
        return pitches;
    }

    public String getUrl() {
        return url;
    }

    public Grade getGrade() {
        return grade;
    }
}
