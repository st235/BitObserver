package github.com.st235.bitobserver.presentation.charts

import github.com.st235.bitobserver.presentation.base.BaseView
import github.com.st235.data.models.ChartResponse

interface ChartView: BaseView {
    fun show(response: ChartResponse)
}