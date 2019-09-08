package github.com.st235.bitobserver.utils

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ObservableModelTest {

    private val observableModel = spy(ObservableModel<Int>())

    private val valueObserver = mock<Observer<Int>>()

    @Test
    fun `test add subscription and notify`() {
        observableModel.addObserver(valueObserver)
        verify(observableModel).addObserver(valueObserver)

        observableModel.notifyObservers(0)

        verify(valueObserver).invoke(0)
    }

    @Test
    fun `test removing subscription`() {
        observableModel.addObserver(valueObserver)
        verify(observableModel).addObserver(valueObserver)

        observableModel.removeObserver(valueObserver)
        verify(observableModel).removeObserver(valueObserver)

        observableModel.notifyObservers(0)

        verifyNoMoreInteractions(valueObserver)
    }
}