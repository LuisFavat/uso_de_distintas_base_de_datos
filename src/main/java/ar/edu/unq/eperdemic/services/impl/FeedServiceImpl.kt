package ar.edu.unq.eperdemic.services.impl

import ar.edu.unq.eperdemic.modelo.Evento
import ar.edu.unq.eperdemic.persistencia.dao.EventoDAO
import ar.edu.unq.eperdemic.services.FeedService

class FeedServiceImpl(val eventoDAO: EventoDAO) : FeedService {

    override fun feedPatogeno(patogenoId: Long): List<Evento> {
        return eventoDAO.feedPatogeno(patogenoId)
    }

    override fun feedVector(vectorId: Long): List<Evento> {
        return eventoDAO.feedVector(vectorId)
    }

    override fun feedUbicacion(ubicacionId: Long): List<Evento> {
        return eventoDAO.feedUbicacion(ubicacionId)
    }

}