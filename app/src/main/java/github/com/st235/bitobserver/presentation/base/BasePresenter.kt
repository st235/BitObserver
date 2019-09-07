package github.com.st235.bitobserver.presentation.base

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.lang.ref.WeakReference

abstract class BasePresenter<View: BaseView> {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var viewRef: WeakReference<View?>

    protected var view: View?
        get() = viewRef.get()
        set(value) {
            viewRef = WeakReference(value)
        }

    fun attach(v: View) {
        view = v
        onAttach(v)
    }

    open fun onAttach(v: View) {
    }

    fun detach() {
        val v = view
        viewRef.clear()
        compositeDisposable.clear()
        onDetach(v)
    }

    open fun onDetach(v: View?) {
    }

    protected fun <T> Single<T>.subscribeTillDetach(
        onNext: (T) -> Unit,
        onError: (error: Throwable) -> Unit = { Timber.e(it) }
    ) {
        compositeDisposable.add(subscribe(onNext, onError))
    }

    protected fun <T> Observable<T>.subscribeTillDetach(
        onNext: (T) -> Unit,
        onError: (error: Throwable) -> Unit = { Timber.e(it) }
    ) {
        compositeDisposable.add(subscribe(onNext, onError))
    }
}
