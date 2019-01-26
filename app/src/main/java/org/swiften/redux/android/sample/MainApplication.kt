/*
 * Copyright (c) haipham 2019. All rights reserved.
 * Any attempt to reproduce this source code in any form shall be met with legal actions.
 */

package org.swiften.redux.android.sample

import android.app.Application
import org.swiften.redux.android.sample.Redux.State
import org.swiften.redux.android.ui.AndroidPropInjector
import org.swiften.redux.android.ui.lifecycle.injectApplicationSerializable
import org.swiften.redux.store.FinalStore

/** Created by haipham on 26/1/19 */
class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    val store = FinalStore(State(), Redux.Reducer)
    val injector = AndroidPropInjector(store)

    injector.injectApplicationSerializable(this) {}
  }
}
