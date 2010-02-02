package viscera

import processing.core._
import processing.core.PConstants._
//import processing.opengl._

import scala.Math
import scala.collection.jcl.ArrayList
import scala.util.Sorting





object Tripe extends PApplet {

  def random = Math.random.asInstanceOf[Float]

  def withMatrix(block: => Unit) = {
    pushMatrix
    block
    popMatrix
  }
  

  
  class Particle(l:PVector, useColor:Boolean, useFill:Boolean) {
    
//    val initial_velocity_multiplier = 2F
//    
//    val init_velocity_x = (mouseX-pmouseX)*0.02F + random * initial_velocity_multiplier-initial_velocity_multiplier/2
//    val init_velocity_y = (mouseY-pmouseY)*0.02F + random * -initial_velocity_multiplier

    val init_velocity_x = 0F
    val init_velocity_y = 0F

    val loc = l.get
    val vel = new PVector(init_velocity_x, init_velocity_y, 0F)
    val acc = new PVector(0F, 0F, 0F)

    val wh_max = 520
    var width  = random(wh_max) + 20 
    var height = wh_max-width + 20 // random(wh_max-width)
    val rot    = mouseY.asInstanceOf[Float]/40F

    val hue   = random(360) // mouseX.asInstanceOf[Float] / screen.width
    val alpha = mouseY.asInstanceOf[Float] / screen.height
    val brightness = 100 //random(0.5F) + 0.5F // mouseX.asInstanceOf[Float] / screen.width
    val weight = random(10)
    
    var timer = 100F
    
    val getBigger = false
    
    def run = {
      update
      render
    }
  
    // Method to update location
    def update = {
      val jitter = new PVector(random(jitterSize)-jitterSize/2, random(jitterSize)-jitterSize/2, 0F)
      
      vel.add(acc)
      vel.add(jitter)
      loc.add(vel)
      timer -= timerDiff
    }
  
    // Method to display
    def render = {
      
      if (useFill) {
        noStroke
        if (useColor)
          fill(hue, 100, 100, timer/8F)
        else
          fill(0, 0, hue/10, timer/8F)
      }
      else {
        noFill
        strokeWeight(weight)
        if (useColor)
          stroke(hue, 100, 100, timer/8F)
        else
          stroke(0, 0, hue/10, timer/8F)
      }
      
      //fill(100,timer)
      
      withMatrix {
        translate(loc.x, loc.y)
        rotate(rot)
        if (useRect) {
          rectMode(CENTER)
          rect(0,0,width,height)
        }
        else {
          ellipseMode(CENTER)
          ellipse(0,0,width,height)
        }
      }
      
      if (getBigger) {
        width += 1
        height += 1
      }
      
      //displayVector(vel,loc.x,loc.y,10)
    }
    
    // Is the particle still useful?
    def dead:Boolean = (timer <= 0.0F) 
    
    def displayVector(v:PVector, x:Float, y:Float, scayl:Float) = {
      withMatrix {
        val arrowsize = 4F
        
        // Translate to location to render vector
        translate(x,y)
        strokeWeight(3)
        //stroke(100,100)
        // Call vector heading function to get direction (note that pointing up is a heading of 0) and rotate
        rotate(v.heading2D());
        // Calculate length of vector & scale it to be bigger or smaller if necessary
        val len = v.mag * scayl
        
        // Draw three lines to make an arrow (draw pointing up since we've rotate to the proper direction)
        line(0,0,len,0)
        line(len,0,len-arrowsize,+arrowsize/2)
        line(len,0,len-arrowsize,-arrowsize/2)
      }
    } 
  }

  class ParticleSystem(num:Int, v:PVector) {

    val particles = new ArrayList[Particle]  // An arraylist for all the particles
    val origin    = v.get                    // An origin point for where particles are born

    for (i <- 0 to num)
      particles.add(new Particle(origin, useColor, useFill))

    def run() = {
      // Cycle through the ArrayList backwards b/c we are deleting
      //    for (int i = particles.size()-1; i >= 0; i--) {
      for (p <- particles.reverse) {
        p.run
        if (p.dead)
          particles.remove(p)
      }
    }

    def addParticle                   = particles.add(new Particle(origin, useColor, useFill))
    def addParticle(x:Float, y:Float) = particles.add(new Particle(new PVector(x,y), useColor, useFill))
    def addParticle(p:Particle)       = particles.add(p)

    // A method to test if the particle system still has particles
    def dead:Boolean                  = particles.isEmpty
  }

  
//  override def setup = {
//      size(screen.width, screen.height, P2D)
//      //noLoop
//      smooth
//
//      colorMode(HSB, 1.0F)
//      background(0)
//  }

  val ps = new ParticleSystem(1, new PVector(screen.width/2, screen.height/2, 0))
  
  var count = 0
  var useColor = false
  var useFill  = true
  var useRect  = false
  var started  = false
  var addMore  = true
  var jitterSize = 1F
  var timerDiff  = 0.25F

  override def mousePressed:Unit  = started = !started
  //override def mouseReleased:Unit = useColor = false
  //override def mouseMoved:Unit    = started = true
      
  override def keyReleased:Unit = {
    key match {
      case 's' => save("TripeParticles-" + count + ".png")
      case 'b' => background(0)
      case 'c' => useColor = !useColor
      case 'f' => useFill = !useFill
      case 'r' => useRect = !useRect
      case ' ' => { addMore = !addMore }
      //case ' ' => started = !started
      case UP   => timerDiff += 0.01F
      case DOWN => timerDiff = Math.max(0.1F, timerDiff-0.01F)
        
      case _   => {}
    }
  }
  
  
  override def setup = {
    size(screen.width, screen.height)
    colorMode(HSB, 360, 100, 100, 100)
    smooth
    background(0)
  }
  
  override def draw = {

    if (started) {
      count = count + 1
      //background(0)
      ps.run
      if (addMore)
        ps.addParticle(mouseX,mouseY)
    }
        
/*    def square(x:Float) = x*x

    val xbase = square(random(20)) - 200 + mouseX
    val ybase = square(random(20)) - 200 + mouseY
    
    val width  = random(250)
    val height = 200-width // random(400-width)
    
    val hue   = random(1.0F) // mouseX.asInstanceOf[Float] / screen.width
    val alpha = mouseY.asInstanceOf[Float] / screen.height
    val brightness = 1.0F //random(0.5F) + 0.5F // mouseX.asInstanceOf[Float] / screen.width
    

    if (useColor) {
      noFill
      strokeWeight(random(20))
      stroke(hue, 1.0F, brightness, alpha * 0.7F + 0.1F)
    }
    else {
      noStroke        
      fill(0.0F, 0.0F, hue * 0.1F, alpha * 0.5F + 0.1F)
    }
    withMatrix {
      translate(xbase, ybase)
      rotate(mouseY/50)
      ellipseMode(CENTER)
      ellipse(0,0,width,height)
//      rectMode(CENTER)
//      rect(0,0,width,height)
    }
*/  }
  
  
  def main(args : Array[String]) : Unit = {
    val frame = new javax.swing.JFrame("Tripe")
    
    frame.getContentPane.add(Tripe)
    Tripe.init
    
    frame.pack
    frame setVisible true
    
  }
}
