package foo.bar.example.asafui.feature.tictactoe;


/**
 * Created by ericmaxwell on 1/19/17.
 */

public class Cell {

    private Player value = Player.NOBODY;

    public Player getValue() {
        return value;
    }

    public void setValue(Player value) {
        this.value = value;
    }
}