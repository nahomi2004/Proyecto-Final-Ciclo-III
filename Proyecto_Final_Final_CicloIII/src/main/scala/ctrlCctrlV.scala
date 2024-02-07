// import para el archivo
import com.github.tototoshi.csv.*
import doobie.util.transactor

import java.io.File

// escribir los datos en un txt
import java.io.{BufferedWriter, FileWriter}

import doobie._
import doobie.implicits._

import cats._
import cats.effect._
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.implicits.global
import cats.implicits._

implicit object CustomFormat extends  DefaultCSVFormat {
  override  val delimiter: Char = ';'
}

object ctrlCctrlV {
  @main
  def ejecutable() =

    val xa = Transactor.fromDriverManager[IO](
      driver = "com.mysql.cj.jdbc.Driver",
      url = "jdbc:mysql://localhost: 3306/mundial",
      user = "root",
      password = "ilovelasranas2",
      logHandler = None)

    val ruta = "C:/Users/D E L L/Documents/Nahomi/CICLO III/PFR/ArchivoPIntegrador/dsAlineacionesXTorneo.csv"
    val reader = CSVReader.open(new File(ruta))
    val contentFile: List[Map[String, String]] =
      reader.allWithHeaders()

    val ruta2 = "C:/Users/D E L L/Documents/Nahomi/CICLO III/PFR/ArchivoPIntegrador/dsPartidosYGoles.csv"
    val reader2 = CSVReader.open(new File(ruta2))
    val contentFile2: List[Map[String, String]] =
      reader2.allWithHeaders()

    def valoresDoBuedos(valor: String) =
      if (valor == "not available" || valor == "not applicable" || valor == "NA" || valor == "\\s") {
        0
      } else {
        valor.toDouble
      }

    def comillasRaras(valor: String) =
      val newValor = valor.replace("'", "\\'")
      newValor

    // -----------------------------------------------------------------------------------------------------------------
    // DATOS PARA EL SCRIPT

    def escribirDatosTXT (nombreTXT: String, archivo: String): Unit =
      val rutaTXT = "C:/Users/D E L L/Documents/Nahomi/CICLO III/PROYECTO FINAL FINAL FINAL/"
      val rutaFinal = rutaTXT + nombreTXT

      val escritor = new BufferedWriter(new FileWriter(rutaFinal, true))
      try {
        escritor.write(archivo)
        escritor.newLine()
      } finally {
        escritor.close()
      }

    def generateDataSquadsTableTXT(data: List[Map[String, String]]): Unit = // en esta función crearemos los insert into o lo que sea necesario para poblar la tabla Genre
      val nombreTXT = "squads.txt"
      val insertFormat = s"INSERT INTO squads(squads_player_id, squads_tournament_id, squads_team_id, squads_shirt_number, squads_position_name) VALUES('%s', '%s', '%s', %d, '%s');"
      val value = data
        .map(x => (x("squads_player_id"),
          x("squads_tournament_id"),
          x("squads_team_id"),
          valoresDoBuedos(x("squads_shirt_number")).toInt,
          x("squads_position_name")))
        .sortBy(x => (x._1, x._2))
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US, x._1, x._2, x._3, x._4, x._5)))


    def generateDataPlayersTableTXT (data: List[Map[String, String]]): Unit =
      val nombreTXT = "players.txt"
      val insertFormat = s"INSERT INTO players(squadsPlayerId, squadsTournamentId, players_given_name, players_family_name, players_birth_date, players_female, players_goal_keepere, players_defender, players_midfielder, players_forward) VALUES('%s', '%s', '%s', '%s', '%s', %d, %d, %d, %d, %d);"
      val value = data
        .map(x => (x("squads_player_id"),
          x("squads_tournament_id"),
          comillasRaras(x("players_given_name")),
          comillasRaras(x("players_family_name")),
          comillasRaras(x("players_birth_date")),
          valoresDoBuedos(x("players_female")).toInt,
          valoresDoBuedos(x("players_goal_keeper")).toInt,
          valoresDoBuedos(x("players_defender")).toInt,
          valoresDoBuedos(x("players_midfielder")).toInt,
          valoresDoBuedos(x("players_forward")).toInt))
        .distinct
        .sortBy(x => (x._3, x._5))
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US, x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10)))

    def generateDataTourmentsTXT(data: List[Map[String, String]]) =
      val nombreTXT = "tournaments.txt"
      val insertFormat = s"INSERT INTO tournaments(tournaments_year, tournaments_tournament_name, tournaments_host_country, tournaments_winner, tournaments_count_teams) VALUES(%d, '%s', '%s', '%s', %d)"
      val valores = data
        .map(x => (valoresDoBuedos(x("tournaments_year").trim).toInt,
          x("tournaments_tournament_name").trim,
          x("tournaments_host_country").trim,
          x("tournaments_winner").trim,
          valoresDoBuedos(x("tournaments_count_teams").trim).toInt))
        .distinct
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US, x._1, x._2, x._3, x._4, x._5)))

    def generateDataStadiumTXT(data: List[Map[String, String]]) =
      0

    // -----------------------------------------------------------------------------------------------------------------
    // DATOS PARA INSERTAR DIRECTO

    def generateDataTourments(data: List[Map[String, String]]) =
      // ? -> indicar parámetros de sustitución.
      val valores = data
        .map(x => (valoresDoBuedos(x("tournaments_year").trim).toInt,
          x("tournaments_tournament_name").trim,
          x("tournaments_host_country").trim,
          x("tournaments_winner").trim,
          valoresDoBuedos(x("tournaments_count_teams").trim).toInt))
        .distinct
        .map(info => sql"INSERT INTO tournaments(tournaments_year, tournaments_tournament_name, tournaments_host_country, tournaments_winner, tournaments_count_teams) VALUES(${info._1}, ${info._2}, ${info._3}, ${info._4}, ${info._5})".update)

      valores

    /*def insertIntoTourmentData(info: (Int, String, String, String, Int)): IO[Int] =
      val sentencia = sql"""
             |USE mundial;
             |INSERT INTO tournaments(tournaments_year, tournaments_tournament_name, tournaments_host_country, tournaments_winner, tournaments_count_teams)
             |VALUES(${info._1}, ${info._2}, ${info._3}, ${info._4}, ${info._5});
             |"""
      sentencia.update.run.transact(xa)*/

    /*def runInsertsIntos() =
      generateDataTourments(contentFile2).debug.as(ExitCode.Success)*/



    // llamar a los métodos
    // generateDataSquadsTableTXT(contentFile)
    generateDataPlayersTableTXT(contentFile)

    // generateDataTourments(contentFile2).foreach(i => i.run.transact(xa).unsafeRunSync())
}
