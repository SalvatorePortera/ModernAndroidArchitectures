package com.nereus.craftbeer.model.printer

import com.nereus.craftbeer.constant.EMPTY_STRING
import com.seikoinstruments.sdk.thermalprinter.printerenum.*

/**
 * Print model
 *
 * @property content
 * @property alignment
 * @property bold
 * @property underline
 * @property reverse
 * @property font
 * @property scale
 * @constructor Create empty Print model
 */
data class PrintModel(

    var content: String = EMPTY_STRING,

    var alignment: PrintAlignment = PrintAlignment.ALIGNMENT_CENTER,

    var bold: CharacterBold = CharacterBold.BOLD_CANCEL,

    var underline: CharacterUnderline = CharacterUnderline.UNDERLINE_CANCEL,

    var reverse: CharacterReverse = CharacterReverse.REVERSE_CANCEL,

    var font: CharacterFont = CharacterFont.FONT_A,

    var scale: CharacterScale = CharacterScale.VARTICAL_1_HORIZONTAL_1
)





