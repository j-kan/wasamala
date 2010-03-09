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


class MongoDbCollectionIterator(collection:    DBCollection, 
                                strjsonquery:  String, 
                                strjsonfields: String)
                                (val block: DBObject => Instance)  extends Iterator[Instance] {

  val jsonquery:  DBObject = JSON.parse(strjsonquery).asInstanceOf[DBObject]
  val jsonfields: DBObject = JSON.parse(strjsonfields).asInstanceOf[DBObject]
  
  val cursor = collection.find(jsonquery, jsonfields)
  
  def hasNext: boolean = cursor.hasNext
  
  def next: Instance = block(cursor.next())
  
  def remove = throw new IllegalStateException ("This Iterator<Instance> does not support remove().")
}
