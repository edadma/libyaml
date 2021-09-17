package io.github.edadma.libyaml.extern

import scala.scalanative.libc.stdio.FILE
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
  type yaml_emitter_t    = CArray[Byte, Digit3[_4, _3, _2]]
  type yaml_emitter_tp   = Ptr[yaml_emitter_t]
  type yaml_parser_tp    = Ptr[yaml_parser_t]
  type yaml_token_type_t = CInt // enum
  type yaml_token_t      = CStruct2[yaml_token_type_t, /* 76 bytes of padding */ CArray[Byte, Digit2[_7, _6]]] // todo: 269
  type event_type_t      = CInt // enum
  type yaml_mark_t       = CStruct3[CSize, CSize, CSize]
  type yaml_event_t =
    CStruct4[event_type_t, /* 52 bytes of padding */ CArray[Byte, Digit2[_5, _2]], yaml_mark_t, yaml_mark_t]
  type data_scalar           = CStruct8[CLong, CString, CString, CString, CSize, CInt, CInt, CInt]
  type data_alias            = CStruct2[CLong, CString]
  type data_sequence_start   = CStruct5[CLong, CString, CString, CInt, yaml_sequence_style_t]
  type data_mapping_start    = CStruct5[CLong, CString, CString, CInt, yaml_mapping_style_t]
  type yaml_event_tp         = Ptr[yaml_event_t]
  type yaml_error_type_t     = CInt // enum
  type yaml_scalar_style_t   = CInt // enum
  type yaml_encoding_t       = CInt // enum
  type yaml_write_handler_t  = CFuncPtr3[Ptr[Byte], Ptr[CUnsignedChar], CSize, CInt]
  type yaml_write_handler_tp = Ptr[yaml_write_handler_t]
  type yaml_break_t          = CInt // enum
  type yaml_char_t           = CUnsignedChar
  type yaml_sequence_style_t = CInt // enum
  type yaml_mapping_style_t  = CInt // enum

  def yaml_get_version_string: CString             = extern //59
  def yaml_token_delete(token: yaml_token_t): Unit = extern //345

  def yaml_stream_start_event_initialize(event: Ptr[yaml_event_t], encoding: yaml_encoding_t): CInt = extern //490
  def yaml_stream_end_event_initialize(event: Ptr[yaml_event_t]): CInt                              = extern //502
//  def yaml_document_start_event_initialize(event: Ptr[yaml_event_t],
//                                           version_directive: Ptr[yaml_version_directive_t],
//                                           tag_directives_start: Ptr[yaml_tag_directive_t],
//                                           tag_directives_end: Ptr[yaml_tag_directive_t],
//                                           _implicit: CInt): CInt                           = extern //524
  def yaml_document_end_event_initialize(event: Ptr[yaml_event_t], _implicit: CInt): CInt   = extern //543
  def yaml_alias_event_initialize(event: Ptr[yaml_event_t], anchor: Ptr[yaml_char_t]): CInt = extern //555
  def yaml_scalar_event_initialize(event: Ptr[yaml_event_t],
                                   anchor: Ptr[yaml_char_t],
                                   tag: Ptr[yaml_char_t],
                                   value: Ptr[yaml_char_t],
                                   length: CInt,
                                   plain_implicit: CInt,
                                   quoted_implicit: CInt,
                                   style: yaml_scalar_style_t): CInt = extern //580
  def yaml_sequence_start_event_initialize(event: Ptr[yaml_event_t],
                                           anchor: Ptr[yaml_char_t],
                                           tag: Ptr[yaml_char_t],
                                           _implicit: CInt,
                                           style: yaml_sequence_style_t): CInt = extern //603
  def yaml_sequence_end_event_initialize(event: Ptr[yaml_event_t]): CInt       = extern //616
  def yaml_mapping_start_event_initialize(event: Ptr[yaml_event_t],
                                          anchor: Ptr[yaml_char_t],
                                          tag: Ptr[yaml_char_t],
                                          _implicit: CInt,
                                          style: yaml_mapping_style_t): CInt = extern //635
  def yaml_mapping_end_event_initialize(event: Ptr[yaml_event_t]): CInt      = extern //648

  def yaml_event_delete(event: yaml_event_t): Unit                                            = extern //657
  def yaml_parser_initialize(parser: yaml_parser_tp): Int                                     = extern //1319
  def yaml_parser_delete(parser: yaml_parser_tp): Unit                                        = extern //1328
  def yaml_parser_set_input_string(parser: yaml_parser_tp, input: CString, size: CSize): Unit = extern //1343
  def yaml_parser_set_encoding(parser: yaml_parser_tp, encoding: yaml_encoding_t): Unit       = extern //1380
  def yaml_parser_scan(parser: yaml_parser_tp, token: yaml_token_t): CInt                     = extern //1404
  def yaml_parser_parse(parser: yaml_parser_tp, event: yaml_event_tp): CInt                   = extern //1428
  def yaml_emitter_initialize(emitter: yaml_emitter_tp): CInt                                 = extern //1783
  def yaml_emitter_delete(emitter: yaml_emitter_tp): Unit                                     = extern //1792
  def yaml_emitter_set_output_string(emitter: yaml_emitter_tp,
                                     output: Ptr[CChar],
                                     size: CSize,
                                     size_written: Ptr[CSize]): Unit                = extern //1810
  def yaml_emitter_set_output_file(emitter: yaml_emitter_tp, file: Ptr[FILE]): Unit = extern //1824
  def yaml_emitter_set_output(emitter: yaml_emitter_tp, handler: yaml_write_handler_tp, data: Ptr[Byte]): Unit =
    extern //1836
  def yaml_emitter_set_encoding(emitter: yaml_emitter_tp, encoding: yaml_encoding_t): Unit = extern //1847
  def yaml_emitter_set_canonical(emitter: yaml_emitter_tp, canonical: CInt): Unit          = extern //1858
  def yaml_emitter_set_indent(emitter: yaml_emitter_tp, indent: CInt): Unit                = extern //1868
  def yaml_emitter_set_width(emitter: yaml_emitter_tp, width: CInt): Unit                  = extern //1878
  def yaml_emitter_set_unicode(emitter: yaml_emitter_tp, unicode: CInt): Unit              = extern //1888
  def yaml_emitter_set_break(emitter: yaml_emitter_tp, line_break: yaml_break_t): Unit     = extern //1898
  def yaml_emitter_emit(emitter: yaml_emitter_tp, event: yaml_event_tp): CInt              = extern //1915
  def yaml_emitter_flush(emitter: yaml_emitter_tp): CInt                                   = extern //1969

}
