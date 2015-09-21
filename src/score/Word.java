package score;

/**
 * Created by bhash90 on 9/20/15.
 */
public class Word {

    public enum Category {
        ADJECTIVE,
        ADVERB,
        VERB,
        NOUN,
        EMOTICON,
        OTHER;
    }

    private String word;
    private Category category;
    private double sentiScore;

    public Word(String word, Category category, double sentiScore) {
        try {
            this.category = category;
        } catch(RuntimeException e) {
            this.category = Category.OTHER;
        }
        this.word = word;
        this.sentiScore = sentiScore;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getSentiScore() {
        return sentiScore;
    }

    public void setSentiScore(double sentiScore) {
        this.sentiScore = sentiScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Word word1 = (Word) o;

        if (!word.equals(word1.word))
            return false;
        return category == word1.category;

    }

    @Override
    public int hashCode() {
        int result = word.hashCode();
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}
