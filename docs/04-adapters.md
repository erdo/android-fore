

## Adapters

Ahh adapters, I miss the good old days when all you had to do was call notifyDataSetChanged(). And the best place to call it is from inside the syncView() method:

    public void syncView() {

		// set enabled states and visibilities etc
		...
		
        adapter.notifyDataSetChanged();
    }

In this way you let your adapters piggy back on the observer which you have already setup for your view (which is what calls syncView()).

(You could also add your adapter as an observer on the model directly, but doing it like that usually causes problems because you will also need to find a way to remove it correctly.)

If you're not overly concerned with list animations I would continue to call notifyDataSetChanged anyway (yes it is marked as deprectated, but the alternative methods that android is offering are so difficult to implement correctly that I strongly suspect they will never be able to remove the original adapter.notifyDataSetChanged() method from the API)

### list animations

So onwards and upwards if you want list animations on android, they make you work quite hard for it.

In order to get animations, you need to tell the adapter what kind of change actually happened, what rows were added or removed etc. This is one case in particular that it was so tempting to just add a parameter to the ASAF observable. It still wasn't worth it though.

//TODO


## Cursor Loaders

Cursor loaders are interesing, underneath the hood they use a standard observer pattern to propogate their changes to a view just as this library does. It works very nicely, the only criticism I have is that (as with so much Android code) it forces you down a path of adding model code to the Activity and Fragment classes.

//TODO


