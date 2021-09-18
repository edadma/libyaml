//import io.github.edadma.libyaml._
//
//import pprint._
//
//object Main extends App {
//
//  val ex1 =
//    """
//      |- Mark McGwire
//      |- Sammy Sosa
//      |- Ken Griffey
//      |""".stripMargin
//  val ex2 =
//    """
//      |hr:  65    # Home runs
//      |avg: 0.278 # Batting average
//      |rbi: 147   # Runs Batted In
//      |""".stripMargin
//  val ex3 =
//    """
//      |american:
//      |  - Boston Red Sox
//      |  - Detroit Tigers
//      |  - New York Yankees
//      |national:
//      |  - New York Mets
//      |  - Chicago Cubs
//      |  - Atlanta Braves
//      |""".stripMargin
//  val ex4 =
//    """
//      |-
//      |  name: Mark McGwire
//      |  hr:   65
//      |  avg:  0.278
//      |-
//      |  name: Sammy Sosa
//      |  hr:   63
//      |  avg:  0.288
//      |""".stripMargin
//  val ex5 =
//    """
//      |- [name        , hr, avg  ]
//      |- [Mark McGwire, 65, 0.278]
//      |- [Sammy Sosa  , 63, 0.288]
//      |""".stripMargin
//  val ex6 =
//    """
//      |Mark McGwire: {hr: 65, avg: 0.278}
//      |Sammy Sosa: {
//      |    hr: 63,
//      |    avg: 0.288
//      |  }
//      |""".stripMargin
//  val ex7 =
//    """
//      |# Ranking of 1998 home runs
//      |---
//      |- Mark McGwire
//      |- Sammy Sosa
//      |- Ken Griffey
//      |
//      |# Team ranking
//      |---
//      |- Chicago Cubs
//      |- St Louis Cardinals
//      |""".stripMargin
//  val ex8 =
//    """
//      |---
//      |time: 20:03:20
//      |player: Sammy Sosa
//      |action: strike (miss)
//      |...
//      |---
//      |time: 20:03:47
//      |player: Sammy Sosa
//      |action: grand slam
//      |...
//      |""".stripMargin
//  val ex9 =
//    """
//      |---
//      |hr: # 1998 hr ranking
//      |  - Mark McGwire
//      |  - Sammy Sosa
//      |rbi:
//      |  # 1998 rbi ranking
//      |  - Sammy Sosa
//      |  - Ken Griffey
//      |""".stripMargin
//  val ex10 =
//    """
//      |---
//      |hr:
//      |  - Mark McGwire
//      |  # Following node labeled SS
//      |  - &SS Sammy Sosa
//      |rbi:
//      |  - *SS # Subsequent occurrence
//      |  - Ken Griffey
//      |""".stripMargin
//  val ex11 =
//    """
//      |? - Detroit Tigers
//      |  - Chicago cubs
//      |:
//      |  - 2001-07-23
//      |
//      |? [ New York Yankees,
//      |    Atlanta Braves ]
//      |: [ 2001-07-02, 2001-08-12,
//      |    2001-08-14 ]
//      |""".stripMargin
//  val ex12 =
//    """
//      |---
//      |# Products purchased
//      |- item    : Super Hoop
//      |  quantity: 1
//      |- item    : Basketball
//      |  quantity: 4
//      |- item    : Big Shoes
//      |  quantity: 1
//      |""".stripMargin
//  val ex13 =
//    """
//      |# ASCII Art
//      |--- |
//      |  \//||\/||
//      |  // ||  ||__
//      |""".stripMargin
//  val ex14 =
//    """
//      |--- >
//      |  Mark McGwire's
//      |  year was crippled
//      |  by a knee injury.
//      |""".stripMargin
//  val ex15 =
//    """
//      |>
//      | Sammy Sosa completed another
//      | fine season with great stats.
//      |
//      |   63 Home Runs
//      |   0.288 Batting Average
//      |
//      | What a year!
//      |""".stripMargin
//  val ex16 =
//    """
//      |name: Mark McGwire
//      |accomplishment: >
//      |  Mark set a major league
//      |  home run record in 1998.
//      |stats: |
//      |  65 Home Runs
//      |  0.278 Batting Average
//      |""".stripMargin
//  val ex17 = util.Using(scala.io.Source.fromFile("ex17.yaml"))(_.mkString).get
//  val ex18 =
//    """
//      |plain:
//      |  This unquoted scalar
//      |  spans many lines.
//      |
//      |quoted: "So does this
//      |  quoted scalar.\n"
//      |""".stripMargin
//  val ex19 =
//    """
//      |canonical: 12345
//      |decimal: +12345
//      |octal: 0o14
//      |hexadecimal: 0xC
//      |""".stripMargin
//  val ex20 =
//    """
//      |canonical: 1.23015e+3
//      |exponential: 12.3015e+02
//      |fixed: 1230.15
//      |negative infinity: -.inf
//      |not a number: .NaN
//      |""".stripMargin
//  val ex21 =
//    """
//      |null:
//      |booleans: [ true, false ]
//      |string: '012345'
//      |""".stripMargin
//  val ex22 =
//    """
//      |canonical: 2001-12-15T02:59:43.1Z
//      |iso8601: 2001-12-14t21:59:43.10-05:00
//      |spaced: 2001-12-14 21:59:43.10 -5
//      |date: 2002-12-14
//      |""".stripMargin
//  val ex23 =
//    """
//      |---
//      |not-date: !!str 2002-04-28
//      |
//      |picture: !!binary |
//      | R0lGODlhDAAMAIQAAP//9/X
//      | 17unp5WZmZgAAAOfn515eXv
//      | Pz7Y6OjuDg4J+fn5OTk6enp
//      | 56enmleECcgggoBADs=
//      |
//      |application specific tag: !something |
//      | The semantics of the tag
//      | above may be different for
//      | different documents.
//      |""".stripMargin
//  val ex24 =
//    """
//      |%TAG ! tag:clarkevans.com,2002:
//      |--- !shape
//      |  # Use the ! handle for presenting
//      |  # tag:clarkevans.com,2002:circle
//      |- !circle
//      |  center: &ORIGIN {x: 73, y: 129}
//      |  radius: 7
//      |- !line
//      |  start: *ORIGIN
//      |  finish: { x: 89, y: 102 }
//      |- !label
//      |  start: *ORIGIN
//      |  color: 0xFFEEBB
//      |  text: Pretty vector drawing.
//      |""".stripMargin
//  val ex25 =
//    """
//      |# Sets are represented as a
//      |# Mapping where each key is
//      |# associated with a null value
//      |--- !!set
//      |? Mark McGwire
//      |? Sammy Sosa
//      |? Ken Griff
//      |""".stripMargin
//  val ex26 =
//    """
//      |# Ordered maps are represented as
//      |# A sequence of mappings, with
//      |# each mapping having one key
//      |--- !!omap
//      |- Mark McGwire: 65
//      |- Sammy Sosa: 63
//      |- Ken Griffy: 58
//      |""".stripMargin
//  val ex27 =
//    """
//      |--- !<tag:clarkevans.com,2002:invoice>
//      |invoice: 34843
//      |date   : 2001-01-23
//      |bill-to: &id001
//      |    given  : Chris
//      |    family : Dumars
//      |    address:
//      |        lines: |
//      |            458 Walkman Dr.
//      |            Suite #292
//      |        city    : Royal Oak
//      |        state   : MI
//      |        postal  : 48046
//      |ship-to: *id001
//      |product:
//      |    - sku         : BL394D
//      |      quantity    : 4
//      |      description : Basketball
//      |      price       : 450.00
//      |    - sku         : BL4438H
//      |      quantity    : 1
//      |      description : Super Hoop
//      |      price       : 2392.00
//      |tax  : 251.42
//      |total: 4443.52
//      |comments:
//      |    Late afternoon is best.
//      |    Backup contact is Nancy
//      |    Billsmer @ 338-4338.
//      |""".stripMargin
//  val ex28 =
//    """
//      |---
//      |Time: 2001-11-23 15:01:42 -5
//      |User: ed
//      |Warning:
//      |  This is an error message
//      |  for the log file
//      |---
//      |Time: 2001-11-23 15:02:31 -5
//      |User: ed
//      |Warning:
//      |  A slightly different error
//      |  message.
//      |---
//      |Date: 2001-11-23 15:03:17 -5
//      |User: ed
//      |Fatal:
//      |  Unknown variable "bar"
//      |Stack:
//      |  - file: TopClass.py
//      |    line: 23
//      |    code: |
//      |      x = MoreObject("345\n")
//      |  - file: MoreClass.py
//      |    line: 58
//      |    code: |-
//      |      foo = bar
//      |""".stripMargin
//  val result =
//    parseFromString(ex24)
//
//  pprintln(result)
//
//}

import io.github.edadma.libyaml._

import pprint._

object Main extends App {

  val example_26 =
    """
        |# Ordered maps are represented as
        |# A sequence of mappings, with
        |# each mapping having one key
        |--- !!omap
        |- Mark McGwire: 65
        |- Sammy Sosa: 63
        |- Ken Griffy: 58
        |""".stripMargin

  pprintln(parseFromString(example_26))
  pprintln(constructFromString(example_26))

}
