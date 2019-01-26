/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.search_fragment.*
import org.swiften.redux.core.IActionDispatcher
import org.swiften.redux.ui.*

/** Created by haipham on 27/1/19 */
class SearchFragment : Fragment(),
  IPropContainer<Redux.State, SearchFragment.S, SearchFragment.A>,
  IPropLifecycleOwner<Redux.State> by EmptyPropLifecycleOwner(),
  IPropMapper<Redux.State, Unit, SearchFragment.S, SearchFragment.A> by SearchFragment {
  data class S(val query: String? = null)
  class A(val updateQuery: (String?) -> Unit)

  companion object : IPropMapper<Redux.State, Unit, S, A> {
    override fun mapAction(dispatch: IActionDispatcher, state: Redux.State, outProps: Unit) =
      A { dispatch(Redux.Action.Search.UpdateQuery(it)) }

    override fun mapState(state: Redux.State, outProps: Unit) = state.search
  }

  override var reduxProps by ObservableReduxProps<Redux.State, S, A> { _, _ -> }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.search_fragment, container, false)

  override fun beforePropInjectionStarts(sp: StaticProps<Redux.State>) {
    this.search_query.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        this@SearchFragment.reduxProps?.variable?.action?.updateQuery?.invoke(s?.toString())
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
  }
}
