package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Vector
import ar.edu.unq.eperdemic.services.impl.UbicacionNoAlcanzable
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.lang.IllegalArgumentException


class UbicacionServiceTest : ServiciosParaTest() {



    @BeforeEach
    fun clearAllAtBeginnig()
    {
        clear()
        clearNeo4j()
    }

    @AfterEach
    fun clearAll() {
        clear()
        clearNeo4j()
    }


    @Test
    fun crearTest()
    {

        //No existe ubicación persistida
        assertTrue(ubicacionService.recuperarTodos().isEmpty())

        //Crear ubicación y recuperarla
        val ubicacionPersistida : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        assertTrue(ubicacionService.recuperarTodos().isNotEmpty())
        assertTrue(ubicacionService.recuperarTodos().size==1)
        assertTrue(ubicacionService.recuperarTodos().any { it.id == ubicacionPersistida.id })
        assertTrue(ubicacionService.recuperarTodos().any { it.nombre == ubicacionPersistida.nombre })

        //Crear más ubicaciones
        ubicacionService.crear("Bernal", 0.0, 0.0)
        ubicacionService.crear("Avellaneda", 0.0, 0.0)
        ubicacionService.crear("Berazategui", 0.0, 0.0)
        assertTrue(ubicacionService.recuperarTodos().size==4)

        //TODO verificar que se hayan creado las mismas ubicaciones en neo4j

    }

    @Test
    fun nombreUnicoTest()
    {

        //No existe ubicación persistida
        assertTrue(ubicacionService.recuperarTodos().isEmpty())

        //Crear ubicación y recuperarla
        ubicacionService.crear("Quilmes", 0.0, 0.0)

        //Crear más ubicaciones
        ubicacionService.crear("Bernal", 0.0, 0.0)
        assertThrows<IllegalArgumentException> {
            ubicacionService.crear("Quilmes", 0.0, 0.0)
        }

    }





    @Test
    fun recuperarTest()
    {

        //No existe ubicación persistida
        assertTrue(ubicacionService.recuperarTodos().isEmpty())

        //Crear ubicación y recuperarla
        val ubicacionPersistida : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        assertTrue(ubicacionService.recuperarTodos().size == 1)
        assertTrue((ubicacionService.recuperarTodos()[0].id == ubicacionPersistida.id) and (ubicacionService.recuperarTodos()[0].nombre == ubicacionPersistida.nombre))

    }


    @Test
    fun recuperarTodosTest()
    {

        //Recuperar a todos cuando no existen ubicaciones persistidas
        assertTrue(ubicacionService.recuperarTodos().isEmpty())

        //Recuperar 1  ubicacion
        val ubicacion1  = ubicacionService.crear("Quilmes", 0.0, 0.0)
        var ubicacionesRecuperadas = ubicacionService.recuperarTodos()
        assertTrue(ubicacionesRecuperadas.isNotEmpty())
        assertEquals(1, ubicacionesRecuperadas.size)
        assertTrue(ubicacionesRecuperadas.any { it.nombre == ubicacion1.nombre })

        //Recuperar varias  ubicaciones
        val ubicacion2 : Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)
        ubicacionesRecuperadas = ubicacionService.recuperarTodos()
        assertEquals(2, ubicacionesRecuperadas.size)
        assertTrue(ubicacionesRecuperadas.any { it.nombre == ubicacion1.nombre })
        assertTrue(ubicacionesRecuperadas.any { it.nombre == ubicacion2.nombre })


    }

    @Test
    fun moverTest()
    {

        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)
        val ubicacion3: Ubicacion = ubicacionService.crear("Wilde", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        ubicacionesConectadaDAO.conectar(ubicacion2.id!!, ubicacion3.id!!, "Terrestre")

        //Crear vector
        val vector  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)
        ubicacionService.mover(vector.id!!, ubicacion3.id!!)

        //chequea que el vector se haya movido
        val vectorDesplazado = vectorService.recuperar(vector.id!!)
        assertEquals(ubicacion3.nombre, vectorDesplazado.ubicacion.nombre)
    }

    @Test
    fun moverCaseCaminoInvalidoTest()
    {

        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)
        val ubicacion3: Ubicacion = ubicacionService.crear("Wilde", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        ubicacionesConectadaDAO.conectar(ubicacion2.id!!, ubicacion3.id!!, "Aereo")

        //Crear vector
        val vector  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)
        assertThrows<UbicacionNoAlcanzable>{ ubicacionService.mover(vector.id!!, ubicacion3.id!!) }
    }

    @Test
    fun moverCasevectorHumanoNoSePuedeMoverPorAirePeroSiPorLosDemas() {
        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)
        val ubicacion3: Ubicacion = ubicacionService.crear("Wilde", 0.0, 0.0)
        val ubicacion4: Ubicacion = ubicacionService.crear("Salta", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        ubicacionesConectadaDAO.conectar(ubicacion2.id!!, ubicacion3.id!!, "Maritimo")
        ubicacionesConectadaDAO.conectar(ubicacion3.id!!, ubicacion4.id!!, "Aereo")

        //Crear vector
        val vector  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)

        ubicacionService.mover(vector.id!!, ubicacion3.id!!)
        //chequea que el vector se haya movido
        val vectorDesplazado = vectorService.recuperar(vector.id!!)
        assertEquals(ubicacion3.nombre, vectorDesplazado.ubicacion.nombre)
        assertThrows<UbicacionNoAlcanzable>{ ubicacionService.mover(vector.id!!, ubicacion4.id!!) }
    }

    @Test
    fun moverCasevectorInsectoNoSePuedeMoverPorMarPeroSiPorLosDemas() {
        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)
        val ubicacion3: Ubicacion = ubicacionService.crear("Wilde", 0.0, 0.0)
        val ubicacion4: Ubicacion = ubicacionService.crear("Salta", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        ubicacionesConectadaDAO.conectar(ubicacion2.id!!, ubicacion3.id!!, "Aereo")
        ubicacionesConectadaDAO.conectar(ubicacion3.id!!, ubicacion4.id!!, "Maritimo")

        //Crear vector
        val vector  = vectorService.crear(TipoDeVector.Insecto, ubicacion1.id!!)

        ubicacionService.mover(vector.id!!, ubicacion3.id!!)
        //chequea que el vector se haya movido
        val vectorDesplazado = vectorService.recuperar(vector.id!!)
        assertEquals(ubicacion3.nombre, vectorDesplazado.ubicacion.nombre)
        assertThrows<UbicacionNoAlcanzable>{ ubicacionService.mover(vector.id!!, ubicacion4.id!!) }
    }

    @Test
    fun moverCasevectorAnimalSePuedeMoverPorTodos() {
        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)
        val ubicacion3: Ubicacion = ubicacionService.crear("Wilde", 0.0, 0.0)
        val ubicacion4: Ubicacion = ubicacionService.crear("Salta", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        ubicacionesConectadaDAO.conectar(ubicacion2.id!!, ubicacion3.id!!, "Aereo")
        ubicacionesConectadaDAO.conectar(ubicacion3.id!!, ubicacion4.id!!, "Maritimo")

        //Crear vector
        val vector  = vectorService.crear(TipoDeVector.Animal, ubicacion1.id!!)

        ubicacionService.mover(vector.id!!, ubicacion4.id!!)
        //chequea que el vector se haya movido
        val vectorDesplazado = vectorService.recuperar(vector.id!!)
        assertEquals(ubicacion4.nombre, vectorDesplazado.ubicacion.nombre)
    }

    @Test
    fun vectorAnimalAlMoverseContagia() {
        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)
        val ubicacion3: Ubicacion = ubicacionService.crear("Wilde", 0.0, 0.0)
        val ubicacion4: Ubicacion = ubicacionService.crear("Salta", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        ubicacionesConectadaDAO.conectar(ubicacion2.id!!, ubicacion3.id!!, "Aereo")
        ubicacionesConectadaDAO.conectar(ubicacion3.id!!, ubicacion4.id!!, "Maritimo")

        //Crear vector
        val vector  = vectorService.crear(TipoDeVector.Animal, ubicacion1.id!!)

        var patogeno = Patogeno("virus", 100, 100, 100, 1, 1 )
        patogeno = patogenoService.crear(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "covid", ubicacion1.id!!)



        vectorService.infectar(vector.id!!, especie.id!!)
        val vector2  = vectorService.crear(TipoDeVector.Persona, ubicacion4.id!!)

        ubicacionService.mover(vector.id!!, ubicacion4.id!!)

        vectorService.recuperar(vector.id!!)
        val vectorYaUbicadoAhi = vectorService.recuperar((vector2.id!!))
        assertEquals(1, vectorYaUbicadoAhi.enfermedades.size)
    }

    @Test
    fun vectorConMultiplesEnfermedadesAlMoverseContagia() {
        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)
        val ubicacion3: Ubicacion = ubicacionService.crear("Wilde", 0.0, 0.0)
        val ubicacion4: Ubicacion = ubicacionService.crear("Salta", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")
        ubicacionesConectadaDAO.conectar(ubicacion2.id!!, ubicacion3.id!!, "Aereo")
        ubicacionesConectadaDAO.conectar(ubicacion3.id!!, ubicacion4.id!!, "Maritimo")

        //Crear vector
        val vector  = vectorService.crear(TipoDeVector.Animal, ubicacion1.id!!)

        var patogeno = Patogeno("virus", 100, 100, 100, 1, 1 )
        patogeno = patogenoService.crear(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "covid", ubicacion1.id!!)
        var patogeno2 = Patogeno("hongo", 100, 100, 100, 1, 1 )
        patogeno2 = patogenoService.crear(patogeno2)
        val especie2 = patogenoService.agregarEspecie(patogeno2.id!!, "Cordyceps", ubicacion1.id!!)



        vectorService.infectar(vector.id!!, especie.id!!)
        vectorService.infectar(vector.id!!, especie2.id!!)
        val vector2  = vectorService.crear(TipoDeVector.Persona, ubicacion4.id!!)

        ubicacionService.mover(vector.id!!, ubicacion4.id!!)

        vectorService.recuperar(vector.id!!)
        val vectorYaUbicadoAhi = vectorService.recuperar((vector2.id!!))
        assertEquals(2, vectorYaUbicadoAhi.enfermedades.size)
    }

    @Test
    fun vectorPersonaAlMoverseNoContagiaAAnimales() {
        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")

        //Crear vector
        val vector  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)

        var patogeno = Patogeno("virus", 100, 100, 100, 1, 1 )
        patogeno = patogenoService.crear(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "covid", ubicacion1.id!!)



        vectorService.infectar(vector.id!!, especie.id!!)
        val vector2  = vectorService.crear(TipoDeVector.Animal, ubicacion2.id!!)

        ubicacionService.mover(vector.id!!, ubicacion2.id!!)

        vectorService.recuperar(vector.id!!)
        val vectorYaUbicadoAhi = vectorService.recuperar((vector2.id!!))
        assertEquals(0, vectorYaUbicadoAhi.enfermedades.size)
    }

    @Test
    fun vectorSinEnfermedadesAlMoverseNoContagia() {
        //Crear ubicaciones
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val ubicacion2: Ubicacion = ubicacionService.crear("Bernal", 0.0, 0.0)

        // Se conectan las ubicaciones
        ubicacionesConectadaDAO.conectar(ubicacion1.id!!, ubicacion2.id!!, "Terrestre")

        //Crear vector
        var vector  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)

        val vector2  = vectorService.crear(TipoDeVector.Persona, ubicacion2.id!!)

        ubicacionService.mover(vector.id!!, ubicacion2.id!!)

        vector = vectorService.recuperar(vector.id!!)
        val vectorYaUbicadoAhi = vectorService.recuperar((vector2.id!!))
        assertEquals("Bernal", vector.ubicacion.nombre)
        assertEquals(0, vectorYaUbicadoAhi.enfermedades.size)
    }


    @Test
    fun crearTestCaseUbicacionesEnNeo4J()
    {
        val tokio = ubicacionService.crear("Tokio", 0.0, 0.0)
        val islaC = ubicacionService.crear("Islas Caiman", 0.0, 0.0)


        assertEquals(2, recuperarUbicacionesNeo4j().size)
        assertEquals("Tokio",recuperarUbicacionNeo4j(tokio.id!!))
        assertEquals("Islas Caiman",recuperarUbicacionNeo4j(islaC.id!!))

    }


    @Test
    fun conectarTest()
    {
        //se crean 2 ubicaciones para poder conectarlas
        val tokio = ubicacionService.crear("Tokio", 0.0, 0.0)
        val islaC = ubicacionService.crear("Islas Caiman", 0.0, 0.0)

        //se verifica que no esten conectadas
        var conectados = nodosConectados(tokio.id!!)
        assertEquals(0, conectados.size)

        //Se conectan y se verifica
        ubicacionService.conectar(tokio.id!!, islaC.id!!, "Maritimo")
        conectados = nodosConectados(tokio.id!!)

        assertEquals(1, conectados.size)
        assertTrue(conectados.any { it == "Islas Caiman" })
        assertEquals("Maritimo", tipDeCamino(tokio.id!!, islaC.id!!))

        //se crea otra conexion y se verifica
        val islaM = ubicacionService.crear("Islas Malvinas", 0.0, 0.0)
        ubicacionService.conectar(tokio.id!!, islaM.id!!, "Aereo")
        conectados = nodosConectados(tokio.id!!)

        assertEquals(2, conectados.size)
        assertTrue(conectados.any { it == "Islas Caiman" })
        assertTrue(conectados.any { it == "Islas Malvinas" })
        assertEquals("Aereo", tipDeCamino(tokio.id!!, islaM.id!!))



    }

    @Test
    fun conectadosTest()
    {
        val tokio = ubicacionService.crear("Tokio", 0.0, 0.0)
        val islaC = ubicacionService.crear("Islas Caiman", 0.0, 0.0)

        ubicacionService.conectar(tokio.id!!, islaC.id!!, "Terrestre")
        var conectados = ubicacionService.conectados(tokio.id!!)

        assertEquals(1, conectados.size)
        assertTrue(conectados.any { it.nombre == "Islas Caiman" })

        //se crea otra conexion entre ubicaciones
        val islaM = ubicacionService.crear("Islas Malvinas", 0.0, 0.0)
        ubicacionService.conectar(tokio.id!!, islaM.id!!, "Terrestre")
        conectados = ubicacionService.conectados(tokio.id!!)

        assertEquals(2, conectados.size)
        assertTrue(conectados.any { it.nombre == "Islas Caiman" })
        assertTrue(conectados.any { it.nombre == "Islas Malvinas" })
    }

    @Test
    fun capacidadDeExpansionTest()
    {

        //TODO falta que los tipos de vectores puedan no ir por los caminos que no corresponden
        val a = ubicacionService.crear("A", 0.0, 0.0)
        val b =ubicacionService.crear("B", 0.0, 0.0)
        val c =ubicacionService.crear("C", 0.0, 0.0)
        val d =ubicacionService.crear("D", 0.0, 0.0)

        ubicacionService.conectar(a.id!!, b.id!!, "Terrestre")
        ubicacionService.conectar(b.id!!, c.id!!, "Terrestre")
        ubicacionService.conectar(b.id!!, d.id!!, "Terrestre")
        ubicacionService.conectar(d.id!!, c.id!!, "Terrestre")

        var vector = Vector(TipoDeVector.Persona, a)
        vector     = runTrx { vectorDAO.crear(vector)}

        val cantidadDeDestinos = ubicacionService.capacidadDeExpansion(vector.id!!,"A",2)
        assertEquals(3, cantidadDeDestinos)
    }



    @Test
    fun menorCaminoTest()
    {
        val a = ubicacionService.crear("A", 0.0, 0.0)
        val b =ubicacionService.crear("B", 0.0, 0.0)
        val c =ubicacionService.crear("C", 0.0, 0.0)
        val d =ubicacionService.crear("D", 0.0, 0.0)


        ubicacionService.conectar(a.id!!, b.id!!, "Terrestre")
        ubicacionService.conectar(b.id!!, c.id!!, "Terrestre")
        ubicacionService.conectar(b.id!!, d.id!!, "Terrestre")
        ubicacionService.conectar(d.id!!, c.id!!, "Terrestre")


        var vector = Vector(TipoDeVector.Persona, a)
        vector     = runTrx { vectorDAO.crear(vector)}

        val camino = ubicacionService.menorCamino(vector.id!!, c.id!!)

        assertTrue(3 == camino.size)
    }

    @Test
    fun testExpandirCasoVectoresEnfermos(){
        val a = ubicacionService.crear("A", 0.0, 0.0)

        // Se crea un vector con una enfermedad
        var vectorConCovid  = vectorService.crear(TipoDeVector.Persona, a.id!!)
        var patogeno = Patogeno("virus", 100, 100, 100, 1, 1 )
        patogeno = patogenoService.crear(patogeno)
        val especie = patogenoService.agregarEspecie(patogeno.id!!, "covid", a.id!!)
        vectorService.infectar(vectorConCovid.id!!,especie.id!!)
        vectorConCovid = vectorService.recuperar(vectorConCovid.id!!)
        // Se crea otro vector en la misma ubicacion con otra enfermedad
        var vectorConCordyceps  = vectorService.crear(TipoDeVector.Persona, a.id!!)
        var patogeno2 = Patogeno("hongo", 100, 100, 100, 1, 1 )
        patogeno2 = patogenoService.crear(patogeno)
        val especie2 = patogenoService.agregarEspecie(patogeno2.id!!, "cordyceps", a.id!!)
        vectorService.infectar(vectorConCordyceps.id!!,especie2.id!!)
        vectorConCordyceps = vectorService.recuperar(vectorConCordyceps.id!!)

        ubicacionService.expandir(a.id!!)
        vectorConCovid = vectorService.recuperar(vectorConCovid.id!!)
        vectorConCordyceps = vectorService.recuperar(vectorConCordyceps.id!!)

        assertTrue(vectorConCovid.enfermedades.size == 2 || vectorConCordyceps.enfermedades.size == 2)
        assertTrue(vectorConCovid.enfermedades.any {it.nombre == "cordyceps"} || vectorConCordyceps.enfermedades.any {it.nombre == "covid"})
    }

    @Test
    fun testExpandirCasoSinVectoresEnfermos(){
        val a = ubicacionService.crear("A", 0.0, 0.0)

        // Se crea un vector sin enfermedad
        var vectorConCovid  = vectorService.crear(TipoDeVector.Persona, a.id!!)
        // Se crea otro vector en la misma ubicacion sin enfermedad
        var vectorConCordyceps  = vectorService.crear(TipoDeVector.Persona, a.id!!)


        ubicacionService.expandir(a.id!!)
        vectorConCovid = vectorService.recuperar(vectorConCovid.id!!)
        vectorConCordyceps = vectorService.recuperar(vectorConCordyceps.id!!)

        assertTrue(vectorConCovid.enfermedades.size == 0 && vectorConCordyceps.enfermedades.size == 0)
    }
}