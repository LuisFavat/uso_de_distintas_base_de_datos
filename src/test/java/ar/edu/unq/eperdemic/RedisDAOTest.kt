package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.ServiciosParaTest
import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Usuario
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Timestamp

class RedisDAOTest: ServiciosParaTest() {

    @BeforeEach
    fun limpiarBase()
    {
        redisUsuarioDAO.borrarDB()
    }

    @Test
    fun addYRecuperarUsuarioTest()
    {
        val now = Timestamp(System.currentTimeMillis()).toString()
        var usuario = Usuario("Luis", "Keyser", encoder.encode("123456"), "uncorreo@gmail.com")
        usuario.id = 1
        usuario.ultimoLogeo = now

        redisUsuarioDAO.addUsuario(usuario)

        val usuarioRecuperado = redisUsuarioDAO.recuperarUsuario(usuario.nombreDeUsuario)

        assertEquals(usuario.nombre, usuarioRecuperado.nombre)
        assertEquals(usuario.nombreDeUsuario, usuarioRecuperado.nombreDeUsuario)
        assertEquals("encrypt", usuarioRecuperado.contraseña)
        assertEquals(usuario.correoElectronico, usuarioRecuperado.correoElectronico)
        assertEquals(usuario.id, usuarioRecuperado.id)
        assertEquals(usuario.ultimoLogeo, usuarioRecuperado.ultimoLogeo)
    }


    @Test
    fun authUsuarioTest()
    {
        val now = Timestamp(System.currentTimeMillis()).toString()
        val rawPassword = "123456"
        var usuario = Usuario("Luis", "Keyser", encoder.encode(rawPassword), "uncorreo@gmail.com")
        usuario.id = 1
        usuario.ultimoLogeo = now

        redisUsuarioDAO.addUsuario(usuario)


        assertTrue(redisUsuarioDAO.authUsuario(usuario.nombreDeUsuario, rawPassword))
        assertFalse(redisUsuarioDAO.authUsuario(usuario.nombreDeUsuario, "unaContraseñaIncorrecta"))
    }


    @Test
    fun existUsuarioTest()
    {
        assertFalse(redisUsuarioDAO.existsUsuario("Keyser"))

        val now = Timestamp(System.currentTimeMillis()).toString()
        var usuario = Usuario("Luis", "Keyser", encoder.encode("123456"), "uncorreo@gmail.com")
        usuario.id = 1
        usuario.ultimoLogeo = now

        redisUsuarioDAO.addUsuario(usuario)

        assertTrue(redisUsuarioDAO.existsUsuario("Keyser"))
    }


    @Test
    fun actualizarTest()
    {
        var now = Timestamp(System.currentTimeMillis()).toString()
        var usuario = Usuario("Luis", "Keyser", encoder.encode("123456"), "uncorreo@gmail.com")
        usuario.id = 1
        usuario.ultimoLogeo = now

        redisUsuarioDAO.addUsuario(usuario)

        var usuarioRecuperado = redisUsuarioDAO.recuperarUsuario(usuario.nombreDeUsuario)

        assertEquals(now, usuarioRecuperado.ultimoLogeo)

        var now2 = Timestamp(System.currentTimeMillis())

        assertTrue(now2.toString() != usuarioRecuperado.ultimoLogeo)

        redisUsuarioDAO.actualizar(usuario, now2)
        usuarioRecuperado = redisUsuarioDAO.recuperarUsuario(usuario.nombreDeUsuario)

        assertEquals(now2.toString(), usuarioRecuperado.ultimoLogeo)
    }

    @Test
    fun ubicacionesA100KMDeDistnacia () {
        val ubi = Ubicacion("varela", 10.0, 10.0)
        val ubi2 = Ubicacion("quilmes", 10.1, 10.1)
        val ubi3 = Ubicacion("Tokio", 50.0, 50.0)
        redisUbicacionDAO.agregarUbicacion(ubi)
        redisUbicacionDAO.agregarUbicacion(ubi2)
        redisUbicacionDAO.agregarUbicacion(ubi3)
        assertEquals(redisUbicacionDAO.getUbicacionesAKmsDeDistancia(ubi,100.0).size, 2)
    }
}