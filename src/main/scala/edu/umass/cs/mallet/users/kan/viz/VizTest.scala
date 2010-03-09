package edu.umass.cs.mallet.users.kan.viz

import processing.core._
import java.lang.Math

object VizTest extends PApplet {

  override def setup() = {
    size(800, 600, PConstants.P3D)
    noLoop
    noStroke
  }

  override def draw() = {
    background(51)

    colorMode(PConstants.HSB, 256)
    pointLight(0,0,255, 1,1,1)
    ambientLight(0,0,255)
    
    0.until(100).foreach { i => 
      val r = random(255)
      val g = random(255)
      val b = random(255)
      
      fill(r, 200, 200)

      pushMatrix
      translate(r*width/256, g*height/256 ,-b)
      
      rotateX( PApplet.radians(i.asInstanceOf[Float]) )
      rotateY( PApplet.radians(i*2.asInstanceOf[Float]))
      rotateZ( PApplet.radians(i*3.asInstanceOf[Float]))
      
//      translate(random(width), random(height), random(-30,30))
//      val angle: Float = (i/50.0 * Math.PI).asInstanceOf[Float]
//      rotateX(angle)
//      rotateY(angle)
      box(i)
      rect(0, 0, 100, 100)
      popMatrix
    }
  }

  def main(args : Array[String]) : Unit = {
    val frame = new javax.swing.JFrame("Test")
    frame.getContentPane.add(VizTest)
    VizTest.init
    frame.pack
    frame setVisible true
  }
}