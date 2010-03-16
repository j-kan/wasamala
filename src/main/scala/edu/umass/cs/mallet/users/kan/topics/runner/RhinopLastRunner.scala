package edu.umass.cs.mallet.users.kan.topics.runner

import java.io.{File,Writer,FileWriter,PrintWriter,BufferedWriter, BufferedOutputStream, ObjectOutputStream, FileOutputStream}

import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList, Instance}
import cc.mallet.pipe.iterator.FileIterator
import cc.mallet.pipe._
import cc.mallet.util.CharSequenceLexer

import com.mongodb.{DBCollection, DBCursor, DBObject, Mongo, MongoException}

import edu.umass.cs.mallet.users.kan.scalautil.FileUtil
import edu.umass.cs.mallet.users.kan.topics._
import edu.umass.cs.mallet.users.kan.pipe.iterator.MongoDbCollectionIterator


object RhinopLastRunner extends MalletRunner {

  def load(filename: String) = {
    val f = new File(filename)
    
    if (f.exists)
      InstanceList.load(f)
    else
      loadFromMongo(f)
  }

  def loadFromMongo(f: File) = {
    
    val instancePipe = new SerialPipes( Array[Pipe]( 
      //new SaveDataInSource,
      //new TargetStringToFeatures,
      new Input2CharSequence,
      new CharSequenceRemoveHTML,
      //new CharSubsequence(CharSubsequence.SKIP_HEADER),CharSequenceLexer.UNICODE_LETTERS, CharSequenceLexer.LEX_NONWHITESPACE_TOGETHER
      new CharSequence2TokenSequence("(?:\\p{L}|\\p{N})+"),
      new TokenSequenceLowercase,
      new TokenSequenceRemoveStopwords(false, false).addStopWords(
        Array("href", "http", "www", "music", "fm", "bbcode", "rel", "nofollow", "artist", "title", "tag", "class", "album", "unknown", "span", "strong", "em", "track", "ndash", "ul", "ol", "li")),
      new TokenSequence2FeatureSequence
      //new PrintInputAndTarget
    ))
        
    // val iterator  = MongoDbCollectionIterator.fromCollection(
    //                   "rhinoplast", 
    //                   "artists_info", 
    //                   "", 
    //                   List("name", "bio.content"),
    //                   (item => {
    //   
    //   val name    = item.get("name").toString
    //   val bio     = item.get("bio").asInstanceOf[DBObject]
    //   val summary = bio.get("content")

    val iterator  = MongoDbCollectionIterator.fromCollection(
                        "rhinoplast", 
                        "bio", 
                        "", 
                        List("name", "content"),
                        (item => {

      val name    = item.get("name").toString
      val summary = item.get("content")
      val data    = if (summary == null) name else summary.toString
      val target  = ""
      val source  = name

      new Instance(data, target, name, source)
    }))
    
    val instances = new InstanceList(instancePipe)
    
    instances.addThruPipe(iterator)
    
    FileUtil.serializeObject(instances, f)

    instances
  }

  
  def run(inputfile:String, numIterations:Int, numTopics:Int) = {

    val basename = inputfile.split("\\.")(0)
    val alpha    = 50.0/numTopics
    val beta     = 0.01
    
    setOutputDirectory("%s-%d-iterations-%d-topics-%f-alpha-%f-beta".format(basename, numIterations, numTopics, alpha, beta))

    val lda = new LDAHyperExtended(numTopics, alpha, beta)

    lda.setRandomSeed(90210)
    lda.setProgressLogFile("progress.txt")

    val instances     = RhinopLastRunner.load(inputfile)
    
    lda.addInstances(instances)
    lda.setTopicDisplay(100, 20)
    lda.setNumIterations(numIterations)
    lda.setOptimizeInterval(50)
    lda.setBurninPeriod(200)

    lda.setNumThreads(1)

    lda.estimate

    lda.printState("state.gz")
    lda.printDocumentTopics("doc-topics.txt")
    lda.printTypeTopicCounts("word-topic-counts.txt")
    lda.printTopWords("topic-keys.txt", 20, false)

    FileUtil.serializeObject(lda, string2File("lda-model.ser"))
  }

    //--input train.ser 
    //--num-topics 20 
    //--output-dir jk-output-dir 
    //--output-state state.gz 
    //--output-doc-topics doc-topics.txt 
    //--output-topic-keys topic-keys.txt  
    //--word-topic-counts-file word-topic-counts.txt 
    //--random-seed 90210 
    //--num-iterations 2000
    //--output-progress-log progress.txt

  def main(args: Array[String]) {

    val inputfile     = args(0)
    val numTopics     = if (args.size > 1) args(1).toInt else 20
    val numIterations = if (args.size > 2) args(2).toInt else 1000

    for (numTopics <- List(16, 32)) {       // , 64, 256, 1024
      println("------------------> " + numTopics)
      run(inputfile, numIterations, numTopics)
    }
  }

}
