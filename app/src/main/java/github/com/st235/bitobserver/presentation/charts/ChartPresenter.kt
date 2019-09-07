package github.com.st235.bitobserver.presentation.charts

import github.com.st235.bitobserver.presentation.base.BasePresenter
import github.com.st235.bitobserver.utils.RxSchedulers
import github.com.st235.data.ChartRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartPresenter @Inject constructor(
    private val chartRepository: ChartRepository,
    private val schedulers: RxSchedulers
): BasePresenter<ChartView>() {

    override fun onAttach(v: ChartView) {
        super.onAttach(v)

        chartRepository.fetchCharts()
            .compose(schedulers.getComputationToMainTransformer())
            .subscribeTillDetach(onNext = {
                Timber.d("$it")
                this.view?.show(it)
            })
    }
}