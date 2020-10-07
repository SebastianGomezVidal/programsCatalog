package it.polito.mad.lab2.classes

enum class ItemStatus{
    PURCHASABLE,
    SOLD,
    BLOCKED
}

data class Adds(var addsId:String = "", var imgPath: String, var title: String,
                var description: String, var price: String,
                var category: String, var location: String,
                var latitude:Double, var longitude:Double,
                var expireDate: String, val ownerId:String, val interestedUsers:List<String> = emptyList(),
                var status:ItemStatus = ItemStatus.PURCHASABLE, var buyerId:String?,
                var rating:Float? = null, var comment:String? = null){

    constructor() : this("","","","","","","", 1000.0, 1000.0, "",
        "", emptyList(),ItemStatus.PURCHASABLE,null,null, null)
}

