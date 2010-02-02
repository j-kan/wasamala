package edu.umass.cs.mallet.users.kan.scalautil


import java.io._
//{File,Writer,FileWriter,PrintWriter,BufferedWriter, BufferedOutputStream, ObjectOutputStream, FileOutputStream}


object FileUtil {

  def serializeObject(obj:Object, filename:String) = {
    val oos = 
      new ObjectOutputStream(
        new BufferedOutputStream(
          new FileOutputStream(filename)))

    try { oos.writeObject(obj) } 
    finally { oos.close() }
  }

  def serializeObject(obj:Object, outFile:File) = {
    val oos = 
      new ObjectOutputStream(
        new BufferedOutputStream(
          new FileOutputStream(outFile)))

    try { oos.writeObject(obj) } 
    finally { oos.close() }
  }
  
  
  def deserializeObject[T](filename:String):T = {
    val ois = new ObjectInputStream (new FileInputStream(filename))
    
    try { ois.readObject.asInstanceOf[T] } 
    finally { ois.close }
  }
}
