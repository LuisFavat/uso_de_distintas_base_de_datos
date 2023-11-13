package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Especie
import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EspecieServiceTest :ServiciosParaTest(){


    val capacidadDeContagioAPersona = 100
    val capacidadDeContagioAAnimal  = 1
    val capacidadDeContagioAInsecto =  5
    val defensa             = 20
    val letalidad           =  5

    //Ubicacion

    var argentina1 = Ubicacion("Argentina", 0.0, 0.0)
    var china = Ubicacion("China", 0.0, 0.0)

    //patogenos
    val bacteria = Patogeno(tipo = "bacteria", capacidadDeContagioAPersona, capacidadDeContagioAAnimal, capacidadDeContagioAInsecto, defensa, letalidad)




    val deltaHongo = - 2
    val hongo = Patogeno(tipo="hongo", capacidadDeContagioAPersona , capacidadDeContagioAAnimal , capacidadDeContagioAInsecto + deltaHongo, defensa + deltaHongo, letalidad + deltaHongo)

    //especies
    val unaEspecie = Especie(bacteria, "especiePrueba", argentina1)




    @BeforeEach
    fun crearDatosIniciales()
    {
        clear()

        runTrx {ubicacionDAO.guardar(argentina1)
                ubicacionDAO.guardar(china)
               }
        patogenoService.crear(bacteria)
        patogenoService.crear(hongo)



    }



    @AfterEach
    fun clearAll() {

      clear()

    }

    @Test
    fun cantidadDeInfectadosSinVectorTest()
    {

        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)

        var patogeno = Patogeno("virus", 100, 100, 100, 1, 1 )
        patogeno = patogenoService.crear(patogeno)
        var especie = patogenoService.agregarEspecie(patogeno.id!!, "covid", ubicacion1.id!!)

        assertEquals(0, especieService.cantidadDeInfectados(especie.id!!))

    }

    @Test
    fun cantidadDeInfectadosConUnVectorTest()
    {
        // Creo un vector y una especie.
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val vector  = vectorService.crear(TipoDeVector.Animal, ubicacion1.id!!)
        val vector2  = vectorService.crear(TipoDeVector.Animal, ubicacion1.id!!)

        var patogeno = Patogeno("virus", 100, 100, 100, 1, 1 )
        patogeno = patogenoService.crear(patogeno)
        var especie = patogenoService.agregarEspecie(patogeno.id!!, "covid", ubicacion1.id!!)

        //Infecto al vector con la especie
        vectorService.infectar(vector.id!!,especie.id!!)
        vectorService.infectar(vector2.id!!,especie.id!!)

        assertEquals(2, especieService.cantidadDeInfectados(especie.id!!))

    }

    @Test
    fun recuperarTest()
    {
        runTrx {especieDAO.guardar(unaEspecie)}
        val especieRecuperada = especieService.recuperar(unaEspecie.id!!)

        assertEquals("bacteria", especieRecuperada.patogeno.toString())
        assertEquals("especiePrueba",especieRecuperada.nombre)
        assertEquals(argentina1.nombre, especieRecuperada.paisDeOrigen.nombre)

    }

    @Test
    fun recuperarTodosTestCaseBaseDeDatosVacia()
    {

        val listaRecuperada = especieService.recuperarTodos()

         assertEquals(0, listaRecuperada.size)
    }



    @Test
    fun recuperarTodosTest()
    {
        runTrx {especieDAO.guardar(unaEspecie)}

        var listaRecuperada = especieService.recuperarTodos()

        assertEquals(1, listaRecuperada.size)
        assertTrue(listaRecuperada.any { it.nombre == unaEspecie.nombre })

        val especie2 = Especie(hongo, "especie2", china)
        runTrx {especieDAO.guardar(especie2)}

        listaRecuperada = especieService.recuperarTodos()

        assertEquals(2, listaRecuperada.size)
        assertTrue(listaRecuperada.any { it.nombre == unaEspecie.nombre })
        assertTrue(listaRecuperada.any { it.nombre == especie2.nombre })
    }

    @Test
    fun esPandemiaTest()
    {
        //Para este test no hago uso de los datos creardos por beforeEach, por eso se borran y se crean un nuevo conjunto de datos
        clear()

        //bacteria
        var bacteria = Patogeno(tipo = "bacteria", 100, 1, 35, 10, 15)

        //hongo
        var hongo = Patogeno(tipo="hongo", 100, 1, 10, 20, 5)

        //Vectores

        var argentina = TransactionRunner.runTrx { ubicacionDAO.crear("Argentina", 0.0, 0.0) }
        var uruguay   = TransactionRunner.runTrx { ubicacionDAO.crear("Uruguay", 0.0, 0.0) }
        var chile     = TransactionRunner.runTrx { ubicacionDAO.crear("Chile", 0.0, 0.0) }

        bacteria = patogenoService.crear(bacteria)
        hongo = patogenoService.crear(hongo)


        var especie1 = patogenoService.agregarEspecie(bacteria.id!!,"bacilo", argentina.id!!)
        var especie2 = patogenoService.agregarEspecie(bacteria.id!!,"coli", chile.id!!)

        var persona1 = vectorService.crear(TipoDeVector.Persona, argentina.id!!)
        var persona2 = vectorService.crear(TipoDeVector.Persona, argentina.id!!)
        var animal1  = vectorService.crear(TipoDeVector.Animal, argentina.id!!)
        var animal2  = vectorService.crear(TipoDeVector.Animal, argentina.id!!)

        vectorService.infectar(persona1.id!!, especie1.id!!)
        vectorService.infectar(persona1.id!!, especie2.id!!)
        vectorService.infectar(persona2.id!!, especie1.id!!)

        vectorService.infectar(animal1.id!!, especie1.id!!)
        vectorService.infectar(animal2.id!!, especie2.id!!)


        assertFalse(especieService.esPandemia(especie2.id!!))
        assertTrue (especieService.esPandemia(especie1.id!!))
    }

    @Test
    fun testInfectarCincoPersonasAumentaElADNDeUnaEspecie(){
        // Creo un vector y una especie.
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val vector  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)
        val vector2  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)
        val vector3  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)
        val vector4  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)
        val vector5  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)

        var patogeno = Patogeno("virus", 100, 100, 100, 1, 1 )
        patogeno = patogenoService.crear(patogeno)
        var especie = patogenoService.agregarEspecie(patogeno.id!!, "covid", ubicacion1.id!!)

        //Infecto al vector con la especie
        vectorService.infectar(vector.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)
        vectorService.infectar(vector2.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)
        vectorService.infectar(vector3.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)
        vectorService.infectar(vector4.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)
        vectorService.infectar(vector5.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)

        assertEquals(0, especie.contadorPersona)
        assertEquals(1, especie.adn)
    }

    @Test
    fun testInfectarCincoVectoresNoPersonaNoAumentaElADNDeUnaEspecie(){
        // Creo un vector y una especie.
        val ubicacion1 : Ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        val vector  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)
        val vector2  = vectorService.crear(TipoDeVector.Animal, ubicacion1.id!!)
        val vector3  = vectorService.crear(TipoDeVector.Animal, ubicacion1.id!!)
        val vector4  = vectorService.crear(TipoDeVector.Persona, ubicacion1.id!!)
        val vector5  = vectorService.crear(TipoDeVector.Insecto, ubicacion1.id!!)

        var patogeno = Patogeno("virus", 100, 100, 100, 1, 1 )
        patogeno = patogenoService.crear(patogeno)
        var especie = patogenoService.agregarEspecie(patogeno.id!!, "covid", ubicacion1.id!!)

        //Infecto al vector con la especie
        vectorService.infectar(vector.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)
        vectorService.infectar(vector2.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)
        vectorService.infectar(vector3.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)
        vectorService.infectar(vector4.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)
        vectorService.infectar(vector5.id!!,especie.id!!)
        especie = especieService.recuperar(especie.id!!)

        assertEquals(2, especie.contadorPersona)
        assertEquals(0, especie.adn)
    }

}