
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as 
    published by the Free Software Foundation, either version 3 of the 
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.


============================================================================

GATE Twitter PoS tagger
 http://www.gate.ac.uk

Copyright (c) 2012-2013, The University of Sheffield


====== Usage (requires Java 1.6.0 or above): ===============================


  java -jar twitie_tag.jar <path to model file> <path to input file>


The model file is any valid Stanford Tagger model file. The input file
should contain one plaintext tweet per line, with spaces separating
tokens. 

To run the tagger using the best model reported in the paper, do:


  java -jar twitie_tag.jar models/gate-EN-twitter.model <input file>


Tagged tokens are output on stdout; status information on stderr.
This means that if you want to save the output, simply redirect stdout;


  java -jar twitie_tag.jar <path to model file>  \
    <path to input file>  >  <output file>


For example:


  java -jar twitie_tag.jar vcboot.1543K-twitter.model \
    corpora/ritter_dev.nolabels > ritter_dev.tagged


There is a known "InvocationTargetException" problem when using Apple's
own Java distribution; using the OpenJDK JRE can remedy this.


====== Archive contents: ===================================================

models/  contains model files for the Stanford tagger and our modified 
         version. Included are:

models/gate-EN-twitter.model       - our final vote-constrained 
                                     bootstrapped model with unknown 
                                     word handling.
models/gate-EN-twitter-fast.model       - a high-speed model (lower accuracy).

corpora/  contains various training and evaluation corpora. The 
          ".nolabels" suffix is used for tokenised, unlabeled files.

corpora/ritter_*                   - the T-Pos corpora T-train, 
                                     T-dev and T-eval.
corpora/foster_*                   - the DCU corpora D-dev and D-eval.


Generated taggings may be evaluated against the supplied gold standard
files using the included eval.py script (requires NLTK, Python 2.7)


====== Support: ============================================================

Support is available from the author, leon@dcs.shef.ac.uk, and via the GATE
user mailing list:

  https://lists.sourceforge.net/lists/listinfo/gate-users


====== Citing the tagger: ==================================================

Please acknowledge the tagger if you use it in your work.

 L. Derczynski, A. Ritter, S. Clark, and K. Bontcheva, 2013: "Twitter 
  Part-of-Speech Tagging for All: Overcoming Sparse and Noisy Data". In:
  Proceedings of the International Conference on Recent Advances in Natural
  Language Processing.

====== To replicate the final result: ======================================

Option A:

 Run show-result.bat or show-result.sh

Option B:

 java -jar twitie_tag.jar models/show-result.model corpora/ritter_eval.nolabels > t_eval.vcb-labelled 

 python eval.py corpora/ritter_eval.stanford t_eval.vcb-labelled

These are the evaluations on the full vote-constrained bootstrapped 
model, that gave the final results on the held-out Ritter/T-Pos eval
corpus. The top result on the dev corpus came using slightly less 
than all the data; the responsible model is not included here for 
space reasons. 

It was sampled at after 1 319 006 tokens of data (e.g. 
from all files in the separately supplied data archive up to and 
including 00.english.0341). This model can be downloaded from:

 http://derczynski.com/sheffield/resources/boot00.5K.0341.model

This should be run against the "ritter_dev.nolabels" file and compared
to "ritter_dev.stanford" to re-produce the result in Section 5.

