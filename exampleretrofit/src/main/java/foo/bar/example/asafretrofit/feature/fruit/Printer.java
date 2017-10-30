package foo.bar.example.asafretrofit.feature.fruit;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.WorkMode;
import co.early.asaf.core.observer.ObservableImp;
import co.early.asaf.core.threading.AsafTaskBuilder;

/**
 *
 */
public class Printer extends ObservableImp {

    private final USBStuff usbStuff;
    private final Formatter formatter;
    private final WorkMode workMode;

    private boolean isBusy = false;
    private boolean hasPaper = true;
    private int numPagesLeftToPrint;


    public Printer(USBStuff usbStuff, Formatter formatter, WorkMode workMode) {
        super(workMode);
        this.usbStuff = Affirm.notNull(usbStuff);
        this.formatter = Affirm.notNull(formatter);
        this.workMode = Affirm.notNull(workMode);
    }

    public void printThis(Page pageToPrint, final CompleteCallBack completeCallBack) {

        if (isBusy){
            completeCallBack.fail();
            return;
        }

        isBusy = true;
        numPagesLeftToPrint++;
        notifyObservers();


        new AsafTaskBuilder<Void, Void>(workMode)
                .doInBackground(input -> {

                    //...do the printing

                    return null;
                })
                .onPostExecute(result -> {

                    //back on the UI thread

                    isBusy = false;
                    numPagesLeftToPrint--;
                    notifyObservers();

                    completeCallBack.complete();
                })
                .execute((Void) null);

    }

    public boolean isBusy() {
        return isBusy;
    }

    public boolean isHasPaper() {
        return hasPaper;
    }

    public int getNumPagesLeftToPrint() {
        return numPagesLeftToPrint;
    }

}
