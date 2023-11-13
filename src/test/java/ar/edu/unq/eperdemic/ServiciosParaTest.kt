package ar.edu.unq.eperdemic


import ar.edu.unq.eperdemic.persistencia.dao.hibernate.*
import ar.edu.unq.eperdemic.persistencia.dao.mongo.MongoEventoDAO
import ar.edu.unq.eperdemic.persistencia.dao.neo4j.Neo4jUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.redis.RedisUbicacionDAO
import ar.edu.unq.eperdemic.persistencia.dao.redis.RedisUsuarioDAO
import ar.edu.unq.eperdemic.services.impl.*
import ar.edu.unq.eperdemic.services.runner.TransactionRunner
import org.neo4j.driver.*
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder

open class ServiciosParaTest {
    val encoder = Pbkdf2PasswordEncoder()

    //DAOs
    val dataDAO = HibernateDataDAO()
    val ubicacionDAO = HibernateUbicacionDAO()
    val patogenoDAO = HibernatePatogenoDAO()
    val especieDAO = HibernateEspecieDAO()
    val vectorDAO = HibernateVectorDAO()
    val ubicacionesConectadaDAO = Neo4jUbicacionDAO(vectorDAO)
    val estadisticaDAO = HibernateEstadisticaDAO()
    val mutacionDAO = HibernateMutacionDAO()
    val eventoDAO = MongoEventoDAO(especieDAO, ubicacionDAO)
    val usuarioDAO = UsuarioDAO(encoder)
    val redisUsuarioDAO = RedisUsuarioDAO(encoder)
    val redisUbicacionDAO = RedisUbicacionDAO()


    //Services
    val especieService = EspecieServiceImpl(especieDAO, eventoDAO)
    val vectorService = VectorServiceImpl( vectorDAO, ubicacionDAO, especieDAO,eventoDAO)
    val ubicacionService = UbicacionServiceImpl(redisUbicacionDAO, ubicacionesConectadaDAO, ubicacionDAO,vectorService, vectorDAO, especieDAO,especieService, eventoDAO)
    val patogenoService = PatogenoServiceImpl(eventoDAO,patogenoDAO,ubicacionService ,especieDAO)
    val estadisticaService = EstadisticasServiceImpl(estadisticaDAO)
    var mutacionService = MutacionServiceImpl(mutacionDAO,especieDAO,patogenoDAO, eventoDAO)
    val usuarioService = UsuarioServiceImp(usuarioDAO, redisUsuarioDAO)


    private val driver: Driver

    init {
        val env = System.getenv()
        val url = env.getOrDefault("NEO_URL", "bolt://localhost:7687")
        val username = env.getOrDefault("NEO_USER", "neo4j")
        val password = env.getOrDefault("NEO_PASSWORD", "root")

        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password),
            Config.builder().withLogging(Logging.slf4j()).build()
        )

    }

    fun recuperarUbicacionesNeo4j(): List<String> {
        return driver.session().use { session ->
            val query = """
                         MATCH (n:Ubicacion ) RETURN n
            """
            val result = session.run(query)
            result.list { record: Record ->
                val ubicacion = record[0]
                val nombre = ubicacion["nombre"].asString()
                nombre
            }
        }

    }


    fun recuperarUbicacionNeo4j(idUbicacion: Long): String {
        return driver.session().use { session ->
            val query = """
                         MATCH (n:Ubicacion {id: ${'$'}idUbicacion}) RETURN n.nombre as nombre
            """
            val result = session.run(query, Values.parameters("idUbicacion", idUbicacion))
            val nombre = result.single()
            return  nombre.get("nombre").asString()

        }
    }

    fun nodosConectados(idUbicacion1:Long):List<String> {
        return driver.session().use { session ->
            val query = """
                         MATCH (n:Ubicacion {id: ${'$'}idUbicacion1}) -[]-> (n2)
                         RETURN n2
            """
            val result = session.run(query, Values.parameters("idUbicacion1", idUbicacion1))
            result.list { record: Record ->
                val rel = record[0]
                val nombre = rel["nombre"].asString()
                nombre
            }
        }

    }


    fun tipDeCamino(idUbicacion1:Long,idUbicacion2:Long):String {
        return driver.session().use { session ->
            val query = """
                         match (n:Ubicacion {id: ${'$'}idUbicacion1})-[tipo]->(n2:Ubicacion {id: ${'$'}idUbicacion2})
                         return Type(tipo) as tipoDeCamino
            """
            val result = session.run(query, Values.parameters("idUbicacion1", idUbicacion1, "idUbicacion2", idUbicacion2 ))
            result.single().get("tipoDeCamino").toString().removeSurrounding("\"")
        }
        }


    fun clear() {
        TransactionRunner.runTrx {dataDAO.clear()}

    }

    fun clearNeo4j(){
        ubicacionesConectadaDAO.clear()
    }


}