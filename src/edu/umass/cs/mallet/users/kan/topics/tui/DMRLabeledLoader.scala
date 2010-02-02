package edu.umass.cs.mallet.users.kan.topics.tui

import java.io.{File,Writer,FileWriter,PrintWriter,BufferedWriter, BufferedOutputStream, ObjectOutputStream, FileOutputStream}

import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList}
import cc.mallet.topics.tui.DMRLoader
import cc.mallet.topics.DMRTopicModel
import cc.mallet.pipe.iterator.FileIterator
import cc.mallet.pipe._

import edu.umass.cs.mallet.users.kan.scalautil.FileUtil


// mallet import-dir 
//	--input * 
//  --keep-sequence
// 	--remove-stopwords 
//  --skip-header 
//  --output 
//  ../train.ser


object DMRLabeledLoader {

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
  
  //new File("20news-bydate-train")
  def main(args: Array[String]) {
    val instances     = DMRLabeledLoader.load(new File(args(0)))
    val numTopics     = if (args.size > 1) args(1).toInt else 20
    val numIterations = if (args.size > 2) args(2).toInt else 1000
    val dirname       = String.format("%d-dmr-iterations-%d-topics", numIterations.asInstanceOf[Object], numTopics.asInstanceOf[Object])     
    val dmr = new DMRTopicModel(numTopics)
    
    dmr.addInstances(instances)
	dmr.setTopicDisplay(50, 20)
    dmr.setNumIterations(numIterations)
    dmr.setOptimizeInterval(50)
	dmr.setRandomSeed(90210);
 
    dmr.estimate(numIterations)

    dmr.printState(new File("dmr-state.gz"))
    dmr.printDocumentTopics(new File("dmr-doc-topics.txt"))
    dmr.printTopWords(new File("dmr-topic-keys.txt"), 20, false)

    FileUtil.serializeObject(dmr, "dmr-model.ser")
    
    //dmr.setModelOutput(outputModelInterval.value, outputModelFilename.value);
    //                lda.setSaveState(outputStateInterval.value, stateFile.value);
    //				try { lda.printDocumentTopics(out, docTopicsThreshold.value, docTopicsMax.value); }
  }

}



//			if (topicReportXMLFile.value != null) {
//				PrintWriter out = new PrintWriter(topicReportXMLFile.value);
//				lda.topicXMLReport(out, topWords.value);
//				out.close();
//			}
//		if (outputModelFilename.value != null) {
//			assert (topicModel != null);
//			try {
//				ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream (outputModelFilename.value));
//				oos.writeObject (topicModel);
//				oos.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new IllegalArgumentException ("Couldn't write topic model to filename "+outputModelFilename.value);
//			}
//			if (topicModel instanceof LDA)
//				System.out.println("Model written.  Vocabulary size = "+((LDA)topicModel).getInstanceList().getDataAlphabet().size());
//			else
//				System.out.println("Model written.");  // TODO: support this for TNG also.
//		}
//    
