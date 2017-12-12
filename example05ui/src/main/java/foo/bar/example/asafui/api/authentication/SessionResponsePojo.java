package foo.bar.example.asafui.api.authentication;

/**
 *
 *
 * <Code>
 *
 *  The server returns a session pojo that looks like this:
 *
 *  {
 *    "sessionToken":"kjskdjfhksjd"
 *  }
 *
 * </Code>
 *
 *
 *
 */
public class SessionResponsePojo {

    public String sessionToken;

    public SessionResponsePojo(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}