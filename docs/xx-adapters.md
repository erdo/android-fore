

# Cursor Loaders

Cursor loaders are interesing, underneath the hood they use a standard observer pattern to propogate their changes to a view just as this library does. It works very nicely, the only criticism I have is that (as with so much Android code) it forces you down a path of adding model code to the Activity and Fragment classes.

//TODO


Though we like to pretend otherwise sometimes, even the most complicated android app is often just some logic and data, a bit of network access, and a UI put on top of it. Once you remove the crazy (writing all the code in the view layer) it turns out that Android development is not rocket science after all.

