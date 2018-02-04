package foo.bar.example.asafui.ui.tictactoe;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.asaf.core.logging.Logger;
import co.early.asaf.core.ui.SyncableView;
import co.early.asaf.ui.SyncTrigger;
import foo.bar.example.asafui.CustomApp;
import foo.bar.example.asafui.R;
import foo.bar.example.asafui.feature.tictactoe.Board;
import foo.bar.example.asafui.feature.tictactoe.Player;


public class TicTacToeView extends ScrollView implements SyncableView {

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
        super.onFinishInflate();

        setupModelReferences();

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
                new SyncTrigger.CheckTriggerThreshold() {
                    @Override
                    public boolean checkThreshold() {
                        return board.getWinner() != Player.NOBODY;
                    }
                }) {
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
                new SyncTrigger.CheckTriggerThreshold() {
                    @Override
                    public boolean checkThreshold() {
                        return board.timeSinceLastMove() > 5000;
                    }
                }) {
        };

        animateResetTrigger = new SyncTrigger(
                () -> {

                    AnimatorSet resetAnimation = new AnimatorSet();

                    ButtonProcessor.runThroughButtons(boardGrid, new ButtonProcessor.ButtonOperation() {
                        @Override
                        public void operate(Button button, int xPos, int yPos) {
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
                        }
                    });

                    resetAnimation.start();
                },
                new SyncTrigger.CheckTriggerThreshold() {
                    @Override
                    public boolean checkThreshold() {
                        return board.getMovesMade() == 0;
                    }
                }) {
        };

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

        animateWinTrigger.check(true);
        animateJiggleTrigger.check(true);
        animateResetTrigger.check(true);

    }



}
