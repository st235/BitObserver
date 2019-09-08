package github.com.st235.bitobserver.utils

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestRxSchedulersImpl: RxSchedulers() {

    override val mainThreadScheduler: Scheduler = Schedulers.trampoline()

    override val ioScheduler: Scheduler  = Schedulers.trampoline()

    override val computationScheduler: Scheduler = Schedulers.trampoline()
}