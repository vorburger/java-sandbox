package ch.vorburger.equalshelper;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class Thing {

    String name;
    Integer age;

    @Override
    public boolean equals(Object obj) {
        return MoreObjects2.equalsHelper(this, obj,
                (a, b) -> Objects.equals(a.name, b.name) && Objects.equals(a.age, b.age));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", name).add("age", age).toString();
    }

    Thing(String name, Integer age) {
        super();
        this.name = name;
        this.age = age;
    }

}
