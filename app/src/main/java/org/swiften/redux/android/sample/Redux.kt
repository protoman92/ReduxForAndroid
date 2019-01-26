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
  data class State(val query: String? = null) : Serializable

  sealed class Action : IReduxAction {
    data class UpdateQuery(val query: String) : Action()
  }

  object Reducer: IReducer<State> {
    override fun invoke(p1: State, p2: IReduxAction): State {
      return when (p2) {
        is Action -> when (p2) {
          is Action.UpdateQuery -> p1.copy(p2.query)
        }

        else -> p1
      }
    }
  }
}
