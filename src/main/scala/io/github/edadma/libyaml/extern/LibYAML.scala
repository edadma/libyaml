package io.github.edadma.libyaml.extern

import scala.scalanative.unsafe.Nat._
import scala.scalanative.unsafe._

@link("yaml")
@extern
object LibYAML {

  /**
    * The parser structure.
    *
    * All members are internal.  Manage the structure using the @c yaml_parser_
    * family of functions.
    */
  type yaml_parser_t = CArray[Byte, Digit3[_4, _8, _0]] // todo: provide access to error information

  /**
    * The emitter structure.
    *
    * All members are internal.  Manage the structure using the @c yaml_emitter_
    * family of functions.
    */
  type yaml_emitter_s = CArray[Byte, Digit3[_4, _3, _2]]

  type yaml_parser_tp    = Ptr[yaml_parser_t]
  type yaml_token_type_t = CInt // enum
  type yaml_token_t      = CStruct2[yaml_token_type_t, /* 76 bytes of padding */ CArray[Byte, Digit2[_7, _6]]] // todo: 269
  type event_type_t      = CInt // enum
  type yaml_mark_t       = CStruct3[CSize, CSize, CSize]
  type yaml_event_t =
    CStruct4[event_type_t, /* 52 bytes of padding */ CArray[Byte, Digit2[_5, _2]], yaml_mark_t, yaml_mark_t]
  type data_scalar         = CStruct8[CLong, CString, CString, CString, CSize, CInt, CInt, CInt]
  type yaml_event_tp       = Ptr[yaml_event_t]
  type yaml_error_type_t   = CInt // enum
  type yaml_scalar_style_t = CInt // enum
  type yaml_encoding_t     = CInt // enum

  def yaml_get_version_string: CString                                                        = extern //59
  def yaml_token_delete(token: yaml_token_t): Unit                                            = extern //345
  def yaml_event_delete(event: yaml_event_t): Unit                                            = extern //657
  def yaml_parser_initialize(parser: yaml_parser_tp): Int                                     = extern //1319
  def yaml_parser_delete(parser: yaml_parser_tp): Unit                                        = extern //1328
  def yaml_parser_set_input_string(parser: yaml_parser_tp, input: CString, size: CSize): Unit = extern //1343
  def yaml_parser_scan(parser: yaml_parser_tp, token: yaml_token_t): CInt                     = extern //1404
  def yaml_parser_parse(parser: yaml_parser_tp, event: yaml_event_tp): CInt                   = extern //1428

}
