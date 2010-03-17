package edu.umass.cs.mallet.users.kan.viz

import processing.core._
import java.lang.Math
import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList, Instance}
import edu.umass.cs.mallet.users.kan.topics._
import edu.umass.cs.mallet.users.kan.topics.runner.MalletRunner

trait P5 extends PApplet {

  def random = Math.random.toFloat
  
  def withMatrix(block: => Unit) = {
    pushMatrix
    block
    popMatrix
  }
}

object TopicModelViz extends P5 with MalletRunner {

  val basename = "rhinoplastfm"
  val numIterations = 1000
  val numTopics = 16
  val alpha    = 50.0/numTopics
  val beta     = 0.01
  
  setOutputDirectory("%s-%d-iterations-%d-topics-%f-alpha-%f-beta".format(basename, numIterations, numTopics, alpha, beta))
  
  val lda = LDAHyperExtended.read("lda-model.ser")
  val numDocs = lda.numDocs
  val topicAssignments = lda.topicAssignments

  val fontLg: PFont = loadFont("GillSans-Bold-20.vlw") 
  val ylineheight = 18
  val colwidth    = 100
  
  val namewidth = 300
  
  var linesperpage = 0
  var startindex = 0
  var drawn = false
  
  override def setup() = {
    size(screen.width-100, screen.height-100, PConstants.P3D)
    //noLoop
    noStroke

    colorMode(PConstants.HSB, 100)
    
    println(lda.getNumTopics)
  }

  def line_xy(panewidth:Int, paneheight:Int, colwidth:Int, lineheight:Int) = new Iterator[(Int,Int)] { 

    private var x = 0
    private var y = 0

    def hasNext = x+colwidth <= panewidth 

    def next = { 
      val result = (x,y)
      y += lineheight
      if (y > paneheight-lineheight) {
        y = 0
        x += colwidth
      }
      result
    } 
  } 
  
  override def keyPressed() = {
    if (keyCode == PConstants.DOWN) {
      drawn = false
      startindex = (startindex + linesperpage) min (numDocs - (numDocs % linesperpage))
      println(startindex)
    }
    else if (keyCode == PConstants.UP) {
      drawn = false
      startindex = (startindex - linesperpage) max 0
      println(startindex)
    }
  }
  
  def drawAlphabet = {
    withMatrix {
      translate(10, 10)
      for ((a, (x,y)) <- lda.getAlphabet.elements zip line_xy(width-20, height-20, colwidth, ylineheight)) {
        //println((x,y) + a.toString)
        text(a.toString, x, y, colwidth, ylineheight)
      }
    }
  }

  override def draw: Unit = {

    if (drawn) return
      
    background(0, 0, 20, 100)

    stroke(0, 0, 80, 60)
    fill(0, 0, 80, 20)
    textFont(fontLg)
    textAlign(PConstants.LEFT)
    textSize(14);
   
    // drawAlphabet
    
   // val hue = random(100)
   // val sat = random(50)
   // val bri = random(40)
   
   //println(List(hue, sat, bri).map(_.toString).mkString(":"))
   
   // fill(hue, sat, bri, 30)
   // noStroke
   // 
   // rect(lda.getNumTopics, lda.getNumTopics, lda.numIterations, lda.numIterations)
   
   // withMatrix {
   //   translate(25, 25)
   //   fill(hue, sat, 75, 80)
   //   
   //   for ((a, (x,y)) <- lda.getTopicAlphabet.elements zip line_xy(width-50, height-50, colwidth*3, ylineheight)) {
   //     //println((x,y) + a.toString)
   //     text(a.toString, x, y, colwidth*3, ylineheight)
   //   }
   // }
   

    val topicwidth = ((width-namewidth)/numTopics).toInt
   
    withMatrix {
      translate(10, 10)
      
      var pagesize = 0
      
      for ((a, (x,y)) <- topicAssignments drop startindex zip line_xy(width-10, height-20, width-10, ylineheight)) {
        
        pagesize += 1
        
        val hue = startindex*100/numDocs
        
        // val name = a.instance.getName.toString
        // val tf   = a.instance.getData.asInstanceOf[FeatureSequence]
        // val content  = tf.getFeatures.map( index => tf.getAlphabet.lookupObject(index).toString ).deepMkString(" ")
        // val features = a.topicSequence.getFeatures
        // 
        // var counts = Array.make[Float](numTopics, 0F)
        // for (f <- features)
        //   counts(f) += 1

        fill(hue, 50, 75, 80)
        text(a.name, x, y, width-10, ylineheight)
        fill(hue, 20, 75, 20)
        text(a.content, x+namewidth, y, width-namewidth, ylineheight)
       
        // println((x,y) + " " + a.name + " " + a.p_topics.deepMkString(" "))

        withMatrix {
          translate(x+namewidth, -2)
          for ((p, i) <- a.p_topics zipWithIndex) {
            stroke(i*100/numTopics, 75, 75, 20)
            fill(i*100/numTopics, 75, 75, 80)
            rect(i*topicwidth, y, p.toFloat*topicwidth, ylineheight-1)
          }
        }
      }
      drawn = true
      if (linesperpage == 0) { linesperpage = pagesize }
    }
  }

  def main(args : Array[String]) : Unit = {
    val frame = new javax.swing.JFrame("TopicModelViz")
    frame.getContentPane.add(TopicModelViz)
    TopicModelViz.init
    frame.pack
    frame setVisible true
  }
}