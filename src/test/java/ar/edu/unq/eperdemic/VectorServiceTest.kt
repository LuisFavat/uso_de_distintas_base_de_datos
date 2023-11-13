package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.*
import ar.edu.unq.eperdemic.modelo.Vector
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.persistence.PersistenceException

class VectorServiceTest : ServiciosParaTest(){



    var ubicacionPersistida : Ubicacion = ubicacionService.crear("Varela", 0.0, 0.0)
    var vectorHumano : Vector = Vector(TipoDeVector.Persona, ubicacionPersistida)
    lateinit var vectorPersistido : Vector
    lateinit var vectorRecuperado : Vector



    @BeforeEach
    fun setUp(){
        vectorPersistido = vectorService.crear(vectorHumano.tipo,vectorHumano.ubicacion.id!!)
        vectorRecuperado = vectorService.recuperar(vectorPersistido.id!!)
    }

    @AfterEach
    fun clearAll() {

       clear()

    }

    @Test
    fun testCrearVector(){
        assertEquals(vectorPersistido.tipo,vectorHumano.tipo)
        assertEquals(vectorPersistido.ubicacion.nombre,vectorHumano.ubicacion.nombre)
    }

    @Test
    fun testRecuperarVector(){

        assertEquals(vectorPersistido.id,vectorRecuperado.id)
    }

    @Test
    fun testRecuperarTodos(){
        vectorService.crear(TipoDeVector.Insecto,ubicacionPersistida.id!!)
        val lista = vectorService.recuperarTodos()
        assertEquals(2,lista.size)
    }

    @Test
    fun testInfectarVector(){
        val unPatogeno = patogenoService.crear(Patogeno("bacteria", 100, 100, 100, 1, 1))
        val unaEspecie = patogenoService.agregarEspecie(unPatogeno.id!!,"chicunguña",ubicacionPersistida.id!!)

        vectorService.infectar(vectorPersistido.id!!,unaEspecie.id!!)

        vectorPersistido = vectorService.recuperar(vectorPersistido.id!!)

        assertEquals(vectorService.enfermedades(vectorPersistido.id!!).size, 1)
    }

    @Test
    fun testEnfermedades() {
        val unPatogeno = patogenoService.crear(Patogeno("patito", 100, 100, 100, 1, 1))
        val unaEspecie = patogenoService.agregarEspecie(unPatogeno.id!!,"chicungunhaa",ubicacionPersistida.id!!)
        val otraEspecie = patogenoService.agregarEspecie(unPatogeno.id!!,"zika",ubicacionPersistida.id!!)


        vectorService.infectar(vectorPersistido.id!!,unaEspecie.id!!)
        vectorService.infectar(vectorPersistido.id!!,otraEspecie.id!!)

        vectorPersistido = vectorService.recuperar(vectorPersistido.id!!)

        assertEquals(2, vectorService.enfermedades(vectorPersistido.id!!).size)
    }

    @Test
    fun testPuedeContagiar () {
        val unVectorHumano = Vector(TipoDeVector.Persona, ubicacionPersistida)
        val unVectorAnimal = Vector(TipoDeVector.Animal, ubicacionPersistida)
        val unVectorInsecto = Vector(TipoDeVector.Insecto, ubicacionPersistida)

        assertTrue(unVectorHumano.esContagioValido(unVectorHumano))
        assertTrue(unVectorHumano.esContagioValido(unVectorAnimal))
        assertTrue(unVectorHumano.esContagioValido(unVectorInsecto))

        assertFalse(unVectorAnimal.esContagioValido(unVectorHumano))
        assertFalse(unVectorAnimal.esContagioValido(unVectorAnimal))
        assertTrue(unVectorAnimal.esContagioValido(unVectorInsecto))

        assertTrue(unVectorInsecto.esContagioValido(unVectorHumano))
        assertTrue(unVectorInsecto.esContagioValido(unVectorAnimal))
        assertFalse(unVectorInsecto.esContagioValido(unVectorInsecto))
    }

    @Test
    fun testInfectarMasDeUnaVezConUnaMismaEspecie() {
        val unPatogeno = patogenoService.crear(Patogeno("bacteria", 100, 100, 100, 1, 1))
        val unaEspecie = patogenoService.agregarEspecie(unPatogeno.id!!,"chicunguña",ubicacionPersistida.id!!)


        vectorService.infectar(vectorPersistido.id!!,unaEspecie.id!!)

        vectorPersistido = vectorService.recuperar(vectorPersistido.id!!)

        assertThrows(PersistenceException::class.java) {
            vectorService.infectar(vectorPersistido.id!!,unaEspecie.id!!)
        }
    }

    @Test
    fun testInfectarUnVectorConVariasEspecies() {
        val unPatogeno = patogenoService.crear(Patogeno("bacteria", 100, 100, 100, 1, 1))
        val unaEspecie = patogenoService.agregarEspecie(unPatogeno.id!!,"chicunguña",ubicacionPersistida.id!!)

        val otroPatogeno = patogenoService.crear(Patogeno("virus", 100, 100, 100, 5, 5))
        val otraEspecie = patogenoService.agregarEspecie(otroPatogeno.id!!,"zika",ubicacionPersistida.id!!)


        vectorService.infectar(vectorPersistido.id!!,unaEspecie.id!!)
        vectorService.infectar(vectorPersistido.id!!,otraEspecie.id!!)

        vectorPersistido = vectorService.recuperar(vectorPersistido.id!!)

        assertEquals(2, vectorPersistido.enfermedades.size)
        assertTrue(vectorPersistido.enfermedades.any { it.nombre == otraEspecie.nombre})
        assertTrue(vectorPersistido.enfermedades.any { it.nombre == unaEspecie.nombre})

    }


}