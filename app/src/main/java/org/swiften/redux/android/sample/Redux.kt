/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import org.swiften.redux.core.IReducer
import org.swiften.redux.core.IReduxAction
import java.io.Serializable

/** Created by haipham on 26/1/19 */
object Redux {
  data class State(
    val main: MainFragment.S = MainFragment.S(),
    val search: SearchFragment.S = SearchFragment.S()
  ) : Serializable

  class Action {
    sealed class Main : IReduxAction {
      data class UpdateSelectedPage(val page: Int) : Main()
    }

    sealed class Search : IReduxAction {
      data class UpdateQuery(val query: String?) : Search()
    }
  }

  object Reducer: IReducer<State> {
    override fun invoke(p1: State, p2: IReduxAction): State {
      return when (p2) {
        is Action.Main -> when (p2) {
          is Action.Main.UpdateSelectedPage ->
            p1.copy(main = p1.main.copy(selectedPage = p2.page))
        }

        is Action.Search -> when (p2) {
          is Action.Search.UpdateQuery ->
            p1.copy(search = p1.search.copy(query = p2.query))
        }

        else -> p1
      }
    }
  }
}
