package com.example.skillflow.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillflow.ui.theme.GradientStart

@Composable
fun FormattedMarkdownText(
    markdownText: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
    fontSize: TextUnit = 15.sp,
    lineHeight: TextUnit = 24.sp,
    headingColor: Color = GradientStart
) {
    if (markdownText.isBlank()) return

    val formattedText = autoFormatMarkdown(markdownText)
    val lines = formattedText.lines()
    val spacing = 8.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        var inCodeBlock = false
        val codeBuffer = mutableListOf<String>()

        lines.forEach { line ->
            val trimmedLine = line.trim()

            if (trimmedLine.startsWith("```")) {
                if (inCodeBlock) {
                    CodeBlockView(code = codeBuffer.joinToString("\n"))
                    codeBuffer.clear()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                }
                return@forEach
            }

            if (inCodeBlock) {
                codeBuffer.add(line)
                return@forEach
            }

            when {
                // Divider line (--- or ***)
                trimmedLine == "---" || trimmedLine == "***" || trimmedLine == "___" -> {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }

                // Header Level 1 (# Title)
                trimmedLine.startsWith("# ") -> {
                    val title = trimmedLine.removePrefix("# ").trim()
                    Text(
                        text = parseMarkdownToAnnotatedString(title, headingColor),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = headingColor,
                        textAlign = textAlign,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                // Header Level 2 (## Subtitle)
                trimmedLine.startsWith("## ") -> {
                    val title = trimmedLine.removePrefix("## ").trim()
                    Text(
                        text = parseMarkdownToAnnotatedString(title, headingColor),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = headingColor,
                        textAlign = textAlign,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }

                // Header Level 3 (### Section)
                trimmedLine.startsWith("### ") -> {
                    val title = trimmedLine.removePrefix("### ").trim()
                    Surface(
                        color = headingColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 2.dp)
                    ) {
                        Text(
                            text = parseMarkdownToAnnotatedString(title, textColor),
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Header Level 4+ (#### Section)
                trimmedLine.startsWith("#### ") || trimmedLine.startsWith("##### ") -> {
                    val title = trimmedLine.replace(Regex("^#+\\s*"), "").trim()
                    Text(
                        text = parseMarkdownToAnnotatedString(title, textColor),
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = textAlign,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Bullet points (* Item or - Item or + Item or • Item)
                trimmedLine.startsWith("* ") || trimmedLine.startsWith("- ") || trimmedLine.startsWith("+ ") || trimmedLine.startsWith("• ") -> {
                    val bulletText = trimmedLine.substring(2).trim()
                    BulletItemRow(
                        annotatedText = parseMarkdownToAnnotatedString(bulletText, textColor, headingColor),
                        bulletColor = headingColor,
                        textColor = textColor,
                        fontSize = fontSize,
                        lineHeight = lineHeight
                    )
                }

                // Numbered list (1. Item, 2. Item)
                trimmedLine.matches(Regex("^\\d+\\.\\s+.*")) -> {
                    val match = Regex("^(\\d+\\.)\\s+(.*)").find(trimmedLine)
                    if (match != null) {
                        val numberPrefix = match.groupValues[1]
                        val itemContent = match.groupValues[2]
                        NumberedItemRow(
                            numberPrefix = numberPrefix,
                            annotatedText = parseMarkdownToAnnotatedString(itemContent, textColor, headingColor),
                            numberColor = headingColor,
                            fontSize = fontSize,
                            lineHeight = lineHeight
                        )
                    } else {
                        StandardParagraph(
                            annotatedText = parseMarkdownToAnnotatedString(trimmedLine, textColor, headingColor),
                            textColor = textColor,
                            textAlign = textAlign,
                            fontSize = fontSize,
                            lineHeight = lineHeight
                        )
                    }
                }

                // Empty line
                trimmedLine.isEmpty() -> {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Standard paragraph
                else -> {
                    StandardParagraph(
                        annotatedText = parseMarkdownToAnnotatedString(trimmedLine, textColor, headingColor),
                        textColor = textColor,
                        textAlign = textAlign,
                        fontSize = fontSize,
                        lineHeight = lineHeight
                    )
                }
            }
        }

        if (inCodeBlock && codeBuffer.isNotEmpty()) {
            CodeBlockView(code = codeBuffer.joinToString("\n"))
        }
    }
}

@Composable
private fun StandardParagraph(
    annotatedText: AnnotatedString,
    textColor: Color,
    textAlign: TextAlign,
    fontSize: TextUnit,
    lineHeight: TextUnit
) {
    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor,
        textAlign = textAlign,
        fontSize = fontSize,
        lineHeight = lineHeight,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BulletItemRow(
    annotatedText: AnnotatedString,
    bulletColor: Color,
    textColor: Color,
    fontSize: TextUnit,
    lineHeight: TextUnit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp, end = 10.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(bulletColor)
        )
        Text(
            text = annotatedText,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontSize = fontSize,
            lineHeight = lineHeight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun NumberedItemRow(
    numberPrefix: String,
    annotatedText: AnnotatedString,
    numberColor: Color,
    fontSize: TextUnit,
    lineHeight: TextUnit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = numberPrefix,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = numberColor,
            fontSize = fontSize,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = annotatedText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = fontSize,
            lineHeight = lineHeight,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CodeBlockView(code: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = code,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun autoFormatMarkdown(rawText: String): String {
    if (rawText.isBlank()) return rawText

    var text = rawText

    if (!text.contains("```")) {
        // Match "For example: \"...\"" or "Example: \"...\""
        val exampleQuoteRegex = Regex("""(For example:|Example:)\s*["']([^"']+)["']\.?""", RegexOption.IGNORE_CASE)
        text = exampleQuoteRegex.replace(text) { match ->
            val prefix = match.groupValues[1]
            val codeSnippet = match.groupValues[2]
            "$prefix\n```kotlin\n$codeSnippet\n```"
        }

        // Match single quote keywords like 'val', 'when', 'is String', 'object', etc.
        val singleQuoteRegex = Regex("""'([^'\n]+)'""")
        text = singleQuoteRegex.replace(text) { match ->
            val inner = match.groupValues[1]
            "`$inner`"
        }
    }

    return text
}

fun parseMarkdownToAnnotatedString(
    text: String,
    defaultColor: Color = Color.Unspecified,
    accentColor: Color = GradientStart
): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        val len = text.length

        while (i < len) {
            // Bold (**text**)
            if (i + 1 < len && text[i] == '*' && text[i + 1] == '*') {
                val endBold = text.indexOf("**", i + 2)
                if (endBold != -1) {
                    val boldContent = text.substring(i + 2, endBold)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldContent)
                    }
                    i = endBold + 2
                    continue
                }
            }

            // Inline Code (`code`)
            if (text[i] == '`') {
                val endCode = text.indexOf('`', i + 1)
                if (endCode != -1) {
                    val codeContent = text.substring(i + 1, endCode)
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            background = accentColor.copy(alpha = 0.12f)
                        )
                    ) {
                        append(" $codeContent ")
                    }
                    i = endCode + 1
                    continue
                }
            }

            // Italic (*text* or _text_)
            if (text[i] == '*' || text[i] == '_') {
                val char = text[i]
                val endItalic = text.indexOf(char, i + 1)
                if (endItalic != -1 && endItalic > i + 1) {
                    val italicContent = text.substring(i + 1, endItalic)
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(italicContent)
                    }
                    i = endItalic + 1
                    continue
                }
            }

            append(text[i])
            i++
        }
    }
}

fun stripMarkdownFormatting(rawText: String): String {
    return rawText
        .replace(Regex("^#+\\s*", RegexOption.MULTILINE), "")
        .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
        .replace(Regex("\\*(.*?)\\*"), "$1")
        .replace(Regex("`(.*?)`"), "$1")
        .replace(Regex("^\\[(.*?)]\\(.*\\)"), "$1")
        .trim()
}
