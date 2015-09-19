//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterTagger {
    private static Boolean do_correction = Boolean.valueOf(true);
    private static Boolean label_fixed = Boolean.valueOf(true);
    private static Boolean do_interjections = Boolean.valueOf(true);
    private static Boolean do_nes = Boolean.valueOf(true);
    private static Boolean do_eval = Boolean.valueOf(false);
    private static Boolean do_debug = Boolean.valueOf(true);
    public static HashMap<String, String> corrections = new HashMap();
    public static ArrayList<Pattern> interjections = new ArrayList();
    public static ArrayList<String> nes = new ArrayList();
    public static String resourceRoot="resource/";
    public TwitterTagger() {
    }

    public static List<TaggedWord> tagSentence(MaxentTagger tagger, ArrayList<String> tokens) {
        ArrayList untagged_string = new ArrayList();

        TaggedWord to_label;
        for(Iterator var4 = tokens.iterator(); var4.hasNext(); untagged_string.add(to_label)) {
            String token = (String)var4.next();
            to_label = new TaggedWord(token);
            if(label_fixed.booleanValue()) {
                if(token.indexOf("#") == 0) {
                    to_label.setTag("HT");
                }

                if(token.indexOf("@") == 0) {
                    to_label.setTag("USR");
                }

                if(token.indexOf(".com") > -1 || token.indexOf("http:") == 0 || token.indexOf("www.") == 0) {
                    to_label.setTag("URL");
                }

                if(token.toLowerCase().equals("rt") || token.substring(0, 1).equals("R") && token.toLowerCase().equals("retweet")) {
                    to_label.setTag("RT");
                }
            }

            String token_lc;
            if(do_correction.booleanValue()) {
                token_lc = token.toLowerCase();
                if(corrections.containsKey(token_lc)) {
                    String replacement = (String)corrections.get(token_lc);
                    if(do_debug.booleanValue()) {
                        System.err.println("Correcting " + token + " to " + replacement);
                    }

                    token = replacement;
                    to_label = new TaggedWord(replacement);
                }
            }

            if(do_interjections.booleanValue()) {
                Iterator replacement1 = interjections.iterator();

                while(replacement1.hasNext()) {
                    Pattern token_lc1 = (Pattern)replacement1.next();
                    Matcher m = token_lc1.matcher(token.toLowerCase());
                    if(m.find()) {
                        if(do_debug.booleanValue()) {
                            System.err.println("Interjection labelled for " + token);
                        }

                        to_label.setTag("UH");
                        break;
                    }
                }
            }

            if(do_nes.booleanValue()) {
                token_lc = token.toLowerCase();
                if(nes.contains(token_lc)) {
                    if(do_debug.booleanValue()) {
                        System.out.println("NE labelled for " + token);
                    }

                    to_label.setTag("NNP");
                }
            }
        }

        return tagger.tagSentence(untagged_string, true);
    }

    public static void main(String[] args) throws Exception {
        if(args.length > 2) {
            do_correction = Boolean.valueOf(true);
            label_fixed = Boolean.valueOf(false);
            do_interjections = Boolean.valueOf(true);
            do_nes = Boolean.valueOf(true);
            if(args[2].equals("slang")) {
                do_correction = Boolean.valueOf(true);
            } else if(args[2].equals("genre")) {
                label_fixed = Boolean.valueOf(true);
            } else if(args[2].equals("smiley")) {
                do_interjections = Boolean.valueOf(true);
            } else if(args[2].equals("ne")) {
                do_nes = Boolean.valueOf(true);
            } else if(!args[2].equals("none")) {
                System.err.println("Unrecognised option: " + args[2]);
                System.exit(-1);
            }
        } else if(args.length < 2) {
            System.err.println("Usage: java -jar twitie_tag.jar <model_file> <file_to_tag>");
            System.exit(-1);
        }

        BufferedReader tagger;
        String eval_filename;
        if(do_correction.booleanValue()) {
            if(do_debug.booleanValue()) {
                System.err.println("Loading orthographic lookup (slang)");
            }

            tagger = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot+"orth.en.csv")));

            while((eval_filename = tagger.readLine()) != null) {
                eval_filename = eval_filename.trim();
                String[] br = eval_filename.split(",");
                corrections.put(br[0].toLowerCase(), br[1]);
            }

            tagger.close();
        }

        if(do_interjections.booleanValue()) {
            if(do_debug.booleanValue()) {
                System.out.println("Loading interjection lookup");
            }

            tagger = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot+"interjections.regex")));

            while((eval_filename = tagger.readLine()) != null) {
                eval_filename = eval_filename.trim();
                interjections.add(Pattern.compile("^" + eval_filename + "$"));
            }

            tagger.close();
        }

        BufferedReader var21;
        if(do_nes.booleanValue()) {
            if(do_debug.booleanValue()) {
                System.err.println("Loading named entities");
            }

            tagger = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot+"names.txt")));

            while((eval_filename = tagger.readLine()) != null) {
                eval_filename = eval_filename.trim();
                nes.add(eval_filename.toLowerCase());
            }

            tagger.close();
            var21 = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot+"cities.txt")));

            while((eval_filename = var21.readLine()) != null) {
                eval_filename = eval_filename.trim();
                nes.add(eval_filename.toLowerCase());
            }

            var21.close();
            BufferedReader tokens_seen = new BufferedReader(new InputStreamReader(new FileInputStream(resourceRoot+"corps.txt")));

            while((eval_filename = tokens_seen.readLine()) != null) {
                eval_filename = eval_filename.trim();
                nes.add(eval_filename.toLowerCase());
            }

            tokens_seen.close();
        }

        MaxentTagger var20 = new MaxentTagger(args[0]);
        eval_filename = args[1];
        var21 = new BufferedReader(new InputStreamReader(new FileInputStream(eval_filename)));
        int var22 = 0;
        int sentences_seen = 0;
        int sentences_correct = 0;
        int tokens_correct = 0;
        Pattern pattern = Pattern.compile("(.+)_([^_]+$)");

        String line;
        while((line = var21.readLine()) != null) {
            line = line.trim();
            if(line.length() != 0) {
                ++sentences_seen;
                ArrayList tokens = new ArrayList();
                ArrayList tags = new ArrayList();
                String[] input_tokens = line.split("\\s+");
                String[] words_correct_in_sentence = input_tokens;
                int outSent = input_tokens.length;

                int sentence_length;
                String ev_tag;
                for(sentence_length = 0; sentence_length < outSent; ++sentence_length) {
                    String taggedSent = words_correct_in_sentence[sentence_length];
                    ++var22;
                    String i;
                    if(do_eval.booleanValue()) {
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

                List var23 = tagSentence(var20, tokens);
                sentence_length = var23.size();
                String var24 = "";

                for(int i=0;i<var23.size();i++)
                {
                    System.out.println();
////                    System.out.println(((TaggedWord) var23.get(i)).word());
////                    System.out.println((((TaggedWord)var23.get(i)).tag()));
                    System.out.println(((var23.get(i)).toString()));
////                    TaggedWord w=((TaggedWord)var23.get(i);
//
                }

                int var25;
                for(var25 = 0; var25 < sentence_length; ++var25) {
                    var24 = var24 + input_tokens[var25] + "_" + ((TaggedWord)var23.get(var25)).tag() + " ";
                }

          //      System.out.println(var24.trim());
                if(do_eval.booleanValue()) {
                    var25 = 0;

                    for(int var26 = 0; var26 < sentence_length; ++var26) {
                        String var27 = (String)tags.get(var26);
                        ev_tag = ((TaggedWord)var23.get(var26)).tag();
                        if(var27.equals(ev_tag)) {
                            ++var25;
                            ++tokens_correct;
                        } else {
                            System.err.println("> For word \'" + (String)tokens.get(var26) + "\', expected " + var27 + ", got " + ev_tag);
                        }
                    }

                    if(var25 == sentence_length) {
                        ++sentences_correct;
                    }
                }
            }
        }

        var21.close();
        if(do_eval.booleanValue()) {
            System.err.println("Sentences seen:    " + sentences_seen);
            System.err.println("Sentences correct: " + sentences_correct);
            System.err.println("Tokens seen:    " + var22);
            System.err.println("Tokens correct: " + tokens_correct);
        }

    }
}
