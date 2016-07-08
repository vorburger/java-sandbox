package ch.vorburger.equalshelper;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

public class EqualsHelperTest {

    @Test
    public void test() {
        new EqualsTester()
                .addEqualityGroup(new Thing("hello", 123), new Thing("hello", 123))
                .addEqualityGroup(new Thing("hoi", 123), new Thing("hoi", 123))
                .addEqualityGroup(new Thing("hoi", null))
                .addEqualityGroup(new Thing(null, null))
                .testEquals();
    }

}
