package edu.umass.cs.mallet.users.kan.topics.tui

object DjvuParagraphPerLine {

  import java.io.File
  import scala.xml.{XML, Node, NodeSeq}

  import cc.mallet.pipe._;
  import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList, Instance}
  import cc.mallet.topics.LDAHyper

  import edu.umass.cs.mallet.users.kan.scalautil.FileUtil

//  val book = XML.loadFile(new File("/Users/jkan/umass/internetarchive/turesalicesadven00carrrich_djvu.xml"))
  
  def main(args:Array[String]):Unit = {
    
    val inputfile = new File(args(0))
    val filename  = inputfile.getName
    
    val numTopics = 10
    val numIterations = 500
    
    val instances = new InstanceList(
                      new SerialPipes( Array[Pipe]( //new SaveDataInSource,
                                                    new Target2Label,
                                                    new CharSequence2TokenSequence,
                                                    new TokenSequenceLowercase,
                                                    new TokenSequenceRemoveNonAlpha,
                                                    new TokenSequenceRemoveStopwords(false, false),
                                                    new TokenSequence2FeatureSequence,
                                                    new PrintInputAndTarget
                                                  )))
    
    val paragraphs = XML.loadFile(inputfile) \ "BODY" \ "OBJECT" //\ "HIDDENTEXT" \ "PAGECOLUMN" \ "REGION" \ "PARAGRAPH"
    
    for (p <- paragraphs) {
        //val text = p.child.flatMap((x:Node)=>x.child).mkString(" ")
        
        val words = p \\ "WORD"
        val line = words.map(_.text).mkString(" ")
        
        Console.println(line)
        
      	instances.addThruPipe(new Instance(line, filename, filename, filename))
    }
    FileUtil.serializeObject(instances, "djvu-instances.ser")

/*    val lda = new LDAHyper(numTopics)
    
    lda.addInstances(instances)
  lda.setTopicDisplay(50, 20)
    lda.setNumIterations(numIterations)
    lda.setOptimizeInterval(50)
  lda.setRandomSeed(90210);
 
    lda.estimate(numIterations)

    lda.printState(new File("lda-state.gz"))
    lda.printDocumentTopics(new File("lda-doc-topics.txt"))
    lda.printTopWords(new File("lda-topic-keys.txt"), 20, false)

    serializeObject(lda, "lda-model.ser")
*/    //instances
  }
}