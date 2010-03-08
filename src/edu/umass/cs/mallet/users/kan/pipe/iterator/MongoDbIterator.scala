/*===========================================================================
  MongoDbIterator.scala
                                    Created by jkan on Mar 7, 2010
                                    Copyright (c)2010 Essbare Weichware, GmbH
                                    All rights reserved.
  =============================================================================*/

package edu.umass.cs.mallet.users.kan.pipe.iterator

import java.util.Iterator

import cc.mallet.types.{FeatureSequence, FeatureVector, InstanceList, Instance}
import cc.mallet.pipe._

import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject;
import com.mongodb.Mongo
import com.mongodb.MongoException
import com.mongodb.util.JSON

import edu.umass.cs.mallet.users.kan.scalautil.FileUtil


class MongoDbIterator(collection:    DBCollection, 
                      strjsonquery:  String, 
                      strjsonfields: String)  extends Iterator[Instance] {

  val jsonquery:  DBObject = JSON.parse(strjsonquery).asInstanceOf[DBObject]
  val jsonfields: DBObject = JSON.parse(strjsonfields).asInstanceOf[DBObject]
  
  val cursor = collection.find(jsonquery, jsonfields)
  
  def hasNext = cursor.hasNext
  
  def next: Instance = {
    
    val item    = cursor.next()
    val name    = item.get("name")
    val bio     = item.get("bio").asInstanceOf[DBObject]
    val summary = bio.get("summary")
    
    val data = summary
    val target = ""
    val source = item.get("_id")
   
    new Instance(data, target, name, source)
  }
  
  def remove = throw new IllegalStateException ("This Iterator<Instance> does not support remove().")
}


object MongoDbIterator {
  
  def parseInstances: InstanceList = {
    
    val collection = new Mongo().getDB("rhinoplast").getCollection("artists_info")
    val query  = """{ 'tags.tag.name' : 'experimental', 
                      'bio.summary'   : { $exists : true, $ne : null } }"""
    val fields = "{'name' : 1, 'bio.summary' : 2}"

    val instancePipe = new SerialPipes( Array[Pipe]( 
      new SaveDataInSource,
      new TargetStringToFeatures,
      new Input2CharSequence,
      //new CharSubsequence(CharSubsequence.SKIP_HEADER),
      new CharSequence2TokenSequence,
      new TokenSequenceLowercase,
      new TokenSequenceRemoveStopwords(false, false),
      new TokenSequence2FeatureSequence
      //new PrintInputAndTarget
    ))
        
    val instances = new InstanceList(instancePipe)
    val removeCommonPrefix = true
    
    instances.addThruPipe(new MongoDbIterator(collection, query, fields))
    
    FileUtil.serializeObject(instances, "mongo-instances.ser")

    instances
  }
  
  def main(args: Array[String]) : Unit = {
    parseInstances
  }

}