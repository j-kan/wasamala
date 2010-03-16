//package edu.umass.cs.mallet.users.kan.viz
//
//import processing.core._
//
//import scala.collection.jcl.ArrayList
//import scala.util.Sorting
//import java.lang.Math
//
//
//object Viz3D extends PApplet {
//
//  val thingwidth:  Int = 15 
//  val borderwidth: Int = 10
//  val colwidth:    Int = thingwidth * TwentyNewsgroups.NUM_TOPICS + borderwidth
//  
//  val groups = load_docs
//  var selection = 0
//
//  //var doc_topics: Array[(Double, Double, Double)] = new Array
//  
//  def load_docs() = {
//    val file_doc_topics     = "/Users/jkan/umass/20news-bydate/doc-topics.txt"
//    //val file_state          = "/Users/jkan/umass/20news-bydate/state"
//    
//    TwentyNewsgroups.readDocTopics(file_doc_topics)
//  }
//  
//  
//  override def setup() = {
//    //size(TwentyNewsgroups.NUM_GROUPS*colwidth+borderwidth, 1024)
//    size(screen.width, screen.height, PConstants.P3D)
//    //noLoop
//    noStroke
//    
//    val font: PFont = loadFont("GillSans-14.vlw"); 
//    textFont(font); 
//
//    colorMode(PConstants.HSB, 1.0F)
//  }
//
//  
//  override def keyPressed() = {
//    if (keyCode == PConstants.LEFT) 
//      selection = Math.max(0, selection-1)
//    else if (keyCode == PConstants.RIGHT)
//      selection = Math.min(TwentyNewsgroups.NUM_GROUPS-1, selection+1)
//    
////      if (key == PConstants.CODED) {
//  }
//
//  override def draw() = {      
//      
//    val xbase = -1500
//    val ybase = 320
//    val zbase = 3000
//    val ylineheight = 20
//
//    noStroke
//    background(0F, 0F, 0.1F, 1F)
//
//    //val selection = mouseX * TwentyNewsgroups.NUM_GROUPS / width
//  
//    pushMatrix
//    
//    var x = xbase
//    
//    //scale(0.25f)
//    translate(0,0,-zbase)
//    //rotateY(PApplet.radians(-60))
//    
//    for ((group, gi) <- groups.zipWithIndex) {
//
//      drawGroup(group, x, ybase, ylineheight)
//      if (gi == selection) {
//        noStroke
//        fill(0,0,0.2f,0.2f)
//        rect(x, 0, colwidth, 1200)
//      }
//      //zbase -= 40
//      x += colwidth
//    }
//    popMatrix
//    
//    pushMatrix
//    drawGroup(groups(selection), borderwidth, ybase, ylineheight)
//    popMatrix
//  }
//
//  def drawGroup(group:TwentyNewsgroups.Group, xbase:Int, ybase:Int, ylineheight: Int)
//  {
//      var y = ybase
//      val significant = group.topicIndicesSorted //.filter { i => group.topics(i) > 10 } 
//      // group.topicIndicesSorted.subArray(0,3)
//      
//      val topics_l1 = group.topics.foldLeft(0.0) ((z:Double, a:Double) => Math.max(z, a))
//      //val topics_l2 = Math.sqrt(group.topics.foldLeft(0.0) ((z:Double, a:Double) => z + a*a))
//      
//      for (i <- significant) {
//        val t:  Float = group.topics(i).asInstanceOf[Float]
//        val br: Float = group.topicsL1Normed(i).asInstanceOf[Float]
//        val x:  Int   = i * thingwidth + xbase
//        
//        noStroke
//        fill(i/20.0F, 1F, 1F, br)
//        rect(x, y-t, thingwidth, t)
//        fill(0, 0, 1.0F-br, br)
//        textAlign(PConstants.CENTER)
//        text(i.toString, x, y-16, thingwidth, ylineheight)
//      }
//      y += 10
//      
//      fill(0, 0, 1, 1)
//      textAlign(PConstants.LEFT)
//      text(group.label, xbase, y, colwidth, ylineheight)
////      text(group.label + " " + significant.mkString(" "), xbase, y, colwidth, ylineheight)
//      y += ylineheight
//      
//      for (doc <- group.docs) {
//        
//        for (i <- 0 until TwentyNewsgroups.NUM_TOPICS) {
//          val br: Float = doc.topic(i).asInstanceOf[Float]
//          val x : Int   = i * thingwidth + xbase
//          
//          fill(i/20.0F, 1F, br)
//          rect(x, y, thingwidth, 1)
////          stroke(i/20.0F, 1F, br)
////          line(x+thingwidth, y, x, y)
//        }
//        y += 1
//      }
//  }
//  
//  
//  def main(args : Array[String]) : Unit = {
//    val frame = new javax.swing.JFrame("Viz")
////    val scroller = new javax.swing.JScrollPane(Viz)
////    //val viz = new Viz
////    scroller.setSize(1280, 800)
//    frame.getContentPane.add(Viz3D)
//    Viz3D.init
//    
//    frame.pack
//    frame setVisible true
//  }
//}
//
//
//
////    for(l <- io.Source.fromFile(file_doc_topics).getLines) {
////      if (l(0) != '#') {
////        var pieces = l.split("\\s+")
////        val i = pieces(0).toInt
////        val newlabel = pieces(1).split('/')(1)
////        
////        if (label != newlabel) {
////          label = newlabel
////          xbase += colwidth
////          y = ybase
////          println(label)
////        }
////        else {
////          y += 1
////        }
////        if (xbase < width && y < height) {
////            pieces = pieces.drop(2)
////            val topics = pieces.filter { i => !(i.contains('.')) }
////            val ps     = pieces.filter { i => i.contains('.') }
////            
////            topics.zip(ps).foreach { z => 
////              val r : Int = (z._2.toDouble * 256.0).toInt
////              val g : Int = r
////              val b : Int = r
////              
////              val topic = z._1.toInt
////              val x : Int = topic * thingwidth + xbase
////              
////              stroke(topic*256/20,255,r)
////              line(x-thingwidth,y,x,y)
////              
//////              fill(topic*256/20,255,r)
//////              rect(x-thingwidth,y,thingwidth,1)
////            }
////        }
////      }
////    }
//    
////    for (i <- 0 until height) {
////      val r = random(255)
////      val g = random(255)
////      val b = random(255)
////      
////      stroke(r,g,b)
////      
////      //val xpos = random(width)
////      line(r, i, r+g, i)
////    }
