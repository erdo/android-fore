

# Architecture

If you like architecture diagrams, then you'll love this page. Discussions of MVC, MVP and MVVM can get quite abstract, and specific implementations of differ considerably. For the purposes of our discussion, the following definitions will do:







 even if we restrict ourselves to Android. , there are a lot of small differences in imp There are a lot of small differences in implementation between 
 
 
 
 
### What's a Controller
It helps to remember that MVC is at least 3 decades old, I think it was Microsoft who invented it [I saw a Microsoft white paper written about it once, but I can't find it anywhere now]. A controller means different things on different platforms.

Originally a controller might have been a class that accepts mouse clicks at specific pixel co-ordinate, did some collision detection to find out which UI component was clicked, then sent that information on to the appropriate UI classes for further processing. (A controller in a web app however, might be a main entry point URL that forwards on requests to different parts of the system.)

In modern app frameworks most of the controller work is implemented for you by the UI framework itself - these are the button click listeners that simply catch user input and send it on to the right place. As we need to worry less about controllers now a days, we talk more about more "modern" things like MVVM which is only about **10(!)** years old.

(Android also lets you use Activities as kind of "Controllers" by letting you specify callback methods right in the XML for buttons which will end up getting called on whatever activity is hosting that particular view. The idea is to not have to write click listeners - I'd recommend not using though because it encourages (forces) you to get the activity involved in something that it doesn't need to be involved in. If you leave everything out of the Activity then you can re-use your custom view in any activity you like, without needing to re-implement all those button call backs each time.)

