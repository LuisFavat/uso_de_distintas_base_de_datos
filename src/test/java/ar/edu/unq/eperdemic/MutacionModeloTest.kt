package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.services.impl.MutacionServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MutacionModeloTest {
    var patogeno = Patogeno("tipo", 1, 2, 3, 4,5)
    var china = Ubicacion("China", 0.0, 0.0)
    var corona: Especie = Especie(patogeno,"Corona", china)

    var covid: Mutacion = StateMutacionContagioAPersona("Covid-19", 10)
    var gripePorcina: Mutacion = StateMutacionContagioAInsecto("Gripe Porcina", 20)
    var gripeAviar: Mutacion = StateMutacionContagioAAnimal("Gripe Aviar", 5)
    var h1n1: Mutacion = StateMutacionDefensa("h1n1", 30)
    var peste : Mutacion = StateMutacionLetalidad("peste", 100)

    @Test
    fun tieneMutacionesRequeridasYPuntosSuficientes(){
        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)
        gripeAviar.addMutacionRequerida(h1n1)
        covid.puntosADN = 100

        corona.adn = 101
        corona.agregarMutacion(gripePorcina)
        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripeAviar)
        Assertions.assertTrue(covid.puedeMutar(corona))
    }

    @Test
    fun tieneMutacionesRequeridasYNoTienePuntosSuficientes(){
        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)
        gripeAviar.addMutacionRequerida(h1n1)
        covid.puntosADN = 100

        corona.adn = 10
        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripePorcina)
        corona.agregarMutacion(gripeAviar)
        Assertions.assertFalse(covid.puedeMutar(corona))
    }

    @Test
    fun noTieneMutacionesRequeridasYTienePuntosSuficientes(){
        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)
        gripeAviar.addMutacionRequerida(h1n1)
        covid.puntosADN = 100

        corona.adn = 200
        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripePorcina)
        Assertions.assertFalse(covid.puedeMutar(corona))
    }

    @Test
    fun noTieneMutacionesRequeridasYNoTienePuntosSuficientes(){
        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)
        gripeAviar.addMutacionRequerida(h1n1)
        covid.puntosADN = 100

        corona.adn = 1
        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripePorcina)

        Assertions.assertFalse(covid.puedeMutar(corona))
    }

    @Test
    fun patogenoMutadoTienePropiedadesAlteradas(){
        covid.addMutacionRequerida(gripePorcina)
        covid.addMutacionRequerida(gripeAviar)
        gripeAviar.addMutacionRequerida(h1n1)
        covid.puntosADN = 100

        corona.adn = 101
        corona.agregarMutacion(gripePorcina)
        corona.agregarMutacion(h1n1)
        corona.agregarMutacion(gripeAviar)
        corona.agregarMutacion(covid)
        Assertions.assertEquals(11, patogeno.capacidadDeContagioAPersona)
        Assertions.assertEquals(23, patogeno.capacidadDeContagioAInsecto)
        Assertions.assertEquals(7, patogeno.capacidadDeContagioAAnimal)
        Assertions.assertEquals(34, patogeno.defensa)
        Assertions.assertEquals(5, patogeno.letalidad)
    }
}