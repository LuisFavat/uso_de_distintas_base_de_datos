package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import javax.persistence.NoResultException

class EstadisticasServiceTest : ServiciosParaTest() {


    lateinit var argentina: Ubicacion
    lateinit var ubicacionVacia: Ubicacion

    @BeforeEach
    fun crearDatosIniciales()
    {
        clear()
        crearDatos()

    }

    fun crearDatos()
    {


        //MyModel
        val capacidadDeContagioAPersona = 100
        val capacidadDeContagioAAnimal  = 1
        val capacidadDeContagioAInsecto =  5
        val defensa             = 20
        val letalidad           =  5

        //bacteria
        var bacteria = Patogeno(tipo = "bacteria", capacidadDeContagioAPersona, capacidadDeContagioAAnimal, capacidadDeContagioAInsecto, defensa, letalidad)

        //hongo
        val deltaHongo = - 2
        var hongo = Patogeno(tipo="hongo", capacidadDeContagioAPersona, capacidadDeContagioAAnimal , capacidadDeContagioAInsecto + deltaHongo, defensa + deltaHongo, letalidad + deltaHongo)





        //Vectores



        argentina = TransactionRunner.runTrx { ubicacionDAO.crear("Argentina", 0.0, 0.0) }
        ubicacionVacia = TransactionRunner.runTrx { ubicacionDAO.crear("Ubicacion vacia", 0.0, 0.0) }

        var uruguay   = TransactionRunner.runTrx { ubicacionDAO.crear("Uruguay", 0.0, 0.0) }
        var chile     = TransactionRunner.runTrx { ubicacionDAO.crear("Chile", 0.0, 0.0) }


        bacteria = patogenoService.crear(bacteria)
        hongo = patogenoService.crear(hongo)


        var especie1 = patogenoService.agregarEspecie(bacteria.id!!,"bacilo", argentina.id!!)
        var especie2 = patogenoService.agregarEspecie(bacteria.id!!,"coli", chile.id!!)
        var especie3 = patogenoService.agregarEspecie(hongo.id!!,"champiñon", uruguay.id!!)

        var persona1 = vectorService.crear(TipoDeVector.Persona, argentina.id!!)
        var persona2 = vectorService.crear(TipoDeVector.Persona, argentina.id!!)
        var animal1  = vectorService.crear(TipoDeVector.Animal, argentina.id!!)
        var animal2  = vectorService.crear(TipoDeVector.Animal, argentina.id!!)

        var insecto1  = vectorService.crear(TipoDeVector.Animal, argentina.id!!)
        var insecto2  = vectorService.crear(TipoDeVector.Animal, argentina.id!!)

        vectorService.infectar(persona1.id!!, especie1.id!!)
        vectorService.infectar(persona1.id!!, especie2.id!!)
        vectorService.infectar(persona2.id!!, especie1.id!!)

        vectorService.infectar(animal1.id!!, especie1.id!!)
        vectorService.infectar(animal2.id!!, especie2.id!!)


    }


    fun clearAll() {

        clear()

    }

    @Test
    fun especieLiderTest(){

        assertEquals( "bacilo", estadisticaService.especieLider().nombre)

    }

    @Test
    fun especieLiderSinVectoresContagiadosTest(){
        clear ()
        assertThrows<NoResultException> {
            estadisticaService.especieLider().nombre
        }
    }

    @Test
    fun lideresTest(){

        var listaResultado = estadisticaService.lideres()
        assertEquals(2, listaResultado.size)
        assertTrue(listaResultado.any { it.nombre == "bacilo" })
        assertTrue(listaResultado.any { it.nombre == "coli" })
    }

    @Test
    fun noHayVectoresLideresTest () {
        clear()
        var resultado = estadisticaService.lideres()
        assertEquals(0, resultado.size)
    }

    @Test
    fun reporteDeContagioTest()
    {
        var reporteDeContagioEsperado = ReporteDeContagios(6,2, "bacilo")

        var result = estadisticaService.reporteDeContagios(argentina.id!!)
        assertEquals(reporteDeContagioEsperado.vectoresPresentes, result.vectoresPresentes)
        assertEquals(reporteDeContagioEsperado.vectoresInfectados, result.vectoresInfectados)
        assertEquals(reporteDeContagioEsperado.nombreDeEspecieMasInfecciosa, result.nombreDeEspecieMasInfecciosa)
    }
    @Test
    fun reporteDeContagioSinVectoresTest()
    {
        var reporteDeContagioEsperado = ReporteDeContagios(0,0, "")

        var result = estadisticaService.reporteDeContagios(ubicacionVacia.id!!)
        assertEquals(reporteDeContagioEsperado.vectoresPresentes, result.vectoresPresentes)
        assertEquals(reporteDeContagioEsperado.vectoresInfectados, result.vectoresInfectados)
        assertEquals(reporteDeContagioEsperado.nombreDeEspecieMasInfecciosa, result.nombreDeEspecieMasInfecciosa)
    }



}