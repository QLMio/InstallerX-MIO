package com.rosan.installer.ui.page.installer.dialog.inner

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rosan.installer.R
import com.rosan.installer.data.common.util.addAll
import com.rosan.installer.data.installer.repo.InstallerRepo
import com.rosan.installer.data.settings.model.room.entity.launcher.getAuthorizerLauncher
import com.rosan.installer.data.settings.util.ConfigUtil
import com.rosan.installer.ui.page.installer.dialog.DialogParams
import com.rosan.installer.ui.page.installer.dialog.DialogParamsType
import com.rosan.installer.ui.page.installer.dialog.DialogViewAction
import com.rosan.installer.ui.page.installer.dialog.DialogViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "InstallSuccessDialog"

@Composable
fun installSuccessDialog(installer: InstallerRepo, viewModel: DialogViewModel): DialogParams {
    val context = LocalContext.current
    val packageName = installer.entities.filter { it.selected }.map { it.app }.first().packageName
    return installInfoDialog(installer, viewModel) {
                context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.fromParts("package", packageName, null))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                viewModel.dispatch(DialogViewAction.Background)
            }
            .copy(
                    buttons =
                            DialogButtons(DialogParamsType.InstallerInstallSuccess.id) {
                                val list = mutableListOf<DialogButton>()
                                val intent =
                                        context.packageManager.getLaunchIntentForPackage(
                                                packageName
                                        )
                                if (intent != null)
                                        list.add(
                                                DialogButton(stringResource(R.string.open)) {
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        try {
                                                            val config =
                                                                    withContext(Dispatchers.IO) {
                                                                        ConfigUtil.getByPackageName(
                                                                                packageName
                                                                        )
                                                                    }
                                                            if (config.useAuthorizerLauncher) {
                                                                getAuthorizerLauncher(
                                                                                config.authorizer
                                                                        )
                                                                        .launchApp(packageName)
                                                                Log.d(TAG, "Authorizer launched")
                                                            } else {
                                                                context.startActivity(
                                                                        intent.addFlags(
                                                                                Intent.FLAG_ACTIVITY_NEW_TASK
                                                                        )
                                                                )
                                                            }
                                                            viewModel.dispatch(
                                                                    DialogViewAction.Close
                                                            )
                                                        } catch (e: Exception) {
                                                            Log.e(TAG, "Error launching app", e)
                                                            viewModel.dispatch(
                                                                    DialogViewAction.Close
                                                            )
                                                        }
                                                    }
                                                }
                                        )
                                list.addAll(
                                        DialogButton(stringResource(R.string.previous), 2f) {
                                            viewModel.dispatch(DialogViewAction.InstallPrepare)
                                        },
                                        DialogButton(stringResource(R.string.finish), 1f) {
                                            viewModel.dispatch(DialogViewAction.Close)
                                        }
                                )
                                return@DialogButtons list
                            }
            )
}
