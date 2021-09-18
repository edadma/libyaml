import io.github.edadma.libyaml._

import scala.annotation.tailrec
import scala.collection.immutable.{ArraySeq, VectorMap}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import pprint._

//object Main extends App {
//
//  val parser = new Parser
//  val event  = new Event
//  val input  = "[3, 4]"
////  val input =
////    """
////      |hr:  65    # Home runs
////      |avg: 0.278 # Batting average
////      |rbi: 147   # Runs Batted In
////      |""".stripMargin
//
//  parser.setInputString(input)
//
//  @tailrec
//  def eventLoop(): Unit = {
//    parser.parse(event)
//
//    if (event.getType == EventType.SCALAR)
//      println(
//        s"${event.getType.value}, ${event.scalar.value}, ${event.scalar.tag}, ${event.scalar.plainImplicit}, ${event.scalar.quotedImplicit}")
//    else
//      println(s"${event.getType.value}")
//
//    if (event.getType == EventType.STREAM_END)
//      event.destroy()
//    else {
//      event.delete()
//      eventLoop()
//    }
//  }
//
//  eventLoop()
//  parser.destroy()
//
//}

object Main extends App {

  val ex1 =
    """
      |- Mark McGwire
      |- Sammy Sosa
      |- Ken Griffey
      |""".stripMargin
  val ex2 =
    """
      |hr:  65    # Home runs
      |avg: 0.278 # Batting average
      |rbi: 147   # Runs Batted In
      |""".stripMargin
  val ex3 =
    """
      |american:
      |  - Boston Red Sox
      |  - Detroit Tigers
      |  - New York Yankees
      |national:
      |  - New York Mets
      |  - Chicago Cubs
      |  - Atlanta Braves
      |""".stripMargin
  val ex4 =
    """
      |-
      |  name: Mark McGwire
      |  hr:   65
      |  avg:  0.278
      |-
      |  name: Sammy Sosa
      |  hr:   63
      |  avg:  0.288
      |""".stripMargin
  val ex5 =
    """
      |- [name        , hr, avg  ]
      |- [Mark McGwire, 65, 0.278]
      |- [Sammy Sosa  , 63, 0.288]
      |""".stripMargin
  val ex6 =
    """
      |Mark McGwire: {hr: 65, avg: 0.278}
      |Sammy Sosa: {
      |    hr: 63,
      |    avg: 0.288
      |  }
      |""".stripMargin
  val ex7 =
    """
      |# Ranking of 1998 home runs
      |---
      |- Mark McGwire
      |- Sammy Sosa
      |- Ken Griffey
      |
      |# Team ranking
      |---
      |- Chicago Cubs
      |- St Louis Cardinals
      |""".stripMargin
  val ex8 =
    """
      |---
      |time: 20:03:20
      |player: Sammy Sosa
      |action: strike (miss)
      |...
      |---
      |time: 20:03:47
      |player: Sammy Sosa
      |action: grand slam
      |...
      |""".stripMargin
  val ex9 =
    """
      |---
      |hr: # 1998 hr ranking
      |  - Mark McGwire
      |  - Sammy Sosa
      |rbi:
      |  # 1998 rbi ranking
      |  - Sammy Sosa
      |  - Ken Griffey
      |""".stripMargin
  val ex10 =
    """
      |---
      |hr:
      |  - Mark McGwire
      |  # Following node labeled SS
      |  - &SS Sammy Sosa
      |rbi:
      |  - *SS # Subsequent occurrence
      |  - Ken Griffey
      |""".stripMargin
  val ex11 =
    """
      |? - Detroit Tigers
      |  - Chicago cubs
      |:
      |  - 2001-07-23
      |
      |? [ New York Yankees,
      |    Atlanta Braves ]
      |: [ 2001-07-02, 2001-08-12,
      |    2001-08-14 ]
      |""".stripMargin
  val ex12 =
    """
      |---
      |# Products purchased
      |- item    : Super Hoop
      |  quantity: 1
      |- item    : Basketball
      |  quantity: 4
      |- item    : Big Shoes
      |  quantity: 1
      |""".stripMargin
  val ex13 =
    """
      |# ASCII Art
      |--- |
      |  \//||\/||
      |  // ||  ||__
      |""".stripMargin
  val ex14 =
    """
      |--- >
      |  Mark McGwire's
      |  year was crippled
      |  by a knee injury.
      |""".stripMargin
  val ex15 =
    """
      |>
      | Sammy Sosa completed another
      | fine season with great stats.
      |
      |   63 Home Runs
      |   0.288 Batting Average
      |
      | What a year!
      |""".stripMargin
  val ex16 =
    """
      |name: Mark McGwire
      |accomplishment: >
      |  Mark set a major league
      |  home run record in 1998.
      |stats: |
      |  65 Home Runs
      |  0.278 Batting Average
      |""".stripMargin
  val ex17 = util.Using(scala.io.Source.fromFile("ex17.yaml"))(_.mkString).get
  val ex18 =
    """
      |plain:
      |  This unquoted scalar
      |  spans many lines.
      |
      |quoted: "So does this
      |  quoted scalar.\n"
      |""".stripMargin
  val ex19 =
    """
      |canonical: 12345
      |decimal: +12345
      |octal: 0o14
      |hexadecimal: 0xC
      |""".stripMargin
  val ex20 =
    """
      |canonical: 1.23015e+3
      |exponential: 12.3015e+02
      |fixed: 1230.15
      |negative infinity: -.inf
      |not a number: .NaN
      |""".stripMargin
  val ex21 =
    """
      |null:
      |booleans: [ true, false ]
      |string: '012345'
      |""".stripMargin
  val ex22 =
    """
      |canonical: 2001-12-15T02:59:43.1Z
      |iso8601: 2001-12-14t21:59:43.10-05:00
      |spaced: 2001-12-14 21:59:43.10 -5
      |date: 2002-12-14
      |""".stripMargin
  val ex23 =
    """
      |---
      |not-date: !!str 2002-04-28
      |
      |picture: !!binary |
      | R0lGODlhDAAMAIQAAP//9/X
      | 17unp5WZmZgAAAOfn515eXv
      | Pz7Y6OjuDg4J+fn5OTk6enp
      | 56enmleECcgggoBADs=
      |
      |application specific tag: !something |
      | The semantics of the tag
      | above may be different for
      | different documents.
      |""".stripMargin
  val ex25 =
    """
      |# Sets are represented as a
      |# Mapping where each key is
      |# associated with a null value
      |--- !!set
      |? Mark McGwire
      |? Sammy Sosa
      |? Ken Griff
      |""".stripMargin
  val ex26 =
    """
      |# Ordered maps are represented as
      |# A sequence of mappings, with
      |# each mapping having one key
      |--- !!omap
      |- Mark McGwire: 65
      |- Sammy Sosa: 63
      |- Ken Griffy: 58
      |""".stripMargin
  val result =
    parseFromString(ex26)

  pprintln(result)

}
