package viscera

import processing.core._
import processing.core.PConstants._
//import processing.opengl._

import scala.Math
import scala.collection.jcl.ArrayList
import scala.util.Sorting


trait P5 extends PApplet {

  def withMatrix(block: => Unit) = {
    pushMatrix
    block
    popMatrix
  }
}


object Tripelet extends P5 {

  def random(x:Int):Float = { Math.random * x toFloat }
  def square(x:Float) = x*x
  def ring(x:Int) = square(x toFloat) - square(random(x))
  def center(r:Int) = square(random(r)) * (random(1) - 0.5f) * 2

  var count = 0
  var useColor = false
  var useFill  = true
  var useRect  = false
  var showText = false
  var started  = false
  
//  var addMore  = true
//  var jitterSize = 1F
  var splay  = 20
  var lastKey = ' ';

  override def mousePressed:Unit  = started = !started
  //override def mouseReleased:Unit = useColor = false
  //override def mouseMoved:Unit    = started = true
      
  override def keyReleased:Unit = {
    key match {
      case 's' => save("Tripelet-" + count + ".png")
      case 'c' => useColor = !useColor
      case 'f' => useFill = !useFill
      case 'r' => useRect = !useRect
      case 't' => showText = !showText
      case ' ' => started = !started
      case UP   => splay += 1
      case DOWN => splay = Math.max(1, splay-1)
      case DELETE => background(0)
      case BACKSPACE => background(0)
        
      case _   => {}
    }
    
    lastKey = key
  }
  
  
//  override def setup = {
//      size(screen.width, screen.height, P2D)
//      //noLoop
//      smooth
//
//      colorMode(HSB, 1.0F)
//      background(0)
//  }
//      if (useFill) {
//        noStroke
//        if (useColor)
//          fill(hue, sat, brightness, alpha)
//        else
//          fill(0, 0, hue/10, alpha)
//      }
//      else {
//        noFill
//        strokeWeight(weight)
//        if (useColor)
//          stroke(hue, sat, brightness, alpha)
//        else
//          stroke(0, 0, hue/10, alpha)
//      }
            


  override def setup = {
    size(screen.width, screen.height)
    colorMode(HSB, 360, 100, 100, 100)
    smooth
    background(0)
    
    textFont(createFont("Helvetica", 12))
  }
  
  def setFill()  : (Float, Float, Float, Float) => Unit = { noStroke; fill } 
  def setStroke(): (Float, Float, Float, Float) => Unit = { noFill; strokeWeight(random(10)); stroke } 

  def rect(   w: Int, h: Int): (Int, Int) => Unit = { def f(x:Int, y:Int):Unit = { rectMode(CENTER);    rect(x,y,w,h) };    f }
  def ellipse(w: Int, h: Int): (Int, Int) => Unit = { def f(x:Int, y:Int):Unit = { ellipseMode(CENTER); ellipse(x,y,w,h) }; f }
  
//  def doRect(   w: Int, h: Int):Unit = { rectMode(CENTER);    rect(0,0,w,h) }
//  def doEllipse(w: Int, h: Int):Unit = { ellipseMode(CENTER); ellipse(0,0,w,h) }

  
  override def draw = {

    //val alpha:Float = random(50) + 25 //mouseY * 50 / screen.height
    
    val color_hsba = ( random(360), 100f, 100f, random(50) + 25 )
    val grays_hsba = ( 0f, 0f, random(75), random(50) + 10 )
    
    val angle  = random(2 * PI)
    val radius = ring(splay)

    val xbase = (radius * Math.cos(angle)) toInt        // square(random(splay)) + mouseX - square(splay)/2
    val ybase = (radius * Math.sin(angle)) toInt        // square(random(splay)) + mouseY - square(splay)/2
    
    val shapewidth  = ring(splay-3) + 50 //random(splay*10).toInt + 40
    val shapeheight = ring(splay-3) + 50 //random(splay*10).toInt + 40 // - shapewidth //square(splay)/2-width   // random(400-width)
    
/*    
    val hue   = random(360) // mouseX.asInstanceOf[Float] / screen.width
    val sat   = 100
    val alpha = mouseY * 50 / screen.height
    val brightness = 100    //random(0.5F) + 0.5F // mouseX.asInstanceOf[Float] / screen.width
    val weight = random(10)
*/    
    val locX = mouseX + xbase
    val locY = mouseY + ybase
    val rot  = mouseY.toFloat / 40F
    
    if (started) {
      
      count += 1
      
//      val clearfn : () => Unit                           = if (useFill) { noStroke } else { noFill }
      //val colorizer : (() => Unit, (Float, Float, Float, Float) => Unit)  => Unit = if (useColor) { colorize } else { greyify }
      
      val shapefn : (Int, Int) => ((Int, Int) => Unit)   = if (useRect)  { rect }       else { ellipse }
      val fillfn  : (Float, Float, Float, Float) => Unit = if (useFill)  { setFill() }  else { setStroke() }
      val hsba                                           = if (useColor) { color_hsba } else { grays_hsba }
      
      
      val drawshape = shapefn(shapewidth toInt, shapeheight toInt)
        
      withMatrix {
        translate(locX, locY)
        rotate(rot)

        fillfn(0,0,0,10)
        for (i <- List.range(1,20))
            drawshape(center(Math.sqrt(shapewidth-40 toInt)).toInt, center(Math.sqrt(shapeheight-40 toInt)).toInt)
        
        fillfn(hsba._1,hsba._2,hsba._3,hsba._4)
        drawshape(0,0)

        fillfn(0,0,0,3)
        for (i <- List.range(1,15))
            drawshape(center(Math.sqrt(shapewidth-40 toInt)).toInt, center(Math.sqrt(shapeheight-40 toInt)).toInt)
        
        if (showText) {
            fill(100,100,100,100)
            text(" " + lastKey.toInt + " (" + xbase + "," + ybase + ")", 0, 0)
        }
      }
      
//      fill(100,0,100,100)
//      rect(0,height-20, 100,20)
    }
    
//      if (useColor) {
//      noFill
//      strokeWeight(random(20))
//      stroke(hue, 100, brightness, alpha) // * 0.7F + 0.1F)
//    }
//    else {
//      noStroke        
//      fill(0, 0, hue * 0.1F, alpha) // * 0.5F + 0.1F)
//    }
//    withMatrix {
//      translate(xbase, ybase)
//      rotate(mouseY/50)
//      ellipseMode(CENTER)
//      ellipse(0,0,width,height)
////      rectMode(CENTER)
////      rect(0,0,width,height)
//    }
  }
  
  
  def main(args : Array[String]) : Unit = {
    val frame = new javax.swing.JFrame("Tripelet")
    
    frame.getContentPane.add(Tripelet)
    Tripelet.init
    
    frame.pack
    frame setVisible true
  }
}
