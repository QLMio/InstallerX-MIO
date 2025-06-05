package com.rosan.installer.ui.page.settings.config.all

import com.rosan.installer.data.settings.model.room.entity.ConfigEntity
import com.rosan.installer.data.settings.util.ConfigOrder
import com.rosan.installer.data.settings.util.OrderType

data class AllViewState(
    val data: Data = Data()
) {
    data class Data(
        val configs: List<ConfigEntity> = emptyList(),
        val configOrder: ConfigOrder = ConfigOrder.Id(OrderType.Ascending),
        val progress: Progress = Progress.Loading,
    ) {
        sealed class Progress {
            data object Loading : Progress()
            data object Loaded : Progress()
        }
    }
}