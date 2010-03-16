package edu.umass.cs.mallet.users.kan.viz

import processing.core._
import java.lang.Math
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

  val fontLg: PFont = loadFont("GillSans-14.vlw") 
  val ylineheight = 18
  val colwidth    = 100
  
  override def setup() = {
    size(screen.width-100, screen.height-100, PConstants.P3D)
    noLoop
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

  override def draw() = {

    background(0, 0, 20, 100)

    stroke(0, 0, 80, 60)
    fill(0, 0, 80, 20)
    textFont(fontLg)
    textAlign(PConstants.LEFT)
    textSize(14);
   
    withMatrix {
      translate(10, 10)
      for ((a, (x,y)) <- lda.getAlphabet.elements zip line_xy(width-20, height-20, colwidth, ylineheight)) {
        //println((x,y) + a.toString)
        text(a.toString, x, y, colwidth, ylineheight)
      }
    }
   
   val hue = random(100)
   val sat = random(50)
   val bri = random(40)
   
   println(List(hue, sat, bri).map(_.toString).mkString(":"))
   
   fill(hue, sat, bri, 30)
   noStroke
   
   rect(lda.getNumTopics, lda.getNumTopics, lda.numIterations, lda.numIterations)
   
   // withMatrix {
   //   translate(25, 25)
   //   fill(hue, sat, 75, 80)
   //   
   //   for ((a, (x,y)) <- lda.getTopicAlphabet.elements zip line_xy(width-50, height-50, colwidth*3, ylineheight)) {
   //     //println((x,y) + a.toString)
   //     text(a.toString, x, y, colwidth*3, ylineheight)
   //   }
   // }
   
    val topicAssignments = lda.getData
   
    withMatrix {
      translate(10, 10)
      fill(hue, sat, 75, 80)
     
      for ((a, (x,y)) <- topicAssignments.iterator zip line_xy(width-10, height-20, width-10, ylineheight)) {
        val name = a.instance.getName.toString
        val features = a.topicSequence.getFeatures

        text(name, x, y, width-10, ylineheight)
       
        println((x,y) + name + ' ' + a.topicSequence.toString)
       
        for ((f, i) <- features.toList zip {0 to features.length}.toList) {
          rect(x+150 + i*10 , y, f*10, ylineheight)
        }
      }
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