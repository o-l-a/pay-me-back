package com.example.paymeback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.paymeback.ui.navigation.PayMeBackNavHost

@Composable
fun PayMeBackApp(navController: NavHostController = rememberNavController()) {
    PayMeBackNavHost(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayMeBackTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    navigateUp: () -> Unit = {},
    canNavigateBack: Boolean,
    hasEditAction: Boolean = false,
    hasDeleteAction: Boolean = false,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(title)
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_content_description)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            if (hasEditAction) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_text))
                }
            }
            if (hasDeleteAction) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_text))
                }
            }
        }
    )
}

@Composable
fun PayMeBackBottomSaveBar(
    modifier: Modifier = Modifier,
    isFormValid: Boolean = false,
    onCancelClicked: () -> Unit,
    onSaveClicked: () -> Unit
) {
    BottomAppBar(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onCancelClicked) {
                Text(stringResource(R.string.dismiss_text))
            }
            TextButton(
                onClick = onSaveClicked,
                enabled = isFormValid
            ) {
                Text(stringResource(R.string.confirm_text))
            }
        }
    }
}

@Composable
fun DeleteAlertDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        text = {
            Text(
                stringResource(R.string.delete_message)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete_text))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dismiss_text))
            }
        }
    )
}
