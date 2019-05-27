package org.schulcloud.mobile.controllers.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewHolder<T : Any, out B : ViewDataBinding>(val binding: B)
    : RecyclerView.ViewHolder(binding.root), LifecycleOwner, CoroutineScope {
    companion object {
        fun <B : ViewDataBinding> createBinding(parent: ViewGroup, @LayoutRes layoutId: Int): B {
            return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId,
                    parent, false)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main

    val context: Context
        get() = binding.root.context
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    private lateinit var _item: T
    var item: T
        get() = _item
        set(value) {
            _item = value
            lifecycleRegistry.markState(Lifecycle.State.CREATED)
            lifecycleRegistry.markState(Lifecycle.State.STARTED)
            onItemSet()
        }

    init {
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    abstract fun onItemSet()

    override fun getLifecycle(): Lifecycle = lifecycleRegistry
}
