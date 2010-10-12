/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beaver.Parser;
import beaver.ParsingTables;
import beaver.Scanner;
import beaver.Symbol;

import com.aptana.editor.js.parsing.ast.JSArgumentsNode;
import com.aptana.editor.js.parsing.ast.JSArrayNode;
import com.aptana.editor.js.parsing.ast.JSAssignmentNode;
import com.aptana.editor.js.parsing.ast.JSBinaryArithmeticOperatorNode;
import com.aptana.editor.js.parsing.ast.JSBinaryBooleanOperatorNode;
import com.aptana.editor.js.parsing.ast.JSBreakNode;
import com.aptana.editor.js.parsing.ast.JSCaseNode;
import com.aptana.editor.js.parsing.ast.JSCatchNode;
import com.aptana.editor.js.parsing.ast.JSCommaNode;
import com.aptana.editor.js.parsing.ast.JSConditionalNode;
import com.aptana.editor.js.parsing.ast.JSConstructNode;
import com.aptana.editor.js.parsing.ast.JSContinueNode;
import com.aptana.editor.js.parsing.ast.JSDeclarationNode;
import com.aptana.editor.js.parsing.ast.JSDefaultNode;
import com.aptana.editor.js.parsing.ast.JSDoNode;
import com.aptana.editor.js.parsing.ast.JSElementsNode;
import com.aptana.editor.js.parsing.ast.JSElisionNode;
import com.aptana.editor.js.parsing.ast.JSEmptyNode;
import com.aptana.editor.js.parsing.ast.JSErrorNode;
import com.aptana.editor.js.parsing.ast.JSFalseNode;
import com.aptana.editor.js.parsing.ast.JSFinallyNode;
import com.aptana.editor.js.parsing.ast.JSForInNode;
import com.aptana.editor.js.parsing.ast.JSForNode;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSGetElementNode;
import com.aptana.editor.js.parsing.ast.JSGetPropertyNode;
import com.aptana.editor.js.parsing.ast.JSGroupNode;
import com.aptana.editor.js.parsing.ast.JSIdentifierNode;
import com.aptana.editor.js.parsing.ast.JSIfNode;
import com.aptana.editor.js.parsing.ast.JSInvokeNode;
import com.aptana.editor.js.parsing.ast.JSLabelledNode;
import com.aptana.editor.js.parsing.ast.JSNameValuePairNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSNodeTypes;
import com.aptana.editor.js.parsing.ast.JSNullNode;
import com.aptana.editor.js.parsing.ast.JSNumberNode;
import com.aptana.editor.js.parsing.ast.JSObjectNode;
import com.aptana.editor.js.parsing.ast.JSParametersNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.parsing.ast.JSPostUnaryOperatorNode;
import com.aptana.editor.js.parsing.ast.JSPreUnaryOperatorNode;
import com.aptana.editor.js.parsing.ast.JSRegexNode;
import com.aptana.editor.js.parsing.ast.JSReturnNode;
import com.aptana.editor.js.parsing.ast.JSStatementsNode;
import com.aptana.editor.js.parsing.ast.JSStringNode;
import com.aptana.editor.js.parsing.ast.JSSwitchNode;
import com.aptana.editor.js.parsing.ast.JSThisNode;
import com.aptana.editor.js.parsing.ast.JSThrowNode;
import com.aptana.editor.js.parsing.ast.JSTrueNode;
import com.aptana.editor.js.parsing.ast.JSTryNode;
import com.aptana.editor.js.parsing.ast.JSVarNode;
import com.aptana.editor.js.parsing.ast.JSWhileNode;
import com.aptana.editor.js.parsing.ast.JSWithNode;
import com.aptana.editor.js.parsing.lexer.JSTokenType;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.editor.js.sdoc.parsing.SDocParser;
import com.aptana.editor.js.vsdoc.parsing.VSDocReader;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IRecoveryStrategy;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseRootNode;

/**
 * This class is a LALR parser generated by
 * <a href="http://beaver.sourceforge.net">Beaver</a> v0.9.6.1
 * from the grammar specification "JS.grammar".
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JSParser extends Parser implements IParser {

	static final ParsingTables PARSING_TABLES = new ParsingTables(
		"U9pjNGTyKsNtFkScQPk4lLesj43PYE9YYmCdGq0KHQR8a2bRXWmHH97fOCZUIxPxxu6AY7i" +
		"huFgSuBxd$znxtpOtRKXxaz3o$xwR$CvztdcUSzxzjcbAbB6U7#AcT2Frvokf3tKnGbINpg" +
		"Ehw4hgHEVmE5fCvr8TkeSMy6mQGsDf78tdVRoNh#1BkIrVmETn1GvmVKxdbjoCp#Ow$3Xjf" +
		"4tyD3$3pz8ssiuly8ly4h$2rRaZTs0$FKf5w2b#af$Yv$Zv43$VoNVn7BwRv$8Ydi1ZU6os" +
		"HKMehMMHLfVy58YRFyXlvEPtuZyz4nT$KNuhJ$zliVcVoaqiY$WiAacbmBy$Blxo$76S$X#" +
		"CYxyYVvcd$$FX$oowWsRJdNGNpQ7AeJ3uluwBloeVZTF$et7nP$1tUVg$C8V$DUt#Su#u#3" +
		"FvF$7vpttYuY$9l#Jf$rBJVyMeDQIoOVxtYuk$AH$Bd$zKFyp$kJIFvcCsxkD1yV1hzVYlg" +
		"FvFYcfBBV0FYSl$I$dVcFo$b#wZ##aIy8#8R$tHE8h$j#PXIGtmZufl$T6yplsdk#bs6arz" +
		"gGBuLyJL$vbQ87P#FXVyg#FYhwOLZMFyNmF#zN6r$xLQoHFs$zGyso4T$9lYuk#cbOcv$Ij" +
		"HPV1lZuj$f5O#LdvkWTrh7##ER$$HgcJnKr8sVys8Ey234LO07tbFp4yz37xQJ5jegy5F3z" +
		"8EsacxRVopw3BAX3IJ8TJ6jgw7xm1xi0EO8uwxQ5LjTiwa7sa3tKmQtqX3#1gg3asNqTFKX" +
		"cjezVNzT35Y3QWXDuZRpeivKmOh6nwZbdoQt6Ip8uK6K3#wYQRG8bf8asaIjO2TNUa6keXk" +
		"eGaqaMxLgj7Lr3rkEv8Shz6U179Q6grMC7feEjhYCkf9lT1t$r7fZz1nWvCxnyF9Lw7lhiw" +
		"$j#QCg4AFqxvucMa4njFppfYvDRr0VyRDR8oTbntry6VKabxbgz733oUeX$#1HZMMqSCppB" +
		"7y9kPPs5Z6FFi2d55kFeaSoMZXQipifVV0#Ln1SVBv7EGYz14uezom4ZjZkJ9ND4U1Bzgjo" +
		"X7dBuO#xeLUFSupJs11RTC2NxIRL4Bzl8ZxysWU1fjyqMvF2UMy0pUDyJoF9oPmhaJbv4ju" +
		"Co$Z##W8trimd7mv$SPhqQhm56kmBzhDC06SzKpERdmTFqe1#XQC1cUKss3ydDpUv7o2Ul5" +
		"Fs8D$vz7q0pYZt00JmDd9v1HggPNLaj6VnxGaS4QvzJdYR4dj8SEu2tk0BMccLXpFFVGh2R" +
		"MZGLXfBw2Xz2Iz1CueDpr7dCjCffPq3EkwReqNhAzTcFX2Ufs#ekzfAVtCr$Bru8noktFeP" +
		"qlJhvQiiG$F94wcFPo26tHLpk3Qz0VVmBMu4Gy6PvGRNGoSsMy#dHlpcLoNolD8NiKRUHll" +
		"ultyBcEDZtQBYulp7Ah8pN5FQCL7#0V#ZV$HF5eHhRIM1iueDpTdd5eDt0JQK0PVJ3KPvsb" +
		"knrtf64$MAciPF8MdyMryEy#Y3716kQqvuipWMT1vcwbz0I$coP3xUGblaS$bCoeYdyedyh" +
		"5y91#2CyeDpGcdV23lotlohXoIT#IWl2q7#3L#KvxbW$mEloSx#GFPmP$AW$mrEAFSoXnnB" +
		"k75SXT$AzjbcsoLBR9PDib6sI3hPPsirT9bjQoHtxLoOgnzKMvYJZYrcbfjt8iRQATHNYyV" +
		"lnqFf#Wuwnt6h9n2JUXqywpNcCwWH#W6FYFxhDU8JgCpwImwctQ1CPbUeJVeMVg67gDtw7t" +
		"wd3wcl#XVaKHfIfWUutLTGhLTdr1jUnEgxI4QmD$aLnk$JCGFy$ly2H#WR$XLGnj$bU3sT$" +
		"MuUbmzhXvNZwidNteIjN#TQllWgUQNgyVLu#fnzJZK4#cdOoKY$4wyCtMarhI6ob1nXtKkZ" +
		"BdEkrX$FrHtukOvxiZJmcA7Q0bzaEiUVRupLgrE0NctognZVmtCONa3QaXhWJzfjV1SH#lf" +
		"E5qHYsMyWulHOAswePIlojL2oxC2N9nBS4aknQMv39VbSfpAQLmU6KyKM6$RNozc$#RLFhx" +
		"3VriLsgVVWlttq6k5OIdtT68fVqQa#RXxeRJff$d#$8kXePnPvyj2iVGxHxr$mAppUw5O#h" +
		"SZIpynHqp4Js6Sz77Qq4dlSoMpJjz2iNIeenarmAnpKw5OUhCZInymwwmf54jNEhBqSxFEX" +
		"aAnTAiZI$ymw#mi54jVZwH1gn3sIOwrsSMDFPpuKCoimodNAyAdgIA#M9sX#7YXjDEFJdfK" +
		"Ip7hV5yebXvsj9fRTNuj54lr62pzkp0irJJdbch52iNIO0oMbYeKIrCTxELfgaubXwp5Oh2" +
		"qT0nrMiLGfqCCTNh7K6TK37LkYw7EIkTrk6WCTQh4KAUQypfQenZgD3RhE9k1goFlTxRNL8" +
		"SQP#IfSNh2RNmWuJQkSBwIdU1rUS8qlPqeJTmxOPe69apJy8HfMfumJUiIfkcBX6dwEM6Q$" +
		"aoK9eqIficVC4qb76booxfbtR9ksV#Milc#tN2zKy9UjwnRrYthblrVB6lyfZhtAt9gr9TM" +
		"aOj5gfipDV9lga#GMYmIKyvK9ngfexaVTSOE5GfTXTQvcgwXRdGjNKVTwNhgOVxrhD4#vs3" +
		"N6eJM74n3Q2WDe#7cNwAEf57OmTHtNwXMdOIslXMjFfMcqNIw3VVysscc#GqXclg6aBjfhe" +
		"tL#4lz1RIG5c5FjFw2DojFircKIcbnys#8o5zm$jTpw7yBcqNBQKNS$CqBsVzc#VI$BcRgw" +
		"PYhJJ0tczh2yV8lBMJ$5yVW$we4#h#oaFrVdc$$inWlfoiIwFy3CVhVqDmdMfmaVhl$qMqf" +
		"BFyleKlfCgp69sVz2VcVboKdP$t9s$$UkCdrnRaXNlxjXSpFSOt$xN7pUwBsVzvsnCl$O5p" +
		"jtpHk$jfnyIyzIVlFgdol0F7oryK8N0dneYMyjdKzzmvWZRXbjCPcsn9QIpjf5ssZZRINDj" +
		"DgQ6Ag1KrUdAeVXZ3vA0cnUEriW7TjPSC6d3kDMCYE7U3HC9QznZSyKZAbK1tO6QGYL9IAK" +
		"N4gGIND4jlYjiFv6hqcWPoMHgiLh1xQ0B#CSxYNCbNw3ggV0CvsQ67DyOnWMXytip6UAXGA" +
		"ip5sKXqn6osV2EOGawNH6ij63njZcJ7FmiUoCSyASYJtflRK0ROLBATnlofOJkDiQuo2n75" +
		"cgb6LXbavBwe51UcdOKyYRkzEE8tUDDgtu3ZNcJxMfaq5nfbfCXgUrYyWpap5oM0iACwiqM" +
		"E8fm0vhVvSbj3#J5FlPRJ5n2NAgxMqcshYlHedYKmpT38ubq2hDLxQcnQiDfbgWdCpB4Wyf" +
		"yUSyuod1kr6txN0$7W0iTecQvsJucVMs#1v08pDbPUXzydZR8uRPZFS4vgUPCwbU2$6UnNU" +
		"A$6kZVviOV8qD$cNegyN9v1pCVGjXLv3UssqQml8Egg7ytmjl3NpQRqJnrahrvlDPmNp36h" +
		"5B4aaf#5BxGZSnliXy#c1rubSXpJSIZt6d90yNd5pKlPPpxWb6MSzuxGGVjOhPNxsjIoLer" +
		"GmbaThLpI#HvQgK5MgHjKfdJAeHW8r9KYFsJwPhhR2QdzNZwl7rUFgSVMuUbmzhXvNZwl7r" +
		"VF$M4$#VoVTp$o4ym0QM61rDDYjgJlnx1ntvArXiLJQOtYPeypiKzcxS4jpM6xqgcRU$uod" +
		"qR$rQLzCbjMbUiPF6asjxNArK7OhKajgHQtfF6f3vzC5T25TH6tfue9imnmzvtnykvRcPQa" +
		"Z3GKxW#9hKzTIj$TTIrrBJxaPfMMNXYUnMchMpvHShzoVfifTnh9KuKQtHnDdgODHu5hgMb" +
		"feUtb1M#hMSUl4NCVH3AIqoFkTRQRKSJNcRoKxWQQQhYPNawlfrDRabdNBkcNTilyrPPsS7" +
		"4y5UzsoRbcth5lsVx4ioaGulqPCZVuoVbCTgMxErCY$gJv1Qhs8J3bI7MaaxVHRpU$W2Vv9" +
		"LAIA4ROBNkT0pbFX3Whl9afR52gJSQQ5QTzQg2KJbM#5m2BWGZB#XmrLDluxa01$8k8AH5K" +
		"C$py3LaAy1l1qrEiFX4s##u7N8$uwS6I8fyeT5lhwvRGpBq6Tfah72eGdgl1UPUzrI6kE#6" +
		"j4$bMsEiiWlOWygQ6qYdzQw5#jT7oXTEYXCfvMDXtR44VRUi#suaNQA1nWOP9FwVaZLCVR8" +
		"xVzLOk1SuqLBls1ic6jXQcFsUhEHzaQLfXwvhzzAdPHE2aydTT4h#Sv6XvFscUpPN$kylwF" +
		"5CzwXMz6rkjBXM0y#8RcruVm#aNcAhJPatIHmfveewQmPGFoFm57IwAYcfLNj1Ro1q7GzrK" +
		"xeCn6X3UhUXZ3LViXxMEKkzHAImuIbUb6LFOQeafLa3x4IYzzhSe$LyMxg$ZD5fRTfdG#ez" +
		"BBGlS4w8OzHUTRQIdJ45PZr5yP#UXR$wUGhh3v3wLtdwh$hAhpICZVgl0lK0uwO7TH7N7Cm" +
		"wJlbBvd4CPyz6#tue66eNg1Co1D8AKWPuRICpeflGVpsmzsWOsZKFTRSFvBLCtWAGqi5yht" +
		"zRQmMdtafIfRcYay2#bjW5YFV0CG7e9mcieRQkFmmAyADbzGhjfqu30L7uxuOk3Da948FuZ" +
		"4pyp$8qNLNi0ynbhaAu7mHy3ga3u8$mAzZq2#8AgUWhJba9SG3bil4MyI7W#WlOkbGov2l$" +
		"og#kStrVvV8dsWilaGnixvYCzHzR0#5jkfocDCbRiQ#PWBnGwfV8o5Od#fV3HafRk8YbTHU" +
		"HYpnTKQK$tIa2qPM4#hzrHrQY8y6cKt0#y4RWUk0ku0FWJS1ToFDa6xLFyA21kg$mMV3qFG" +
		"5iP$qj7Aenp6OW1b0XYpwMYpm6$0MigEpx6dL0GNncomiaexZlpcLhZSLjYz4xRsKtaT4LR" +
		"hUygho7iNSJL70bSYV425$u0En0ETa7RSIYlz3jBGFe7EH2Mo#rwLnrWBOEm7Cz3cFc3MEj" +
		"G2OOPe48oPe1TI5r8FWl4NR01f26a4EGtI688zAeY#I9y4dLt0XNKZY7bIeZVmJE0C8CPe4" +
		"Fj9ElQzm5K8juMS2#c1j4r8mzmj0GmsWs1lAOEsJuKDbVv1EjeyQDY5CPV#5ChUWJIqTx0l" +
		"v0QYafZBGQm$WTa8O$qAmcS$zjdA6ASbqRz1p8i0#hFaL8G78kyyw0PVPOo$akkGXdKjWB5" +
		"IyXM4RqB#6EGXhUH$41#Cl3b4fJ3dWvWpmLd87uyzlJNAp4KwvbzmACfWxo#5zIuu3FbRaB" +
		"S7SRH74FCgO9J3E0gEW61#fMDS1#OX3VCgYBcMWVPD$nvfpo4Dwq1mbEe$hAT1xFc1vu5B8" +
		"C#4#Y$uhBq$LThpQamRVRS2c1RAyw8zVMrpr$5#dnN6kUD0ABtyhb2Or5fT$lLGMfdTAkr#" +
		"2prDb3w5FkF$n7zYhAj8mtptO7noDSmFxFbid6dGXeoz8Nb5I6VAenRoafnsvYMee$RFKk$" +
		"PV3WI2ciuJvNwnLu7$lvhd772zPHk41wlmD5vFTZdF1XJdbUKtnw5KtBegHXTJ#cseR1tOt" +
		"YUxwqSkjIvqjiYcixmEgMUK7NRXwSdZOXUpzipHxoFBNp2Ve8$lHJVWFpQw4JG9ez6in$y2" +
		"mr47wjzF6cBmiEsCcz4gFUDXSdLR7vYBfV1$bS6wrfoj$pPLsPSXBG5IlV2K5hoVbk#BUpT" +
		"Ow5VdPVyrIrCEMuhVrIr1UPM2lRViXcXF3$M8p$E4VxEAlvIP3lzDiuavNk5QVdnCRA#ihk" +
		"KZcKARUTC$mEgJCHnY$D4YYft06N6HYv34v5$C3pDCp8yxhNTZPAm7mICyn7E4SdOt$sP4E" +
		"m9oMyZ3Uj$0FjG2YJG6e9z8MMALJPm0SB7ShHLezps965$Iu5#lpgZ1MndYvG9jdR1Vf#or" +
		"vRtaoqFzxcoHw2ZheshRoXSBZshxLIUkSPXREyEfIM$YKUJK5nJwx2crccFGjyCPVSOw$oG" +
		"tCCw7wGKjVRbm8GioT7kgZxRE5BKtI0mFfJ6gbmvt1lB6HobGdc1osq#OVyBpB3bFMNBUqs" +
		"bPPtbBaJQOIiS33jBXkhuegvZJWHd0tEkeNr$LButKjn3R6MQHQWtMfMtdRjJSUP9nVgH#W" +
		"OYsqFfQG5LziTG6ZUoXMqwqh3siAqks#gaeV$Ing6VRNKrUztTYDlgQigd5FKpYJIShShxC" +
		"JvizJss#kLnFlJOwdjitEMljz2hsiVV7MbZLPfDH#3ph71EGPkMFN5UXRSKghkPThe99F$A" +
		"t$Ap$6I6VuIyBY$Z#PB#lRIJAuZqxtIS3dMiYZfEkEA5120uYGjEvuARhE0KAxZDILEv6C#" +
		"sg7UfN28NoFdIHiwJrj9AMaeBQIvNIaVfRlAW7$KFzD#bjnaReB#F9yBINsx44pD7SFALpl" +
		"g$CaW6YaTco6tAMjmGv5S8R1ASAFNtv0$notIP9bDbYba2xWdEWOApeOoGOMRQK2akPQIqb" +
		"9AI#a7zRVs0$fR#fevH99CXasISZ9OAKaagIdMf9bMbYbIMJAahTQIsD9JJf94qaRFbB3bJ" +
		"SA8Mh9g2MIsto1WPAwDalB9jkFmWtyjtygry8qVbY1oMh#Kh#L9mevEt86yA6FLtoVQIvyD" +
		"YpsI7dhQbFWN1YLTMGbP0S3CKt8vaMNQ9fO9RlConrS7jKt3QbNiWYsIXcJPVvfbuf$cSBJ" +
		"Xzo#roHtQTcVgtShbS1Xk$qG$hN#jVwL$gN#YVwv$fd#gVw1$h7#aVwXWd#d6hlFwZT93sS" +
		"erP5pSMwQeVG#e$qjDCmLr2#aetesUbZ$JG$qP8atw2#v2aI3AUnGJhfAHA6fur1Fk9r1FS" +
		"YgInO6pAENAkO4#H2sM$B03E5Up2SX$aNbF$ayhg9ypduzbUF6O#7rMnHypdmx3ha7vKsW6" +
		"FwCQumIrI$ylyOI594gG8f2maNJAad0J5ful#f$Bp1$rNfUrLUH7F5rJi8VEvJyNsciyzye" +
		"gAxpBOpD1k$HrvBZJ7v3LhxE9fZXKpP1ih9tg5HiS9IqpBgqHihooV46gN7Vh7bdzCq9xof" +
		"tIINeBpW5nhfUi$OtrjJ0yJGuYsaV6zZqnh81t9IzUG1yAq7UdR4Nx85AO7oVhUbviGjiG8" +
		"QxPu$kJER36#MrMpnVCdWyZuLbyFhGUkHtm7miRtMsu2RW9kCCL3wu3hWBTGCiH7Tm4dcEA" +
		"39Acm8IasDCHlGqC2sVbpW7DGVmxgMs53KcneYD#6XWIoyoV2vecmQH9m4d0oS3BmLkYy6p" +
		"8LUgUYh2KfIloq5kNMmfzbABiCVRGPySq8Ju7CXKo3JCyMB#s6hYqeSpNg679bBl6GyLtrd" +
		"Kqqn0lnqHxaxK7w7gJjGTfMXBSYhIkmAzAw8cqludiHtWdPXN2MN9KjNeZ7b968ZqHyf1ds" +
		"K3V4kq7N3E0COFTiyK8yD0fXIxmGZ$f6B0$jCuNfMYKtAxdU58rk1zuEN0uqv3fJD9e9d0d" +
		"i0inBUfZYeS70mK3ZMr#pfASf7Xe2709S1JIaboaU6WeS2bmCp9BUfdXe670OS1vm7d09SI" +
		"vPhQIFAHuQ3XmEd0#S3rmAj4jVfFK5ZWQE1Yu0BW3U0Bm1E0Ou1hWGk13O3sXymc0iS2nm4" +
		"N0Hy4RWZS1nm770Uu3t00S01m1704S0xmNU2nneYezc0MS1xmVU3#mFx8yonlSr6z#PV1$m" +
		"Fk0Ae1EPZJh6fzV70yS3Dm8t0ZkTG6xxFz#JLsO=");

	// suppress parser error reporting and let the custom error recovery mechanism handle it
	private static class JSEvents extends Events
	{
		public void scannerError(Scanner.Exception e)
		{
		}

		public void syntaxError(Symbol token)
		{
		}

		public void unexpectedTokenRemoved(Symbol token)
		{
		}

		public void missingTokenInserted(Symbol token)
		{
		}

		public void misspelledTokenReplaced(Symbol token)
		{
		}

		public void errorPhraseRemoved(Symbol error)
		{
		}
	}

	private final IRecoveryStrategy[] recoveryStrategies;
	private JSScanner fScanner;

	/**
	 * attachPostDocumentationBlocks
	 * 
	 * @param root
	 * @param source
	 */
	private void attachPostDocumentationBlocks(JSParseRootNode root, String source)
	{
		// process each post-documentation block
		for (DocumentationBlock block : this.parsePostDocumentationBlocks())
		{
			int index = block.getStart() - 1;

			while (index >= 0 && Character.isWhitespace(source.charAt(index)))
			{
				index--;
			}

			IParseNode node = root.getNodeAtOffset(index);

			if (node instanceof JSNode)
			{
				switch (node.getNodeType())
				{
					case JSNodeTypes.STATEMENTS:
						IParseNode parent = node.getParent();

						if (parent.getNodeType() == JSNodeTypes.FUNCTION)
						{
							((JSNode) parent).setDocumentation(block);
						}
						break;

					default:
						((JSNode) node).setDocumentation(block);
						break;
				}
			}
		}
	}

	/**
	 * attachPreDocumentationBlocks
	 * 
	 * @param root
	 * @param source
	 */
	private void attachPreDocumentationBlocks(JSParseRootNode root, String source)
	{
		// process each pre-documentation block
		for (DocumentationBlock block : this.parsePreDocumentationBlocks())
		{
			int index = block.getEnd() + 1;

			while (index < source.length() && Character.isWhitespace(source.charAt(index)))
			{
				index++;
			}

			IParseNode node = root.getNodeAtOffset(index);

			if (node instanceof JSNode)
			{
				IParseNode statement = ((JSNode) node).getContainingStatementNode();

				if (statement instanceof JSAssignmentNode)
				{
					((JSNode) statement.getLastChild()).setDocumentation(block);
				}
				else
				{
					switch (node.getNodeType())
					{
						case JSNodeTypes.VAR:
							// associate documentation with first declared variable's value
							JSVarNode varNode = (JSVarNode) node;
							((JSNode) varNode.getFirstChild().getLastChild()).setDocumentation(block);
							break;

						case JSNodeTypes.IDENTIFIER:
							IParseNode parent = node.getParent();

							if (parent instanceof JSNameValuePairNode)
							{
								// associate documentation with property's value
								JSNameValuePairNode entry = (JSNameValuePairNode) parent;
								((JSNode) entry.getValue()).setDocumentation(block);
							}
							break;

						default:
							((JSNode) node).setDocumentation(block);
							break;
					}
				}
			}
		}
	}

	/**
	 * buildVSDocXML
	 *
	 * @param lines
	 * @return
	 */
	private String buildVSDocXML(List<Symbol> lines)
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<docs>\n");

		for (Symbol line : lines)
		{
			String text = (String) line.value;

			buffer.append(text.substring(3));
			buffer.append("\n");
		}

		buffer.append("</docs>");

		return buffer.toString();
	}

	/**
	 * getNextSymbolIndex
	 * 
	 * @return
	 */
	protected Symbol getLastSymbol()
	{
		Symbol result = null;

		if (0 <= this.top && this.top < this._symbols.length)
		{
			result = this._symbols[this.top];
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.IParser#parse(com.aptana.parsing.IParseState)
	 */
	public synchronized IParseRootNode parse(IParseState parseState) throws java.lang.Exception
	{
		// grab source
		char[] characters = parseState.getSource();

		// make sure we have some source
		String source = (characters != null) ? new String(characters) : "";

		// create scanner and send source to it
		fScanner = new JSScanner();
		fScanner.setSource(source);
	
		try
		{
			// parse
			ParseRootNode result = (ParseRootNode) parse(fScanner);
			int start = parseState.getStartingOffset();
			int end = start + source.length();
			result.setLocation(start, end);

			// store results in the parse state
			parseState.setParseResult(result);
	
			// TODO: We probably don't need documentation nodes in all cases. For
			// example, the outline view probably doesn't rely on them. We should
			// include a flag (maybe in the parseState) that makes this step
			// optional.
	
			// attach documentation
			if (result instanceof JSParseRootNode)
			{
				JSParseRootNode root = (JSParseRootNode) result;
	
				attachPreDocumentationBlocks(root, source);
				attachPostDocumentationBlocks(root, source);
			}
			
			return result;
		}
		finally
		{
			// clear scanner for garbage collection
			fScanner = null;
		}
	}

	/**
	 * parsePostDocumentationBlocks
	 * 
	 * @return
	 */
	protected List<DocumentationBlock> parsePostDocumentationBlocks()
	{
		VSDocReader parser = new VSDocReader();
		List<DocumentationBlock> blocks = new ArrayList<DocumentationBlock>();

		for (Symbol doc : fScanner.getVSDocComments())
		{
			ByteArrayInputStream input = null;

			try
			{
				List<Symbol> lines = (List<Symbol>) doc.value;
				String source = this.buildVSDocXML(lines);

				input = new ByteArrayInputStream(source.getBytes());

				parser.loadXML(input);

				DocumentationBlock result = parser.getBlock(); 

				if (result != null)
				{
					if (lines.size() > 0)
					{
						result.setRange(lines.get(0).getStart(), lines.get(lines.size() - 1).getEnd());
					}

					blocks.add(result);
				}
			}
			catch (java.lang.Exception e)
			{
			}
			finally
			{
				try
				{
					if (input != null)
					{
						input.close();
					}
				}
				catch (IOException e)
				{
				}
			}
		}

		return blocks;
	}

	/**
	 * parsePreDocumentationBlocks
	 * 
	 * @return
	 */
	protected List<DocumentationBlock> parsePreDocumentationBlocks()
	{
		SDocParser parser = new SDocParser();
		List<DocumentationBlock> blocks = new ArrayList<DocumentationBlock>();

		for (Symbol doc : fScanner.getSDocComments())
		{
			try
			{
				Object result = parser.parse((String) doc.value, doc.getStart());

				if (result instanceof DocumentationBlock)
				{
					blocks.add((DocumentationBlock) result);
				}
			}
			catch (java.lang.Exception e)
			{
			}
		}

		return blocks;
	}

	/*
	 * (non-Javadoc)
	 * @see beaver.Parser#recoverFromError(beaver.Symbol, beaver.Parser.TokenStream)
	 */
	@Override
	protected void recoverFromError(Symbol token, TokenStream in) throws IOException, Parser.Exception
	{
		boolean success = false;

		if (this.recoveryStrategies != null)
		{
			// NOTE: Consider building a Map<Object,List<IRecoveryStrategy>> which
			// would allow us to reduce the number of recovery strategies that will
			// be attempted based on the last symbol on the stack. We may need
			// catch-all cases: 1) try these before the mapped strategies, 2)
			// try the strategies, 3) try these after the mapped strategies
			for (IRecoveryStrategy strategy : this.recoveryStrategies)
			{
				if (strategy.recover(this, token, in))
				{
					success = true;
					break;
				}
			}
		}

		if (success == false)
		{
			super.recoverFromError(token, in);
		}
	}

	public JSParser() {
		super(PARSING_TABLES);


		report = new JSEvents();

		recoveryStrategies = new IRecoveryStrategy[] {
			new IRecoveryStrategy() {
				public boolean recover(IParser parser, Symbol token, TokenStream in) throws IOException
				{
					boolean result = false;

					Symbol term = new Symbol(JSTokenType.SEMICOLON.getIndex(), token.getStart(), token.getStart() - 1, ";");
					Simulator sim = new Simulator();

					in.alloc(2);
					in.insert(term, token);
					in.rewind();

					if (sim.parse(in))
					{
						result = true;

						in.rewind();

						report.missingTokenInserted(term);
					}

					return result;
				}
			},
			new IRecoveryStrategy() {
				public boolean recover(IParser parser, Symbol token, TokenStream in) throws IOException
				{
					Symbol lastSymbol = getLastSymbol();
					int type = lastSymbol.getId();
					boolean result = false;

					if (type == JSTokenType.DOT.getIndex() || type == JSTokenType.NEW.getIndex())
					{
						Symbol term1 = new Symbol(JSTokenType.IDENTIFIER.getIndex(), token.getStart(), token.getStart() - 1, "");
						Symbol term2 = new Symbol(JSTokenType.SEMICOLON.getIndex(), token.getStart(), token.getStart() - 1, ";");

						Simulator sim = new Simulator();

						in.alloc(3);
						in.insert(token);
						in.insert(term2);
						in.insert(term1);
						in.rewind();

						if (sim.parse(in))
						{
							result = true;

							in.rewind();

							report.missingTokenInserted(term1);
							report.missingTokenInserted(term2);
						}
					}

					return result;
				}
			},
			new IRecoveryStrategy() {
				public boolean recover(IParser parser, Symbol token, TokenStream in) throws IOException
				{
					Symbol lastSymbol = getLastSymbol();
					int type = lastSymbol.getId();
					boolean result = false;

					if (type == JSTokenType.DOT.getIndex() || type == JSTokenType.NEW.getIndex())
					{
						Symbol term1 = new Symbol(JSTokenType.IDENTIFIER.getIndex(), token.getStart(), token.getStart() - 1, "");

						Simulator sim = new Simulator();

						in.alloc(2);
						in.insert(token);
						in.insert(term1);
						in.rewind();

						if (sim.parse(in))
						{
							result = true;

							in.rewind();

							report.missingTokenInserted(term1);
						}
					}

					return result;
				}
			},
			new IRecoveryStrategy() {
				public boolean recover(IParser parser, Symbol token, TokenStream in) throws IOException
				{
					Symbol lastSymbol = getLastSymbol();
					boolean result = false;

					if (top >= 2)
					{
						Symbol symbol1 = _symbols[top - 2];
						Symbol symbol2 = _symbols[top - 1];

						if (lastSymbol.getId() == JSTokenType.COMMA.getIndex() && symbol2.value instanceof List<?> && symbol1.getId() == JSTokenType.LPAREN.getIndex())
						{
							Symbol term = new Symbol(JSTokenType.IDENTIFIER.getIndex(), token.getStart(), token.getStart() - 1, "");
							Simulator sim = new Simulator();

							in.alloc(2);
							in.insert(term, token);
							in.rewind();

							if (sim.parse(in))
							{
								result = true;

								in.rewind();

								report.missingTokenInserted(term);
							}
						}
					}

					return result;
				}
			}
		};
	}

	protected Symbol invokeReduceAction(int rule_num, int offset) {
		switch(rule_num) {
			case 0: // Program = SourceElements.p
			{
					final Symbol _symbol_p = _symbols[offset + 1];
					final ArrayList _list_p = (ArrayList) _symbol_p.value;
					final JSNode[] p = _list_p == null ? new JSNode[0] : (JSNode[]) _list_p.toArray(new JSNode[_list_p.size()]);
					
			return new JSParseRootNode(p);
			}
			case 1: // Program = 
			{
					
			return new JSParseRootNode();
			}
			case 2: // SourceElements = SourceElements SourceElement
			{
					((ArrayList) _symbols[offset + 1].value).add(_symbols[offset + 2].value); return _symbols[offset + 1];
			}
			case 3: // SourceElements = SourceElement
			{
					ArrayList lst = new ArrayList(); lst.add(_symbols[offset + 1].value); return new Symbol(lst);
			}
			case 5: // FunctionDeclaration = FUNCTION IDENTIFIER.ident FunctionParameters.params FunctionBody.body
			{
					final Symbol ident = _symbols[offset + 2];
					final Symbol _symbol_params = _symbols[offset + 3];
					final JSNode params = (JSNode) _symbol_params.value;
					final Symbol _symbol_body = _symbols[offset + 4];
					final JSNode body = (JSNode) _symbol_body.value;
					
			return new JSFunctionNode(
				new JSIdentifierNode(ident),
				params,
				body
			);
			}
			case 6: // FunctionExpression = FUNCTION.f FunctionParameters.params FunctionBody.body
			{
					final Symbol f = _symbols[offset + 1];
					final Symbol _symbol_params = _symbols[offset + 2];
					final JSNode params = (JSNode) _symbol_params.value;
					final Symbol _symbol_body = _symbols[offset + 3];
					final JSNode body = (JSNode) _symbol_body.value;
					
			return new JSFunctionNode(
				new JSEmptyNode(f),
				params,
				body
			);
			}
			case 8: // FunctionParameters = LPAREN RPAREN
			{
					
			return new JSParametersNode();
			}
			case 9: // FunctionParameters = LPAREN FormalParameterList.params RPAREN
			{
					final Symbol _symbol_params = _symbols[offset + 2];
					final JSNode params = (JSNode) _symbol_params.value;
					
			return params;
			}
			case 10: // FormalParameterList = FormalParameterList.list COMMA IDENTIFIER.ident
			{
					final Symbol _symbol_list = _symbols[offset + 1];
					final JSNode list = (JSNode) _symbol_list.value;
					final Symbol ident = _symbols[offset + 3];
					
			JSNode identifier = new JSIdentifierNode(ident);
			
			// add identifier to existing list
			list.addChild(identifier);
			
			return list;
			}
			case 11: // FormalParameterList = IDENTIFIER.ident
			{
					final Symbol ident = _symbols[offset + 1];
					
			JSNode identifier = new JSIdentifierNode(ident);
			
			return new JSParametersNode(identifier);
			}
			case 12: // FunctionBody = LCURLY RCURLY
			{
					
			return new JSStatementsNode();
			}
			case 13: // FunctionBody = LCURLY SourceElements.s RCURLY
			{
					final Symbol _symbol_s = _symbols[offset + 2];
					final ArrayList _list_s = (ArrayList) _symbol_s.value;
					final JSNode[] s = _list_s == null ? new JSNode[0] : (JSNode[]) _list_s.toArray(new JSNode[_list_s.size()]);
					
			return new JSStatementsNode(s);
			}
			case 16: // Statement = VAR.v VariableDeclarationList.l SEMICOLON
			{
					final Symbol v = _symbols[offset + 1];
					final Symbol _symbol_l = _symbols[offset + 2];
					final ArrayList _list_l = (ArrayList) _symbol_l.value;
					final JSNode[] l = _list_l == null ? new JSNode[0] : (JSNode[]) _list_l.toArray(new JSNode[_list_l.size()]);
					
			JSNode node = new JSVarNode(v, l);
			node.setSemicolonIncluded(true);
			return node;
			}
			case 17: // Statement = Expression_NoLBF.e SEMICOLON
			{
					final Symbol _symbol_e = _symbols[offset + 1];
					final JSNode e = (JSNode) _symbol_e.value;
					
			e.setSemicolonIncluded(true);
			return e;
			}
			case 28: // Statement = SEMICOLON.s
			{
					final Symbol s = _symbols[offset + 1];
					
			JSNode node = new JSEmptyNode(s);
			node.setSemicolonIncluded(true);
			return node;
			}
			case 29: // Statement = error
			{
					
			return new JSErrorNode();
			}
			case 32: // Statement_NoIf = VAR.v VariableDeclarationList.l SEMICOLON
			{
					final Symbol v = _symbols[offset + 1];
					final Symbol _symbol_l = _symbols[offset + 2];
					final ArrayList _list_l = (ArrayList) _symbol_l.value;
					final JSNode[] l = _list_l == null ? new JSNode[0] : (JSNode[]) _list_l.toArray(new JSNode[_list_l.size()]);
					
			JSNode node = new JSVarNode(v, l);
			node.setSemicolonIncluded(true);
			return node;
			}
			case 33: // Statement_NoIf = Expression_NoLBF.e SEMICOLON
			{
					final Symbol _symbol_e = _symbols[offset + 1];
					final JSNode e = (JSNode) _symbol_e.value;
					
			e.setSemicolonIncluded(true);
			return e;
			}
			case 44: // Statement_NoIf = SEMICOLON.s
			{
					final Symbol s = _symbols[offset + 1];
					
			return new JSEmptyNode(s);
			}
			case 45: // Statement_NoIf = error
			{
					
			return new JSErrorNode();
			}
			case 46: // Block = LCURLY RCURLY
			{
					
			return new JSStatementsNode();
			}
			case 47: // Block = LCURLY StatementList.a RCURLY
			{
					final Symbol _symbol_a = _symbols[offset + 2];
					final ArrayList _list_a = (ArrayList) _symbol_a.value;
					final JSNode[] a = _list_a == null ? new JSNode[0] : (JSNode[]) _list_a.toArray(new JSNode[_list_a.size()]);
					
			return new JSStatementsNode(a);
			}
			case 48: // StatementList = StatementList Statement
			{
					((ArrayList) _symbols[offset + 1].value).add(_symbols[offset + 2].value); return _symbols[offset + 1];
			}
			case 49: // StatementList = Statement
			{
					ArrayList lst = new ArrayList(); lst.add(_symbols[offset + 1].value); return new Symbol(lst);
			}
			case 50: // VariableDeclarationList = VariableDeclarationList COMMA VariableDeclaration
			{
					((ArrayList) _symbols[offset + 1].value).add(_symbols[offset + 3].value); return _symbols[offset + 1];
			}
			case 51: // VariableDeclarationList = VariableDeclaration
			{
					ArrayList lst = new ArrayList(); lst.add(_symbols[offset + 1].value); return new Symbol(lst);
			}
			case 52: // VariableDeclarationList_NoIn = VariableDeclarationList_NoIn COMMA VariableDeclaration_NoIn
			{
					((ArrayList) _symbols[offset + 1].value).add(_symbols[offset + 3].value); return _symbols[offset + 1];
			}
			case 53: // VariableDeclarationList_NoIn = VariableDeclaration_NoIn
			{
					ArrayList lst = new ArrayList(); lst.add(_symbols[offset + 1].value); return new Symbol(lst);
			}
			case 54: // VariableDeclaration = IDENTIFIER.i
			{
					final Symbol i = _symbols[offset + 1];
					
			return new JSDeclarationNode(new JSIdentifierNode(i), null, new JSEmptyNode(i));
			}
			case 55: // VariableDeclaration = IDENTIFIER.i EQUAL.e AssignmentExpression.expression
			{
					final Symbol i = _symbols[offset + 1];
					final Symbol e = _symbols[offset + 2];
					final Symbol _symbol_expression = _symbols[offset + 3];
					final JSNode expression = (JSNode) _symbol_expression.value;
					
			return new JSDeclarationNode(new JSIdentifierNode(i), e, expression);
			}
			case 56: // VariableDeclaration_NoIn = IDENTIFIER.i
			{
					final Symbol i = _symbols[offset + 1];
					
			return new JSDeclarationNode(new JSIdentifierNode(i), null, new JSEmptyNode(i));
			}
			case 57: // VariableDeclaration_NoIn = IDENTIFIER.i EQUAL.e AssignmentExpression_NoIn.expression
			{
					final Symbol i = _symbols[offset + 1];
					final Symbol e = _symbols[offset + 2];
					final Symbol _symbol_expression = _symbols[offset + 3];
					final JSNode expression = (JSNode) _symbol_expression.value;
					
			return new JSDeclarationNode(new JSIdentifierNode(i), e, expression);
			}
			case 58: // IfStatement = IF LPAREN.l Expression.e RPAREN.r Statement_NoIf.sn ELSE Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 4];
					final Symbol _symbol_sn = _symbols[offset + 5];
					final JSNode sn = (JSNode) _symbol_sn.value;
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSIfNode(l, e, r, sn, s);
			}
			case 59: // IfStatement = IF LPAREN.l Expression.e RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 4];
					final Symbol _symbol_s = _symbols[offset + 5];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSIfNode(l, e, r, s, new JSEmptyNode(s));
			}
			case 60: // IfStatement_NoIf = IF LPAREN.l Expression.e RPAREN.r Statement_NoIf.sn ELSE Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 4];
					final Symbol _symbol_sn = _symbols[offset + 5];
					final JSNode sn = (JSNode) _symbol_sn.value;
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSIfNode(l, e, r, sn, s);
			}
			case 61: // IterationStatement = DO Statement.s WHILE LPAREN.l Expression.e RPAREN.r SEMICOLON
			{
					final Symbol _symbol_s = _symbols[offset + 2];
					final JSNode s = (JSNode) _symbol_s.value;
					final Symbol l = _symbols[offset + 4];
					final Symbol _symbol_e = _symbols[offset + 5];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 6];
					
			JSNode node = new JSDoNode(s, l, e, r);
			node.setSemicolonIncluded(true);
			return node;
			}
			case 62: // IterationStatement = WHILE LPAREN.l Expression.e RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 4];
					final Symbol _symbol_s = _symbols[offset + 5];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSWhileNode(l, e, r, s);
			}
			case 63: // IterationStatement = FOR LPAREN.l SEMICOLON.s1 SEMICOLON.s2 RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol s1 = _symbols[offset + 3];
					final Symbol s2 = _symbols[offset + 4];
					final Symbol r = _symbols[offset + 5];
					final Symbol _symbol_s = _symbols[offset + 6];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSEmptyNode(l), s1, new JSEmptyNode(s1), s2, new JSEmptyNode(s2), r, s);
			}
			case 64: // IterationStatement = FOR LPAREN.l SEMICOLON.s1 SEMICOLON.s2 Expression.a RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol s1 = _symbols[offset + 3];
					final Symbol s2 = _symbols[offset + 4];
					final Symbol _symbol_a = _symbols[offset + 5];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 6];
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSEmptyNode(l), s1, new JSEmptyNode(s1), s2, a, r, s);
			}
			case 65: // IterationStatement = FOR LPAREN.l SEMICOLON.s1 Expression.c SEMICOLON.s2 RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol s1 = _symbols[offset + 3];
					final Symbol _symbol_c = _symbols[offset + 4];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 5];
					final Symbol r = _symbols[offset + 6];
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSEmptyNode(l), s1, c, s2, new JSEmptyNode(s2), r, s);
			}
			case 66: // IterationStatement = FOR LPAREN.l SEMICOLON.s1 Expression.c SEMICOLON.s2 Expression.a RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol s1 = _symbols[offset + 3];
					final Symbol _symbol_c = _symbols[offset + 4];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 5];
					final Symbol _symbol_a = _symbols[offset + 6];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSEmptyNode(l), s1, c, s2, a, r, s);
			}
			case 67: // IterationStatement = FOR LPAREN.l Expression_NoIn.i SEMICOLON.s1 SEMICOLON.s2 RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol s1 = _symbols[offset + 4];
					final Symbol s2 = _symbols[offset + 5];
					final Symbol r = _symbols[offset + 6];
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, i, s1, new JSEmptyNode(s1), s2, new JSEmptyNode(s2), r, s);
			}
			case 68: // IterationStatement = FOR LPAREN.l Expression_NoIn.i SEMICOLON.s1 SEMICOLON.s2 Expression.a RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol s1 = _symbols[offset + 4];
					final Symbol s2 = _symbols[offset + 5];
					final Symbol _symbol_a = _symbols[offset + 6];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, i, s1, new JSEmptyNode(s1), s2, a, r, s);
			}
			case 69: // IterationStatement = FOR LPAREN.l Expression_NoIn.i SEMICOLON.s1 Expression.c SEMICOLON.s2 RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol s1 = _symbols[offset + 4];
					final Symbol _symbol_c = _symbols[offset + 5];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 6];
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, i, s1, c, s2, new JSEmptyNode(s2), r, s);
			}
			case 70: // IterationStatement = FOR LPAREN.l Expression_NoIn.i SEMICOLON.s1 Expression.c SEMICOLON.s2 Expression.a RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol s1 = _symbols[offset + 4];
					final Symbol _symbol_c = _symbols[offset + 5];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 6];
					final Symbol _symbol_a = _symbols[offset + 7];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 8];
					final Symbol _symbol_s = _symbols[offset + 9];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, i, s1, c, s2, a, r, s);
			}
			case 71: // IterationStatement = FOR LPAREN.l VAR.v VariableDeclarationList_NoIn.i SEMICOLON.s1 SEMICOLON.s2 RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final ArrayList _list_i = (ArrayList) _symbol_i.value;
					final JSNode[] i = _list_i == null ? new JSNode[0] : (JSNode[]) _list_i.toArray(new JSNode[_list_i.size()]);
					final Symbol s1 = _symbols[offset + 5];
					final Symbol s2 = _symbols[offset + 6];
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSVarNode(v, i), s1, new JSEmptyNode(s1), s2, new JSEmptyNode(s2), r, s);
			}
			case 72: // IterationStatement = FOR LPAREN.l VAR.v VariableDeclarationList_NoIn.i SEMICOLON.s1 SEMICOLON.s2 Expression.a RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final ArrayList _list_i = (ArrayList) _symbol_i.value;
					final JSNode[] i = _list_i == null ? new JSNode[0] : (JSNode[]) _list_i.toArray(new JSNode[_list_i.size()]);
					final Symbol s1 = _symbols[offset + 5];
					final Symbol s2 = _symbols[offset + 6];
					final Symbol _symbol_a = _symbols[offset + 7];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 8];
					final Symbol _symbol_s = _symbols[offset + 9];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSVarNode(v, i), s1, new JSEmptyNode(s1), s2, a, r, s);
			}
			case 73: // IterationStatement = FOR LPAREN.l VAR.v VariableDeclarationList_NoIn.i SEMICOLON.s1 Expression.c SEMICOLON.s2 RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final ArrayList _list_i = (ArrayList) _symbol_i.value;
					final JSNode[] i = _list_i == null ? new JSNode[0] : (JSNode[]) _list_i.toArray(new JSNode[_list_i.size()]);
					final Symbol s1 = _symbols[offset + 5];
					final Symbol _symbol_c = _symbols[offset + 6];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 7];
					final Symbol r = _symbols[offset + 8];
					final Symbol _symbol_s = _symbols[offset + 9];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSVarNode(v, i), s1, c, s2, new JSEmptyNode(s2), r, s);
			}
			case 74: // IterationStatement = FOR LPAREN.l VAR.v VariableDeclarationList_NoIn.i SEMICOLON.s1 Expression.c SEMICOLON.s2 Expression.a RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final ArrayList _list_i = (ArrayList) _symbol_i.value;
					final JSNode[] i = _list_i == null ? new JSNode[0] : (JSNode[]) _list_i.toArray(new JSNode[_list_i.size()]);
					final Symbol s1 = _symbols[offset + 5];
					final Symbol _symbol_c = _symbols[offset + 6];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 7];
					final Symbol _symbol_a = _symbols[offset + 8];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 9];
					final Symbol _symbol_s = _symbols[offset + 10];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSVarNode(v, i), s1, c, s2, a, r, s);
			}
			case 75: // IterationStatement = FOR LPAREN.l LeftHandSideExpression.i IN.in Expression.o RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol in = _symbols[offset + 4];
					final Symbol _symbol_o = _symbols[offset + 5];
					final JSNode o = (JSNode) _symbol_o.value;
					final Symbol r = _symbols[offset + 6];
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForInNode(l, i, in, o, r, s);
			}
			case 76: // IterationStatement = FOR LPAREN.l VAR.v VariableDeclaration_NoIn.i IN.in Expression.o RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol in = _symbols[offset + 5];
					final Symbol _symbol_o = _symbols[offset + 6];
					final JSNode o = (JSNode) _symbol_o.value;
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForInNode(l, new JSVarNode(v, i), in, o, r, s);
			}
			case 77: // IterationStatement_NoIf = DO Statement.s WHILE LPAREN.l Expression.e RPAREN.r SEMICOLON
			{
					final Symbol _symbol_s = _symbols[offset + 2];
					final JSNode s = (JSNode) _symbol_s.value;
					final Symbol l = _symbols[offset + 4];
					final Symbol _symbol_e = _symbols[offset + 5];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 6];
					
			JSNode node = new JSDoNode(s, l, e, r);
			node.setSemicolonIncluded(true);
			return node;
			}
			case 78: // IterationStatement_NoIf = WHILE LPAREN.l Expression.e RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 4];
					final Symbol _symbol_s = _symbols[offset + 5];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSWhileNode(l, e, r, s);
			}
			case 79: // IterationStatement_NoIf = FOR LPAREN.l SEMICOLON.s1 SEMICOLON.s2 RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol s1 = _symbols[offset + 3];
					final Symbol s2 = _symbols[offset + 4];
					final Symbol r = _symbols[offset + 5];
					final Symbol _symbol_s = _symbols[offset + 6];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSEmptyNode(l), s1, new JSEmptyNode(s1), s2, new JSEmptyNode(s2), r, s);
			}
			case 80: // IterationStatement_NoIf = FOR LPAREN.l SEMICOLON.s1 SEMICOLON.s2 Expression.a RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol s1 = _symbols[offset + 3];
					final Symbol s2 = _symbols[offset + 4];
					final Symbol _symbol_a = _symbols[offset + 5];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 6];
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSEmptyNode(l), s1, new JSEmptyNode(s1), s2, a, r, s);
			}
			case 81: // IterationStatement_NoIf = FOR LPAREN.l SEMICOLON.s1 Expression.c SEMICOLON.s2 RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol s1 = _symbols[offset + 3];
					final Symbol _symbol_c = _symbols[offset + 4];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 5];
					final Symbol r = _symbols[offset + 6];
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSEmptyNode(l), s1, c, s2, new JSEmptyNode(s2), r, s);
			}
			case 82: // IterationStatement_NoIf = FOR LPAREN.l SEMICOLON.s1 Expression.c SEMICOLON.s2 Expression.a RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol s1 = _symbols[offset + 3];
					final Symbol _symbol_c = _symbols[offset + 4];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 5];
					final Symbol _symbol_a = _symbols[offset + 6];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSEmptyNode(l), s1, c, s2, a, r, s);
			}
			case 83: // IterationStatement_NoIf = FOR LPAREN.l Expression_NoIn.i SEMICOLON.s1 SEMICOLON.s2 RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol s1 = _symbols[offset + 4];
					final Symbol s2 = _symbols[offset + 5];
					final Symbol r = _symbols[offset + 6];
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, i, s1, new JSEmptyNode(s1), s2, new JSEmptyNode(s2), r, s);
			}
			case 84: // IterationStatement_NoIf = FOR LPAREN.l Expression_NoIn.i SEMICOLON.s1 SEMICOLON.s2 Expression.a RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol s1 = _symbols[offset + 4];
					final Symbol s2 = _symbols[offset + 5];
					final Symbol _symbol_a = _symbols[offset + 6];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, i, s1, new JSEmptyNode(s1), s2, a, r, s);
			}
			case 85: // IterationStatement_NoIf = FOR LPAREN.l Expression_NoIn.i SEMICOLON.s1 Expression.c SEMICOLON.s2 RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol s1 = _symbols[offset + 4];
					final Symbol _symbol_c = _symbols[offset + 5];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 6];
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, i, s1, c, s2, new JSEmptyNode(s2), r, s);
			}
			case 86: // IterationStatement_NoIf = FOR LPAREN.l Expression_NoIn.i SEMICOLON.s1 Expression.c SEMICOLON.s2 Expression.a RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol s1 = _symbols[offset + 4];
					final Symbol _symbol_c = _symbols[offset + 5];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 6];
					final Symbol _symbol_a = _symbols[offset + 7];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 8];
					final Symbol _symbol_s = _symbols[offset + 9];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, i, s1, c, s2, a, r, s);
			}
			case 87: // IterationStatement_NoIf = FOR LPAREN.l VAR.v VariableDeclarationList_NoIn.i SEMICOLON.s1 SEMICOLON.s2 RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final ArrayList _list_i = (ArrayList) _symbol_i.value;
					final JSNode[] i = _list_i == null ? new JSNode[0] : (JSNode[]) _list_i.toArray(new JSNode[_list_i.size()]);
					final Symbol s1 = _symbols[offset + 5];
					final Symbol s2 = _symbols[offset + 6];
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSVarNode(v, i), s1, new JSEmptyNode(s1), s2, new JSEmptyNode(s2), r, s);
			}
			case 88: // IterationStatement_NoIf = FOR LPAREN.l VAR.v VariableDeclarationList_NoIn.i SEMICOLON.s1 SEMICOLON.s2 Expression.a RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final ArrayList _list_i = (ArrayList) _symbol_i.value;
					final JSNode[] i = _list_i == null ? new JSNode[0] : (JSNode[]) _list_i.toArray(new JSNode[_list_i.size()]);
					final Symbol s1 = _symbols[offset + 5];
					final Symbol s2 = _symbols[offset + 6];
					final Symbol _symbol_a = _symbols[offset + 7];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 8];
					final Symbol _symbol_s = _symbols[offset + 9];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSVarNode(v, i), s1, new JSEmptyNode(s1), s2, a, r, s);
			}
			case 89: // IterationStatement_NoIf = FOR LPAREN.l VAR.v VariableDeclarationList_NoIn.i SEMICOLON.s1 Expression.c SEMICOLON.s2 RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final ArrayList _list_i = (ArrayList) _symbol_i.value;
					final JSNode[] i = _list_i == null ? new JSNode[0] : (JSNode[]) _list_i.toArray(new JSNode[_list_i.size()]);
					final Symbol s1 = _symbols[offset + 5];
					final Symbol _symbol_c = _symbols[offset + 6];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 7];
					final Symbol r = _symbols[offset + 8];
					final Symbol _symbol_s = _symbols[offset + 9];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSVarNode(v, i), s1, c, s2, new JSEmptyNode(s2), r, s);
			}
			case 90: // IterationStatement_NoIf = FOR LPAREN.l VAR.v VariableDeclarationList_NoIn.i SEMICOLON.s1 Expression.c SEMICOLON.s2 Expression.a RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final ArrayList _list_i = (ArrayList) _symbol_i.value;
					final JSNode[] i = _list_i == null ? new JSNode[0] : (JSNode[]) _list_i.toArray(new JSNode[_list_i.size()]);
					final Symbol s1 = _symbols[offset + 5];
					final Symbol _symbol_c = _symbols[offset + 6];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol s2 = _symbols[offset + 7];
					final Symbol _symbol_a = _symbols[offset + 8];
					final JSNode a = (JSNode) _symbol_a.value;
					final Symbol r = _symbols[offset + 9];
					final Symbol _symbol_s = _symbols[offset + 10];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForNode(l, new JSVarNode(v, i), s1, c, s2, a, r, s);
			}
			case 91: // IterationStatement_NoIf = FOR LPAREN.l LeftHandSideExpression.i IN.in Expression.o RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_i = _symbols[offset + 3];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol in = _symbols[offset + 4];
					final Symbol _symbol_o = _symbols[offset + 5];
					final JSNode o = (JSNode) _symbol_o.value;
					final Symbol r = _symbols[offset + 6];
					final Symbol _symbol_s = _symbols[offset + 7];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForInNode(l, i, in, o, r, s);
			}
			case 92: // IterationStatement_NoIf = FOR LPAREN.l VAR.v VariableDeclaration_NoIn.i IN.in Expression.o RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol v = _symbols[offset + 3];
					final Symbol _symbol_i = _symbols[offset + 4];
					final JSNode i = (JSNode) _symbol_i.value;
					final Symbol in = _symbols[offset + 5];
					final Symbol _symbol_o = _symbols[offset + 6];
					final JSNode o = (JSNode) _symbol_o.value;
					final Symbol r = _symbols[offset + 7];
					final Symbol _symbol_s = _symbols[offset + 8];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSForInNode(l, new JSVarNode(v, i), in, o, r, s);
			}
			case 93: // ContinueStatement = CONTINUE SEMICOLON
			{
					
			JSNode node = new JSContinueNode();
			node.setSemicolonIncluded(true);
			return node;
			}
			case 94: // ContinueStatement = CONTINUE IDENTIFIER.i SEMICOLON
			{
					final Symbol i = _symbols[offset + 2];
					
			JSNode node = new JSContinueNode(i);
			node.setSemicolonIncluded(true);
			return node;
			}
			case 95: // BreakStatement = BREAK SEMICOLON
			{
					
			JSNode node = new JSBreakNode();
			node.setSemicolonIncluded(true);
			return node;
			}
			case 96: // BreakStatement = BREAK IDENTIFIER.i SEMICOLON
			{
					final Symbol i = _symbols[offset + 2];
					
			JSNode node = new JSBreakNode(i);
			node.setSemicolonIncluded(true);
			return node;
			}
			case 97: // ReturnStatement = RETURN.r SEMICOLON
			{
					final Symbol r = _symbols[offset + 1];
					
			JSNode node = new JSReturnNode(new JSEmptyNode(r));
			node.setSemicolonIncluded(true);
			return node;
			}
			case 98: // ReturnStatement = RETURN Expression.e SEMICOLON
			{
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					
			JSNode node = new JSReturnNode(e);
			node.setSemicolonIncluded(true);
			return node;
			}
			case 99: // WithStatement = WITH LPAREN.l Expression.e RPAREN.r Statement.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 4];
					final Symbol _symbol_s = _symbols[offset + 5];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSWithNode(l, e, r, s);
			}
			case 100: // WithStatement_NoIf = WITH LPAREN.l Expression.e RPAREN.r Statement_NoIf.s
			{
					final Symbol l = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 4];
					final Symbol _symbol_s = _symbols[offset + 5];
					final JSNode s = (JSNode) _symbol_s.value;
					
			return new JSWithNode(l, e, r, s);
			}
			case 101: // SwitchStatement = SWITCH LPAREN.lp Expression.e RPAREN.rp LCURLY.lc RCURLY.rc
			{
					final Symbol lp = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol rp = _symbols[offset + 4];
					final Symbol lc = _symbols[offset + 5];
					final Symbol rc = _symbols[offset + 6];
					
			return new JSSwitchNode(lp, e, rp, lc, rc);
			}
			case 102: // SwitchStatement = SWITCH LPAREN.lp Expression.e RPAREN.rp LCURLY.lc CaseClauses.c RCURLY.rc
			{
					final Symbol lp = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol rp = _symbols[offset + 4];
					final Symbol lc = _symbols[offset + 5];
					final Symbol _symbol_c = _symbols[offset + 6];
					final ArrayList _list_c = (ArrayList) _symbol_c.value;
					final JSNode[] c = _list_c == null ? new JSNode[0] : (JSNode[]) _list_c.toArray(new JSNode[_list_c.size()]);
					final Symbol rc = _symbols[offset + 7];
					
			List<JSNode> nodes = new ArrayList<JSNode>();
			
			for (JSNode statement : c)
			{
				nodes.add(statement);
			}
			
			JSNode[] children = nodes.toArray(new JSNode[nodes.size()]);
			
			return new JSSwitchNode(lp, e, rp, lc, rc, children);
			}
			case 103: // SwitchStatement = SWITCH LPAREN.lp Expression.e RPAREN.rp LCURLY.lc DefaultClause.d RCURLY.rc
			{
					final Symbol lp = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol rp = _symbols[offset + 4];
					final Symbol lc = _symbols[offset + 5];
					final Symbol _symbol_d = _symbols[offset + 6];
					final JSNode d = (JSNode) _symbol_d.value;
					final Symbol rc = _symbols[offset + 7];
					
			return new JSSwitchNode(lp, e, rp, lc, rc, d);
			}
			case 104: // SwitchStatement = SWITCH LPAREN.lp Expression.e RPAREN.rp LCURLY.lc DefaultClause.d CaseClauses.c RCURLY.rc
			{
					final Symbol lp = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol rp = _symbols[offset + 4];
					final Symbol lc = _symbols[offset + 5];
					final Symbol _symbol_d = _symbols[offset + 6];
					final JSNode d = (JSNode) _symbol_d.value;
					final Symbol _symbol_c = _symbols[offset + 7];
					final ArrayList _list_c = (ArrayList) _symbol_c.value;
					final JSNode[] c = _list_c == null ? new JSNode[0] : (JSNode[]) _list_c.toArray(new JSNode[_list_c.size()]);
					final Symbol rc = _symbols[offset + 8];
					
			List<JSNode> nodes = new ArrayList<JSNode>();
			
			nodes.add(d);
			
			for (JSNode statement : c)
			{
				nodes.add(statement);
			}
			
			JSNode[] children = nodes.toArray(new JSNode[nodes.size()]);
			
			return new JSSwitchNode(lp, e, rp, lc, rc, children);
			}
			case 105: // SwitchStatement = SWITCH LPAREN.lp Expression.e RPAREN.rp LCURLY.lc CaseClauses.c DefaultClause.d RCURLY.rc
			{
					final Symbol lp = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol rp = _symbols[offset + 4];
					final Symbol lc = _symbols[offset + 5];
					final Symbol _symbol_c = _symbols[offset + 6];
					final ArrayList _list_c = (ArrayList) _symbol_c.value;
					final JSNode[] c = _list_c == null ? new JSNode[0] : (JSNode[]) _list_c.toArray(new JSNode[_list_c.size()]);
					final Symbol _symbol_d = _symbols[offset + 7];
					final JSNode d = (JSNode) _symbol_d.value;
					final Symbol rc = _symbols[offset + 8];
					
			List<JSNode> nodes = new ArrayList<JSNode>();
			
			for (JSNode statement : c)
			{
				nodes.add(statement);
			}
			
			nodes.add(d);
			
			JSNode[] children = nodes.toArray(new JSNode[nodes.size()]);
			
			return new JSSwitchNode(lp, e, rp, lc, rc, children);
			}
			case 106: // SwitchStatement = SWITCH LPAREN.lp Expression.e RPAREN.rp LCURLY.lc CaseClauses.c1 DefaultClause.d CaseClauses.c2 RCURLY.rc
			{
					final Symbol lp = _symbols[offset + 2];
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol rp = _symbols[offset + 4];
					final Symbol lc = _symbols[offset + 5];
					final Symbol _symbol_c1 = _symbols[offset + 6];
					final ArrayList _list_c1 = (ArrayList) _symbol_c1.value;
					final JSNode[] c1 = _list_c1 == null ? new JSNode[0] : (JSNode[]) _list_c1.toArray(new JSNode[_list_c1.size()]);
					final Symbol _symbol_d = _symbols[offset + 7];
					final JSNode d = (JSNode) _symbol_d.value;
					final Symbol _symbol_c2 = _symbols[offset + 8];
					final ArrayList _list_c2 = (ArrayList) _symbol_c2.value;
					final JSNode[] c2 = _list_c2 == null ? new JSNode[0] : (JSNode[]) _list_c2.toArray(new JSNode[_list_c2.size()]);
					final Symbol rc = _symbols[offset + 9];
					
			List<JSNode> nodes = new ArrayList<JSNode>();
			
			for (JSNode statement : c1)
			{
				nodes.add(statement);
			}
			
			nodes.add(d);
			
			for (JSNode statement : c2)
			{
				nodes.add(statement);
			}
			
			JSNode[] children = nodes.toArray(new JSNode[nodes.size()]);
			
			return new JSSwitchNode(lp, e, rp, lc, rc, children);
			}
			case 107: // CaseClauses = CaseClauses CaseClause
			{
					((ArrayList) _symbols[offset + 1].value).add(_symbols[offset + 2].value); return _symbols[offset + 1];
			}
			case 108: // CaseClauses = CaseClause
			{
					ArrayList lst = new ArrayList(); lst.add(_symbols[offset + 1].value); return new Symbol(lst);
			}
			case 109: // CaseClause = CASE Expression.e COLON.c
			{
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol c = _symbols[offset + 3];
					
			return new JSCaseNode(e, c);
			}
			case 110: // CaseClause = CASE Expression.e COLON.c StatementList.s
			{
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol c = _symbols[offset + 3];
					final Symbol _symbol_s = _symbols[offset + 4];
					final ArrayList _list_s = (ArrayList) _symbol_s.value;
					final JSNode[] s = _list_s == null ? new JSNode[0] : (JSNode[]) _list_s.toArray(new JSNode[_list_s.size()]);
					
			return new JSCaseNode(e, c, s);
			}
			case 111: // DefaultClause = DEFAULT COLON.c
			{
					final Symbol c = _symbols[offset + 2];
					
			return new JSDefaultNode(c);
			}
			case 112: // DefaultClause = DEFAULT COLON.c StatementList.s
			{
					final Symbol c = _symbols[offset + 2];
					final Symbol _symbol_s = _symbols[offset + 3];
					final ArrayList _list_s = (ArrayList) _symbol_s.value;
					final JSNode[] s = _list_s == null ? new JSNode[0] : (JSNode[]) _list_s.toArray(new JSNode[_list_s.size()]);
					
			return new JSDefaultNode(c, s);
			}
			case 113: // LabelledStatement = IDENTIFIER.i COLON.c Statement.s
			{
					final Symbol i = _symbols[offset + 1];
					final Symbol c = _symbols[offset + 2];
					final Symbol _symbol_s = _symbols[offset + 3];
					final JSNode s = (JSNode) _symbol_s.value;
					
			JSNode id = new JSIdentifierNode(i);
			id.setLocation(i.getStart(), i.getEnd());
			
			return new JSLabelledNode(id, c, s);
			}
			case 114: // LabelledStatement_NoIf = IDENTIFIER.i COLON.c Statement_NoIf.s
			{
					final Symbol i = _symbols[offset + 1];
					final Symbol c = _symbols[offset + 2];
					final Symbol _symbol_s = _symbols[offset + 3];
					final JSNode s = (JSNode) _symbol_s.value;
					
			JSNode id = new JSIdentifierNode(i);
			id.setLocation(i.getStart(), i.getEnd());
			
			return new JSLabelledNode(id, c, s);
			}
			case 115: // ThrowStatement = THROW Expression.e SEMICOLON
			{
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					
			JSNode node = new JSThrowNode(e);
			
			node.setSemicolonIncluded(true);
			
			return node;
			}
			case 116: // TryStatement = TRY Block.b Catch.c
			{
					final Symbol _symbol_b = _symbols[offset + 2];
					final JSNode b = (JSNode) _symbol_b.value;
					final Symbol _symbol_c = _symbols[offset + 3];
					final JSNode c = (JSNode) _symbol_c.value;
					
			return new JSTryNode(b, c, new JSEmptyNode(c));
			}
			case 117: // TryStatement = TRY Block.b Finally.f
			{
					final Symbol _symbol_b = _symbols[offset + 2];
					final JSNode b = (JSNode) _symbol_b.value;
					final Symbol _symbol_f = _symbols[offset + 3];
					final JSNode f = (JSNode) _symbol_f.value;
					
			return new JSTryNode(b, new JSEmptyNode(b), f);
			}
			case 118: // TryStatement = TRY Block.b Catch.c Finally.f
			{
					final Symbol _symbol_b = _symbols[offset + 2];
					final JSNode b = (JSNode) _symbol_b.value;
					final Symbol _symbol_c = _symbols[offset + 3];
					final JSNode c = (JSNode) _symbol_c.value;
					final Symbol _symbol_f = _symbols[offset + 4];
					final JSNode f = (JSNode) _symbol_f.value;
					
			return new JSTryNode(b, c, f);
			}
			case 119: // Catch = CATCH LPAREN IDENTIFIER.i RPAREN Block.b
			{
					final Symbol i = _symbols[offset + 3];
					final Symbol _symbol_b = _symbols[offset + 5];
					final JSNode b = (JSNode) _symbol_b.value;
					
			JSNode id = new JSIdentifierNode(i);
			
			return new JSCatchNode(id, b);
			}
			case 120: // Finally = FINALLY Block.b
			{
					final Symbol _symbol_b = _symbols[offset + 2];
					final JSNode b = (JSNode) _symbol_b.value;
					
			return new JSFinallyNode(b);
			}
			case 123: // PrimaryExpression_NoLBF = THIS.t
			{
					final Symbol t = _symbols[offset + 1];
					
			return new JSThisNode(t);
			}
			case 124: // PrimaryExpression_NoLBF = IDENTIFIER.i
			{
					final Symbol i = _symbols[offset + 1];
					
			return new JSIdentifierNode(i);
			}
			case 127: // PrimaryExpression_NoLBF = LPAREN.l Expression.e RPAREN.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 3];
					
			return new JSGroupNode(l, e, r);
			}
			case 128: // ArrayLiteral = LBRACKET.l RBRACKET.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol r = _symbols[offset + 2];
					
			return new JSArrayNode(l, r);
			}
			case 129: // ArrayLiteral = LBRACKET.l Elision.e RBRACKET.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 3];
					
			return new JSArrayNode(l, r, e);
			}
			case 130: // ArrayLiteral = LBRACKET.l ElementList.e RBRACKET.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 3];
					
			return new JSArrayNode(l, r, e);
			}
			case 131: // ArrayLiteral = LBRACKET.l ElementList.e COMMA RBRACKET.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol r = _symbols[offset + 4];
					
			return new JSArrayNode(l, r, e, new JSNullNode());
			}
			case 132: // ArrayLiteral = LBRACKET.l ElementList.e COMMA Elision.n RBRACKET.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol _symbol_n = _symbols[offset + 4];
					final JSNode n = (JSNode) _symbol_n.value;
					final Symbol r = _symbols[offset + 5];
					
			return new JSArrayNode(l, r, e, n);
			}
			case 133: // ElementList = AssignmentExpression.e
			{
					final Symbol _symbol_e = _symbols[offset + 1];
					final JSNode e = (JSNode) _symbol_e.value;
					
			return new JSElementsNode(e);
			}
			case 134: // ElementList = Elision.n AssignmentExpression.e
			{
					final Symbol _symbol_n = _symbols[offset + 1];
					final JSNode n = (JSNode) _symbol_n.value;
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					
			return new JSElementsNode(n, e);
			}
			case 135: // ElementList = ElementList.l COMMA AssignmentExpression.e
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_e = _symbols[offset + 3];
					final JSNode e = (JSNode) _symbol_e.value;
					
			l.addChild(e);
			
			return l;
			}
			case 136: // ElementList = ElementList.l COMMA Elision.n AssignmentExpression.e
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_n = _symbols[offset + 3];
					final JSNode n = (JSNode) _symbol_n.value;
					final Symbol _symbol_e = _symbols[offset + 4];
					final JSNode e = (JSNode) _symbol_e.value;
					
			l.addChild(n);
			l.addChild(e);
			
			return l;
			}
			case 137: // Elision = Elision.e COMMA
			{
					final Symbol _symbol_e = _symbols[offset + 1];
					final JSNode e = (JSNode) _symbol_e.value;
					
			e.addChild(new JSNullNode());
			
			return e;
			}
			case 138: // Elision = COMMA
			{
					
			return new JSElisionNode(new JSNullNode(), new JSNullNode());
			}
			case 139: // ObjectLiteral = LCURLY.l RCURLY.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol r = _symbols[offset + 2];
					
			return new JSObjectNode(l, r);
			}
			case 140: // ObjectLiteral = LCURLY.l PropertyNameAndValueList.p RCURLY.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol _symbol_p = _symbols[offset + 2];
					final ArrayList _list_p = (ArrayList) _symbol_p.value;
					final JSNode[] p = _list_p == null ? new JSNode[0] : (JSNode[]) _list_p.toArray(new JSNode[_list_p.size()]);
					final Symbol r = _symbols[offset + 3];
					
			return new JSObjectNode(l, r, p);
			}
			case 141: // ObjectLiteral = LCURLY.l PropertyNameAndValueList.p COMMA RCURLY.r
			{
					final Symbol l = _symbols[offset + 1];
					final Symbol _symbol_p = _symbols[offset + 2];
					final ArrayList _list_p = (ArrayList) _symbol_p.value;
					final JSNode[] p = _list_p == null ? new JSNode[0] : (JSNode[]) _list_p.toArray(new JSNode[_list_p.size()]);
					final Symbol r = _symbols[offset + 4];
					
			return new JSObjectNode(l, r, p);
			}
			case 142: // PropertyNameAndValueList = PropertyNameAndValue
			{
					ArrayList lst = new ArrayList(); lst.add(_symbols[offset + 1].value); return new Symbol(lst);
			}
			case 143: // PropertyNameAndValueList = PropertyNameAndValueList COMMA PropertyNameAndValue
			{
					((ArrayList) _symbols[offset + 1].value).add(_symbols[offset + 3].value); return _symbols[offset + 1];
			}
			case 144: // PropertyNameAndValue = PropertyName.n COLON.c AssignmentExpression.v
			{
					final Symbol _symbol_n = _symbols[offset + 1];
					final JSNode n = (JSNode) _symbol_n.value;
					final Symbol c = _symbols[offset + 2];
					final Symbol _symbol_v = _symbols[offset + 3];
					final JSNode v = (JSNode) _symbol_v.value;
					
			return new JSNameValuePairNode(n, c, v);
			}
			case 145: // PropertyName = IDENTIFIER.i
			{
					final Symbol i = _symbols[offset + 1];
					
			return new JSIdentifierNode(i);
			}
			case 146: // PropertyName = STRING.s
			{
					final Symbol s = _symbols[offset + 1];
					
			return new JSStringNode(s);
			}
			case 147: // PropertyName = NUMBER.n
			{
					final Symbol n = _symbols[offset + 1];
					
			return new JSNumberNode(n);
			}
			case 150: // MemberExpression = MemberExpression.l LBRACKET.lb Expression.r RBRACKET.rb
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol lb = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					final Symbol rb = _symbols[offset + 4];
					
			return new JSGetElementNode(l, lb, r, rb);
			}
			case 151: // MemberExpression = MemberExpression.l DOT.o IDENTIFIER.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol r = _symbols[offset + 3];
					
			return new JSGetPropertyNode(l, o, new JSIdentifierNode(r));
			}
			case 152: // MemberExpression = NEW MemberExpression.e Arguments.a
			{
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol _symbol_a = _symbols[offset + 3];
					final JSNode a = (JSNode) _symbol_a.value;
					
			return new JSConstructNode(e, a);
			}
			case 154: // MemberExpression_NoLBF = MemberExpression_NoLBF.l LBRACKET.lb Expression.r RBRACKET.rb
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol lb = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					final Symbol rb = _symbols[offset + 4];
					
			return new JSGetElementNode(l, lb, r, rb);
			}
			case 155: // MemberExpression_NoLBF = MemberExpression_NoLBF.l DOT.o IDENTIFIER.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol r = _symbols[offset + 3];
					
			return new JSGetPropertyNode(l, o, new JSIdentifierNode(r));
			}
			case 156: // MemberExpression_NoLBF = NEW MemberExpression.e Arguments.a
			{
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol _symbol_a = _symbols[offset + 3];
					final JSNode a = (JSNode) _symbol_a.value;
					
			return new JSConstructNode(e, a);
			}
			case 158: // NewExpression = NEW NewExpression.e
			{
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					
			return new JSConstructNode(e, new JSEmptyNode(e));
			}
			case 160: // NewExpression_NoLBF = NEW NewExpression.e
			{
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					
			return new JSConstructNode(e, new JSEmptyNode(e));
			}
			case 161: // CallExpression = MemberExpression.l Arguments.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_r = _symbols[offset + 2];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSInvokeNode(l, r);
			}
			case 162: // CallExpression = CallExpression.l Arguments.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_r = _symbols[offset + 2];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSInvokeNode(l, r);
			}
			case 163: // CallExpression = CallExpression.l LBRACKET.lb Expression.r RBRACKET.rb
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol lb = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					final Symbol rb = _symbols[offset + 4];
					
			return new JSGetElementNode(l, lb, r, rb);
			}
			case 164: // CallExpression = CallExpression.l DOT.o IDENTIFIER.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol r = _symbols[offset + 3];
					
			return new JSGetPropertyNode(l, o, new JSIdentifierNode(r));
			}
			case 165: // CallExpression_NoLBF = MemberExpression_NoLBF.l Arguments.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_r = _symbols[offset + 2];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSInvokeNode(l, r);
			}
			case 166: // CallExpression_NoLBF = CallExpression_NoLBF.l Arguments.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_r = _symbols[offset + 2];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSInvokeNode(l, r);
			}
			case 167: // CallExpression_NoLBF = CallExpression_NoLBF.l LBRACKET.lb Expression.r RBRACKET.rb
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol lb = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					final Symbol rb = _symbols[offset + 4];
					
			return new JSGetElementNode(l, lb, r, rb);
			}
			case 168: // CallExpression_NoLBF = CallExpression_NoLBF.l DOT.o IDENTIFIER.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol r = _symbols[offset + 3];
					
			return new JSGetPropertyNode(l, o, new JSIdentifierNode(r));
			}
			case 169: // Arguments = LPAREN RPAREN
			{
					
			return new JSArgumentsNode();
			}
			case 170: // Arguments = LPAREN ArgumentList.a RPAREN
			{
					final Symbol _symbol_a = _symbols[offset + 2];
					final ArrayList _list_a = (ArrayList) _symbol_a.value;
					final JSNode[] a = _list_a == null ? new JSNode[0] : (JSNode[]) _list_a.toArray(new JSNode[_list_a.size()]);
					
			return new JSArgumentsNode(a);
			}
			case 171: // ArgumentList = ArgumentList COMMA AssignmentExpression
			{
					((ArrayList) _symbols[offset + 1].value).add(_symbols[offset + 3].value); return _symbols[offset + 1];
			}
			case 172: // ArgumentList = AssignmentExpression
			{
					ArrayList lst = new ArrayList(); lst.add(_symbols[offset + 1].value); return new Symbol(lst);
			}
			case 178: // PostfixExpression = LeftHandSideExpression.e PostfixOperator.o
			{
					final Symbol _symbol_e = _symbols[offset + 1];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol o = _symbols[offset + 2];
					
			return new JSPostUnaryOperatorNode(o, e);
			}
			case 180: // PostfixExpression_NoLBF = LeftHandSideExpression_NoLBF.e PostfixOperator.o
			{
					final Symbol _symbol_e = _symbols[offset + 1];
					final JSNode e = (JSNode) _symbol_e.value;
					final Symbol o = _symbols[offset + 2];
					
			return new JSPostUnaryOperatorNode(o, e);
			}
			case 184: // UnaryExpression = UnaryOperator.o UnaryExpression.e
			{
					final Symbol o = _symbols[offset + 1];
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					
			return new JSPreUnaryOperatorNode(o, e);
			}
			case 186: // UnaryExpression_NoLBF = UnaryOperator.o UnaryExpression.e
			{
					final Symbol o = _symbols[offset + 1];
					final Symbol _symbol_e = _symbols[offset + 2];
					final JSNode e = (JSNode) _symbol_e.value;
					
			return new JSPreUnaryOperatorNode(o, e);
			}
			case 197: // MultiplicativeExpression = MultiplicativeExpression.l MultiplicativeOperator.o UnaryExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 199: // MultiplicativeExpression_NoLBF = MultiplicativeExpression_NoLBF.l MultiplicativeOperator.o UnaryExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 204: // AdditiveExpression = AdditiveExpression.l AdditiveOperator.o MultiplicativeExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 206: // AdditiveExpression_NoLBF = AdditiveExpression_NoLBF.l AdditiveOperator.o MultiplicativeExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 210: // ShiftExpression = ShiftExpression.l ShiftOperator.o AdditiveExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 212: // ShiftExpression_NoLBF = ShiftExpression_NoLBF.l ShiftOperator.o AdditiveExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 217: // RelationalExpression = RelationalExpression.l RelationalOperator.o ShiftExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 219: // RelationalExpression_NoLBF = RelationalExpression_NoLBF.l RelationalOperator.o ShiftExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 221: // RelationalExpression_NoIn = RelationalExpression_NoIn.l RelationalOperator_NoIn.o ShiftExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 230: // EqualityExpression = EqualityExpression.l EqualityOperator.o RelationalExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 232: // EqualityExpression_NoLBF = EqualityExpression_NoLBF.l EqualityOperator.o RelationalExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 234: // EqualityExpression_NoIn = EqualityExpression_NoIn.l EqualityOperator.o RelationalExpression_NoIn.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 239: // BitwiseAndExpression = BitwiseAndExpression.l AMPERSAND.o EqualityExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 241: // BitwiseAndExpression_NoLBF = BitwiseAndExpression_NoLBF.l AMPERSAND.o EqualityExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 243: // BitwiseAndExpression_NoIn = BitwiseAndExpression_NoIn.l AMPERSAND.o EqualityExpression_NoIn.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 245: // BitwiseXorExpression = BitwiseXorExpression.l CARET.o BitwiseAndExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 247: // BitwiseXorExpression_NoLBF = BitwiseXorExpression_NoLBF.l CARET.o BitwiseAndExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 249: // BitwiseXorExpression_NoIn = BitwiseXorExpression_NoIn.l CARET.o BitwiseAndExpression_NoIn.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 251: // BitwiseOrExpression = BitwiseOrExpression.l PIPE.o BitwiseXorExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 253: // BitwiseOrExpression_NoLBF = BitwiseOrExpression_NoLBF.l PIPE.o BitwiseXorExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 255: // BitwiseOrExpression_NoIn = BitwiseOrExpression_NoIn.l PIPE.o BitwiseXorExpression_NoIn.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryArithmeticOperatorNode(l, o, r);
			}
			case 257: // LogicalAndExpression = LogicalAndExpression.l AMPERSAND_AMPERSAND.o BitwiseOrExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 259: // LogicalAndExpression_NoLBF = LogicalAndExpression_NoLBF.l AMPERSAND_AMPERSAND.o BitwiseOrExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 261: // LogicalAndExpression_NoIn = LogicalAndExpression_NoIn.l AMPERSAND_AMPERSAND.o BitwiseOrExpression_NoIn.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 263: // LogicalOrExpression = LogicalOrExpression.l PIPE_PIPE.o LogicalAndExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 265: // LogicalOrExpression_NoLBF = LogicalOrExpression_NoLBF.l PIPE_PIPE.o LogicalAndExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 267: // LogicalOrExpression_NoIn = LogicalOrExpression_NoIn.l PIPE_PIPE.o LogicalAndExpression_NoIn.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSBinaryBooleanOperatorNode(l, o, r);
			}
			case 270: // ConditionalExpression = LogicalOrExpression.l QUESTION.q AssignmentExpression.t COLON.c AssignmentExpression.f
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol q = _symbols[offset + 2];
					final Symbol _symbol_t = _symbols[offset + 3];
					final JSNode t = (JSNode) _symbol_t.value;
					final Symbol c = _symbols[offset + 4];
					final Symbol _symbol_f = _symbols[offset + 5];
					final JSNode f = (JSNode) _symbol_f.value;
					
			return new JSConditionalNode(l, q, t, c, f);
			}
			case 272: // ConditionalExpression_NoLBF = LogicalOrExpression_NoLBF.l QUESTION.q AssignmentExpression.t COLON.c AssignmentExpression.f
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol q = _symbols[offset + 2];
					final Symbol _symbol_t = _symbols[offset + 3];
					final JSNode t = (JSNode) _symbol_t.value;
					final Symbol c = _symbols[offset + 4];
					final Symbol _symbol_f = _symbols[offset + 5];
					final JSNode f = (JSNode) _symbol_f.value;
					
			return new JSConditionalNode(l, q, t, c, f);
			}
			case 274: // ConditionalExpression_NoIn = LogicalOrExpression_NoIn.l QUESTION.q AssignmentExpression_NoIn.t COLON.c AssignmentExpression_NoIn.f
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol q = _symbols[offset + 2];
					final Symbol _symbol_t = _symbols[offset + 3];
					final JSNode t = (JSNode) _symbol_t.value;
					final Symbol c = _symbols[offset + 4];
					final Symbol _symbol_f = _symbols[offset + 5];
					final JSNode f = (JSNode) _symbol_f.value;
					
			return new JSConditionalNode(l, q, t, c, f);
			}
			case 276: // AssignmentExpression = LeftHandSideExpression.l AssignmentOperator.o AssignmentExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSAssignmentNode(l, o, r);
			}
			case 278: // AssignmentExpression_NoLBF = LeftHandSideExpression_NoLBF.l AssignmentOperator.o AssignmentExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSAssignmentNode(l, o, r);
			}
			case 280: // AssignmentExpression_NoIn = LeftHandSideExpression.l AssignmentOperator.o AssignmentExpression_NoIn.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol o = _symbols[offset + 2];
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSAssignmentNode(l, o, r);
			}
			case 293: // Expression = Expression.l COMMA AssignmentExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSCommaNode(l, r);
			}
			case 295: // Expression_NoLBF = Expression_NoLBF.l COMMA AssignmentExpression.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSCommaNode(l, r);
			}
			case 297: // Expression_NoIn = Expression_NoIn.l COMMA AssignmentExpression_NoIn.r
			{
					final Symbol _symbol_l = _symbols[offset + 1];
					final JSNode l = (JSNode) _symbol_l.value;
					final Symbol _symbol_r = _symbols[offset + 3];
					final JSNode r = (JSNode) _symbol_r.value;
					
			return new JSCommaNode(l, r);
			}
			case 299: // Literal = NULL.n
			{
					final Symbol n = _symbols[offset + 1];
					
			return new JSNullNode(n);
			}
			case 300: // Literal = TRUE.t
			{
					final Symbol t = _symbols[offset + 1];
					
			return new JSTrueNode(t);
			}
			case 301: // Literal = FALSE.f
			{
					final Symbol f = _symbols[offset + 1];
					
			return new JSFalseNode(f);
			}
			case 302: // Literal = NUMBER.n
			{
					final Symbol n = _symbols[offset + 1];
					
			return new JSNumberNode(n);
			}
			case 303: // Literal = STRING.s
			{
					final Symbol s = _symbols[offset + 1];
					
			return new JSStringNode(s);
			}
			case 304: // Literal = REGEX.r
			{
					final Symbol r = _symbols[offset + 1];
					
			return new JSRegexNode(r);
			}
			case 4: // SourceElement = Statement
			case 7: // FunctionExpression = FunctionDeclaration
			case 14: // Statement = Block
			case 15: // Statement = FunctionDeclaration
			case 18: // Statement = IfStatement
			case 19: // Statement = IterationStatement
			case 20: // Statement = ContinueStatement
			case 21: // Statement = BreakStatement
			case 22: // Statement = ReturnStatement
			case 23: // Statement = WithStatement
			case 24: // Statement = LabelledStatement
			case 25: // Statement = SwitchStatement
			case 26: // Statement = ThrowStatement
			case 27: // Statement = TryStatement
			case 30: // Statement_NoIf = Block
			case 31: // Statement_NoIf = FunctionDeclaration
			case 34: // Statement_NoIf = IfStatement_NoIf
			case 35: // Statement_NoIf = IterationStatement_NoIf
			case 36: // Statement_NoIf = ContinueStatement
			case 37: // Statement_NoIf = BreakStatement
			case 38: // Statement_NoIf = ReturnStatement
			case 39: // Statement_NoIf = WithStatement_NoIf
			case 40: // Statement_NoIf = LabelledStatement_NoIf
			case 41: // Statement_NoIf = SwitchStatement
			case 42: // Statement_NoIf = ThrowStatement
			case 43: // Statement_NoIf = TryStatement
			case 121: // PrimaryExpression = PrimaryExpression_NoLBF
			case 122: // PrimaryExpression = ObjectLiteral
			case 125: // PrimaryExpression_NoLBF = Literal
			case 126: // PrimaryExpression_NoLBF = ArrayLiteral
			case 148: // MemberExpression = PrimaryExpression
			case 149: // MemberExpression = FunctionExpression
			case 153: // MemberExpression_NoLBF = PrimaryExpression_NoLBF
			case 157: // NewExpression = MemberExpression
			case 159: // NewExpression_NoLBF = MemberExpression_NoLBF
			case 173: // LeftHandSideExpression = NewExpression
			case 174: // LeftHandSideExpression = CallExpression
			case 175: // LeftHandSideExpression_NoLBF = NewExpression_NoLBF
			case 176: // LeftHandSideExpression_NoLBF = CallExpression_NoLBF
			case 177: // PostfixExpression = LeftHandSideExpression
			case 179: // PostfixExpression_NoLBF = LeftHandSideExpression_NoLBF
			case 181: // PostfixOperator = PLUS_PLUS
			case 182: // PostfixOperator = MINUS_MINUS
			case 183: // UnaryExpression = PostfixExpression
			case 185: // UnaryExpression_NoLBF = PostfixExpression_NoLBF
			case 187: // UnaryOperator = DELETE
			case 188: // UnaryOperator = EXCLAMATION
			case 189: // UnaryOperator = MINUS
			case 190: // UnaryOperator = MINUS_MINUS
			case 191: // UnaryOperator = PLUS
			case 192: // UnaryOperator = PLUS_PLUS
			case 193: // UnaryOperator = TILDE
			case 194: // UnaryOperator = TYPEOF
			case 195: // UnaryOperator = VOID
			case 196: // MultiplicativeExpression = UnaryExpression
			case 198: // MultiplicativeExpression_NoLBF = UnaryExpression_NoLBF
			case 200: // MultiplicativeOperator = STAR
			case 201: // MultiplicativeOperator = FORWARD_SLASH
			case 202: // MultiplicativeOperator = PERCENT
			case 203: // AdditiveExpression = MultiplicativeExpression
			case 205: // AdditiveExpression_NoLBF = MultiplicativeExpression_NoLBF
			case 207: // AdditiveOperator = PLUS
			case 208: // AdditiveOperator = MINUS
			case 209: // ShiftExpression = AdditiveExpression
			case 211: // ShiftExpression_NoLBF = AdditiveExpression_NoLBF
			case 213: // ShiftOperator = LESS_LESS
			case 214: // ShiftOperator = GREATER_GREATER
			case 215: // ShiftOperator = GREATER_GREATER_GREATER
			case 216: // RelationalExpression = ShiftExpression
			case 218: // RelationalExpression_NoLBF = ShiftExpression_NoLBF
			case 220: // RelationalExpression_NoIn = ShiftExpression
			case 222: // RelationalOperator_NoIn = LESS
			case 223: // RelationalOperator_NoIn = GREATER
			case 224: // RelationalOperator_NoIn = LESS_EQUAL
			case 225: // RelationalOperator_NoIn = GREATER_EQUAL
			case 226: // RelationalOperator_NoIn = INSTANCEOF
			case 227: // RelationalOperator = RelationalOperator_NoIn
			case 228: // RelationalOperator = IN
			case 229: // EqualityExpression = RelationalExpression
			case 231: // EqualityExpression_NoLBF = RelationalExpression_NoLBF
			case 233: // EqualityExpression_NoIn = RelationalExpression_NoIn
			case 235: // EqualityOperator = EQUAL_EQUAL
			case 236: // EqualityOperator = EXCLAMATION_EQUAL
			case 237: // EqualityOperator = EQUAL_EQUAL_EQUAL
			case 238: // EqualityOperator = EXCLAMATION_EQUAL_EQUAL
			case 240: // BitwiseAndExpression = EqualityExpression
			case 242: // BitwiseAndExpression_NoLBF = EqualityExpression_NoLBF
			case 244: // BitwiseAndExpression_NoIn = EqualityExpression_NoIn
			case 246: // BitwiseXorExpression = BitwiseAndExpression
			case 248: // BitwiseXorExpression_NoLBF = BitwiseAndExpression_NoLBF
			case 250: // BitwiseXorExpression_NoIn = BitwiseAndExpression_NoIn
			case 252: // BitwiseOrExpression = BitwiseXorExpression
			case 254: // BitwiseOrExpression_NoLBF = BitwiseXorExpression_NoLBF
			case 256: // BitwiseOrExpression_NoIn = BitwiseXorExpression_NoIn
			case 258: // LogicalAndExpression = BitwiseOrExpression
			case 260: // LogicalAndExpression_NoLBF = BitwiseOrExpression_NoLBF
			case 262: // LogicalAndExpression_NoIn = BitwiseOrExpression_NoIn
			case 264: // LogicalOrExpression = LogicalAndExpression
			case 266: // LogicalOrExpression_NoLBF = LogicalAndExpression_NoLBF
			case 268: // LogicalOrExpression_NoIn = LogicalAndExpression_NoIn
			case 269: // ConditionalExpression = LogicalOrExpression
			case 271: // ConditionalExpression_NoLBF = LogicalOrExpression_NoLBF
			case 273: // ConditionalExpression_NoIn = LogicalOrExpression_NoIn
			case 275: // AssignmentExpression = ConditionalExpression
			case 277: // AssignmentExpression_NoLBF = ConditionalExpression_NoLBF
			case 279: // AssignmentExpression_NoIn = ConditionalExpression_NoIn
			case 281: // AssignmentOperator = EQUAL
			case 282: // AssignmentOperator = STAR_EQUAL
			case 283: // AssignmentOperator = FORWARD_SLASH_EQUAL
			case 284: // AssignmentOperator = PERCENT_EQUAL
			case 285: // AssignmentOperator = PLUS_EQUAL
			case 286: // AssignmentOperator = MINUS_EQUAL
			case 287: // AssignmentOperator = LESS_LESS_EQUAL
			case 288: // AssignmentOperator = GREATER_GREATER_EQUAL
			case 289: // AssignmentOperator = GREATER_GREATER_GREATER_EQUAL
			case 290: // AssignmentOperator = AMPERSAND_EQUAL
			case 291: // AssignmentOperator = CARET_EQUAL
			case 292: // AssignmentOperator = PIPE_EQUAL
			case 294: // Expression = AssignmentExpression
			case 296: // Expression_NoLBF = AssignmentExpression_NoLBF
			case 298: // Expression_NoIn = AssignmentExpression_NoIn
			{
				return _symbols[offset + 1];
			}
			default:
				throw new IllegalArgumentException("unknown production #" + rule_num);
		}
	}
}
