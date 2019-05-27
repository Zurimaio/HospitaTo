package com.ma.se.hospitato;


public class Feedback {

    String h_name;
    String id;
    String eval;
    Integer rat;

    public Feedback(){}

    public Feedback(String id, String h_name, String eval, Integer rat) {
        this.h_name = h_name;
        this.id = id;
        this.eval = eval;
        this.rat=rat;
    }

    public String getH_name() { return h_name; }

    public String getId() { return id; }

    public String getEval() { return eval; }

    public Integer getRat() { return rat; }

    public void setH_name(String h_name) { this.h_name = h_name; }

    public void setId(String id) { this.id = id; }

    public void setEval(String eval) { this.eval = eval; }

    public void setRat(Integer rat) { this.rat = rat; }
}
