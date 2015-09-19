#!/usr/bin/python

# takes two command line params: <gold standard file> <candidate input file>
# outputs sentence comparison, token errors (label got/expected), confusion matrix, tok+sent level acc%

import os
import sys

if len(sys.argv) < 2:
    print "Usage: eval.py gold_standard_file candidate_tagging file"
    print
    print "Evaluate a PoS labeling. File format is one sentence/tweet per line, space tokenised, with tags following tokens separated by underscores"
    print " e.g. 'The_DT big_JJ dog_NN #scary_HT'"
    print "(requires NLTK 2 or above to be installed; possible via e.g. 'apt-get install python-nltk' or 'easy_install nltk')"
    sys.exit()

import nltk


gs_corpus_path = os.path.realpath(sys.argv[1])
candidate_corpus_path = os.path.realpath(sys.argv[2])

gs = nltk.corpus.reader.TaggedCorpusReader(os.path.dirname(gs_corpus_path), os.path.basename(gs_corpus_path), sep='_')
candidate = nltk.corpus.reader.TaggedCorpusReader(os.path.dirname(candidate_corpus_path), os.path.basename(candidate_corpus_path), sep='_')

g_sents = gs.tagged_sents()
c_sents = candidate.tagged_sents()

sentences_correct = 0

for i in range(len(g_sents)):
    if g_sents[i] == c_sents[i]:
        sentences_correct += 1
        print 'Correct:   ', g_sents[i]
    else:
        print "Expected:  ", g_sents[i]
        print "Candidate: ", c_sents[i]
        
        for j in range(len(g_sents[i])):
            if g_sents[i][j] != c_sents[i][j]:
                print 'For "' + g_sents[i][j][0] +'", expected ', g_sents[i][j][1] ,' got ', c_sents[i][j][1]
    print '-' * 20


g_words = gs.tagged_words()
c_words = candidate.tagged_words()
tokens_correct = 0

for i in range(len(g_words)):
    if g_words[i] == c_words[i]:
        tokens_correct += 1

print nltk.ConfusionMatrix([tag for (word, tag) in g_words], [tag for (word, tag) in c_words])
print
print "Token accuracy: ", tokens_correct / float(len(g_words)), tokens_correct, '/', len(g_words)
print "Sent. accuracy: ", sentences_correct / float(len(g_sents)), sentences_correct, '/', len(g_sents)
