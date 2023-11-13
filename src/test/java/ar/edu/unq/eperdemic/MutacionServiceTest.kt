package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalArgumentException

class MutacionServiceTest: ServiciosParaTest() {

    @AfterEach
    fun clearAll() {

        clear()

    }

    @Test
    fun testSePersisteMutacion() {
        var unaMutacion = StateMutacionDefensa("fiebre",10)
        var mutacionPersistida = mutacionService.crear(unaMutacion)

        assertEquals("fiebre", mutacionPersistida.nombre)
        assertEquals(10, mutacionPersistida.modificador)
    }

    @Test
    fun testSeRecuperaMutacion() {
        var unaMutacion = StateMutacionDefensa("fiebre",10)
        var mutacionPersistida = mutacionService.crear(unaMutacion)
        var mutacionRecuperada = mutacionService.recuperar(mutacionPersistida.id!!)

        assertEquals(mutacionRecuperada.nombre, mutacionPersistida.nombre)
        assertEquals(mutacionRecuperada.modificador, mutacionPersistida.modificador)
        assertEquals(mutacionRecuperada.id, mutacionPersistida.id)
    }

    @Test
    fun testSeRecuperanTodasLasMutaciones() {
        var unaMutacion = StateMutacionDefensa("fiebre",10)
        mutacionService.crear(unaMutacion)
        var otraMutacion = StateMutacionLetalidad("tos",10)
        mutacionService.crear(otraMutacion)
        assertEquals(2,mutacionService.recuperarTodos().size)
    }

    @Test
    fun testMutarUnaEspecieConMutacionesPreviasCorrectas() {
        var patogeno = Patogeno("Un tipo de patogeno", 1, 2, 3, 4,5)
        patogeno = patogenoService.crear(patogeno)
        var china = ubicacionService.crear("China", 0.0, 0.0)
        var corona: Especie = patogenoService.agregarEspecie(patogeno.id!!, "Corona", china.id!!)

        var covid: Mutacion = StateMutacionContagioAPersona("Covid-19", 10)

        var gripePorcina: Mutacion = StateMutacionContagioAInsecto("Gripe Porcina", 20)
        var gripeAviar: Mutacion = StateMutacionContagioAAnimal("Gripe Aviar", 5)
        var h1n1: Mutacion = StateMutacionDefensa("h1n1", 30)

        gripePorcina = mutacionService.crear(gripePorcina)
        gripeAviar = mutacionService.crear(gripeAviar)
        h1n1 = mutacionService.crear(h1n1)

        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)

        covid = mutacionService.crear(covid)

        corona.adn = 999
        patogenoService.actualizarEspecie(corona.id!!, corona)

        corona.agregarMutacion(gripePorcina)
        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripeAviar)

        var especieMutada =  mutacionService.mutar(corona.id!!,h1n1.id!!)
        especieMutada = mutacionService.mutar(especieMutada.id!!,gripePorcina.id!!)
        especieMutada = mutacionService.mutar(especieMutada.id!!,gripeAviar.id!!)
        especieMutada = mutacionService.mutar(especieMutada.id!!,covid.id!!)

        Assertions.assertTrue(especieMutada.mutaciones.any{ it.nombre == covid.nombre})
    }

    @Test
    fun testMutarUnaEspecieSinMutacionesPreviasCorrectas() {
        var patogeno = Patogeno("Un tipo de patogeno", 1, 2, 3, 4,5)
        patogeno = patogenoService.crear(patogeno)
        var china = ubicacionService.crear("China", 0.0, 0.0)
        var corona: Especie = patogenoService.agregarEspecie(patogeno.id!!, "Corona", china.id!!)

        var covid: Mutacion = StateMutacionContagioAPersona("Covid-19", 10)

        var gripePorcina: Mutacion = StateMutacionContagioAInsecto("Gripe Porcina", 20)
        var gripeAviar: Mutacion = StateMutacionContagioAAnimal("Gripe Aviar", 5)
        var h1n1: Mutacion = StateMutacionDefensa("h1n1", 30)

        gripePorcina = mutacionService.crear(gripePorcina)
        gripeAviar = mutacionService.crear(gripeAviar)
        h1n1 = mutacionService.crear(h1n1)

        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)
        covid.addMutacionRequerida(h1n1)

        covid = mutacionService.crear(covid)

        corona.adn = 999
        patogenoService.actualizarEspecie(corona.id!!, corona)

        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripeAviar)

        var especieMutada =  mutacionService.mutar(corona.id!!,h1n1.id!!)
        especieMutada = mutacionService.mutar(especieMutada.id!!,gripeAviar.id!!)

        assertThrows<IllegalArgumentException> {
            especieMutada = mutacionService.mutar(especieMutada.id!!,covid.id!!)
        }
    }

    @Test
    fun testMutarRestaADN() {
        var patogeno = Patogeno("Un tipo de patogeno", 1, 2, 3, 4,5)
        patogeno = patogenoService.crear(patogeno)
        var china = ubicacionService.crear("China", 0.0, 0.0)
        var corona: Especie = patogenoService.agregarEspecie(patogeno.id!!, "Corona", china.id!!)

        var covid: Mutacion = StateMutacionContagioAPersona("Covid-19", 10)

        var gripePorcina: Mutacion = StateMutacionContagioAInsecto("Gripe Porcina", 20)
        var gripeAviar: Mutacion = StateMutacionContagioAAnimal("Gripe Aviar", 5)
        var h1n1: Mutacion = StateMutacionDefensa("h1n1", 30)

        gripePorcina.puntosADN = 10
        gripePorcina = mutacionService.crear(gripePorcina)

        gripeAviar.puntosADN = 20
        gripeAviar = mutacionService.crear(gripeAviar)

        h1n1.puntosADN = 2
        h1n1 = mutacionService.crear(h1n1)

        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)

        covid.puntosADN = 50
        covid = mutacionService.crear(covid)

        corona.adn = 100
        patogenoService.actualizarEspecie(corona.id!!, corona)

        corona.agregarMutacion(gripePorcina)
        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripeAviar)

        var especieMutada =  mutacionService.mutar(corona.id!!,h1n1.id!!)
        Assertions.assertEquals(especieMutada.adn, 98)
        especieMutada = mutacionService.mutar(especieMutada.id!!,gripePorcina.id!!)
        Assertions.assertEquals(especieMutada.adn, 88)

        especieMutada = mutacionService.mutar(especieMutada.id!!,gripeAviar.id!!)
        Assertions.assertEquals(especieMutada.adn, 68)

        especieMutada = mutacionService.mutar(especieMutada.id!!,covid.id!!)
        Assertions.assertEquals(especieMutada.adn, 18)
    }

    @Test
    fun testMutarIncrementaValoresNumericosDeLaEspecie() {
        var patogeno = Patogeno("Un tipo de patogeno", 1, 2, 3, 4,5)
        patogeno = patogenoService.crear(patogeno)
        var china = ubicacionService.crear("China", 0.0, 0.0)
        var corona: Especie = patogenoService.agregarEspecie(patogeno.id!!, "Corona", china.id!!)

        var covid: Mutacion = StateMutacionContagioAPersona("Covid-19", 10)

        var gripePorcina: Mutacion = StateMutacionContagioAInsecto("Gripe Porcina", 20)
        var gripeAviar: Mutacion = StateMutacionContagioAAnimal("Gripe Aviar", 5)
        var h1n1: Mutacion = StateMutacionDefensa("h1n1", 30)

        gripePorcina.puntosADN = 10
        gripePorcina = mutacionService.crear(gripePorcina)

        gripeAviar.puntosADN = 20
        gripeAviar = mutacionService.crear(gripeAviar)

        h1n1.puntosADN = 2
        h1n1 = mutacionService.crear(h1n1)

        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)

        covid.puntosADN = 50
        covid = mutacionService.crear(covid)

        corona.adn = 100
        patogenoService.actualizarEspecie(corona.id!!, corona)

        corona.agregarMutacion(gripePorcina)
        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripeAviar)

        var especieMutada =  mutacionService.mutar(corona.id!!,h1n1.id!!)
        Assertions.assertEquals(1, especieMutada.patogeno.capacidadDeContagioAPersona)
        Assertions.assertEquals(3, especieMutada.patogeno.capacidadDeContagioAInsecto)
        Assertions.assertEquals(2, especieMutada.patogeno.capacidadDeContagioAAnimal)
        Assertions.assertEquals(34, especieMutada.patogeno.defensa)
        Assertions.assertEquals(5, especieMutada.patogeno.letalidad)

        especieMutada = mutacionService.mutar(especieMutada.id!!,gripePorcina.id!!)
        Assertions.assertEquals(1, especieMutada.patogeno.capacidadDeContagioAPersona)
        Assertions.assertEquals(23, especieMutada.patogeno.capacidadDeContagioAInsecto)
        Assertions.assertEquals(2, especieMutada.patogeno.capacidadDeContagioAAnimal)
        Assertions.assertEquals(34, especieMutada.patogeno.defensa)
        Assertions.assertEquals(5, especieMutada.patogeno.letalidad)


        especieMutada = mutacionService.mutar(especieMutada.id!!,gripeAviar.id!!)
        Assertions.assertEquals(1, especieMutada.patogeno.capacidadDeContagioAPersona)
        Assertions.assertEquals(23, especieMutada.patogeno.capacidadDeContagioAInsecto)
        Assertions.assertEquals(7, especieMutada.patogeno.capacidadDeContagioAAnimal)
        Assertions.assertEquals(34, especieMutada.patogeno.defensa)
        Assertions.assertEquals(5, especieMutada.patogeno.letalidad)


    }

}