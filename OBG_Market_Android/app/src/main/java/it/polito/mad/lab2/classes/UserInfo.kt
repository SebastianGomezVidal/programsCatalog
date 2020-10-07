package it.polito.mad.lab2.classes

data class UserInfo (val profileImg:String = "", val fullName:String = "",
                     val nickName:String = "", val email:String = "",
                     val location:String = "",val latitude: Double = 1000.0,
                     val longitude:Double = 1000.0)

