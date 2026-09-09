package com.example.skillflow.ui.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillflow.R
import com.example.skillflow.ui.theme.SkillflowTheme
import com.example.skillflow.ui.theme.spacing

@Composable
fun NoteInputCard(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false
) {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium)
        ) {
            Text(
                text = if (isEditing) stringResource(R.string.edit_note) else stringResource(R.string.add_your_thoughts),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(text = stringResource(R.string.note_title_placeholder), style = MaterialTheme.typography.bodySmall) },
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(spacing.small))

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text(text = stringResource(R.string.note_details_placeholder), style = MaterialTheme.typography.bodySmall) },
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            Button(
                onClick = onSave,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Update else Icons.Default.Save,
                    contentDescription = if (isEditing) stringResource(R.string.update_note) else stringResource(R.string.save_note),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = if (isEditing) stringResource(R.string.update_note) else stringResource(R.string.save_note),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteInputCardPreview() {
    SkillflowTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NoteInputCard(
                title = "Title",
                onTitleChange = {},
                description = "Details",
                onDescriptionChange = {},
                onSave = {},
                isEditing = false
            )
        }
    }
}
