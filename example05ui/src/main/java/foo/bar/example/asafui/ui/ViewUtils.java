package foo.bar.example.asafui.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;


public class ViewUtils {

    public static AppCompatActivity getActivityFromContext(Context context) {

        if (context instanceof AppCompatActivity) {//this maybe a context from a view hosted in a regular fragment for example
            return (AppCompatActivity) context;
        } else if (context instanceof ContextWrapper) {//this maybe a context from a view hosted in a dialogfragment for example
            return (AppCompatActivity) ((ContextWrapper) context).getBaseContext();
        } else {//some other kind of context
            return null;
        }
    }

}