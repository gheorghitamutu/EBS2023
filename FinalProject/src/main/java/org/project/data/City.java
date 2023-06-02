package org.project.data;

public class City {
    public enum Name {
        SAN_FRANCISCO,
        NEW_YORK,
        LONDON,
        PARIS,
        TOKYO
    }

    final private Name name;

    public City(Name name) {
        this.name = name;
    }

    public Name getName() {
        return name;
    }

    public String ToString() {
        return name.toString().replace("_", " ");
    }
}
