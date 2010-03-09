package edu.umass.cs.mallet.users.kan.topics.runner

import java.io.{File,Writer,FileWriter,PrintWriter,BufferedWriter, BufferedOutputStream, ObjectOutputStream, FileOutputStream}

import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList, Instance}
import cc.mallet.pipe.iterator.FileIterator
import cc.mallet.pipe._

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
    
    val collection = new Mongo().getDB("rhinoplast").getCollection("artists_info")
    val query  = ""
    val fields = "{'name' : 1, 'bio.content' : 2}"

    val instancePipe = new SerialPipes( Array[Pipe]( 
      //new SaveDataInSource,
      new TargetStringToFeatures,
      new Input2CharSequence,
      //new CharSubsequence(CharSubsequence.SKIP_HEADER),
      new CharSequence2TokenSequence,
      new TokenSequenceLowercase,
      new TokenSequenceRemoveStopwords(false, false).addStopWords(
        Array("href", "http", "www", "music", "fm", "bbcode", "rel", "nofollow", "artist", "title", "tag", "class", "album", "unknown", "span", "strong", "em", "track", "ndash", "ul", "ol", "li")),
      new TokenSequence2FeatureSequence
      //new PrintInputAndTarget
    ))
        
    val instances = new InstanceList(instancePipe)
    val iterator  = new MongoDbCollectionIterator(collection, query, fields) (item => {
      val source  = item.get("name")
      val bio     = item.get("bio").asInstanceOf[DBObject]
      val summary = bio.get("content")

      val data = if (summary == null) "" else summary
      val target = ""
      val name = source

      new Instance(data, target, name, source)
    })
    
    instances.addThruPipe(iterator)
    
    FileUtil.serializeObject(instances, f)

    instances
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
    val instances     = RhinopLastRunner.load(inputfile)
    val numTopics     = if (args.size > 1) args(1).toInt else 20
    val numIterations = if (args.size > 2) args(2).toInt else 1000
    val basename      = inputfile.split("\\.")(0)
    val outputDir     = outputDirectory("%s-%d-iterations-%d-topics".format(basename, numIterations, numTopics))

    val alpha = 50.0
    val beta  = 0.01

    val ptm = new ParallelTopicModel(numTopics, alpha, beta)

    ptm.setRandomSeed(90210)
    ptm.setProgressLogFile(new File(outputFilePath(outputDir, "progress.txt")))

    ptm.addInstances(instances)
    ptm.setTopicDisplay(50, 20)
    ptm.setNumIterations(numIterations)
    ptm.setOptimizeInterval(50)
    ptm.setBurninPeriod(200)

    ptm.setNumThreads(1)

    ptm.estimate

    ptm.printState(outputFile(outputDir, "state.gz"))
    ptm.printDocumentTopics(outputFile(outputDir, "doc-topics.txt"))
    ptm.printTypeTopicCounts(outputFile(outputDir, "word-topic-counts.txt"))
    ptm.printTopWords(outputFile(outputDir, "topic-keys.txt"), 20, false)

    FileUtil.serializeObject(ptm, outputFile(outputDir, "ptm-model.ser"))

    //ptm.setModelOutput(outputModelInterval.value, outputModelFilename.value);
    //                lda.setSaveState(outputStateInterval.value, stateFile.value);
    //              try { lda.printDocumentTopics(out, docTopicsThreshold.value, docTopicsMax.value); }
  }

}
