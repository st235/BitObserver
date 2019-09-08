package github.com.st235.bitobserver.presentation.charts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import github.com.st235.bitobserver.R
import github.com.st235.data.models.TimeInterval
import kotlinx.android.synthetic.main.item_interval_selector.view.*

typealias OnItemClickListener<T> = (interval: T, index: Int) -> Unit

class ChartIntervalAdapter: RecyclerView.Adapter<ChartIntervalAdapter.ViewHolder>() {

    private var selectedIndex = -1

    private val intervals = mutableListOf<TimeInterval>()

    var itemClickListener: OnItemClickListener<TimeInterval>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val v = LayoutInflater.from(context).inflate(R.layout.item_interval_selector, parent, false)
        return ViewHolder(v) { _, index ->
            this@ChartIntervalAdapter.selectNewIndex(index)
            itemClickListener?.invoke(intervals[index], index)
        }
    }

    override fun getItemCount(): Int = intervals.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(intervals[position])

        if (selectedIndex == position) {
            holder.select()
        } else {
            holder.unselect()
        }
    }

    fun addAll(intervals: List<TimeInterval>) {
        this.intervals.clear()
        this.intervals.addAll(intervals)
        selectedIndex = intervals.indexOf(TimeInterval.FIVE_WEEKS)
        notifyDataSetChanged()
    }

    private fun selectNewIndex(newIndex: Int) {
        val oldIndex = selectedIndex
        selectedIndex = newIndex

        if (oldIndex >= 0 && oldIndex < intervals.size) {
            notifyItemChanged(oldIndex)
        }

        if (selectedIndex >= 0 && selectedIndex < intervals.size) {
            notifyItemChanged(selectedIndex)
        }
    }

    class ViewHolder(
        itemView: View,
        private val itemClickListener: OnItemClickListener<Unit>
    ): RecyclerView.ViewHolder(itemView) {

        private val timePeriod = itemView.timePeriod

        init {
            itemView.isClickable = true
            itemView.setOnClickListener {
                itemClickListener.invoke(Unit, adapterPosition)
            }
        }

        fun bind(interval: TimeInterval) {
            timePeriod.setText(interval.localizationId)
        }

        fun select() {
            timePeriod.setBackgroundResource(R.drawable.time_interval_background)
            timePeriod.setTextColor(ContextCompat.getColor(timePeriod.context, R.color.colorTimeIntervalTextSelected))
        }

        fun unselect() {
            timePeriod.setBackgroundDrawable(null)
            timePeriod.setTextColor(ContextCompat.getColor(timePeriod.context, R.color.colorTimeIntervalTextUnselected))
        }
    }
}
