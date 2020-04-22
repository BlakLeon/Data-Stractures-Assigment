package Tree;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Person {
    private String name;
    private String gender;
    private Person partner;
    private final Set<Person> parents;
    private final Set<Person> siblings;
    private final Set<Person> children;

    public Person() {
        this("", "");
    }

    public Person(String name) {
        this(name, "");
    }

    public Person(String name, String gender) {
        this.name = name;
        this.gender = gender;
        this.partner = null;
        this.parents = new HashSet<>();
        this.siblings = new HashSet<>();
        this.children = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Person getPartner() {
        return partner;
    }

    public void setPartner(Person partner) {
        this.partner = partner;
    }

    public Set<Person> getParents() {
        return parents;
    }

    public Set<Person> getSiblings() {
        return siblings;
    }

    public Set<Person> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}