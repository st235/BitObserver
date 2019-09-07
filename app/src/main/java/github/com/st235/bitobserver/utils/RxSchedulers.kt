package github.com.st235.bitobserver.utils

import io.reactivex.SingleTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.FlowableTransformer
import io.reactivex.Scheduler


abstract class RxSchedulers {

    abstract val mainThreadScheduler: Scheduler
    abstract val ioScheduler: Scheduler
    abstract val computationScheduler: Scheduler

    fun <T> getIoToMainTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer{ objectObservable ->
            objectObservable
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

    fun <T> getIoToMainTransformerSingle(): SingleTransformer<T, T> {
        return SingleTransformer{ objectObservable ->
            objectObservable
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

    fun <T> getIoToMainTransformerFlowable(): FlowableTransformer<T, T> {
        return FlowableTransformer{ objectObservable ->
            objectObservable
                .subscribeOn(ioScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

    fun <T> getComputationToMainTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer{ objectObservable ->
            objectObservable
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

    fun <T> getComputationToMainTransformerSingle(): SingleTransformer<T, T> {
        return SingleTransformer{ objectObservable ->
            objectObservable
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
        }
    }

}