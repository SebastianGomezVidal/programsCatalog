package it.polito.mad.lab2.classes

import androidx.recyclerview.widget.DiffUtil

class ItemDiffCallback(
    private val oldItems:List<Adds>,
    private val newItems:List<Adds>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size
    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean { //two item are the same one if they have
                                                                                        //both same addsId and ownerId
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return oldItem.addsId == newItem.addsId && oldItem.ownerId == newItem.ownerId
    }


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        //TODO check all the other fields!
        return oldItem.title == newItem.title
    }

}