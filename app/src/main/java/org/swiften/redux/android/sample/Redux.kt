/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import kotlinx.coroutines.async
import org.swiften.kotlinfp.Option
import org.swiften.redux.core.IReducer
import org.swiften.redux.core.IReduxAction
import org.swiften.redux.saga.common.*
import org.swiften.redux.saga.rx.SagaEffects.putInStore
import org.swiften.redux.saga.rx.SagaEffects.selectFromState
import org.swiften.redux.saga.rx.SagaEffects.takeLatestAction
import org.swiften.redux.saga.rx.TakeEffectOptions
import java.io.Serializable

/** Created by haipham on 26/1/19 */
object Redux {
  data class State(
    val main: MainFragment.S = MainFragment.S(),
    val search: SearchFragment.S = SearchFragment.S(),
    val musicResult: MusicResult? = null,
    val selectedTrack: Int? = null
  ) : Serializable

  sealed class Action : IReduxAction {
    sealed class Main : Action() {
      data class UpdateSelectedPage(val page: Int) : Main()
    }

    sealed class Search : Action() {
      data class SetLoading(val loading: Boolean) : Search()
      data class UpdateQuery(val query: String?) : Search()
      data class UpdateResultCount(val resultCount: ResultCount?) : Search()
    }

    data class UpdateMusicResult(val result: MusicResult?) : Action()
    data class UpdateSelectedTrack(val index: Int?) : Action()
  }

  object Reducer: IReducer<State> {
    override fun invoke(p1: State, p2: IReduxAction): State {
      return when (p2) {
        is Action -> when (p2) {
          is Action.UpdateMusicResult -> p1.copy(musicResult = p2.result)

          is Action.UpdateSelectedTrack -> p1.copy(
            selectedTrack = p2.index,
            main = p1.main.copy(selectedPage = Constants.MAIN_PAGE_DETAIL_INDEX)
          )

          is Action.Main -> when (p2) {
            is Action.Main.UpdateSelectedPage ->
              p1.copy(main = p1.main.copy(selectedPage = p2.page))
          }

          is Action.Search -> when (p2) {
            is Action.Search.SetLoading -> p1.copy(search = p1.search.copy(loading = p2.loading))
            is Action.Search.UpdateQuery -> p1.copy(search = p1.search.copy(query = p2.query))

            is Action.Search.UpdateResultCount ->
              p1.copy(search = p1.search.copy(resultCount = p2.resultCount))
          }
        }

        else -> p1
      }
    }
  }

  object Saga {
    val takeOptions = TakeEffectOptions(500)

    fun searchSaga(api: ISearchAPI<MusicResult?>): SagaEffect<Any> {
      return takeLatestAction<Action.Search, Unit, Any>({
        when (it) {
          is Action.Search.UpdateQuery -> Unit
          is Action.Search.UpdateResultCount -> Unit
          else -> null
        }
      }, this.takeOptions) { _ ->
        selectFromState(State::class) {
          Option.wrap(it.search.query)
            .zipWithNullable(it.search.resultCount) { a, b -> a to b.count } }
          .mapIgnoringNull { it.value }
          .thenMightAsWell(putInStore(Action.Search.SetLoading(true)))
          .mapAsync { this.async {
            Option.wrap(api.searchMusicStore(it.first, it.second))
          } }
          .putInStore { Action.UpdateMusicResult(it.value) }
          .thenNoMatterWhat(putInStore(Action.Search.SetLoading(false)))
      }
    }

    fun allSagas(api: ISearchAPI<MusicResult?>) = arrayListOf(searchSaga(api))
  }
}
