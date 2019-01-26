/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import androidx.fragment.app.Fragment
import org.swiften.redux.core.IActionDispatcher
import org.swiften.redux.ui.*
import java.io.Serializable

/** Created by haipham on 27/1/19 */
class MainFragment : Fragment(),
  IPropContainer<Redux.State, MainFragment.S, MainFragment.A>,
  IPropLifecycleOwner<Redux.State> by EmptyPropLifecycleOwner(),
  IPropMapper<Redux.State, Unit, MainFragment.S, MainFragment.A> by MainFragment {
  companion object : IPropMapper<Redux.State, Unit, S, A> {
    override fun mapAction(dispatch: IActionDispatcher, state: Redux.State, outProps: Unit) =
      A { dispatch(Redux.Action.Main.UpdateSelectedPage(it)) }

    override fun mapState(state: Redux.State, outProps: Unit) = S(state.main.selectedPage)
  }

  data class S(val selectedPage: Int = 0) : Serializable
  class A(val selectPage: (Int) -> Unit)

  override var reduxProps by ObservableReduxProps<Redux.State, S, A> { _, _ -> }
}
