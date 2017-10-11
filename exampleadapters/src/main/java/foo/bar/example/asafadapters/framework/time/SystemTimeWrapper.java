package foo.bar.example.asafadapters.framework.time;

/**
 * This enables us to set a mock the system time for testing.
 */
public class SystemTimeWrapper {

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
