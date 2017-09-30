

## Data Binding
***AKA - how can I stop getting bug reports about displaying the wrong state in some weird hard to reproduce situation - while also writing less code?***

**One Way Data Binding**: Any changes of state in your underlying model get automatically represented in your view.

So if your shopping basket model is empty: the checkout button on your view needs to be invisible or disabled. And as soon as your shopping basket model has something in it: your checkout button needs to reflect that by being enabled (and obvs, it still needs to work when you rotate the screen)

**Two Way Data Binding**: In addition to the above, the binding goes the other way too. So lets say you are editing your online profile in an editable text view, your view edits will automatically be reflected in your underlying profile model.

Automatic two way data binding turns out to be a bit of a pain in the derriere, and it doesn't seem to be as useful as you might expect. It's also very easy to do for specific cases (just not in the general case).

Anyway I'm going to show you how to do rock solid one way data binding with this library, if it turns out you need some two way data binding you can just do something like this:

	saveChangesButton.setOnClickListener(new View.OnClickListener() {  
            @Override
            public void onClick(View v) {
                myProfile.setText(profileEditText.getText().toString());
            }
        });


### SyncView()

There are a load of different ways of implementing one way data binding. In line with the name of this framework, we are going to use the most simple (but extremely reliable) implementation you can have.

It really all boils down to a single syncView() method, but there are some important implementation details to discuss. The basic philosophy is: if a model being observed changes **in anyway**, then the **entire** view is refreshed. That simplicity is surprisingly powerful so we're going to go into further detail about why, after I've quoted myself to make it seem more profound...

> "If a model being observed changes **in anyway**, then the **entire** view is refreshed."

### simple example

Here's an example of how addhoc data binding can get rapidly complicated, especially when you have a lifecycle to deal with.

Let's say you're developing a view for a very basic shopping basket. We need to be able to **add** and **remove** items, and to apply (or not apply) a **10% discount**. The basket model has already been written and has already been nicely unit tested. All we need now is to hook up our basic view to this basket model.


**Step 1)** First we hook up the **add item button** so in the onclick listener we: call basket.addItem(), and then we just call an updateTotalPriceView() method which updates the amount shown in the total field (no point in syncing the whole basket view here).

        addItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
                updateTotalPriceView();
            }
        });

**Step 2)** Then when we hook up the **remove item button** we do something similar: call basket.removeItem(), and the call the updateTotalPriceView() method (again, no point syncing the entire view here)

        removeItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
                updateTotalPriceView();
            }
        });

**Step 3)** The designers decide they want to display the **total number of items in the basket** as well as the price, so now we add a updateTotalNumberOfItemsView() method, which does what you think it does. Of course, we need to hook that up with the Add and Remove buttons so that they now both call updateTotalPriceView(); and then updateTotalNumberOfItemsView();

        addItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
                updateTotalPriceView();
                updateTotalNumberOfItemsView();
            }
        });

        removeItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.removeItem();
                updateTotalPriceView();
                updateTotalNumberOfItemsView();
            }
        });

**Step 4)** Now we get to the **apply discount** checkbox, if the box is checked, the discount is applied, if not there is no discount applied. Remember the model calculations have already been written and tested so what we need in the click listener is: basket.setDiscount(applyDiscount); then updateDiscountView() which just shows the discount that has been applied. We also need to call updateTotalPriceView() as that will have changed, but not updateTotalNumberOfItemsView() because of course discounts have no effect there.

        apply10PercOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean applyDiscount) {
                basket.setDiscount(applyDiscount);
                updateDiscountView();
                updateTotalPriceView();
            }
        });

Here is the psuedo code we end up with for this (very over simplified) case

    Button addItemButton;
    Button removeItemButton;
    CheckBox apply10PercOff;

    TextView totalItems;
    TextView totalDiscount;
    TextView totalPrice;

    private void setupButtonListeners() {

        addItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
                updateTotalPriceView();
                updateTotalNumberOfItemsView();
            }
        });

        removeItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.removeItem();
                updateTotalPriceView();
                updateTotalNumberOfItemsView();
            }
        });

        apply10PercOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean applyDiscount) {
                basket.setDiscount(applyDiscount);
                updateDiscountView();
                updateTotalPriceView();
            }
        });
    }


    private void updateTotalNumberOfItemsView(){
        totalItems.setText(basket.getTotalItems);
    }

    private void updateDiscountView(){
        totalDiscount.setText(basket.getTotalDiscount);
    }

    private void updateTotalPriceView(){
        totalPrice.setText(basket.getTotalPrice);
    }

And don't forget if we need to rotate this view, all the fields will be out of sync with our model. We could use the sticking plasters that android gives to deal with this problem, but because we have been smart and seperated our model from our view anyway, we don't care about such lifecycle trivialities and we can just re sync everything up like so:

	private void updatePostRotation(){
		updateTotalNumberOfItemsView();
		updateDiscountView();
		updateTotalPriceView();
	}

That's already looking like quite a bit of code, but what if we want to add some more UI details like disabling a checkout button if there is nothing in the basket, or making the total colour red if it is under the minumum card transaction value of $1.

It very quickly starts to become untidy and complicated (which is not what you want in a view class which is not easy to test).

### But that's not the worst problem....
The worst problem with this code is that there is a **bug** in it. Did you spot it?

It's a class of bug related to UI consistency that crops up *all the time* in any code that doesn't have proper data binding, and given that data binding is barely even a thing among most android developers, it's a class of bugs that crops up *all the time* in android apps, even ones that dissable rotation.

I'm guessing you have gone back and spotted the bug by now, but in case you haven't you can recreate it in your brain by selecting the discount checkbox first and then adding or removing an item. It's that simple. The add and remove item click listeners will correctly talk to the model, so the model state is correct. However the developer forgot to call updateDiscountView() so this value will be incorrect in the view until the discount checkbox is toggled again.

Very simple views can easily become a complete mess and it's easy to create subtle UI consistency bugs like this. Luckily there is a simple solution and all you have to do is apply it everywhere you have a view.

> if a model being observed changes **in anyway**, then the **entire** view is refreshed.

Using a syncView() for the example above we end up with something like this:


    Button addItemButton;
    Button removeItemButton;
    CheckBox apply10PercOff;

    TextView totalItems;
    TextView totalDiscount;
    TextView totalPrice;

    private void setupButtonListeners() {

        addItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.addItem();
            }
        });

        removeItemButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                basket.removeItem();
            }
        });

        apply10PercOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean applyDiscount) {
                basket.setDiscount(applyDiscount);
            }
        });
    }

    private void syncView(){
        totalItems.setText(basket.getTotalItems);
        totalDiscount.setText(basket.getTotalDiscount);
        totalPrice.setText(basket.getTotalPrice);
    }

The syncView() gets called by the observer which is triggered whenever **any** state of the model changes. It's also called on rotation. If you want to add any more states it's easy, and clean, and totally consistent if they are set inside the syncView() method:

    private void syncView(){
        checkoutButton.setEnabled(basket.isAboveMinimum());
        totalPrice.setColour(basket.isAboveMinimum() ? black : red);
        removeButton.setEnabled(basket.getTotalItems>0);
        totalItems.setText(basket.getTotalItems);
        totalDiscount.setText(basket.getTotalDiscount);
        totalPrice.setText(basket.getTotalPrice);
	}

### Writing an effective syncView() method


The important thing about the syncView() method is that it must set an **affirmative state** for every view element property that you are interested in. What that means is that where there is an **if** there must always be an **else** for each property.

It's not good enough to just set a button as **disabled** if a total is 0 or less. You must also set that button as **enabled** if the total is greater than 0. If you don't set an affirmative step for both the positive and negative scenarios, then you run the risk of a syncView() call not setting any state, which means that the result will be undeterministic (it will be whatever state it had previously).

So don't do this:

	if (basket.isBelowMinimum()){
		checkoutButton.setEnabled(false);
		totalPrice.setColour(red);
	}
	
At the very least you must do this:
	
	if (basket.isBelowMinimum()){
		checkoutButton.setEnabled(false);
		totalPrice.setColour(red);
	} else {
		checkoutButton.setEnabled(true);
		totalPrice.setColour(black);
	}

But you'll find that by focusing on the property first rather than the condition, you can get some extremely clean code using the elvis operator like so:

	checkoutButton.setEnabled(!basket.isBelowMinimum());
	totalPrice.setColour(basket.isBelowMinimum() ? red : black);

