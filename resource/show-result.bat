@echo off
java -jar twitie_tag.jar models\show-result.model corpora\ritter_eval.nolabels > t_eval.vcb-labelled
python eval.py corpora\ritter_eval.stanford t_eval.vcb-labelled

