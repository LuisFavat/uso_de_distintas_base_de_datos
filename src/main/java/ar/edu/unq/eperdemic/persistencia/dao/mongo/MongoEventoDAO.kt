package ar.edu.unq.eperdemic.persistencia.dao.mongo

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.persistencia.dao.EspecieDAO
import ar.edu.unq.eperdemic.persistencia.dao.EventoDAO
import ar.edu.unq.eperdemic.persistencia.dao.UbicacionDAO
import com.mongodb.client.model.Filters.*
import java.sql.Timestamp
import java.time.LocalDateTime


class MongoEventoDAO(val especieDAO: EspecieDAO, val ubicacionDAO: UbicacionDAO) : GenericMongoDAO<Evento>(Evento::class.java), EventoDAO {

    override fun saveEspecie(patogeno : Patogeno, especie : Especie, fecha : Timestamp)
    {
        val evento = Evento("Mutacion", fecha)
        evento.idPatogeno     = patogeno.id!!
        evento.idEspecie      = especie.id!!
        evento.descripcion = "El Patogeno: ${patogeno.toString()} (id: ${patogeno.id}) a incorporado la especie ${especie.nombre} (id: ${especie.id}) el: $fecha"

        save(evento)
    }

    override fun saveMutacion(patogeno: Patogeno, especie: Especie, fecha: Timestamp) {
        val evento = Evento("Mutacion", fecha)
        evento.idPatogeno      = patogeno.id!!
        evento.idEspecie      = especie.id!!
        evento.descripcion = "El Patogeno: ${patogeno.toString()} (id: ${patogeno.id}) ha mutado la especie ${especie.nombre} (id: ${especie.id}) el: $fecha"

        save(evento)
    }

    override fun saveEsPandemia(patogeno : Patogeno, especie: Especie, fecha: Timestamp) {

        val evento = Evento ("Contagio", fecha)
        evento.idPatogeno = patogeno.id
        evento.idEspecie      = especie.id!!
        evento.descripcion = "La especie: ${especie.nombre} (id: ${especie.id}) del patogeno ${patogeno.toString()} (id: ${patogeno.id}) se ha vuelto pandemia el: $fecha"

        save(evento)
    }

    override fun savePrimerContagioEnUbicacion(vectorContagiante: Vector?, vectorContagiado: Vector, especie: Especie, patogeno: Patogeno, fecha: Timestamp) {
        var evento = Evento("Contagio", fecha)
        evento.idVector = vectorContagiado.id
        evento.idUbicacion    =  vectorContagiado.ubicacion.id
        evento.idPatogeno = patogeno.id
        evento.idEspecie = especie.id
        evento.idVectorContagiante = vectorContagiante?.id
        evento.idVector = vectorContagiado.id
        evento.descripcion = """El patogeno ${patogeno.toString()} con id ${patogeno.id} infecto por primera vez en ubicacion ${vectorContagiado.ubicacion.nombre}
            | infectando al vector ${vectorContagiado.tipo.name} con id ${vectorContagiado.id}""".trimMargin()

        save(evento)
    }

    override fun saveArribo(vector: Vector, ubicacion: Ubicacion, fecha: Timestamp) {
        var evento = Evento("Arribo", fecha)
        evento.idVector = vector.id
        evento.idUbicacion    = ubicacion.id
        evento.descripcion = """El vector ${vector.tipo.name} con id ${vector.id} arribó en ubicación ${ubicacion.nombre}"""

        save(evento)
    }

    override fun feedPatogeno(patogenoId: Long): List<Evento> {

        var queryMutaciones = and(eq("tipoDeEvento","Mutacion"),eq("idPatogeno",patogenoId))
        var queryContagios = and(eq("tipoDeEvento","Contagio"),eq("idPatogeno",patogenoId))
        var queryConjunta = find(or(queryMutaciones,queryContagios))

        return queryConjunta
    }

    override fun saveContagio(vectorContagiante: Vector?, vectorContagiado: Vector, especie: Especie, fecha: Timestamp) {
        var evento = Evento("Contagio", fecha)
        evento.idEspecie = especie.id
        evento.idVectorContagiante = vectorContagiante?.id
        evento.idVector = vectorContagiado.id
        evento.idUbicacion = vectorContagiado.ubicacion.id
        evento.descripcion = """El vector ${vectorContagiado.tipo.name} con id ${vectorContagiado.id} se contagio con la especie ${especie.nombre} en la ubicación ${vectorContagiado.ubicacion.nombre}"""

        save(evento)
    }

    override fun feedUbicacion(ubicacionId: Long): List<Evento> {

        var queryArribos =and(eq("tipoDeEvento","Arribo"), eq("idUbicacion", ubicacionId))
        var queryContagios = and(eq("tipoDeEvento","Contagio"), eq("idUbicacion", ubicacionId))
        var queryConjunta = find((or(queryArribos,queryContagios)))

        return queryConjunta
    }

    override fun feedVector(vectorId: Long): List<Evento> {
        var queryArribos = and(eq("tipoDeEvento","Arribo"),eq("idVector",vectorId))
        var queryContagiosPropios = and(eq("tipoDeEvento","Contagio"),eq("idVector",vectorId))
        var queryContagiosAOtros = and(eq("tipoDeEvento","Contagio"),eq("idVectorContagiante",vectorId))
        var queryConjunta = find(or(queryArribos,queryContagiosPropios,queryContagiosAOtros))

        return queryConjunta
    }

    override fun infectoEnEsaUbicacion (idEspecie: Long, idUbicacion: Long): Boolean {
        var arribos =this.find(and(eq("tipoDeEvento","Arribo"), eq("idUbicacion", idUbicacion), eq("idEspecie", idEspecie)))
        return arribos.size > 0
    }


}