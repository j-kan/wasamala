package edu.umass.cs.mallet.users.kan.topics

import java.io.File
import edu.umass.cs.mallet.users.kan.topics._
import cc.mallet.util.Randoms

@SerialVersionUID(7743904988817001775L)
class LDAHyperExtended(numTopics:Int, alpha:Double, beta:Double) 
    extends ParallelTopicModel(numTopics, alpha, beta) 
    with MalletExtensions {    
      
  def numDocs = data.size
  
  def topicAssignments = data.iterator
      
  def doc_topic_assignment(doc:Int) = data.get(doc)
  
  def p_topics_given_docs = new Iterator[Seq[Double]] { 
    
    private var doc = 0

    def hasNext = doc < data.size 

    def next = { 
      val seq = doc_topic_assignment(doc).p_topics
      doc+=1
      seq
    } 
  }
}

object LDAHyperExtended {
  def read(f:File):LDAHyperExtended = ParallelTopicModel.read(f).asInstanceOf[LDAHyperExtended]
}