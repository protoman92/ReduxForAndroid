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
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.search_fragment.*
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

  override var reduxProps by ObservableReduxProps<Redux.State, S, A> { _, next ->
    next?.state?.also { this.view_pager.currentItem = it.selectedPage }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.main_fragment, container, false)

  override fun beforePropInjectionStarts(sp: StaticProps<Redux.State>) {
    this.fragmentManager?.also {
      val adapter = object : FragmentPagerAdapter(it) {
        override fun getItem(position: Int): Fragment = when (position) {
          Constants.MAIN_PAGE_DETAIL_INDEX -> DetailFragment()
          else -> SearchFragment()
        }

        override fun getCount() = 2
      }

      this.view_pager.adapter = adapter

      this.view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}

        override fun onPageScrolled(
          position: Int,
          positionOffset: Float,
          positionOffsetPixels: Int
        ) {}

        override fun onPageSelected(position: Int) {
          this@MainFragment.reduxProps?.variable?.action?.selectPage?.invoke(position)
        }
      })
    }
  }
}
