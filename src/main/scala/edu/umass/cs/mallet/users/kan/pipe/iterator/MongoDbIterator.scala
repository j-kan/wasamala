/*===========================================================================
  MongoDbIterator.scala
                                    Created by jkan on Mar 7, 2010
                                    Copyright (c)2010 Essbare Weichware, GmbH
                                    All rights reserved.
  =============================================================================*/

package edu.umass.cs.mallet.users.kan.pipe.iterator

import java.util.Iterator

import cc.mallet.types.Instance

import com.mongodb.{DBCollection, DBCursor, DBObject, Mongo, MongoException}
import com.mongodb.util.JSON


class MongoDbCollectionIterator(collection: DBCollection, 
                                 jsonquery: String, 
                                    fields: List[String],
                                 val block: DBObject => Instance)  extends Iterator[Instance] {

  private def makeJsonFieldSpec(fields: List[String]): String =
        (fields zip List.make(fields.length, 1)).map( _ match {
           case (str:String, index:Int) => "'" ++ str ++ "' :" ++ index.toString
        }).mkString("{ ", ", ", "  }")
        
  val cursor = collection.find(
                  JSON.parse(jsonquery).asInstanceOf[DBObject], 
                  JSON.parse(makeJsonFieldSpec(fields)).asInstanceOf[DBObject])
  
  def hasNext: boolean = cursor.hasNext
  
  def next: Instance = block(cursor.next())
  
  def remove = throw new IllegalStateException ("This Iterator<Instance> does not support remove().")
}

object MongoDbCollectionIterator {
  
  def fromCollection(databaseName: String,
                   collectionName: String, 
                            query: String, 
                           fields: List[String],
                            block: DBObject => Instance) = 
                                  new MongoDbCollectionIterator(
                                          new Mongo().getDB(databaseName).getCollection(collectionName),
                                          query,
                                          fields,
                                          block)
    
}
