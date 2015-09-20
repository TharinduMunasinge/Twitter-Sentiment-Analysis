package score;

import edu.stanford.nlp.util.StringUtils;

import java.util.Arrays;

/**
 * Created by bhash90 on 9/20/15.
 */
public class ScoreCalculator {

    private String tweet;
    private Word[] words;

    public ScoreCalculator(String tweet) {
        this.tweet = tweet;
        words = generateWordList(tweet);
    }

    private double calculate() {
        double adgScore = getAdverbGroupScore();
        double vgScore = getVerbGroupScore();
        double emoticonScore = getEmoticonsScore();

        int emoticonsCount = getEmoticonsCount();
        int repetitions = getRepitions();
        int exclamations = getExclamations();
        double fractionInCaps = getFractionInCaps();
        int opinionGroupsCount = getOpinionGroupCount();

        int emoticansAndGroupsCount = opinionGroupsCount + emoticonsCount;

        double score = (1 + (fractionInCaps + Math.log(repetitions) + Math.log(exclamations)) / 3) * (adgScore + vgScore + emoticonScore);
        return repetitions;
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
    * Word array index category(adjective/adverb,verb) Score
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
