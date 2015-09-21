package score;

/**
 * Created by bhash90 on 9/20/15.
 */
public class ScoreCalculator {

    private String tweet;
    private Word[] words;

    public ScoreCalculator(String tweet, Word[] words) {
        this.tweet = tweet;
        this.words = words;
    }

    public Scores getScores() {
        Scores scores = new Scores();
        scores.adgScore = getAdverbGroupScore();
        scores.vgScore = getVerbGroupScore();
        scores.emoticonScore = getEmoticonsScore();

        scores.emoticonsCount = getEmoticonsCount();
        scores.repetitions = getRepitions();
        scores.exclamations = getExclamations();
        scores.fractionInCaps = getFractionInCaps();
        scores.opinionGroupsCount = getOpinionGroupCount();
        scores.emoticansAndGroupsCount = scores.opinionGroupsCount + scores.emoticonsCount;

        return scores;
    }

    public double calculate() {
        Scores scores = getScores();
        double logReps = scores.repetitions > 0 ? Math.log(scores.repetitions) : 0;
        double logExclams = scores.exclamations > 0 ? Math.log(scores.exclamations) : 0;
        return (1 + (scores.fractionInCaps + logReps + logExclams) / 3) *
                (scores.adgScore + scores.vgScore + scores.emoticonScore);
    }

    public double getFractionInCaps() {
        String[] splits = tweet.split("\\s");
        int length = splits.length;
        int upperCount = 0;
        for (int i = 0; i < splits.length; i++) {
            String word = splits[i];
            boolean noUppercase = word.equals(word.toLowerCase());
            boolean noLowerCase = word.equals(word.toUpperCase());

            if (word.length() > 1 && noLowerCase && !noUppercase) { // if no lower case letters and contains uppercase letters
                upperCount++;
            }
        }
        return (double) (upperCount / length);
    }

    public int getRepitions() {
        String tempTweet = tweet;
        int tweetSize = tempTweet.length();
        int repCount = 0;
        if (tweetSize < 2) {
            return 0;
        }
        for (int i = 1; i < tempTweet.length() - 1; i++) {
            char char_1 = tempTweet.charAt(i - 1);
            char char_2 = tempTweet.charAt(i);
            char char_3 = tempTweet.charAt(i + 1);
            if (char_1 == char_2 && char_2 == char_3) {
                repCount++;
            }
        }

        return repCount;
    }

    public int getExclamations() {
        String tempTweet = tweet.trim();
        int tweetSize = tempTweet.length();
        int exclamationCount = 0;
        if (!tempTweet.contains("!")) {
            return 0;
        }
        for (int i = 0; i < tweetSize; i++) {
            if (tempTweet.charAt(i) == '!') {
                exclamationCount++;
            }

        }
        return exclamationCount;
    }

    /*
    * Assumes a table with following order
    * Word array index category(adjective/adverb,verb) Scores
    *
    * extremely 7 adverb 0.7
    * impress 8 verb 0.5
    */
    public double getAdverbGroupScore() {
        Word[] tempWords = this.words.clone();
        double score = 0;
        for (int i = 0; i < tempWords.length; i++) {
            Word word = tempWords[i];
            if (word.getCategory().equals(Word.Category.ADVERB)) {
                if ((i - 1) >= 0 && tempWords[i - 1].getCategory().equals(Word.Category.ADVERB)) {
                    score += tempWords[i - 1].getSentiScore() * tempWords[i].getSentiScore();
                }
            }
        }

        return score;
    }

    public double getVerbGroupScore() {
        Word[] tempWords = this.words.clone();
        Double DEFAULT_WHEN_NOADVERB = 0.5;
        double score = 0;
        for (int i = 0; i < tempWords.length; i++) {
            Word word = tempWords[i];
            boolean noAdverb = true;
            if (word.getCategory().equals(Word.Category.VERB)) {
                if ((i - 1) >= 0 && tempWords[i - 1].getCategory().equals(Word.Category.ADVERB)) {
                    score += tempWords[i - 1].getSentiScore() * tempWords[i].getSentiScore();
                    noAdverb = false;
                }

                if ((i + 1) <= tempWords.length && tempWords[i + 1].getCategory().equals(Word.Category.ADVERB)) {
                    score += tempWords[i + 1].getSentiScore() * tempWords[i].getSentiScore();
                    noAdverb = false;
                }

                if (noAdverb) {
                    score += DEFAULT_WHEN_NOADVERB * tempWords[i].getSentiScore();
                }
            }
        }

        return score;
    }

    public int getOpinionGroupCount() {
        Word[] tempWords = this.words.clone();
        int opinionGroups = 0;
        for (int i = 0; i < tempWords.length; i++) {
            Word word = tempWords[i];
            boolean noAdverb = true;
            if (word.getCategory().equals(Word.Category.VERB)) {
                if ((i - 1) >= 0 && tempWords[i - 1].getCategory().equals(Word.Category.ADVERB)) {
                    opinionGroups++;
                    noAdverb = false;
                }

                if ((i + 1) <= tempWords.length && tempWords[i + 1].getCategory().equals(Word.Category.ADVERB)) {
                    opinionGroups++;
                }
                noAdverb = false;
            }

            if (noAdverb) {
                opinionGroups++;
            }

            if (word.getCategory().equals(Word.Category.ADVERB)) {
                if ((i - 1) >= 0 && tempWords[i - 1].getCategory().equals(Word.Category.ADVERB)) {
                    opinionGroups++;
                }
            }
        }

        return opinionGroups;
    }

    private Word[] generateWordList(String tweet) {
        return null;
    }

    private int getEmoticonsCount() {
// return the emoticons count
        return 0;
    }

    private double getEmoticonsScore() {
// return the emoticons count
        return 0;
    }
}
