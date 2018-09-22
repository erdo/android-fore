package foo.bar.example.foreui.ui.tictactoe;

import android.widget.Button;
import android.widget.GridLayout;

/**
 * some specific stuff related to this tic tac toe view that I wouldn't expect to be relevant
 * to any other situation
 */
public class ButtonProcessor {

    public static void runThroughButtons(GridLayout boardLayout, ButtonOperation buttonOperation){
        int cellNumber = 0;
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                buttonOperation.operate((Button) boardLayout.getChildAt(cellNumber++), x, y);
            }
        }
    }

    public interface ButtonOperation{
        void operate(Button button, int xPos, int yPos);
    }
}
