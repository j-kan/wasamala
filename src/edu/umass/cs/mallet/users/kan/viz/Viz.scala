package edu.umass.cs.mallet.users.kan.viz

import processing.core._
import processing.opengl._

import scala.collection.jcl.ArrayList
import scala.util.Sorting
import java.lang.Math

object TwentyNewsgroups {
  
  val NUM_GROUPS:Int = 20
  var NUM_TOPICS:Int = 0

  class Document(id: Int, label: String, topics: Array[Double]) {
    def topic(topic_id: Int) = topics(topic_id)
  }
    
  object Document {
    def readDocTopics(id: Int, label: String, pieces: Array[String]):Document = {
      val ts = pieces.filter { i => !(i.contains('.')) }
      val ps = pieces.filter { i => i.contains('.') }
      
      var topics = new Array[Double](TwentyNewsgroups.NUM_TOPICS)
      
      ts.zip(ps).foreach { z => topics(z._1.toInt) = z._2.toDouble }
      
      new Document(id, label, topics)
    }
  }
    
    
  class Group(lbl: String) {
      
    var docsArrayList = new ArrayList[Document]()
    var topicSums     = new Array[Double](TwentyNewsgroups.NUM_TOPICS)
      
    var indices: Array[Int]      = null
    var docs:    Array[Document] = null
      
    def addDocument(doc: Document) = {
      docsArrayList.add(doc)
        
      for (i <- 0 until topicSums.size)
        topicSums(i) += doc.topic(i)
    }
      
    def label = lbl
    
    private def isUnfinished = docs==null
    def finish: Unit = {
      if (isUnfinished) {
        docs = docsArrayList.toArray
        docsArrayList = null
        indices = Sorting.stableSort[Int]((0 until topicSums.size),((i:Int, j:Int) => (topicSums(j) < topicSums(i))))
        //val topics_total = topicSums.foldLeft(0.0) ((z:Double, a:Double) => z + a)
        //topicSums = topicSums.map (sum => sum/docs.size)
        Console.println(topicSums.map((t:Double) => (t+0.5).asInstanceOf[Int])mkString(" "))
      }
    }
      
    def topicIndicesSorted = { finish; indices }
    def topics             = topicSums	// { if (isUnfinished) finish; topicSums }
    
     
    def topicsL1Normed     = {
      //val topics_l2 = Math.sqrt(group.topics.foldLeft(0.0) ((z:Double, a:Double) => z + a*a))
      val topics_l1 = topicSums.foldLeft(0.0) ((z:Double, a:Double) => Math.max(z, a))
          
      topicSums.map((t:Double) => t/topics_l1)
    }
  }
  
    
  def readDocTopics(file_doc_topics: String) = {
    
    var label = ""
    var groups: Array[Group] = new Array[Group](TwentyNewsgroups.NUM_GROUPS)
    var curGroup = -1
    
    for(l <- io.Source.fromFile(file_doc_topics).getLines.drop(1)) {
      val pieces = l.split("\\s+")
      val i = pieces(0).toInt
      var labelpieces = pieces(1).split('/')
      
      if (labelpieces.size == 3)
        labelpieces = labelpieces.drop(1)
      
      val docpieces = pieces.drop(2)
      
      if (TwentyNewsgroups.NUM_TOPICS == 0)
        TwentyNewsgroups.NUM_TOPICS = docpieces.size/2
      
      val newlabel = labelpieces(0)
      val doclabel = labelpieces(1)
    
      if (label != newlabel) {
        if (curGroup > -1)
          groups(curGroup).finish
        curGroup += 1
        label = newlabel
        Console.println(label)
        groups(curGroup) = new Group(label)
      }
      groups(curGroup).addDocument(Document.readDocTopics(i, doclabel, docpieces))
    }
      
    groups
  }
}

//class V(file_doc_topics: String) {


object Viz {

  val file_doc_topics = "parallel-lda/doc-topics.txt"
    //"1000-dmr-iterations-10-topics/dmr-doc-topics.txt"
  //val file_state    = "/Users/jkan/umass/20news-bydate/state"
  val groups          = TwentyNewsgroups.readDocTopics(file_doc_topics)

  abstract class Viewer extends PApplet {
    
    trait ThingView {
      def draw: Unit

      def drawSelectionRect = {
        noStroke
        fill(0,0,0.2f,0.9f)
        rect(-borderwidth, -ybase, colwidth+borderwidth, height)
      }

      def drawHighlight = {
        //stroke(0,0,0.2f,0.8f)
        stroke(0.5f,0.1f,0.8f,1.0f)
        noFill
        rect(-borderwidth, -ybase, colwidth+borderwidth, height)
      }

      def containsPoint(x:Int, y:Int) = {
        //Console.println("("+x+","+y+")")
        (x >= -borderwidth) &&
        (y >= -ybase) &&
        (x <= colwidth) &&
        (y <= height) 
      }
    }

    
    val thingwidth:  Int = 15 
    val borderwidth: Int = 10
    
    val numthings: Int = initNumThings
    val numblocks: Int = initNumBlocks
    
    val colwidth:    Int = thingwidth * numthings + borderwidth

    val xbase = colwidth + borderwidth * 3
    val ybase = 400
    val ylineheight = 20

    val shrink = (screen.width - xbase - borderwidth).asInstanceOf[Float]/(colwidth * numblocks) 		// 0.25f

    var selection = 0
    var highlight = -1
    var bighighlight = false

    val fontLg: PFont = loadFont("GillSans-Bold-20.vlw") 
    val fontSm: PFont = fontLg //loadFont("GillSans-14.vlw")

    var views: Array[ThingView] = initViews
    
    def initViews: Array[ThingView]
    def initNumThings: Int
    def initNumBlocks: Int
    
    override def setup() = {
      //size(TwentyNewsgroups.NUM_GROUPS*colwidth+borderwidth, 1024)
      size(screen.width, screen.height, PConstants.P3D)
      //noLoop
      noStroke

      colorMode(PConstants.HSB, 1.0F)
    }
  
    override def draw = {
      
      def drawBg = {
        noStroke
        background(0F, 0F, 0.1F, 1F)

        //val selection = mouseX * TwentyNewsgroups.NUM_GROUPS / width

        pushMatrix
            translate(xbase, ybase)
            scale(shrink)

            for ((view, gi) <- views.zipWithIndex) {
              if (gi == selection) 
                view.drawSelectionRect
              if (gi == highlight) 
                view.drawHighlight
              view.draw
              translate(colwidth, 0)
            }
        popMatrix
      }

      drawBg
      pushMatrix
          translate(borderwidth, ybase)

          val selected = views(selection)  
          selected.drawSelectionRect
          if (bighighlight)
            selected.drawHighlight
          selected.draw
      popMatrix
    }

    override def keyPressed() = {
      if (keyCode == PConstants.LEFT) 
        selection = Math.max(0, selection-1)
      else if (keyCode == PConstants.RIGHT)
        selection = Math.min(views.size-1, selection+1)
    }

    def findSelection = {

      if (views(selection).containsPoint(mouseX, mouseY))
        -2
      else {
          0.until(views.size).findIndexOf { i =>
              val x = (mouseX - i*colwidth*shrink - xbase)/shrink
              val y = (mouseY - ybase)/shrink

              //Console.print(i+":")
              views(i).containsPoint(x.asInstanceOf[Int], y.asInstanceOf[Int])
          }
      }
    }

    override def mouseClicked = {
      val sel = findSelection

      if (sel == -2)
        bighighlight = !bighighlight

      if (sel > -1)
        selection = sel

    }

    override def mouseMoved = {
      highlight = findSelection
    }
  
  }
  
  
  object ViewByNewsgroup extends Viewer {

    class GroupView(group:TwentyNewsgroups.Group) extends ThingView {

      def draw() = {
        def drawGroupHist = {

            val y = -ylineheight-10 
            val significant = group.topicIndicesSorted //.filter { i => group.topics(i) > 10 } 
            // group.topicIndicesSorted.subArray(0,3)

            for (i <- significant) {
              val t:  Float = group.topics(i).asInstanceOf[Float]
              val br: Float = group.topicsL1Normed(i).asInstanceOf[Float]
              val x:  Int   = i * thingwidth

              noStroke
              fill(i/20.0F, 1F, 1F, br)
              rect(x, y-t, thingwidth, t)
              fill(0, 0, 1.0F-br, br)
              textFont(fontSm)
              textSize(12);
              textAlign(PConstants.CENTER)
              text(i.toString, x, y-16, thingwidth, ylineheight)
            }
        }

        def drawCaption = {
            fill(0, 0, 1, 1)
            textFont(fontLg)
            textAlign(PConstants.LEFT)
            textSize(16);
            text(group.label, 0, -ylineheight, colwidth, ylineheight)
      //      text(group.label + " " + significant.mkString(" "), xbase, y, colwidth, ylineheight)
        }

        def drawDocTopics(doc:TwentyNewsgroups.Document, y:Int) = {
          for (i <- 0 until TwentyNewsgroups.NUM_TOPICS) {
            val br: Float = doc.topic(i).asInstanceOf[Float]
            val x : Int   = i * thingwidth

            fill(i/20.0F, 1F, br)
            rect(x, y, thingwidth, 1)
  //          stroke(i/20.0F, 1F, br)
  //          line(x+thingwidth, y, x, y)
          }
        }

        drawGroupHist
        drawCaption
        for ((doc,y) <- group.docs.zipWithIndex) drawDocTopics(doc, y)
      }
    }

    def initViews: Array[ThingView] = groups.map[ThingView] ((group:TwentyNewsgroups.Group) => new GroupView(group))
    def initNumThings = TwentyNewsgroups.NUM_TOPICS
    def initNumBlocks = TwentyNewsgroups.NUM_GROUPS
  }
  
  
  object ViewByTopic extends Viewer {

    class TopicView(topic:Int) extends ThingView {
      
      val topicGroups = groups.map[Double] {(g:TwentyNewsgroups.Group) => g.topics(topic)}
      
      val topicGroupsL1Normed     = {
        //val topics_l2 = Math.sqrt(group.topics.foldLeft(0.0) ((z:Double, a:Double) => z + a*a))
        val groups_l1 = topicGroups.foldLeft(0.0) ((z:Double, a:Double) => Math.max(z, a))

        topicGroups.map((t:Double) => t/groups_l1)
      }
      
      def draw: Unit = {
        def drawGroupHist = {

            val y = -ylineheight-10 

            for (g <- 0 until TwentyNewsgroups.NUM_GROUPS) {
              val t:  Float = topicGroups(g).asInstanceOf[Float]
              val br: Float = topicGroupsL1Normed(g).asInstanceOf[Float]
              val x:  Int   = g * thingwidth

              noStroke
              pushMatrix
                translate(x, y)
                fill(g/20.0F, 1F, 1F, br)
                rect(0, -t, thingwidth, t)
                fill(0, 0, 1.0F-br, br)
                textFont(fontSm)
                textSize(12);
                textAlign(PConstants.LEFT)
                rotate(-PConstants.HALF_PI)
                text(groups(g).label, 3, 2, 500, thingwidth)
              popMatrix
            }
        }

        def drawCaption = {
            fill(0, 0, 1, 1)
            textFont(fontLg)
            textAlign(PConstants.LEFT)
            textSize(16);
            text("topic " + topic.toString, 0, -ylineheight, colwidth, ylineheight)
      //      text(group.label + " " + significant.mkString(" "), xbase, y, colwidth, ylineheight)
        }

        def drawDocGroups(doc:TwentyNewsgroups.Document, y:Int) = {
          if (doc.topic(topic).asInstanceOf[Float] > 0.02) {
              for (i <- 0 until TwentyNewsgroups.NUM_TOPICS) {
                val br: Float = doc.topic(i).asInstanceOf[Float]
                val x : Int   = i * thingwidth
    
                fill(i/20.0F, 1F, br)
                rect(x, y, thingwidth, 1)
      //          stroke(i/20.0F, 1F, br)
      //          line(x+thingwidth, y, x, y)
              }
          }
        }

        drawGroupHist
        drawCaption
        //for ((doc,y) <- group.docs.zipWithIndex) drawDocTopics(doc, y)
      }
    }

    def initViews: Array[ThingView] = {
      0.until(TwentyNewsgroups.NUM_TOPICS).toArray.map[ThingView] ((topic:Int) => new TopicView(topic))
    }
    def initNumThings = TwentyNewsgroups.NUM_GROUPS
    def initNumBlocks = TwentyNewsgroups.NUM_TOPICS
  }
  
  
  def main(args : Array[String]) : Unit = {
    val frame = new javax.swing.JFrame("20Newsgroups")
    //    val scroller = new javax.swing.JScrollPane(Viz)
    //    scroller.setSize(1280, 800)
    //val viz = new Viz("doc-topics.txt")                        //"dmr-doc-topics.txt"
    //val viz = new V("1000-lda-iterations-10-topics/doc-topics.txt")
 
    if (args.contains("ViewByTopic"))
    {
       frame.getContentPane.add(ViewByTopic)
       ViewByTopic.init
    }
    else
    {
       frame.getContentPane.add(ViewByNewsgroup)
       ViewByNewsgroup.init
    }
    frame.pack
    frame setVisible true
  }
  
}



//    for(l <- io.Source.fromFile(file_doc_topics).getLines) {
//      if (l(0) != '#') {
//        var pieces = l.split("\\s+")
//        val i = pieces(0).toInt
//        val newlabel = pieces(1).split('/')(1)
//        
//        if (label != newlabel) {
//          label = newlabel
//          xbase += colwidth
//          y = ybase
//          println(label)
//        }
//        else {
//          y += 1
//        }
//        if (xbase < width && y < height) {
//            pieces = pieces.drop(2)
//            val topics = pieces.filter { i => !(i.contains('.')) }
//            val ps     = pieces.filter { i => i.contains('.') }
//            
//            topics.zip(ps).foreach { z => 
//              val r : Int = (z._2.toDouble * 256.0).toInt
//              val g : Int = r
//              val b : Int = r
//              
//              val topic = z._1.toInt
//              val x : Int = topic * thingwidth + xbase
//              
//              stroke(topic*256/20,255,r)
//              line(x-thingwidth,y,x,y)
//              
////              fill(topic*256/20,255,r)
////              rect(x-thingwidth,y,thingwidth,1)
//            }
//        }
//      }
//    }
    
//    for (i <- 0 until height) {
//      val r = random(255)
//      val g = random(255)
//      val b = random(255)
//      
//      stroke(r,g,b)
//      
//      //val xpos = random(width)
//      line(r, i, r+g, i)
//    }




  
  
  

  
//    var x = xbase
//    var y = ybase
//    val scaledcolwidth = colwidth * shrink
//    val ytop = ybase - ybase * shrink
//    val ybottom = height * shrink - ytop
//        (mouseX >= xbase + i*scaledcolwidth) && (mouseX <= xbase + (i+1)*scaledcolwidth) && (mouseY >= ytop) && (mouseY <= ybottom)
    
    //Console.println("("+mouseX+","+mouseY+")")
