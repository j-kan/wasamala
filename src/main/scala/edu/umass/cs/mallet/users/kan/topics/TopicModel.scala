package edu.umass.cs.mallet.users.kan.topics

import scala.collection.jcl.MutableIterator.Wrapper

import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList, Alphabet}
import cc.mallet.pipe.iterator.FileIterator
import cc.mallet.pipe._
import cc.mallet.topics.TopicAssignment

import edu.umass.cs.mallet.users.kan.topics._


class DocTopicAssignment(val topicAssignment:TopicAssignment) {
  
  val name          = topicAssignment.instance.getName.toString
  val wordSequence  = topicAssignment.instance.getData.asInstanceOf[FeatureSequence]
  val wordFeatures  = wordSequence.getFeatures take wordSequence.size toArray
  val wordAlphabet  = wordSequence.getAlphabet

  val numTopics     = topicAssignment.topicSequence.getLabelAlphabet.size
  val topicFeatures = topicAssignment.topicSequence.getFeatures
  
  def content         = wordFeatures.map(index => wordAlphabet.lookupObject(index).toString ).deepMkString(" ")
  
  def c_topics = {
    
    var counts          = Array.make[Int](numTopics, 0)
    
    for (f <- topicFeatures)
      counts(f) += 1
    
    (counts, topicFeatures.length)
  }  
  
  def p_topics = {
    val (counts, len) = c_topics
    
    counts.map(_.toDouble / len)
  }
}


trait MalletExtensions {
  
  implicit def javaIteratorToScalaIterator[A](it : java.util.Iterator[A]) = new Wrapper[A](it)
  
  class AlphabetIterable(val alphabet:Alphabet) extends Iterable[Object] {
    def elements: Iterator[Object] = new Wrapper[Object](alphabet.iterator.asInstanceOf[java.util.Iterator[Object]])
  }
  
  implicit def alphabetToIterable(a:Alphabet):AlphabetIterable = new AlphabetIterable(a)
  
  implicit def topicAssignmentToDocTopicAssignment(ta: TopicAssignment):DocTopicAssignment = new DocTopicAssignment(ta)
}


