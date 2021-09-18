package io.github.edadma.libyaml

import scala.collection.immutable.ArraySeq

class Binary(array: Array[Byte]) {

  val data: Seq[Byte] = array to ArraySeq

  override def toString: String = s"[binary data: ${data.length} bytes]"

}
