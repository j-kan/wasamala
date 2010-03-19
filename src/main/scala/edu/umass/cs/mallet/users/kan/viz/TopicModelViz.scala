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
  
  implicit def doubleToFloat(d: Double) = d.toFloat
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

  val windowWidth  = screen.width-100
  val windowHeight = screen.height-100

  val yLineHeight = 18
  val colWidth    = 100
  val nameWidth   = 300
  val topicWidth  = ((windowWidth-nameWidth)/numTopics).toInt

  val fontLg: PFont = loadFont("GillSans-Bold-20.vlw") 
  
  var linesPerPage    = 0
  var linesPerPageTmp = 0
  var startIndex      = 0
  var drawn           = false
  
  override def setup() = {
    size(windowWidth, windowHeight, PConstants.P2D)
    //noLoop
    noStroke

    frameRate(5)
    colorMode(PConstants.HSB, 100)
    //println(lda.getNumTopics)
  }

  override def keyPressed() = {
    if (keyCode == PConstants.DOWN) {
      drawn = false
      startIndex = (startIndex + linesPerPage) min (numDocs - (numDocs % linesPerPage))
      println((startIndex, linesPerPage, numDocs))
    }
    else if (keyCode == PConstants.UP) {
      drawn = false
      startIndex = (startIndex - linesPerPage) max 0
      println((startIndex, linesPerPage, numDocs))
    }
  }
  
  def line_xy(panewidth:Int, paneheight:Int, colWidth:Int, lineheight:Int) = new Iterator[(Int,Int)] { 

    private var x = 0
    private var y = 0

    def hasNext = x+colWidth <= panewidth 

    def next = { 
      val result = (x,y)
      y += lineheight
      if (y > paneheight-lineheight) {
        y = 0
        x += colWidth
      }
      result
    } 
  } 
  
  
  def drawAlphabet = {
    for ((a, (x,y)) <- lda.getAlphabet.elements zip line_xy(width-20, height-20, colWidth, yLineHeight)) {
      //println((x,y) + a.toString)
      text(a.toString, x, y, colWidth, yLineHeight)
    }
  }

  def drawDocTopics = {
    for ((a, (x,y)) <- lda.topicAssignments drop startIndex zip line_xy(width-10, height-20, width-10, yLineHeight)) {
      
      val hue = startIndex*100/numDocs

      fill(hue, 50, 75, 80)
      text(a.name, x, y, width-10, yLineHeight)
      fill(hue, 20, 75, 20)
      text(a.content, x+nameWidth, y, width-nameWidth, yLineHeight)
     
      // println((x,y) + " " + a.name + " " + a.p_topics.deepMkString(" "))

      withMatrix {
        translate(x+nameWidth, -2)
        for ((p, i) <- a.p_topics zipWithIndex) {
          stroke(i*100/numTopics, 75, 75, 20)
          fill(i*100/numTopics, 75, 75, 80)
          rect(i*topicWidth, y, p*topicWidth, yLineHeight-1)
        }
      }
      if (linesPerPage == 0) { linesPerPageTmp += 1 }
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
      
    withMatrix {
      translate(10, 10)
      
      drawDocTopics
      //drawAlphabet
      drawn = true
      if (linesPerPage == 0) { linesPerPage = linesPerPageTmp }
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