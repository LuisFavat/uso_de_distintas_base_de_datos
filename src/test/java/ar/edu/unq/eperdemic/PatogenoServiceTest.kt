package ar.edu.unq.eperdemic


import ar.edu.unq.eperdemic.modelo.Patogeno
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.services.impl.UbicacionServiceImpl
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import java.lang.IllegalArgumentException


class PatogenoServiceTest : ServiciosParaTest()
{

    //MOCKITO
    val serviceUbicacion = mock(UbicacionServiceImpl::class.java)

    val capacidadDeContagioAPersona = 100
    val capacidadDeContagioAAnimal  = 15
    val capacidadDeContagioAInsecto =  5
    val defensa             = 20
    val letalidad           =  5

    //bacteria
    val bacteria = Patogeno(tipo = "bacteria", capacidadDeContagioAPersona, capacidadDeContagioAAnimal, capacidadDeContagioAInsecto, defensa, letalidad)

    //hongo
    val deltaHongo = - 2
    val hongo = Patogeno(tipo="hongo", capacidadDeContagioAPersona + deltaHongo, capacidadDeContagioAAnimal + deltaHongo, capacidadDeContagioAInsecto + deltaHongo, defensa + deltaHongo, letalidad + deltaHongo)

    //Ubicacion

    val argentina = Ubicacion("Argentina", 0.0, 0.0)

    fun serviceUbicacionMockFuncionality()
    {
        `when`(serviceUbicacion.recuperar(1)).thenReturn(argentina)
    }


    @BeforeEach
    fun crearDatosIniciales()
    {
        clear()
        serviceUbicacionMockFuncionality()
        runTrx {
            ubicacionDAO.guardar(argentina)
        }


    }


    @AfterEach
     fun clearAll() {

        clear()

    }




    @Test
    fun crearTest()
    {
        assertNull(bacteria.id)

        val cantidadDeEspecies = bacteria.cantidadDeEspecies

        var bacteriaPersistida = patogenoService.crear(bacteria)


        assertEquals(bacteria.toString(), bacteriaPersistida.toString())
        assertEquals(cantidadDeEspecies, bacteriaPersistida.cantidadDeEspecies)
        assertEquals(capacidadDeContagioAPersona, bacteriaPersistida.capacidadDeContagioAPersona)
        assertEquals(capacidadDeContagioAAnimal, bacteriaPersistida.capacidadDeContagioAAnimal)
        assertEquals(capacidadDeContagioAInsecto, bacteriaPersistida.capacidadDeContagioAInsecto)
        assertEquals(defensa, bacteriaPersistida.defensa)
        assertEquals(letalidad, bacteriaPersistida.letalidad)
        assertNotNull(bacteriaPersistida.id)

    }

    @Test
    fun atributosDentroDeRangoTestCasePorDebajoDeUno()
    {
        assertThrows<IllegalArgumentException> {
            Patogeno("patogeno0", 0, 0, 0, 0, 0)
        }


    }

    @Test
    fun atributosDentroDeRangoTestCaseIgualAUno()
    {
        val patogeno1   = Patogeno("patogeno1",1,1,1,1,1,)

        assertEquals("patogeno1", patogeno1.toString())
        assertEquals(1, patogeno1.capacidadDeContagioAPersona)
        assertEquals(1, patogeno1.capacidadDeContagioAAnimal)
        assertEquals(1, patogeno1.capacidadDeContagioAInsecto)
        assertEquals(1, patogeno1.defensa)
        assertEquals(1, patogeno1.letalidad)



    }

    @Test
    fun atributosDentroDeRangoTestCaseIgualACincuenta()
    {
        val patogeno50  = Patogeno("patogeno50",50,50,50,50,50)


        assertEquals("patogeno50", patogeno50.toString())
        assertEquals(50, patogeno50.capacidadDeContagioAPersona)
        assertEquals(50, patogeno50.capacidadDeContagioAAnimal)
        assertEquals(50, patogeno50.capacidadDeContagioAInsecto)
        assertEquals(50, patogeno50.defensa)
        assertEquals(50, patogeno50.letalidad)

    }

    @Test
    fun atributosDentroDeRangoTestCaseIgualACien()
    {
        val patogeno100 = Patogeno("patogeno100", 100,100,100,100,100)

        assertEquals("patogeno100", patogeno100.toString())
        assertEquals(100, patogeno100.capacidadDeContagioAPersona)
        assertEquals(100, patogeno100.capacidadDeContagioAAnimal)
        assertEquals(100, patogeno100.capacidadDeContagioAInsecto)
        assertEquals(100, patogeno100.defensa)
        assertEquals(100, patogeno100.letalidad)

    }

    @Test
    fun atributosDentroDeRangoTestCasePorEncimaDeCien()
    {
        assertThrows<IllegalArgumentException> {
            Patogeno("patogeno101", 101, 101,101, 101,101)
        }
    }

    @Test
    fun crearTestCaseIntentarCrear2VecesElMismoPatogeno()
    {
        assertNull(bacteria.id)

        patogenoService.crear(bacteria)
        patogenoService.crear(bacteria)

        val listaDePatogenos = patogenoService.recuperarTodos()

        assertEquals(1, listaDePatogenos.size)
    }

    @Test
    fun recuperarTest()
    {
        var bacteriaPersistida = patogenoService.crear(bacteria)

        val patogenoRecuperado = patogenoService.recuperar(bacteriaPersistida.id!!)


        Assertions.assertEquals(bacteriaPersistida.id, patogenoRecuperado.id)
        Assertions.assertEquals(bacteriaPersistida.toString(), patogenoRecuperado.toString())
        Assertions.assertEquals(bacteriaPersistida.cantidadDeEspecies, patogenoRecuperado.cantidadDeEspecies)
        Assertions.assertEquals(bacteriaPersistida.capacidadDeContagioAPersona, patogenoRecuperado.capacidadDeContagioAPersona)
        Assertions.assertEquals(bacteriaPersistida.capacidadDeContagioAAnimal, patogenoRecuperado.capacidadDeContagioAAnimal)
        Assertions.assertEquals(bacteriaPersistida.capacidadDeContagioAInsecto, patogenoRecuperado.capacidadDeContagioAInsecto)
        Assertions.assertEquals(bacteriaPersistida.defensa, patogenoRecuperado.defensa)
        Assertions.assertEquals(bacteriaPersistida.letalidad, patogenoRecuperado.letalidad)







    }


    @Test
    fun recuperarTestCaseIdInexistente()
    {
        val patogenoRecuperado = patogenoService.recuperar(100)

        Assertions.assertNull(patogenoRecuperado)
    }

    @Test
    fun recuperarTodosTestCaseBaseDeDatosVacia()
    {

        val listaRecuperada = patogenoService.recuperarTodos()

        Assertions.assertEquals(0, listaRecuperada.size)
    }



    @Test
    fun recuperarTodosTest()
    {
        val patogeno1 = patogenoService.crear(bacteria)

        var listaRecuperada = patogenoService.recuperarTodos()

        Assertions.assertEquals(1, listaRecuperada.size)
        Assertions.assertTrue(listaRecuperada.any { it.toString() == patogeno1.toString() })

        val patogeno2 = patogenoService.crear(hongo)

        listaRecuperada = patogenoService.recuperarTodos()

        Assertions.assertEquals(2, listaRecuperada.size)
        Assertions.assertTrue(listaRecuperada.any { it.toString() == patogeno1.toString() })
        Assertions.assertTrue(listaRecuperada.any { it.toString() == patogeno2.toString() })
    }


    @Test
    fun actualizarTest()
    {
        val unaUbicacion = Ubicacion("Argentina", 0.0, 0.0)

        patogenoService.crear(bacteria)
        assertEquals(0,bacteria.cantidadDeEspecies)

        bacteria.crearEspecie("bacilo",unaUbicacion)

        patogenoService.actualizarPatogeno(bacteria)

        val bacteriaActualizada = patogenoService.recuperar(bacteria.id!!)

        assertEquals(1,bacteriaActualizada.cantidadDeEspecies)
    }

    @Test
    fun agregarEspecieTest()
    {
        var unPatogeno = patogenoService.crear(Patogeno("virus",100,100,100,1,1))
        val ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        patogenoService.agregarEspecie(unPatogeno.id!!, "especie1", ubicacion.id!!)


        var patogenoPersistido = patogenoService.recuperar(unPatogeno.id!!)

        Assertions.assertEquals(1, patogenoPersistido.cantidadDeEspecies)

        patogenoService.agregarEspecie(unPatogeno.id!!, "especie2", ubicacion.id!!)

        patogenoPersistido = patogenoService.recuperar(unPatogeno.id!!)

        Assertions.assertEquals(2, patogenoPersistido.cantidadDeEspecies)

    }

    @Test
    fun especiesDePatogenoTest()
    {
        var unPatogeno = patogenoService.crear(Patogeno("virus",100,100,100,1,1))
        val ubicacion = ubicacionService.crear("Quilmes", 0.0, 0.0)
        patogenoService.agregarEspecie(unPatogeno.id!!, "especie1", ubicacion.id!!)

        var listaResult = patogenoService.especiesDePatogeno(unPatogeno.id!!)
        assertEquals(1, listaResult.size )

        assertTrue(listaResult.any { it.nombre =="especie1" })

        patogenoService.agregarEspecie(unPatogeno.id!!, "especie2", ubicacion.id!!)
        listaResult = patogenoService.especiesDePatogeno(unPatogeno.id!!)
        assertEquals(2, listaResult.size )

        assertTrue(listaResult.any { it.nombre =="especie1" })
        assertTrue(listaResult.any { it.nombre =="especie2" })



    }


}