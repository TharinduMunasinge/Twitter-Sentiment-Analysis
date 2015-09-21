//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterTagger {

    private boolean do_correction = true;
    private boolean label_fixed = false;
    private boolean do_interjections = true;
    private boolean do_nes = true;
    private boolean do_debug = true;

    private Map<String, String> corrections;
    private List<Pattern> interjections;
    private List<String> nes;

    public TwitterTagger(Map<String, String> corrections, List<Pattern> interjections, List<String> nes,
                         boolean do_correction, boolean label_fixed, boolean do_interjections, boolean do_nes,
                         boolean do_debug) {
        this.corrections = corrections;
        this.interjections = interjections;
        this.nes = nes;

        this.do_correction = do_correction;
        this.label_fixed = label_fixed;
        this.do_interjections = do_interjections;
        this.do_nes = do_nes;
        this.do_debug = do_debug;
    }

    public List<TaggedWord> tagSentence(MaxentTagger tagger, ArrayList<String> tokens) {
        List untagged_string = new ArrayList();

        TaggedWord to_label;
        for (Iterator var4 = tokens.iterator(); var4.hasNext(); untagged_string.add(to_label)) {
            String token = (String) var4.next();
            to_label = new TaggedWord(token);
            if (label_fixed) {
                if (token.indexOf("#") == 0) {
                    to_label.setTag("HT");
                }

                if (token.indexOf("@") == 0) {
                    to_label.setTag("USR");
                }

                if (token.indexOf(".com") > -1 || token.indexOf("http:") == 0 || token.indexOf("www.") == 0) {
                    to_label.setTag("URL");
                }

                if (token.toLowerCase().equals("rt") || token.substring(0, 1).equals("R") && token.toLowerCase().equals("retweet")) {
                    to_label.setTag("RT");
                }
            }

            String token_lc;
            if (do_correction) {
                token_lc = token.toLowerCase();
                if (corrections.containsKey(token_lc)) {
                    String replacement = (String) corrections.get(token_lc);
                    if (do_debug) {
                        System.err.println("Correcting " + token + " to " + replacement);
                    }

                    token = replacement;
                    to_label = new TaggedWord(replacement);
                }
            }

            if (do_interjections) {
                Iterator replacement1 = interjections.iterator();

                while (replacement1.hasNext()) {
                    Pattern token_lc1 = (Pattern) replacement1.next();
                    Matcher m = token_lc1.matcher(token.toLowerCase());
                    if (m.find()) {
                        if (do_debug) {
                            System.err.println("Interjection labelled for " + token);
                        }

                        to_label.setTag("UH");
                        break;
                    }
                }
            }

            if (do_nes) {
                token_lc = token.toLowerCase();
                if (nes.contains(token_lc)) {
                    if (do_debug) {
                        System.out.println("NE labelled for " + token);
                    }

                    to_label.setTag("NNP");
                }
            }
        }

        return tagger.tagSentence(untagged_string, true);
    }
}
