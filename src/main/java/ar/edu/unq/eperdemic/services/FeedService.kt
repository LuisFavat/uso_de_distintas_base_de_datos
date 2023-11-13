package ar.edu.unq.eperdemic.services

import ar.edu.unq.eperdemic.modelo.Evento

interface FeedService {
    fun feedPatogeno(patogenoId : Long) : List<Evento>
    fun feedVector(vectorId: Long) : List<Evento>
    fun feedUbicacion(ubicacionId: Long) : List<Evento>
}