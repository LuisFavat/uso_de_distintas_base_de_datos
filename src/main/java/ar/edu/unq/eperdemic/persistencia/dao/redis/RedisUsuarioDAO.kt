package ar.edu.unq.eperdemic.persistencia.dao.redis

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.modelo.Usuario
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import redis.clients.jedis.GeoCoordinate
import redis.clients.jedis.Jedis
import java.sql.Timestamp


class RedisUsuarioDAO (val encoder: Pbkdf2PasswordEncoder) {
    var jedis : Jedis = Jedis("localhost",6379)


    fun addUsuario(usuario: Usuario)
    {
        val map : MutableMap<String, String> = mutableMapOf()
        map["nombre"]            = usuario.nombre
        map["contraseña"]        = usuario.contraseña
        map["correoElectronico"] = usuario.correoElectronico
        map["id"]                = usuario.id.toString()
        map["ultimoLogeo"]       = usuario.ultimoLogeo.toString()

        jedis.hmset(usuario.nombreDeUsuario,map )

        //tiempo en segundos para que expire el cache
        val timeExpire : Long = 24*60*60

        jedis.expire(usuario.nombreDeUsuario, timeExpire )
    }


    fun authUsuario(nombreDeUsuario: String, contraseña :String) : Boolean
    {
        println("Trying to auth user")
        val password = jedis.hmget(nombreDeUsuario, "contraseña")[0]

        return encoder.matches(contraseña, password)
    }

    fun recuperarUsuario(nombreDeUsuario: String): Usuario
    {
        var list = jedis.hmget(nombreDeUsuario, "nombre","correoElectronico","id", "ultimoLogeo" )

        val nombre = list[0]
        val password = "encrypt"
        val correoElectronico = list[1]
        val id = list[2]
        val ultimoLogeo = list[3]

        val usuario = Usuario(nombre,nombreDeUsuario,password,correoElectronico)
        usuario.id = id.toLong()
        usuario.ultimoLogeo = ultimoLogeo

        return usuario
    }

    fun existsUsuario(nombreDeUsuario: String): Boolean
    {
        return jedis.exists(nombreDeUsuario)
    }


    fun actualizar(usuario: Usuario, ultimoLogeo : Timestamp)
    {
        val map = mutableMapOf<String, String>()
        map["ultimoLogeo"] = ultimoLogeo.toString()
        jedis.hset(usuario.nombreDeUsuario,map )

    }


    fun borrarDB()
    {
        jedis.flushDB()
    }




}