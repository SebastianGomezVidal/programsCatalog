package it.polito.mad.lab2.classes

//The AppRate is not stored inside the user class bacause it should be easily readable for the app creator without
//reading all the user list for understanding if a certain user has given his rate
data class UserAppRate (val appRate:Float, val comment:String)