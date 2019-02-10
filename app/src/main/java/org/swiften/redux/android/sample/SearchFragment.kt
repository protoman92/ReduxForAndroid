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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_fragment.*
import org.swiften.redux.android.ui.recyclerview.IDiffItemCallback
import org.swiften.redux.android.ui.recyclerview.ReduxRecyclerViewAdapter
import org.swiften.redux.android.ui.recyclerview.injectDiffedAdapter
import org.swiften.redux.core.IActionDispatcher
import org.swiften.redux.ui.IPropContainer
import org.swiften.redux.ui.IPropMapper
import org.swiften.redux.ui.ObservableReduxProp
import org.swiften.redux.ui.StaticProp

class SearchAdapter : ReduxRecyclerViewAdapter<SearchAdapter.ViewHolder>() {
  companion object : IPropMapper<Redux.State, Unit, List<S>, A>, IDiffItemCallback<S> {
    override fun mapAction(dispatch: IActionDispatcher, outProp: Unit): A {
      return A { dispatch(Redux.Action.UpdateSelectedTrack(it)) }
    }

    override fun mapState(state: Redux.State, outProp: Unit): List<S> {
      return (state.musicResult?.results ?: arrayListOf()).map { S(it) }
    }

    override fun areContentsTheSame(oldItem: S, newItem: S): Boolean {
      return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: S, newItem: S): Boolean {
      return oldItem.track?.trackName == newItem.track?.trackName
    }
  }

  data class S(val track: MusicTrack?)
  class A(val selectTrack: (Int?) -> Unit)

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    IPropContainer<Redux.State, Unit, S, A> {
    override var reduxProp by ObservableReduxProp<S, A> { _, next ->
      next.state?.track?.also {
        this.trackName.text = it.trackName
        this.artistName.text = it.artistName
      }
    }

    private val trackName: TextView = this.itemView.findViewById(R.id.trackName)
    private val artistName: TextView = this.itemView.findViewById(R.id.artistName)

    init {
      this.itemView.setOnClickListener {
        this@ViewHolder.reduxProp.action?.selectTrack?.invoke(this@ViewHolder.layoutPosition)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val itemView = inflater.inflate(R.layout.search_item_view, parent, false)
    return ViewHolder(itemView)
  }
}

/** Created by haipham on 27/1/19 */
class SearchFragment : Fragment(),
  IPropContainer<Redux.State, Unit, SearchFragment.S, SearchFragment.A>,
  IPropMapper<Redux.State, Unit, SearchFragment.S, SearchFragment.A> by SearchFragment {
  companion object : IPropMapper<Redux.State, Unit, S, A> {
    override fun mapState(state: Redux.State, outProp: Unit) = state.search

    override fun mapAction(dispatch: IActionDispatcher, outProp: Unit): A {
      return A { dispatch(Redux.Action.Search.UpdateQuery(it)) }
    }
  }

  data class S(val query: String? = null, val loading: Boolean = false)
  class A(val updateQuery: (String?) -> Unit)

  override var reduxProp by ObservableReduxProp<S, A> { _, next ->
    next.state?.also {
      this.progress_bar.visibility = if (it.loading) View.VISIBLE else View.INVISIBLE
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.search_fragment, container, false)

  override fun beforePropInjectionStarts(sp: StaticProp<Redux.State, Unit>) {
    this.search_query.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        this@SearchFragment.reduxProp.action?.updateQuery?.invoke(s?.toString())
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })

    this.search_result.also {
      it.setHasFixedSize(true)
      it.layoutManager = LinearLayoutManager(this.context)

      it.adapter = sp.injector.injectDiffedAdapter(
        Unit,
        this@SearchFragment,
        SearchAdapter(),
        SearchAdapter,
        SearchAdapter
      )
    }
  }
}
