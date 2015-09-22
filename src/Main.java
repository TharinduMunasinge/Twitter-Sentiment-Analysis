import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import score.ScoreCalculator;
import score.Scores;
import score.Word;
import sentiment.EmoticonStrength;
import sentiment.WordStrength;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by janaka on 9/21/15.
 */
public class Main {

    private static boolean do_correction = true;
    private static boolean label_fixed = false;
    private static boolean do_interjections = true;
    private static boolean do_nes = true;
    private static boolean do_eval = false;
    private static boolean do_debug = true;

    public static final String resourceRoot = "resource/";

    private static WordStrength wordStrength;
    private static EmoticonStrength emoticonStrength;

    private static void validateArgs(String[] args) {
        if (args.length > 2) {
            if (args[2].equals("slang")) {
                do_correction = true;
            } else if (args[2].equals("genre")) {
                label_fixed = true;
            } else if (args[2].equals("smiley")) {
                do_interjections = true;
            } else if (args[2].equals("ne")) {
                do_nes = true;
            } else if (!args[2].equals("none")) {
                System.err.println("Unrecognised option: " + args[2]);
                System.exit(-1);
            }
        } else if (args.length < 2) {
            System.err.println("Usage: java -jar twitie_tag.jar <model_file> <file_to_tag>");
            System.exit(-1);
        }
    }

    private static Map<String, String> loadCorrections() throws IOException {
        Map<String, String> corrections = new HashMap<String, String>();
        BufferedReader tagger;
        String eval_filename;
        if (do_correction) {
            if (do_debug) {
                System.err.println("Loading orthographic lookup (slang)");
            }

            tagger = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot + "orth.en.csv")));

            while ((eval_filename = tagger.readLine()) != null) {
                eval_filename = eval_filename.trim();
                String[] br = eval_filename.split(",");
                corrections.put(br[0].toLowerCase(), br[1]);
            }

            tagger.close();
        }
        return corrections;
    }

    private static List<Pattern> loadInterjections() throws IOException {
        List<Pattern> interjections = new ArrayList<Pattern>();
        BufferedReader tagger;
        String eval_filename;
        if (do_interjections) {
            if (do_debug) {
                System.out.println("Loading interjection lookup");
            }

            tagger = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot + "interjections.regex")));

            while ((eval_filename = tagger.readLine()) != null) {
                eval_filename = eval_filename.trim();
                interjections.add(Pattern.compile("^" + eval_filename + "$"));
            }

            tagger.close();
        }
        return interjections;
    }

    private static List<String> loadNEs() throws IOException {
        List<String> nes = new ArrayList<String>();
        BufferedReader tagger;
        String eval_filename;
        BufferedReader var21;
        if (do_nes) {
            if (do_debug) {
                System.err.println("Loading named entities");
            }

            tagger = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot + "names.txt")));

            while ((eval_filename = tagger.readLine()) != null) {
                eval_filename = eval_filename.trim();
                nes.add(eval_filename.toLowerCase());
            }

            tagger.close();
            var21 = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot + "cities.txt")));

            while ((eval_filename = var21.readLine()) != null) {
                eval_filename = eval_filename.trim();
                nes.add(eval_filename.toLowerCase());
            }

            var21.close();
            BufferedReader tokens_seen = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot + "corps.txt")));

            while ((eval_filename = tokens_seen.readLine()) != null) {
                eval_filename = eval_filename.trim();
                nes.add(eval_filename.toLowerCase());
            }

            tokens_seen.close();
        }
        return nes;
    }

    public static void main(String[] args) throws Exception {
        validateArgs(args);

        Map<String, String> corrections = loadCorrections();
        List<Pattern> interjections = loadInterjections();
        List<String> nes = loadNEs();

        wordStrength = new WordStrength("sentiwordnet.csv");
        emoticonStrength = new EmoticonStrength();

        TwitterTagger tagger = new TwitterTagger(corrections, interjections, nes, do_correction, label_fixed,
                do_interjections, do_nes, do_debug);

        MaxentTagger var20 = new MaxentTagger(args[0]);
        BufferedReader var21 = new BufferedReader(new InputStreamReader(new FileInputStream(args[1])));
        int var22 = 0;
        int sentences_seen = 0;
        int sentences_correct = 0;
        int tokens_correct = 0;
        Pattern pattern = Pattern.compile("(.+)_([^_]+$)");

        PrintWriter writer = new PrintWriter("dataset.csv");
        writer.write(Scores.getHeader());

        String line;
        while ((line = var21.readLine()) != null) {
            line = line.trim();
            if (line.length() != 0) {
                ++sentences_seen;
                ArrayList tokens = new ArrayList();
                ArrayList tags = new ArrayList();
                String[] input_tokens = line.split("\\s+");
                String[] words_correct_in_sentence = input_tokens;
                int outSent = input_tokens.length;

                int sentence_length;
                String ev_tag;
                for (sentence_length = 0; sentence_length < outSent; ++sentence_length) {
                    String taggedSent = words_correct_in_sentence[sentence_length];
                    ++var22;
                    String i;
                    if (do_eval) {
                        Matcher gs_tag = pattern.matcher(taggedSent);
                        gs_tag.find();
                        i = gs_tag.group(1);
                        ev_tag = gs_tag.group(2);
                        tags.add(ev_tag);
                    } else {
                        i = taggedSent;
                    }

                    tokens.add(i);
                }

                List<TaggedWord> var23 = tagger.tagSentence(var20, tokens);
                sentence_length = var23.size();
                String var24 = "";

                List<Word> words = new ArrayList<Word>();
                for (TaggedWord tagged : var23) {
                    double strength = getStrength(tagged);

                    String tag = tagged.tag();
                    Word.Category category;
                    if (tag.startsWith("JJ")) {        // adjective
                        category = Word.Category.ADJECTIVE;
                    } else if (tag.startsWith("RB")) { // adverb
                        category = Word.Category.ADVERB;
                    } else if (tag.startsWith("NN")) { // noun
                        category = Word.Category.NOUN;
                    } else if (tag.startsWith("VB")) { // verb
                        category = Word.Category.VERB;
                    } else if (tag.equals("UH")) {     // emoticon
                        category = Word.Category.EMOTICON;
                    } else {
                        category = Word.Category.OTHER;
                    }

                    System.out.println(tagged.word() + ": " + tag + ", " + strength);
                    words.add(new Word(tagged.word(), category, strength));
                }
                ScoreCalculator calc = new ScoreCalculator(line, words.toArray(new Word[words.size()]));
                double score = calc.calculate();
                System.out.println("\nText: " + line + "\nScore: " + score + "\n");

                // write CSV
                writer.write("\n" + calc.getScores().toString());

                int var25;
                for (var25 = 0; var25 < sentence_length; ++var25) {
                    var24 = var24 + input_tokens[var25] + "_" + var23.get(var25).tag() + " ";
                }

                // TODO may not be necessaary
                if (do_eval) {
                    var25 = 0;

                    for (int var26 = 0; var26 < sentence_length; ++var26) {
                        String var27 = (String) tags.get(var26);
                        ev_tag = var23.get(var26).tag();
                        if (var27.equals(ev_tag)) {
                            ++var25;
                            ++tokens_correct;
                        } else {
                            System.err.println("> For word \'" + tokens.get(var26) + "\', expected " + var27 + ", got " + ev_tag);
                        }
                    }

                    if (var25 == sentence_length) {
                        ++sentences_correct;
                    }
                }
            }
        }
        writer.close();
        var21.close();

        if (do_eval) {
            System.err.println("Sentences seen:    " + sentences_seen);
            System.err.println("Sentences correct: " + sentences_correct);
            System.err.println("Tokens seen:    " + var22);
            System.err.println("Tokens correct: " + tokens_correct);
        }
    }

    private static double getStrength(TaggedWord word) {
        //lowercase, remove punctuations

        String lowerText = word.word().toLowerCase();
        String dropS = lowerText.replaceAll("\\'s$" , "");
        String text = dropS.replaceAll("[^\\w]*", "");
        String tag = word.tag();

        if (tag.startsWith("JJ")) {        // adjective
            return wordStrength.extract(text, "a");
        } else if (tag.startsWith("RB")) { // adverb
            return wordStrength.extract(text, "r");
        } else if (tag.startsWith("NN")) { // noun
            return wordStrength.extract(text, "n");
        } else if (tag.startsWith("VB")) { // verb
            return wordStrength.extract(text, "v");
        } else if (tag.equals("UH")) {     // emoticon
            return emoticonStrength.extract(word.word(), null);
        }

        if (do_debug) {
            System.err.println("Unknown tag in " + word);
        }
        return 0.0;
    }
}
