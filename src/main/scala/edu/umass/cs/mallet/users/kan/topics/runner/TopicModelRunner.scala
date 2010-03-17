package edu.umass.cs.mallet.users.kan.topics.runner

import java.io.{File,Writer,FileWriter,PrintWriter,BufferedWriter, BufferedOutputStream, ObjectOutputStream, FileOutputStream}

import scala.collection.jcl.MutableIterator.Wrapper

import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList, Alphabet}
import cc.mallet.pipe.iterator.FileIterator
import cc.mallet.pipe._

import edu.umass.cs.mallet.users.kan.scalautil.FileUtil
import edu.umass.cs.mallet.users.kan.topics._



trait MalletRunner extends MalletExtensions {

  var outputDir: Option[File] = None
  
  def setOutputDirectory(path: String): Unit = {
    outputDir = path match {
        case null => None
        case p    => {
          val dir = new File(p)

          dir.mkdirs
          Some(dir)
        }
    }
  }
  
  def outputFilePath(dir: Option[File], filename: String):String = dir match {
    case None    => filename
    case Some(d) => new File(d, filename).getPath
  }

  def outputFile(dir: Option[File], filename: String): File = dir match {
    case None    => new File(filename)
    case Some(d) => new File(d, filename)
  }
  
  implicit def string2File(filename:String):File = {
    println(List(outputDir, filename).mkString(":"))
    outputFile(outputDir, filename)
  }
}


object TopicModelRunner extends MalletRunner {

  def load(f: File) = {
    if (f.isDirectory)
      loadDirectories(f)
    else
      InstanceList.load(f)
  }

  def loadDirectories(dir: File) = {
    val directories = dir.listFiles.filter (f => f.isDirectory)

    val instancePipe = new SerialPipes( Array[Pipe]( 
      new SaveDataInSource,
      new TargetStringToFeatures,
      new Input2CharSequence,
      new CharSubsequence(CharSubsequence.SKIP_HEADER),
      new CharSequence2TokenSequence,
      new TokenSequenceLowercase,
      new TokenSequenceRemoveStopwords(false, false),
      new TokenSequence2FeatureSequence
      //new PrintInputAndTarget
    ))
        
    val instances = new InstanceList(instancePipe)
    val removeCommonPrefix = true
    
    instances.addThruPipe(
      new FileIterator(directories, FileIterator.STARTING_DIRECTORIES, removeCommonPrefix))
    
    FileUtil.serializeObject(instances, "instances.ser")

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
    
    val inputfile     = new File(args(0))
    val instances     = TopicModelRunner.load(inputfile)
    val numTopics     = if (args.size > 1) args(1).toInt else 20
    val numIterations = if (args.size > 2) args(2).toInt else 1000
    val basename      = inputfile.getName.split("\\.")(0)
    
    setOutputDirectory("%s-%d-iterations-%d-topics".format(basename, numIterations, numTopics))
    
    val alpha = 50.0
    val beta  = 0.01
    
    val ptm = new ParallelTopicModel(numTopics, alpha, beta)
    
    ptm.setRandomSeed(90210)
    ptm.setProgressLogFile("progress.txt")
    
    ptm.addInstances(instances)
    ptm.setTopicDisplay(50, 20)
    ptm.setNumIterations(numIterations)
    ptm.setOptimizeInterval(50)
    ptm.setBurninPeriod(200)
    
    ptm.setNumThreads(1)
 
    ptm.estimate

    ptm.printState("state.gz")
    ptm.printDocumentTopics("doc-topics.txt")
    ptm.printTypeTopicCounts("word-topic-counts.txt")
    ptm.printTopWords("topic-keys.txt", 20, false)

    FileUtil.serializeObject(ptm, "ptm-model.ser")
    
    //ptm.setModelOutput(outputModelInterval.value, outputModelFilename.value);
    //                lda.setSaveState(outputStateInterval.value, stateFile.value);
    //              try { lda.printDocumentTopics(out, docTopicsThreshold.value, docTopicsMax.value); }
  }

}
