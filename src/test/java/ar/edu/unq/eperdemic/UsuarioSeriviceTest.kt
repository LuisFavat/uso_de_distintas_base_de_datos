package ar.edu.unq.eperdemic

import ar.edu.unq.eperdemic.modelo.Usuario

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import redis.clients.jedis.Jedis
import java.sql.Timestamp


class UsuarioSeriviceTest : ServiciosParaTest() {

    @BeforeEach
    fun limpiarBase()
    {
        clear()
        redisUsuarioDAO.borrarDB()
    }

    @Test
    fun crearTest()
    {
        val usuario = Usuario("Luis Favatier", "ElMasHumilde", encoder.encode("123456"), "uncorreo@gmail.com")

        val usuarioPersistido = usuarioService.crear(usuario)

        assertEquals(usuario.nombre, usuarioPersistido.nombre)
        assertEquals(usuario.nombreDeUsuario, usuarioPersistido.nombreDeUsuario)
        assertEquals(usuario.contraseña, usuarioPersistido.contraseña)
        assertEquals(usuario.correoElectronico, usuarioPersistido.correoElectronico)
    }


    @Test
    fun recuperar()
    {
        val usuario = Usuario("Luis Favatier", "ElMasHumilde", encoder.encode("123456"), "uncorreo@gmail.com")

        usuarioService.crear(usuario)

        val usuarioPersistido = usuarioService.recuperar("ElMasHumilde")

        assertEquals(usuario.id, usuarioPersistido.id)
        assertEquals(usuario.nombre, usuarioPersistido.nombre)
        assertEquals(usuario.nombreDeUsuario, usuarioPersistido.nombreDeUsuario)
        assertEquals("encrypt", usuarioPersistido.contraseña)
        assertEquals(usuario.correoElectronico, usuarioPersistido.correoElectronico)




    }


    @Test
    fun authUsuario()
    {
        val usuario = Usuario("Luis Favatier", "ElMasHumilde", encoder.encode("123456"), "uncorreo@gmail.com")

        usuarioService.crear(usuario)

        var bool = usuarioService.authUsuario("ElMasHumilde", "123456")

        assertTrue(bool)

        bool = usuarioService.authUsuario("ElMasHumilde", "123457")

        assertFalse(bool)

    }


    @Test
    fun actualizar()
    {
        val usuario = Usuario("Luis Favatier", "ElMasHumilde", encoder.encode("123456"), "uncorreo@gmail.com")

        val usuarioPersistido = usuarioService.crear(usuario)

        assertNull(usuarioPersistido.ultimoLogeo)

        val ultimoLogeo = Timestamp(System.currentTimeMillis())

        usuarioService.actualizar(usuario,ultimoLogeo)

        val usuarioRecuperado = usuarioService.recuperar("ElMasHumilde")

        assertEquals(ultimoLogeo.toString(), usuarioRecuperado.ultimoLogeo)

    }

}