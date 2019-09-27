package com.inseoul.add_place

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(
    adapter: OnItemMoveListener
) : ItemTouchHelper.Callback() {

    val mAdapter = adapter

    interface OnItemMoveListener {
        fun onItemMove(fromPosition: Int, toPosition: Int, viewHolder: RecyclerView.ViewHolder):Boolean
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

        var dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//        var swipeFlags = ItemTouchHelper.START; ItemTouchHelper.END

        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition, viewHolder)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }


    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

}
