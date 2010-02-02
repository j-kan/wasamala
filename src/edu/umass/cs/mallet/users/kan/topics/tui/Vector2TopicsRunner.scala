package edu.umass.cs.mallet.users.kan.topics.tui

/*
    --input train.ser 
    --num-topics 5 
    --num-iterations 100
    --output-state parallel-lda/state.gz 
    --output-doc-topics parallel-lda/doc-topics.txt 
    --output-topic-keys parallel-lda/topic-keys.txt 
    --word-topic-counts-file parallel-lda/word-topic-counts.txt 
    --topic-word-weights-file parallel-lda/topic-word-weights.txt
    --random-seed 90210
*/

object Vector2TopicsRunner {
  def main(args : Array[String]) : Unit = {
    
    val iterations  = 100
    val topics      = 5
    
    val dirname     = String.format("%d-iterations-%d-topics-parallel-lda", iterations.asInstanceOf[Object], topics.asInstanceOf[Object])
    
    print(dirname)

  }
}
