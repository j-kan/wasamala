package edu.umass.cs.mallet.users.kan.topics

import java.io.File
import edu.umass.cs.mallet.users.kan.topics._
import cc.mallet.util.Randoms


class LDAHyperExtended(numTopics:Int, alpha:Double, beta:Double) 
    extends ParallelTopicModel(numTopics, alpha, beta) {    


  
}

object LDAHyperExtended {
  def read(f:File):LDAHyperExtended = ParallelTopicModel.read(f).asInstanceOf[LDAHyperExtended]
}