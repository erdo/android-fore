package foo.bar.example.foredb.db.todoitems;

import java.util.Random;

import foo.bar.example.foredb.App;
import foo.bar.example.foredb.R;

/**
 *
 */
public class RandomTodoCreator {

    private static final Random random = new Random();
    private static final String DASH = "-";
    private static final String THE = App.getInst().getString(R.string.todo_the);
    private static final String[] words = {"ask", "now", "use", "bus", "cool", "eye", "bean", "dial", "echo",
            "fan", "evil", "goat", "hat", "iron", "junk", "key", "lion", "medal", "oak",
            "pony", "raw", "sea", "tick"};

    public static TodoItemEntity create(long creationTime){
        return new TodoItemEntity(creationTime, createLabel());
    }

    public static String createLabel(){

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(words[random.nextInt(words.length)]);
        stringBuilder.append(DASH);
        stringBuilder.append(words[random.nextInt(words.length)]);
        stringBuilder.append(THE);
        stringBuilder.append(words[random.nextInt(words.length)]);
        stringBuilder.append(DASH);
        stringBuilder.append(words[random.nextInt(words.length)]);

        return stringBuilder.toString();
    }
}
