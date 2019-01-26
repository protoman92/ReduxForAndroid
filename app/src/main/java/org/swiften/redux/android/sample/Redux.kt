/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import kotlinx.coroutines.async
import org.swiften.redux.core.IReducer
import org.swiften.redux.core.IReduxAction
import org.swiften.redux.saga.common.catchError
import org.swiften.redux.saga.common.mapAsync
import org.swiften.redux.saga.common.put
import org.swiften.redux.saga.rx.SagaEffects.just
import org.swiften.redux.saga.rx.SagaEffects.takeLatestAction
import org.swiften.redux.saga.rx.TakeEffectOptions
import java.io.Serializable

/** Created by haipham on 26/1/19 */
object Redux {
  data class State(
    val main: MainFragment.S = MainFragment.S(),
    val search: SearchFragment.S = SearchFragment.S(),
    val musicResult: MusicResult? = null
  ) : Serializable

  sealed class Action : IReduxAction {
    sealed class Main : Action() {
      data class UpdateSelectedPage(val page: Int) : Main()
    }

    sealed class Search : Action() {
      data class UpdateQuery(val query: String?) : Search()
    }

    data class UpdateMusicResult(val result: MusicResult?) : Action()
  }

  object Reducer: IReducer<State> {
    override fun invoke(p1: State, p2: IReduxAction): State {
      return when (p2) {
        is Action -> when (p2) {
          is Action.UpdateMusicResult -> p1.copy(musicResult = p2.result)

          is Action.Main -> when (p2) {
            is Action.Main.UpdateSelectedPage ->
              p1.copy(main = p1.main.copy(selectedPage = p2.page))
          }

          is Action.Search -> when (p2) {
            is Action.Search.UpdateQuery ->
              p1.copy(search = p1.search.copy(query = p2.query))
          }
        }

        else -> p1
      }
    }
  }

  object Saga {
    val takeOptions = TakeEffectOptions(500)

    fun searchSaga(api: ISearchAPI<MusicResult?>, query: String) =
      just(query)
        .mapAsync { this.async { api.searchMusicStore(it) } }
        .put { Action.UpdateMusicResult(it) }
        .catchError {}

    fun allSagas(api: ISearchAPI<MusicResult?>) = arrayListOf(
      takeLatestAction<Action.Search.UpdateQuery, String, Any>({ it.query }, this.takeOptions) {
        searchSaga(api, it)
      }
    )
  }
}
