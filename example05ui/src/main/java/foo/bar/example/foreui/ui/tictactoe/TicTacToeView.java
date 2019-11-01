package foo.bar.example.foreui.ui.tictactoe;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.ui.SyncTrigger;
import co.early.fore.lifecycle.LifecycleSyncer;
import co.early.fore.lifecycle.view.SyncScrollView;
import foo.bar.example.foreui.CustomApp;
import foo.bar.example.foreui.R;
import foo.bar.example.foreui.feature.tictactoe.Board;
import foo.bar.example.foreui.feature.tictactoe.Player;

/**
 * In this example we are using one of the fore Sync... helper classes to handle
 * the observer boiler plate for us
 */
public class TicTacToeView extends SyncScrollView {

    public static String TAG = TicTacToeView.class.getSimpleName();

    private Board board;
    private Logger logger;

    @BindView(R.id.ttt_board_gridl)
    public GridLayout boardGrid;
    @BindView(R.id.ttt_nxtplayercontainer_linearl)
    public View nextPlayerContainer;
    @BindView(R.id.ttt_winscontainer_linear)
    public View winContainer;
    @BindView(R.id.ttt_nxtplayerlabel_txt)
    public TextView nextPlayer;
    @BindView(R.id.ttt_winslabel_txt)
    public TextView winningPlayer;
    @BindView(R.id.ttt_restart_btn)
    public Button restartBtn;

    //one off UI triggers
    SyncTrigger animateWinTrigger;
    SyncTrigger animateJiggleTrigger;
    SyncTrigger animateResetTrigger;

    private final float jiggleAngle = 5f;


    public TicTacToeView(Context context) {
        super(context);
    }

    public TicTacToeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TicTacToeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onFinishInflate() {// grab a reference to all the view elements, setup buttons listeners

        //we need to get the model references before
        //super.onFinishInflate() runs as that's
        //where getThingsToObserve() is called from
        setupModelReferences();

        super.onFinishInflate();

        ButterKnife.bind(this, this);

        setupClickListeners();

        setUiTriggers();

        syncView();
    }


    private void setupModelReferences() {
        board = CustomApp.get(Board.class);
        logger = CustomApp.get(Logger.class);
    }


    private void setupClickListeners() {

        restartBtn.setOnClickListener(v -> board.restart());

        ButtonProcessor.runThroughButtons(boardGrid, (button, xPos, yPos) -> button.setOnClickListener(v -> {
            board.mark(xPos, yPos);
        }));
    }


    private void setUiTriggers(){

        animateWinTrigger = new SyncTrigger(
                () -> {
                    AnimatorSet winAnimation = new AnimatorSet();
                    winAnimation.playTogether(
                            //ObjectAnimator.ofFloat(winContainer, "rotation", 0f, 360f),
                            ObjectAnimator.ofFloat(winContainer, "scaleX", 0.1f, 3f, 1f),
                            ObjectAnimator.ofFloat(winContainer, "scaleY", 0.1f, 3f, 1f));
                    winAnimation.setDuration(500);
                    winAnimation.start();
                },
                () -> board.getWinner() != Player.NOBODY) {
        };

        animateJiggleTrigger = new SyncTrigger(
                () -> {
                    AnimatorSet jiggleAnimation = new AnimatorSet();
                    jiggleAnimation.play(
                            ObjectAnimator.ofFloat(boardGrid, "rotation", -jiggleAngle, jiggleAngle,
                                    -jiggleAngle, jiggleAngle, -jiggleAngle, 0f));
                    jiggleAnimation.setDuration(300);
                    jiggleAnimation.start();
                },
                () -> board.timeSinceLastMove() > 5000) {
        };

        animateResetTrigger = new SyncTrigger(
                () -> {

                    AnimatorSet resetAnimation = new AnimatorSet();

                    ButtonProcessor.runThroughButtons(boardGrid, (button, xPos, yPos) -> {
                        Animator anim = ObjectAnimator.ofFloat(button, "rotation", 0f, 90f);
                        anim.setStartDelay(20*(xPos*1+yPos*3));
                        anim.setDuration(200);
                        anim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                button.setRotation(0f);
                            }
                            @Override
                            public void onAnimationCancel(Animator animation) {
                                button.setRotation(0f);
                            }
                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                        resetAnimation.play(anim);
                    });

                    resetAnimation.start();
                },
                () -> board.getMovesMade() == 0) {
        };

    }

    //data binding stuff below

    @Override
    public LifecycleSyncer.Observables getThingsToObserve() {
        return new LifecycleSyncer.Observables(board);
    }

    public void syncView() {

        logger.i(TAG, "syncView()");

        winningPlayer.setText(board.getWinner().name());
        nextPlayer.setText(board.getNextPlayer().name());
        winContainer.setVisibility(board.isGameInProgress() ? View.INVISIBLE : View.VISIBLE);
        nextPlayerContainer.setVisibility(board.isGameInProgress() ? View.VISIBLE : View.INVISIBLE);

        ButtonProcessor.runThroughButtons(boardGrid, (button, xPos, yPos) -> {
            Player player = board.valueAtCell(xPos, yPos);
            button.setText(player == Player.NOBODY ? "" : player.name());
        });

        animateWinTrigger.checkLazy();
        animateJiggleTrigger.checkLazy();
        animateResetTrigger.checkLazy();

    }
}
