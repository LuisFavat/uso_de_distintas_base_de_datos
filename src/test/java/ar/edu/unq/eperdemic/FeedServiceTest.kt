package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.services.impl.FeedServiceImpl
import ar.edu.unq.eperdemic.services.impl.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class feedServiceTest : ServiciosParaTest() {

    var feedService = FeedServiceImpl(eventoDAO)




    //@AfterEach
    @BeforeEach
    fun clearAll(){
        clear()
        clearNeo4j()
        eventoDAO.deleteAll()
    }

    @Test
    fun testFeedUbicacionAlMoverInfectarAUnVector(){
        var ubicacion1 = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var ubicacion2 = ubicacionService.crear("Varela", 0.0, 0.0)
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        var vector1 = vectorService.crear(TipoDeVector.Persona,ubicacion1.id!!)
        var vector2 = vectorService.crear(TipoDeVector.Persona,ubicacion2.id!!)
        var unPatogeno = patogenoService.crear(Patogeno("Virus",100,100,100,1,100))
        var unaEspecie = patogenoService.agregarEspecie(unPatogeno.id!!, "zika", ubicacion1.id!!)
        vectorService.infectar(vector1.id!!, unaEspecie.id!!)
        ubicacionService.mover(vector1.id!!,ubicacion2.id!!)

        assertEquals(2,feedService.feedUbicacion(ubicacion2.id!!).size)
        assertTrue(feedService.feedUbicacion(ubicacion2.id!!).any { it.tipoDeEvento == "Contagio" && it.idUbicacion == ubicacion2.id!!})
        assertTrue(feedService.feedUbicacion(ubicacion2.id!!).any { it.tipoDeEvento == "Arribo" && it.idUbicacion == ubicacion2.id!!})
        // chequeo que los resultados esten ordenados por fecha
        assertTrue(feedService.feedUbicacion(ubicacion2.id!!)[0].fecha < feedService.feedUbicacion(ubicacion2.id!!)[1].fecha)
    }

    @Test
    fun testNoSeHizoNingunViajeALaLocacion() {
        var ubicacion1 = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var ubicacion2 = ubicacionService.crear("Varela", 0.0, 0.0)
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")

        assertEquals(feedService.feedUbicacion(ubicacion2.id!!).size, 0)
    }

    @Test
    fun testSeHizoViajeALaLocacionPeroNadieFueInfectadoAlli() {
        var ubicacion1 = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var ubicacion2 = ubicacionService.crear("Varela", 0.0, 0.0)
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        var vector1 = vectorService.crear(TipoDeVector.Persona,ubicacion1.id!!)
        ubicacionService.mover(vector1.id!!,ubicacion2.id!!)
        assertEquals(feedService.feedUbicacion(ubicacion2 .id!!).size, 1)
        assertEquals(feedService.feedUbicacion(ubicacion2.id!!).get(0).tipoDeEvento, "Arribo")
        assertEquals(feedService.feedUbicacion(ubicacion2.id!!).get(0).idVector, vector1.id!!)
    }


    @Test
    fun feedPatogenoTestCaseAgregarEspecie()
    {
        var unPatogeno = patogenoService.crear(Patogeno("virus",100,100,100,1,1))
        val ubicacion  = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val especie1    = patogenoService.agregarEspecie(unPatogeno.id!!, "especie1", ubicacion.id!!)

        var result = eventoDAO.feedPatogeno(unPatogeno.id!!)

        assertEquals(1, result.size)
        var evento = result.get(0)

        assertEquals("Mutacion", evento.tipoDeEvento)
        assertEquals(especie1.id,evento.idEspecie)
        assertEquals(unPatogeno.id, evento.idPatogeno)

        val especie2    = patogenoService.agregarEspecie(unPatogeno.id!!, "especie2", ubicacion.id!!)
        result = eventoDAO.feedPatogeno(unPatogeno.id!!)

        assertEquals(2, result.size)
        evento = result.get(1)

        assertEquals("Mutacion", evento.tipoDeEvento)
        assertEquals(especie2.id,evento.idEspecie)
        assertEquals(unPatogeno.id, evento.idPatogeno)
        assertTrue(result.get(0).fecha <result.get(1).fecha)

    }

    @Test
    fun feedPatogenoCaseMutaciones()
    {
        var patogeno = Patogeno("Patogeno1", 1, 2, 3, 4,5)
        patogeno = patogenoService.crear(patogeno)
        var china = ubicacionService.crear("China", 0.0, 0.0)
        var especie: Especie = patogenoService.agregarEspecie(patogeno.id!!, "Corona", china.id!!)

        var covid: Mutacion = StateMutacionContagioAPersona("Covid-19", 10)

        var gripePorcina: Mutacion = StateMutacionContagioAInsecto("Gripe Porcina", 20)
        var gripeAviar: Mutacion = StateMutacionContagioAAnimal("Gripe Aviar", 5)
        var h1n1: Mutacion = StateMutacionDefensa("h1n1", 30)

        gripePorcina = mutacionService.crear(gripePorcina)
        gripeAviar = mutacionService.crear(gripeAviar)
        h1n1 = mutacionService.crear(h1n1)

        especie.adn = 999
        patogenoService.actualizarEspecie(especie.id!!, especie)

        especie.agregarMutacion(gripePorcina)

        //se verifica que no haya ningun evento el cual haya una mutacion de momento

        var eventosMutaciones = eventoDAO.feedPatogeno(patogeno.id!!)

        val cadenaDondeBuscar = eventosMutaciones.get(0).descripcion
        val loQueQuieroBuscar = "mutado"
        val palabras = loQueQuieroBuscar.split("\\W+".toRegex()).toTypedArray()
        for (palabra in palabras) {
            if (cadenaDondeBuscar != null) {
                //esto significa que los eventos de mutacion en eventos no son debido a que una especie haya mutado
                assertFalse(cadenaDondeBuscar.contains(palabra!!))
            }

        }


        //caso favorable, ahora se "agrega" en evento donde una especie haya mutado

        var especieMutada =  mutacionService.mutar(especie.id!!,h1n1.id!!)
        eventosMutaciones = eventoDAO.feedPatogeno(patogeno.id!!)

        var evento = eventosMutaciones.get(1)


        assertEquals(2, eventosMutaciones.size)
        assertEquals("Mutacion", evento.tipoDeEvento)
        assertEquals(especie.id,evento.idEspecie)
        assertEquals(patogeno.id, evento.idPatogeno)

        //Se agregan eventos
        especie.agregarMutacion(h1n1)
        especieMutada = mutacionService.mutar(especieMutada.id!!,gripePorcina.id!!)

        eventosMutaciones = eventoDAO.feedPatogeno(patogeno.id!!)
        evento = eventosMutaciones.get(2)

        assertEquals(3, eventosMutaciones.size)
        assertEquals("Mutacion", evento.tipoDeEvento)
        assertEquals(especie.id,evento.idEspecie)
        assertEquals(patogeno.id, evento.idPatogeno)

        //se verifica que los resultados esten ordenados cronologicamente
        for(i in 0..1) {
            assertTrue(eventosMutaciones.get(i).fecha < eventosMutaciones.get(i+1).fecha)


        }


    }

    @Test
    fun feedPatogenoTestCaseEsPandemia()
    {
        //bacteria
        var bacteria = Patogeno(tipo = "bacteria", 100, 1, 35, 10, 15)


        //Vectores

        var argentina = TransactionRunner.runTrx { ubicacionDAO.crear("Argentina", 0.0, 0.0) }
        var uruguay   = TransactionRunner.runTrx { ubicacionDAO.crear("Uruguay", 0.0, 0.0) }
        var chile     = TransactionRunner.runTrx { ubicacionDAO.crear("Chile", 0.0, 0.0) }

        bacteria = patogenoService.crear(bacteria)

        var especie1 = patogenoService.agregarEspecie(bacteria.id!!,"bacilo", argentina.id!!)

        var persona1  = vectorService.crear(TipoDeVector.Persona, argentina.id!!)
        var persona2  = vectorService.crear(TipoDeVector.Persona, uruguay.id!!)
        var persona3  = vectorService.crear(TipoDeVector.Persona, chile.id!!)

        vectorService.infectar(persona1.id!!, especie1.id!!)

        //Se verifica el caso desfavorable de que no se haya registrado un evento de pandemia por error
        var eventos = eventoDAO.feedPatogeno(bacteria.id!!)
        assertFalse(eventos.any {it.tipoDeEvento == "Contagio" && it.idUbicacion == null})

        //caso favorable
        vectorService.infectar(persona2.id!!, especie1.id!!)

        assertTrue(especieService.esPandemia(especie1.id!!))

        eventos = eventoDAO.feedPatogeno(bacteria.id!!)
        var evento = eventos.get(3)

        assertEquals(4, eventos.size)
        assertEquals("Contagio", evento.tipoDeEvento)
        assertEquals(especie1.id,evento.idEspecie)
        assertEquals(bacteria.id, evento.idPatogeno)

        //se verifica que los resultados esten ordenados cronologicamente
        for(i in 0..2) {
            assertTrue(eventos.get(i).fecha < eventos.get(i+1).fecha)
        }
    }

    @Test
    fun feedPatogenoTestCasePrimerContagioEnUbicacionLibreDeEspecie()
    {

        var patogeno = Patogeno(tipo = "bacteria", 100, 100, 100, 10, 15)

        var argentina = ubicacionService.crear("Argentina", 0.0, 0.0)
        var chile     = ubicacionService.crear("chile", 0.0, 0.0)

        patogeno = patogenoService.crear(patogeno)

        var especie1 = patogenoService.agregarEspecie(patogeno.id!!,"bacilo", argentina.id!!)

        var chileno = vectorService.crear(TipoDeVector.Persona, chile.id!!)
        vectorService.infectar(chileno.id!!, especie1.id!!)

        var eventos = eventoDAO.feedPatogeno(patogeno.id!!)

        var evento = eventos.get(1)

        assertEquals(2, eventos.size)
        assertEquals("Contagio", evento.tipoDeEvento)
        assertEquals(especie1.id,evento.idEspecie)
        assertEquals(patogeno.id, evento.idPatogeno)

        //se verifica que los resultados esten ordenados cronologicamente
        assertTrue(eventos.get(0).fecha < eventos.get(1).fecha)

        //caso desfavorable alguien mas se infecta con la misma especie en la misma ubicacion

        var chileno2 = vectorService.crear(TipoDeVector.Persona, chile.id!!)
        vectorService.infectar(chileno2.id!!, especie1.id!!)
        //la cantidad de eventos no deberia cambiar
        assertEquals(2, eventos.size)

    }


    @Test
    fun testFeedVector(){
        var ubicacion1 = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var ubicacion2 = ubicacionService.crear("Varela", 0.0, 0.0)
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        var vector1 = vectorService.crear(TipoDeVector.Persona,ubicacion1.id!!)
        var vector2 = vectorService.crear(TipoDeVector.Persona,ubicacion2.id!!)
        var unPatogeno = patogenoService.crear(Patogeno("Virus",100,100,100,1,100))
        var unaEspecie = patogenoService.agregarEspecie(unPatogeno.id!!, "zika", ubicacion1.id!!)
        vectorService.infectar(vector1.id!!, unaEspecie.id!!)
        ubicacionService.mover(vector1.id!!,ubicacion2.id!!)

        var query = feedService.feedVector(vector1.id!!)
        assertEquals(3,query.size)
        assertTrue(query.any { it.idVector == vector1.id!! && it.tipoDeEvento == "Arribo"})
        assertTrue(query.any { it.idVector == vector1.id!! && it.tipoDeEvento == "Contagio"})
        assertTrue(query.any { it.idVectorContagiante == vector1.id!! && it.tipoDeEvento == "Contagio"})
        assertTrue(query[0].fecha < query[1].fecha)
    }

    @Test
    fun testVectorQueNoViajoANingunLado () {
        var ubicacion1 = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var vector1 = vectorService.crear(TipoDeVector.Persona,ubicacion1.id!!)


        var query = feedService.feedVector(vector1.id!!)
        assertEquals(0,query.size)
    }

    @Test
    fun testVectorQueViajoPeroNoTieneEnfermedades () {
        var ubicacion1 = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var ubicacion2 = ubicacionService.crear("Varela", 0.0, 0.0)
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        var vector1 = vectorService.crear(TipoDeVector.Persona,ubicacion1.id!!)
        ubicacionService.mover(vector1.id!!,ubicacion2.id!!)

        var query = feedService.feedVector(vector1.id!!)
        assertEquals(1,query.size)
        assertEquals("Arribo", query.get(0).tipoDeEvento)
        assertEquals(ubicacion2.id!!,query.get(0).idUbicacion)
    }

    @Test
    fun testVectorQueViajoTieneEnfermedadesPeroNoContagioAOtrosVectores () {
        var ubicacion1 = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var ubicacion2 = ubicacionService.crear("Varela", 0.0, 0.0)
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        var vector1 = vectorService.crear(TipoDeVector.Persona,ubicacion1.id!!)

        var unPatogeno = patogenoService.crear(Patogeno("Virus",100,100,100,1,100))
        var unaEspecie = patogenoService.agregarEspecie(unPatogeno.id!!, "zika", ubicacion1.id!!)
        vectorService.infectar(vector1.id!!, unaEspecie.id!!)
        ubicacionService.mover(vector1.id!!,ubicacion2.id!!)

        var query = feedService.feedVector(vector1.id!!)
        assertEquals(2,query.size)

        assertEquals("Arribo", query.get(1).tipoDeEvento)
        assertEquals(ubicacion2.id!!,query.get(1).idUbicacion)

        assertEquals("Contagio", query.get(0).tipoDeEvento)
        assertEquals(vector1.id!!,query.get(0).idVector)
    }

    @Test
    fun testVectorQueNoPudoViajarPorCaminoInvalido() {
        var ubicacion1 = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var ubicacion2 = ubicacionService.crear("Varela", 0.0, 0.0)
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Aereo")
        var vector1 = vectorService.crear(TipoDeVector.Persona,ubicacion1.id!!)

        var unPatogeno = patogenoService.crear(Patogeno("Virus",100,100,100,1,100))
        var unaEspecie = patogenoService.agregarEspecie(unPatogeno.id!!, "zika", ubicacion1.id!!)
        vectorService.infectar(vector1.id!!, unaEspecie.id!!)

        org.junit.jupiter.api.assertThrows<UbicacionNoAlcanzable>{ubicacionService.mover(vector1.id!!,ubicacion2.id!!)}

        var query = feedService.feedVector(vector1.id!!)
        assertEquals(1,query.size)

        assertEquals("Contagio", query.get(0).tipoDeEvento)
        assertEquals(vector1.id!!,query.get(0).idVector)
    }
}