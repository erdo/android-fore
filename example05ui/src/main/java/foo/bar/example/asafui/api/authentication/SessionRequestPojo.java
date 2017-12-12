package foo.bar.example.asafui.api.authentication;

/**
 *
 *
 * <Code>
 *
 *  A session request pojo looks like this:
 *
 *  {
 *    "un":"jfhff@fgdgsdf.com",
 *    "pw":"sdfsdfsfd"
 *  }
 *
 * </Code>
 *
 *
 *
 */
public class SessionRequestPojo {

    public String un;
    public String pw;

    public SessionRequestPojo(String un, String pw) {
        this.un = un;
        this.pw = pw;
    }
}