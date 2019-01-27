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
import kotlinx.android.synthetic.main.detail_fragment.*
import org.swiften.redux.core.IActionDispatcher
import org.swiften.redux.ui.*

/** Created by haipham on 27/1/19 */
class DetailFragment : Fragment(),
  IPropContainer<Redux.State, MusicTrack?, Unit>,
  IPropLifecycleOwner<Redux.State> by EmptyPropLifecycleOwner(),
  IPropMapper<Redux.State, Unit, MusicTrack?, Unit> by DetailFragment {
  companion object : IPropMapper<Redux.State, Unit, MusicTrack?, Unit> {
    override fun mapAction(dispatch: IActionDispatcher, state: Redux.State, outProps: Unit) = Unit

    override fun mapState(state: Redux.State, outProps: Unit): MusicTrack? {
      return state.selectedTrack?.let { i -> state.musicResult?.results?.elementAtOrNull(i) }
    }
  }

  override var reduxProps by ObservableReduxProps<Redux.State, MusicTrack?, Unit> { _, next ->
    next?.state?.also {
      this.trackName.text = it.trackName
      this.artistName.text = it.artistName
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.detail_fragment, container, false)
}
