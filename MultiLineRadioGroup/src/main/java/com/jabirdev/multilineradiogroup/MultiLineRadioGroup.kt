package com.jabirdev.multilineradiogroup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jabirdev.multilineradiogroup.databinding.RadioButtonBinding
import kotlin.collections.ArrayList

class MultiLineRadioGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {
    private var mList: MutableList<CharSequence> = ArrayList()
    private var mAdapter: MultiLineRadioButtonAdapter? = null
    override fun canScrollVertically(direction: Int): Boolean {
        return false
    }

    /**
     * External implementation interface monitoring click position
     */
    private var spanCount: Int = 3
    private var spacing: Int = 4
    private var includeEdge: Boolean = false
    var setOnChoseListener: OnChoseListener? = null

    private fun initView(attrs: AttributeSet?) {
        Log.d("tag", "initView")
        if (attrs != null){
            initAttrs(attrs)
        }
        setupView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupView() {
        layoutManager = GridLayoutManager(context, spanCount)
        addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        adapter = MultiLineRadioButtonAdapter(context).also { this.mAdapter = it }
        mAdapter!!.onChoseListener = OnChoseListener {position, text ->
            mAdapter!!.currentSelected = position
            mAdapter!!.notifyDataSetChanged()
            if (setOnChoseListener != null) {
                setOnChoseListener!!.onChose(position, text)
            }
        }
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.MultiLineRadioGroup, 0, 0
        )
        try {
            setSpanCount(typedArray.getInt(R.styleable.MultiLineRadioGroup_spanCount, spanCount))
            setSpacing(typedArray.getInt(R.styleable.MultiLineRadioGroup_spacing, spacing))
            setIncludeEdge(typedArray.getBoolean(R.styleable.MultiLineRadioGroup_includeEdge, includeEdge))
            val buttonText = typedArray.getTextArray(R.styleable.MultiLineRadioGroup_addList)
            setData(buttonText.asList() as MutableList<CharSequence>)
        } finally {
            typedArray.recycle()
        }
    }

    /**
     * External setting data in
     */

    fun setSpanCount(spanCount: Int){
        if (spanCount < 2){
            throw IllegalArgumentException("spanCount minimum is 2, current: $spanCount")
        }
        this.spanCount = spanCount
        setupView()
    }

    fun setSpacing(spacing: Int){
        this.spacing = spacing
        setupView()
    }

    fun setIncludeEdge(includeEdge: Boolean){
        this.includeEdge = includeEdge
        setupView()
    }

    fun setData(list: MutableList<CharSequence>) {
        Log.d("tag", "setData")
        mAdapter!!.setData(list)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setChecked(position: Int){
        mAdapter!!.currentSelected = position
        mAdapter!!.notifyDataSetChanged()
    }

    internal inner class MultiLineRadioButtonAdapter(private val context: Context) :
        Adapter<ViewHolder>() {
        var currentSelected: Int? = null
        var onChoseListener: OnChoseListener? = null

        internal inner class GridRadioHolder(itemView: RadioButtonBinding) : ViewHolder(itemView.root) {
            private val binding = itemView

            fun setData(s: String, selected: Boolean, function: (Int) -> Unit, position: Int) {
                binding.multiLineRadioGroupDefaultRadioButton.text = s
                binding.multiLineRadioGroupDefaultRadioButton.isChecked = selected
                binding.multiLineRadioGroupDefaultRadioButton.setOnClickListener {
                    onChoseListener!!.onChose(
                        position, s
                    )
                    function(position)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = RadioButtonBinding.inflate(LayoutInflater.from(context), parent, false)
            return GridRadioHolder(view)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val function = { pos: Int ->
                if (currentSelected == null || currentSelected != pos) {
                    currentSelected = pos
                    notifyDataSetChanged()
                }
            }
            (holder as GridRadioHolder).setData(
                mList[position].toString(), position == currentSelected, function, position
            )
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        fun setData(newList: MutableList<CharSequence>){
            val diffUtil = MultiLineDiffUtil(mList, newList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)
            mList = newList
            diffResult.dispatchUpdatesTo(this)
        }
    }

    internal inner class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            if (includeEdge){
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount
                if (position < spanCount){
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount){
                    outRect.top = spacing
                }
            }
        }
    }

    internal inner class MultiLineDiffUtil(
        private val oldList: List<CharSequence>,
        private val newList: List<CharSequence>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList == newList
        }
    }

    class OnChoseListener(
        private val callbackChoseListener: (position: Int, text: String) -> Unit
    ) : CallbackChoseListener {
        override fun onChose(position: Int, text: String) {
            callbackChoseListener(position, text)
        }
    }

    interface CallbackChoseListener {
        fun onChose(position: Int, text: String)
    }

    init {
        initView(attrs)
    }
}