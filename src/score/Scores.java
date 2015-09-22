package score;

/**
 * Created by janaka on 9/21/15.
 */
public class Scores {

    public double adjNounGScore;
    public double adverbGroupScore;
    public double adverbAdjectiveGroupScore;
    public double verbGroupScore;

    public double emoticonScore;

    public int emoticonCount;
    public int repetitions;
    public int exclamations;
    public double fractionInCaps;

    public int adverbGroupCount;
    public int adverbAdjectiveGroupCount;
    public int verbGroupCount;

    public static String getHeader() {
        return "adverbGroupScore,adverbAdjectiveGroupScore,verbGroupScore,emoticonScore,emoticonCount,repetitions,exclamations,fractionInCaps,adverbGroupCount,adverbAdjectiveGroupCount,verbGroupCount";
    }

    @Override
    public String toString() {
        return adverbGroupScore +
                "," + adverbAdjectiveGroupScore +
                "," + verbGroupScore +
                "," + emoticonScore +
                "," + emoticonCount +
                "," + repetitions +
                "," + exclamations +
                "," + fractionInCaps +
                "," + adverbGroupCount +
                "," + adverbAdjectiveGroupCount +
                "," + verbGroupCount;
    }
}
