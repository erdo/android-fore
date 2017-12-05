package foo.bar.example.asafretrofit.api.fruits;

/**
 *
 *
 * <Code>
 *
 *  The server returns us a list of fruit that look like this:
 *
 *  {
 *    "name":"papaya",
 *    "isCitrus":false,
 *    "tastyPercentScore":98
 *  }
 *
 * </Code>
 *
 *
 *
 */
public class FruitPojo {

    public String name;
    public boolean isCitrus;
    public int tastyPercentScore;

    public FruitPojo(String name, boolean isCitrus, int tastyPercentScore) {
        this.name = name;
        this.isCitrus = isCitrus;
        this.tastyPercentScore = tastyPercentScore;
    }
}
