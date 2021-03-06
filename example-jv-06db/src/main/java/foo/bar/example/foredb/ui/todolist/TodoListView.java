package foo.bar.example.foredb.ui.todolist;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.early.fore.core.logging.Logger;
import co.early.fore.core.observer.ObservableGroup;
import co.early.fore.core.observer.ObservableGroupImp;
import co.early.fore.core.observer.Observer;
import co.early.fore.core.ui.SyncableView;
import foo.bar.example.foredb.OG;
import foo.bar.example.foredb.R;
import foo.bar.example.foredb.feature.remote.RemoteWorker;
import foo.bar.example.foredb.feature.todoitems.TodoListModel;
import foo.bar.example.foredb.ui.common.uiutils.SyncerTextWatcher;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


/**
 * In this example we are using one of the fore Sync... helper classes to handle
 * the observer boiler plate for us
 */
public class TodoListView extends RelativeLayout implements SyncableView {

    //models that we need
    private TodoListModel todoListModel;
    private RemoteWorker remoteWorker;
    private ObservableGroup observableGroup;

    //single observer reference
    private Observer observer = this::syncView;

    private Logger logger;
    private InputMethodManager keyboard;


    //UI elements that we care about
    @BindView(R.id.todo_add50_button)
    protected Button add50;

    @BindView(R.id.todo_do10pc_button)
    protected Button do10pc;

    @BindView(R.id.todo_del10pc_button)
    protected Button del10pc;

    @BindView(R.id.todo_showdone_switch)
    protected Switch showDoneItems;

    @BindView(R.id.todo_clear_button)
    protected Button clearList;

    @BindView(R.id.todo_add_button)
    protected Button addButton;

    @BindView(R.id.todo_newcontainer_linear)
    protected LinearLayout newItemContainer;

    @BindView(R.id.todo_description_edit)
    protected EditText newDescription;

    @BindView(R.id.todo_create_button)
    protected Button createButton;

    @BindView(R.id.todo_hide_button)
    protected Button hideButton;

    @BindView(R.id.todo_list_recycleview)
    protected RecyclerView todoListRecyclerView;

    @BindView(R.id.todo_number_text)
    protected TextView numberTodos;

    @BindView(R.id.todo_networking_prog)
    protected ProgressBar networking;



    private TodoListAdapter todoListAdapter;
    private AnimatorSet animationSet;


    public TodoListView(Context context) {
        super(context);
    }

    public TodoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TodoListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {

        //we need to get the model references before
        //super.onFinishInflate() runs as that's
        //where getThingsToObserve() is called from
        getModelReferences();

        super.onFinishInflate();

        ButterKnife.bind(this, this);

        setupClickListeners();

        setupAdapters();

        setupAnimations();
    }

    private void getModelReferences(){
        todoListModel = OG.get(TodoListModel.class);
        remoteWorker = OG.get(RemoteWorker.class);

        observableGroup = new ObservableGroupImp(todoListModel, remoteWorker);

        logger = OG.get(Logger.class);
        keyboard = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void setupClickListeners() {

        addButton.setOnClickListener(v -> showAddDropDown());
        hideButton.setOnClickListener(v -> hideAddDropDown());
        createButton.setOnClickListener(v -> addItem());
        clearList.setOnClickListener(v -> todoListModel.clear());
        add50.setOnClickListener(v -> todoListModel.addRandom(50));
        do10pc.setOnClickListener(v -> todoListModel.doRandom(10));
        del10pc.setOnClickListener(v -> todoListModel.deleteRandom(10));

        showDoneItems.setOnCheckedChangeListener(
                (buttonView, isChecked) -> todoListModel.setShowDone(isChecked));

        newDescription.addTextChangedListener(new SyncerTextWatcher(this));
        newDescription.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == IME_ACTION_DONE
                    || event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                addItem();
                return true;
            }else{
                return false;
            }
        });
    }


    private void setupAdapters(){

        todoListAdapter = new TodoListAdapter(todoListModel);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);

        todoListRecyclerView.setLayoutManager(linearLayoutManager1);
        todoListRecyclerView.setAdapter(todoListAdapter);
    }

    private void setupAnimations(){

        animationSet = new AnimatorSet();
        animationSet.setDuration(300);
    }

    private void addItem(){
        if (createButton.isEnabled()) {
            todoListModel.add(newDescription.getText().toString());
            newDescription.setText("");

            //once the item is added to the db, we scroll the list a little
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    todoListRecyclerView.scrollToPosition(0);
                }
            }, 250);
        }
    }

    private void showAddDropDown(){

        // NB you might notice that the next line conflicts with the advice followed elsewhere in
        // fore and explicitly mentioned here:
        // https://erdo.github.io/android-fore/05-extras.html#adhoc-state-setting
        //
        // By setting the enabled state of the addButton here in an *adhoc* way (rather than using
        // the syncview() method together with a boolean state which is stored in a model and
        // accessed using a getter()), we are indeed sacrificing:
        //
        // 1) the ability to easily unit test this behaviour (e.g. how do we test that when the
        // "to do" editing drop down is displayed - we have correctly set the enabled state of the
        // addButton? well we would have to use espresso or similar, rather than junit.
        // 2) we will loose this enabled/disabled state completely when the view is rotated
        // 3) we risk introducing UI consistency bugs if the requirements for the addButton enabled
        // state get more complicated.
        // (for an in depth discussion of UI consistency bugs and why they happen, see here:
        // https://erdo.github.io/android-fore/03-databinding.html#syncview)
        //
        // In this case I'm leaning towards thinking it's fine - partly because it's a state that's
        // very closely related to a temporary condition of the view (having the drop down visible),
        // and not related to the underlying TodoListModel. It also doesn't matter if we loose this
        // enabled/disabled state on rotation - we will also loose the temporary view condition on
        // which it is based (the visibility of the edit drop down) - and I think that also doesn't
        // matter from a UX perspective. So in full knowledge, we'll take a small risk here and
        // save ourselves some code (recognising that the more code you add, the more
        // cluttered and risky a codebase becomes anyway).
        //
        addButton.setEnabled(false);
        newDescription.requestFocus();
        keyboard.showSoftInput(newDescription, 0);
        animationSet.playTogether(
                ObjectAnimator.ofFloat(newItemContainer, "translationY", -200f, 0f));
        animationSet.start();
    }


    private void hideAddDropDown(){
        addButton.setEnabled(true);
        keyboard.hideSoftInputFromWindow(newDescription.getWindowToken(), 0);
        animationSet.playTogether(ObjectAnimator.ofFloat(newItemContainer, "translationY", 0f, -200f));
        animationSet.start();
    }


    //data binding stuff below


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        observableGroup.addObserver(observer);
        syncView();  // <- don't forget this
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        observableGroup.removeObserver(observer);
    }

    public void syncView(){
        createButton.setEnabled(todoListModel.isValidItemLabel(newDescription.getText().toString()));
        showDoneItems.setChecked(todoListModel.isShowDone());
        clearList.setEnabled(todoListModel.size()>0);
        numberTodos.setText("remaining todos: " + todoListModel.getTotalNumberOfRemainingTodos() + "/" + todoListModel.getTotalNumberOfTodos());
        networking.setVisibility(remoteWorker.isBusy() ? VISIBLE : INVISIBLE);
        todoListAdapter.notifyDataSetChangedAuto();
    }

}
