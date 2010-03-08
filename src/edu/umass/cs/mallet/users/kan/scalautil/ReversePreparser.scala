package edu.umass.cs.mallet.users.kan.scalautil

import java.io.{File,Writer,FileWriter,PrintWriter}
import cc.mallet.types.{FeatureSequence, FeatureVector, Alphabet}
import edu.umass.cs.mallet.users.kan.instanceprovider.PreparsedTextWithMetadataLoader.Reader
import scala.collection.jcl.Conversions._

object ReversePreparser //extends Application
{
  def writeData(writer: PrintWriter, data: FeatureSequence) {
    for (i <- 0 to data.size - 1) {
      writer.print(data.get(i))
      writer.print(" ")
    }
    writer.println
  }

  def writeFeatures(writer: PrintWriter, features: FeatureVector) {
    
    val dictionary = features.getAlphabet
    
    for (i <- 0 to features.numLocations - 1) {
      val idx   = features.indexAtLocation(i)
      val value = features.singleValue(idx)
      
      writer.print(dictionary.lookupObject(idx))
      
      if (value != 1.0) {
        writer.print("=")
        writer.print(value)
      }        
      writer.print(" ")
    }
    writer.println
  }
  
  /*override*/ 
  def main(args: Array[String]) {
    
    Console.print("Reverse engineering ")
    
    //args.foreach((arg) => Console.println(arg))
    
    for (arg <- args)
      Console.println(arg)

    val dir          = new File(args(0))
    val instanceList = new Reader(dir).readCorpus()
    val dataOut	     = new PrintWriter(new FileWriter(new File(dir, "corpus-text.txt"))) 
    
    try {
        for (instance <- instanceList)
          writeData(dataOut, instance.getData.asInstanceOf[FeatureSequence])
    }
    finally
      dataOut close

    val featureOut	 = new PrintWriter(new FileWriter(new File(dir, "corpus-features.txt"))) 
    
    try {
        for (instance <- instanceList) {
          val features = instance.getTarget.asInstanceOf[FeatureVector]
          writeFeatures(featureOut, features)
          println(features.toString(true) )
        }
    }
    finally
      featureOut close
  }
}
