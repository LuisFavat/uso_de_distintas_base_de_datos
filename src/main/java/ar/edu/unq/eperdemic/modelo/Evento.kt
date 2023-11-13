package ar.edu.unq.eperdemic.modelo


import java.sql.Timestamp
import java.text.DateFormat
import java.time.LocalDateTime
import java.util.*


class Evento {

    lateinit var fecha : Date
    lateinit var tipoDeEvento:String
    var idPatogeno : Long?             = null
    var idEspecie : Long?              = null
    var idUbicacion : Long?            = null
    var descripcion : String?          = null
    var idVectorContagiante: Long? = null
    var idVector: Long? = null


    protected constructor(){
    }

    constructor(tipoDeEvento: String,
                fecha: Date, idUbicacion: Long? = null,
                idPatogeno: Long? = null,
                idEspecie : Long? = null,
                descripcion : String? = null,
                idVectorContagiante: Long? = null,
                idVector : Long? = null) {

        this.fecha        = fecha
        this.tipoDeEvento = tipoDeEvento
        this.idUbicacion  = idUbicacion
        this.idPatogeno   = idPatogeno
        this.idEspecie    = idEspecie
        this.descripcion  = descripcion
        this.idVectorContagiante     =idVectorContagiante
        this.idVector      =idVector
    }

}