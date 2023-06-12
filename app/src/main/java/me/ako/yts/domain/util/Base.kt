package me.ako.yts.domain.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class Base {
    class PairLiveData<F, S>(first: LiveData<F>, second: LiveData<S>) :
        MediatorLiveData<Pair<F?, S?>>() {
        init {
            addSource(first) {
                value = Pair(it, second.value)
            }
            addSource(second) {
                value = Pair(first.value, it)
            }
        }
    }

    class CombineLiveData<F, S, R>(
        first: LiveData<F>,
        second: LiveData<S>,
        combine: (F?, S?) -> R
    ) : MediatorLiveData<R>() {
        init {
            addSource(first) {
                value = combine(it, second.value)
            }
            addSource(second) {
                value = combine(first.value, it)
            }
        }
    }
}