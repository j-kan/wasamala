package edu.umass.cs.mallet.users.kan.topics.runner

import java.io.{File,Writer,FileWriter,PrintWriter,BufferedWriter, BufferedOutputStream, ObjectOutputStream, FileOutputStream}

import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList}
import cc.mallet.pipe.iterator.FileIterator
import cc.mallet.pipe._

import edu.umass.cs.mallet.users.kan.scalautil.FileUtil
import edu.umass.cs.mallet.users.kan.topics._

object TopicModelRunner {

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
  
  def outputDirectory(path: String): File = {
    if (path == null)
        return null
        
    val outputDir = new File(path)
    
    outputDir.mkdirs
    
    outputDir
  }
  
  def outputFilePath(outputDir: File, filename: String):String = {
    if (outputDir == null) 
        filename
    else
        new File(outputDir, filename).getPath
  }

  def outputFile(outputDir: File, filename: String): File = {
    if (outputDir == null) 
        new File(filename)
    else
        new File(outputDir, filename)
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
    
    val instances     = TopicModelRunner.load(new File(args(0)))
    val numTopics     = if (args.size > 1) args(1).toInt else 20
    val numIterations = if (args.size > 2) args(2).toInt else 1000
    
    val outputDir     = outputDirectory("ptm-%d-iterations-%d-topics".format(numIterations, numTopics))
    
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
