package edu.umass.cs.mallet.users.kan.topics.tui

//--input train.ser 
//--num-topics 10 
//--output-state 1000-lda-iterations-10-topics/state.gz 
//--output-doc-topics 1000-lda-iterations-10-topics/doc-topics.txt 
//--output-topic-keys 1000-lda-iterations-10-topics/topic-keys.txt 
//--random-seed 90210

object LDAHyperTUI {
  
  import java.io.{File, PrintWriter, FileWriter}

  import cc.mallet.pipe._;
  import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList, Instance}
  import cc.mallet.util.Randoms
  import cc.mallet.topics.ParallelTopicModel
  //import cc.mallet.topics.LDAHyper

  import edu.umass.cs.mallet.users.kan.scalautil.FileUtil
  import edu.umass.cs.mallet.users.kan.topics.LDAHyperExtended

  val ALPHA = 50.0  // Alpha parameter: smoothing over topic distribution.
  val BETA  = 0.01  // Beta parameter: smoothing over unigram distribution.

  val TOP_WORDS = 20 // The number of most probable words to print for each topic after model estimation.
  val SHOW_TOPICS_INTERVAL = 50 //The number of iterations between printing a brief summary of the topics so far.
  val NUM_ITERATIONS = 100
  val OPTIMIZE_INTERVAL = 50
  val RANDOM_SEED = 90210

  
  def main(args:Array[String]):Unit = {

    val numTopics = args(0).toInt
    val trainingFile = new File(args(1))
    val testFile     = new File(args(2))
    val training = InstanceList.load(trainingFile)

    Console.println("Training instances loaded from " + trainingFile.getName)
    
//    val testing  = InstanceList.load(trainingFile)
//
//    Console.println("Test instances loaded from " + testFile.getName)

    val outputDirName = "output-ldahex-%d-topics-%s".format(numTopics, trainingFile.getName.replace('.','-'))

    Console.println("Output to " + outputDirName)

    val outputDir = new File(outputDirName)
    
    outputDir.mkdirs

    val lda = new LDAHyperExtended(numTopics, ALPHA, BETA)
    
    lda.setRandomSeed(RANDOM_SEED)
    lda.addInstances(training)
    //lda.setTestingInstances(testing)
    lda.setTopicDisplay(SHOW_TOPICS_INTERVAL, TOP_WORDS)
    lda.setNumIterations(NUM_ITERATIONS)
    lda.setOptimizeInterval(OPTIMIZE_INTERVAL)

    lda.estimate

    lda.printTopWords(new File(outputDir, "topic-keys.txt"), TOP_WORDS, false)
    lda.printState(new File(outputDir, "state.gz"))
    
    val out = new PrintWriter (new FileWriter (new File(outputDir, "doc-topics.txt")) )

    try { lda.printDocumentTopics(out, 0.0, -1) }
    finally { out.close }
    
    //lda.printTopicWordWeights(new File(outputDir, "topic-word-weights.txt"))
                       // topic word weights is basically just type-topic-counts with added smoothing param beta
    
    lda.printTypeTopicCounts( new File(outputDir, "type-topic-counts.txt")) 

    FileUtil.serializeObject(lda, new File(outputDir, "model.ser"))
  }

  // perform calculations on type topic counts
}
