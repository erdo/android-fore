package foo.bar.example.asafretrofit.api.fruits;

/**
 * Created by eric on 16/02/2017.
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
