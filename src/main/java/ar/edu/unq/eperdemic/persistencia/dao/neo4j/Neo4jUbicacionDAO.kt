package ar.edu.unq.eperdemic.persistencia.dao.neo4j

import ar.edu.unq.eperdemic.modelo.Ubicacion
import ar.edu.unq.eperdemic.persistencia.dao.VectorDAO
import ar.edu.unq.eperdemic.modelo.TipoDeVector
import org.neo4j.driver.*
import ar.edu.unq.eperdemic.services.runner.TransactionRunner.runTrx
import redis.clients.jedis.GeoCoordinate

class Neo4jUbicacionDAO (var vectorDAO: VectorDAO){
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

    fun menorCamino(vectorId : Long, ubicacionId : Long) : List<Ubicacion>
    {
        val ubicacionDePartidaId = runTrx {vectorDAO.recuperar(vectorId).ubicacion.id}
        val tipoDeVector: TipoDeVector = runTrx{ vectorDAO.recuperar(vectorId).tipo}

        return driver.session().use { session ->
            //query
            val query = """MATCH p=shortestPath((n:Ubicacion {id:${'$'}id1})-[*]->(m:Ubicacion {id:${'$'}id2}))
                           WHERE none(r IN relationships(p) WHERE type(r) = ${'$'}invalido)
                           UNWIND nodes(p) as x
                           RETURN x"""
            //ejecutar query
            val result = session.run(query, Values.parameters(
                "id1", ubicacionDePartidaId,
                "id2", ubicacionId,
                "invalido", tipoDeCaminoInvalidoPara(tipoDeVector),
            ))
            //operar sobre el resultado de la query
            result.list() { record: Record ->
                val rel = record[0]
                val nombre = rel["nombre"].asString()
                val id = rel["id"].asLong()
                var ubicacion : Ubicacion = Ubicacion(nombre, 0.0, 0.0)
                ubicacion.id = id
                ubicacion
            }
        }
    }

    fun tipoDeCaminoInvalidoPara(tipoDeVector : TipoDeVector) : String {
        var ret = ""
        when (tipoDeVector.name) {
            "Persona" -> ret = "Aereo"
            "Animal" -> ret = ""
            "Insecto" -> ret = "Maritimo"
        }
        return ret
    }


    fun create(ubicaion: Ubicacion) {
        driver.session().use { session ->

            session.writeTransaction {
                val query = "MERGE (n:Ubicacion {nombre: ${'$'}nombre, id: ${'$'}id})"
                it.run(query, Values.parameters(
                    "nombre", ubicaion.nombre,
                    "id", ubicaion.id,
                ))
            }
        }
    }


    fun conectar (idUbicacion1: Long, idUbicacion2: Long, tipoCamino: String) {
        driver.session().use { session ->
            session.writeTransaction {
                val query = """
                           match (ubicacion1:Ubicacion{id: ${'$'}idUbicacion1})
                           match (ubicacion2:Ubicacion{id: ${'$'}idUbicacion2})
                           create (ubicacion1)-[:${tipoCamino}]->(ubicacion2)
            """
                it.run(
                    query, Values.parameters(
                        "idUbicacion1", idUbicacion1,
                        "idUbicacion2", idUbicacion2,
                    )
                )
            }
        }
    }

    fun conectados(ubicacionId:Long): List<Ubicacion>
    {
        return driver.session().use { session ->
            val query = """
                         MATCH (n:Ubicacion {id: ${'$'}ubicacionId}) -[]-> (n2)
                         RETURN n2
            """
            val result = session.run(query, Values.parameters("ubicacionId", ubicacionId))
            result.list { record: Record ->
                val rel = record[0]
                val nombre = rel["nombre"].asString()
                val id = rel["id"].asLong()

                var ubicacion = Ubicacion(nombre, 0.0, 0.0)
                ubicacion.id = id

                ubicacion
            }
        }
    }

    fun capacidadDeExpansion(vectorId: Long, nombreDeUbicacion:String, movimientos:Int): Int
    {
        // que dado un vector, retorna la cantidad de diferentes ubicaciones a las que podría moverse el Vector dada una cierta cantidad de movimientos.
        var tipoDeVector = runTrx { vectorDAO.recuperar(vectorId!!).tipo }
        return driver.session().use { session ->
            val query = """
                         MATCH p = ((n:Ubicacion {nombre: ${'$'}nombreDeUbicacion}) -[*1..$movimientos]->(ubicacion))
                         WHERE none(r IN relationships(p) WHERE type(r) = ${'$'}invalido)
                         RETURN count(ubicacion) as cantidadDeUbicacionesPosibles
            """
            val result = session.run(query, Values.parameters(
                "nombreDeUbicacion", nombreDeUbicacion,
                "invalido", tipoDeCaminoInvalidoPara(tipoDeVector)
            ))
            val r = result.single()
            r.get("cantidadDeUbicacionesPosibles").asInt()

        }
    }


    fun clear() {
        return driver.session().use { session ->
            session.run("MATCH (n) DETACH DELETE n")
        }
    }

}