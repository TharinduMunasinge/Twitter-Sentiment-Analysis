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
        scores.adGScore = getAdverbGroupScore();
        scores.adAjGScore = getAdverbAdjectiveGroupScore();
        scores.vGScore = getVerbGroupScore();
        scores.adjNounGScore = getAdjectiveNounGroupScore();
        scores.emoticonScore = getEmoticonsScore();

        scores.emoticonsCount = getEmoticonsCount();
        scores.repetitions = getRepetitions();
        scores.exclamations = getExclamations();
        scores.fractionInCaps = getFractionInCaps();
        scores.opinionGroupsCount = getOpinionGroupCount();
        scores.emoticonsAndGroupsCount = scores.opinionGroupsCount + scores.emoticonsCount;

        return scores;
    }

    public double calculate() {
        Scores scores = getScores();
        double logReps = scores.repetitions > 0 ? Math.log(scores.repetitions) : 0;
        double logExclams = scores.exclamations > 0 ? Math.log(scores.exclamations) : 0;
        return ((1 + ((scores.fractionInCaps + logReps + logExclams) / 3))/scores.emoticonsAndGroupsCount) *
                (scores.adGScore + scores.adAjGScore + scores.vGScore + scores.emoticonScore + scores.adjNounGScore);
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

    public int getRepetitions() {
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
    *
    * ADVERB ADVERB
    */
    public double getAdverbGroupScore() {
        return getOrderedGroupScore(Word.Category.ADVERB, Word.Category.ADVERB);
    }

    /* ADVERB ADJECTIVE
    public double getAdverbAdjectiveGroupScore() {
        return getOrderedGroupScore(Word.Category.ADVERB, Word.Category.ADJECTIVE);
    }*/


    private double getOrderedGroupScore(Word.Category first, Word.Category second) {
        double score = 0;
        for (int i = 0; i < words.length; i++) {
            Word word = words[i];
            if (word.getCategory().equals(second)) {
                if ((i - 1) >= 0 && words[i - 1].getCategory().equals(first)) {
                    score += words[i - 1].getSentiScore() * words[i].getSentiScore();
                }
            }
        }
        return score;
    }

   /* ADJECTIVE ADVERB */
   public double getAdverbAdjectiveGroupScore() {
      Double DEFAULT_WHEN_NOADVERB = 0.5;
      double score = 0;
      for (int i = 0; i < words.length; i++) {
         Word word = words[i];
         boolean noAdverb = true;
         if (word.getCategory().equals(Word.Category.ADJECTIVE)) {
            if ((i - 1) >= 0 && words[i - 1].getCategory().equals(Word.Category.ADVERB)) {
               score += words[i - 1].getSentiScore() * words[i].getSentiScore();
               noAdverb = false;
            }
            if (noAdverb) {
               score += DEFAULT_WHEN_NOADVERB * words[i].getSentiScore();
            }
         }
      }

      return score;
   }

   /* ADJECTIVE ADVERB */
   public double getAdjectiveNounGroupScore() {
      Double DEFAULT_WHEN_NOADVERB = 0.5;
      double score = 0;
      for (int i = 0; i < words.length; i++) {
         Word word = words[i];
         boolean noAdverb = true;
         if (word.getCategory().equals(Word.Category.NOUN)) {
            if ((i - 1) >= 0 && words[i - 1].getCategory().equals(Word.Category.ADJECTIVE)) {
               score += words[i - 1].getSentiScore() * words[i].getSentiScore();
               noAdverb = false;
            }
            if (noAdverb) {
               score += DEFAULT_WHEN_NOADVERB * words[i].getSentiScore();
            }
         }
      }

      return score;
   }
    /* ADVERB VERB or VERB ADVERB */
    public double getVerbGroupScore() {
        Double DEFAULT_WHEN_NOADVERB = 0.5;
        double score = 0;
        for (int i = 0; i < words.length; i++) {
            Word word = words[i];
            boolean noAdverb = true;
            if (word.getCategory().equals(Word.Category.VERB)) {
                if ((i - 1) >= 0 && words[i - 1].getCategory().equals(Word.Category.ADVERB)) {
                    score += words[i - 1].getSentiScore() * words[i].getSentiScore();
                    noAdverb = false;
                }

                if ((i + 1) < words.length && words[i + 1].getCategory().equals(Word.Category.ADVERB)) {
                    score += words[i + 1].getSentiScore() * words[i].getSentiScore();
                    noAdverb = false;
                }

                if (noAdverb) {
                    score += DEFAULT_WHEN_NOADVERB * words[i].getSentiScore();
                }
            }
        }

        return score;
    }

    /* ADVERB VERB or VERB ADVERB */
    public int getOpinionGroupCount() {
        int opinionGroups = 0;
        for (int i = 0; i < words.length; i++) {
            Word word = words[i];
            boolean noAdverb = true;
            if (word.getCategory().equals(Word.Category.VERB)) {
                if ((i - 1) >= 0 && words[i - 1].getCategory().equals(Word.Category.ADVERB)) {
                    opinionGroups++;
                    noAdverb = false;
                }

                if ((i + 1) < words.length && words[i + 1].getCategory().equals(Word.Category.ADVERB)) {
                    opinionGroups++;
                    noAdverb = false;
                }
            }

            if (noAdverb) {
                opinionGroups++;
            }

            if (word.getCategory().equals(Word.Category.ADVERB)) {
                if ((i - 1) >= 0 && words[i - 1].getCategory().equals(Word.Category.ADVERB)) {
                    opinionGroups++;
                }
            }
           if (word.getCategory().equals(Word.Category.ADJECTIVE)) {
              if ((i - 1) >= 0 && words[i - 1].getCategory().equals(Word.Category.ADVERB)) {
                 opinionGroups++;
              }
           }

           if (word.getCategory().equals(Word.Category.NOUN)) {
              if ((i - 1) >= 0 && words[i - 1].getCategory().equals(Word.Category.ADJECTIVE)) {
                 opinionGroups++;
              }
           }
        }

        return opinionGroups;
    }

    private int getEmoticonsCount() {
        int count = 0;
        for (Word word : words) {
            if (word.getCategory() == Word.Category.EMOTICON) {
                count++;
            }
        }
        return count;
    }

    private double getEmoticonsScore() {
        double score = 0.0;
        for (Word word : words) {
            if (word.getCategory() == Word.Category.EMOTICON) {
                score += word.getSentiScore();
            }
        }
        return score;
    }
}
