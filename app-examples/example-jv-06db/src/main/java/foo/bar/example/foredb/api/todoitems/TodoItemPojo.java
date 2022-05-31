package foo.bar.example.foredb.api.todoitems;

/**
 *
 *
 * <Code>
 *
 *  The server returns us a list of todoitems that look like this:
 *
 *  {
 *    "label":"goat-pony",
 *    "done":false
 *  }
 *
 * </Code>
 *
 *
 *
 */
public class TodoItemPojo {

    public String label;
    public boolean done;

    public TodoItemPojo(String label, boolean done) {
        this.label = label;
        this.done = done;
    }
}
