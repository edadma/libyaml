package io.github.edadma.libyaml

import scala.collection.immutable.VectorMap

class Constructor {

  def construct(s: YAMLStream): List[Any] = s.documents map construct

  def construct(d: YAMLDocument): Any = construct(d.document)

  def construct(v: YAMLValue): Any =
    v match {
      case YAMLSequence(s)           => s map construct
      case YAMLMapping(elems)        => elems map { case YAMLPair(k, v) => (construct(k), construct(v)) } toMap
      case YAMLOrderedMapping(elems) => elems map { case YAMLPair(k, v) => (construct(k), construct(v)) } to VectorMap
      case YAMLString(s)             => s
      case YAMLInteger(n)            => n
      case YAMLBigInt(n)             => n
      case YAMLFloat(n)              => n
      case YAMLDecimal(n)            => n
      case YAMLNull                  => null
      case YAMLBinary(data)          => data
      case YAMLBoolean(bool)         => bool
      //      case YAMLTaggedScalar(tag, quoted, v, mark) => v // todo: application specific tags
      case _ => problem(s"don't know how to construct '$v'", null) // todo: handle remaining cases; all values should carry a mark
    }

}
