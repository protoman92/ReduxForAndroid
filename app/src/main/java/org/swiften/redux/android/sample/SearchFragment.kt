/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.swiften.redux.core.IActionDispatcher
import org.swiften.redux.ui.*

/** Created by haipham on 27/1/19 */
class SearchFragment : Fragment(),
  IPropContainer<Redux.State, SearchFragment.S, SearchFragment.A>,
  IPropLifecycleOwner<Redux.State> by EmptyPropLifecycleOwner(),
  IPropMapper<Redux.State, Unit, SearchFragment.S, SearchFragment.A> by SearchFragment {
  data class S(val query: String = "")
  class A

  companion object : IPropMapper<Redux.State, Unit, S, A> {
    override fun mapAction(dispatch: IActionDispatcher, state: Redux.State, outProps: Unit) = A()
    override fun mapState(state: Redux.State, outProps: Unit) = S()
  }

  override var reduxProps by ObservableReduxProps<Redux.State, S, A> { _, _ -> }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.search_fragment, container, false)
}
