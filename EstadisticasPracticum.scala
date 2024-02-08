import com.github.tototoshi.csv.*
import java.io.File

import org.nspl.*
import org.nspl.awtrenderer.*
import org.nspl.data.HistogramData

import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._

implicit object CustomFormat2 extends DefaultCSVFormat {
  override val delimiter: Char = ';'
}

object EstadisticasPracticum {
  @main
  def estidsticas(): Unit =
    val pathDataFile: String = "D:\\Users\\LENOVO\\Downloads\\ArchivoPIntegrador\\dsPartidosYGoles.csv"
    val reader = CSVReader.open(new File(pathDataFile))
    val contentFile: List[Map[String, String]] = reader.allWithHeaders()
    reader.close()

    val pathDataFile2: String = "D:\\Users\\LENOVO\\Downloads\\ArchivoPIntegrador\\dsAlineacionesXTorneo.csv"
    val reader2 = CSVReader.open(new File(pathDataFile2))
    val contentFile2: List[Map[String, String]] = reader2.allWithHeaders()
    reader2.close()


    // grafica1(contentFile)
    // grafica2(contentFile)
    // grafica3(contentFile)

    // capacidad maxima de los estadios.
    def grafica1(data: List[Map[String, String]]): Unit =
      val capMax: List[Double] = data
        .map(x => (x("stadiums_stadium_name"), x("stadiums_stadium_capacity")))
        .distinct
        .map(_._2.toDouble)

      val histForward1 = xyplot(HistogramData(capMax, 12) -> bar())(
        par
          .xlab("Capacidad de los Estadios")
          .ylab("freq.")
          .main("estadios")
      )

      pngToFile(new File("D:\\Users\\LENOVO\\Desktop\\Verónica Luna\\graficas\\img1.png"),histForward1.build, 1000)

    def grafica2(data: List[Map[String, String]]): Unit =
      val penales: List[Double] = data.map(_("goals_own_goal")).filter(_ != "NA").map(_.toDouble)

      val histForward2 = xyplot(HistogramData(penales, 4) -> bar())(
        par
          .xlab("Autogoles")
          .ylab("freq.")
          .main("Anotaciones en propia puerta")
      )
      pngToFile(new File("D:\\Users\\LENOVO\\Desktop\\Verónica Luna\\graficas\\img2.png"), histForward2.build, 1000)

    // Saca al frecuencia por equipo
    def grafica3(data: List[Map[String, String]]): Unit =
      val teamId: List[Double] = data.map(_("goals_team_id"))
        .filter(_ != "NA")
        .map(_.replaceAll("T-", " "))
        .map(_.toDouble)

      val histForward3 = xyplot(HistogramData(teamId, 12) -> bar())(
        par
          .xlab("Equipos")
          .ylab("freq.")
          .main("Identificador de los equipos")
      )
        pngToFile(new File("D:\\Users\\LENOVO\\Desktop\\Verónica Luna\\graficas\\img3.png"), histForward3.build, 1000)



    // sql"SELECT t.tournaments_winner, t.tournaments_year FROM tournaments t WHERE tournaments_year = $year"
    val consulta1 = contentFile.filter(x => x("tournaments_year").trim == "1999")
      .map(x => x("tournaments_winner") -> x("tournaments_year"))
      .distinct

    // sql"SELECT s.squads_player_id, s.squads_tournament_id FROM squads s"
    val consulta2 = contentFile2.map(x => x("squads_player_id") -> x("squads_tournament_id"))

    // SELECT p.players_family_name, p.players_given_name, sq.squads_team_id ,sq.squads_position_name , sq.squads_shirt_number
    //            FROM squads sq
    //            INNER JOIN players p ON sq.squads_player_id = p.squadsPlayerId
    val consulta3 = contentFile2.map(x => (x("squads_player_id"), x("squads_team_id"), x("squads_position_name"), x("squads_shirt_number")))


    println(s"CONSULTAS DESDE EL DATASET")
    // Impresión de consulta1 después de su definición
    println(s"Equipo Ganador del torneo es: $consulta1 \n")
    println(s"ID participantes Torneo es: $consulta2\n")
    println(s"El esquads al que pertenecen los jugadores: $consulta3\n")

}

