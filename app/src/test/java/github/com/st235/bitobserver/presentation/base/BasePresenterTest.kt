package github.com.st235.bitobserver.presentation.base

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

class BasePresenterTest {

    open class Presenter: BasePresenter<BaseView>()

    private val view = mock<BaseView>()

    private val presenter = spy(Presenter())

    @Test
    fun `test that view attached to presenter`() {
        presenter.attach(view)
        verify(presenter).onAttach(view)
    }

    @Test
    fun `test that on detach the same view will be detached`() {
        presenter.attach(view)
        verify(presenter).onAttach(view)

        presenter.detach()
        verify(presenter).onDetach(view)
    }
}
