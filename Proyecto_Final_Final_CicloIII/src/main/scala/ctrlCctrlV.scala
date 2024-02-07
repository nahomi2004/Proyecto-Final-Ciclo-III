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
      val insertFormat = s"INSERT INTO squads(squads_player_id, squads_tournament_id, squads_team_id, " +
          s"squads_shirt_number, squads_position_name) VALUES('%s', '%s', '%s', %d, '%s');"
      val value = data
        .map(x => (x("squads_player_id").trim,
          x("squads_tournament_id").trim,
          x("squads_team_id").trim,
          valoresDoBuedos(x("squads_shirt_number").trim).toInt,
          x("squads_position_name").trim))
        .sortBy(x => (x._1, x._2))
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5)))


    def generateDataPlayersTableTXT (data: List[Map[String, String]]): Unit =
      val nombreTXT = "players.txt"
      val insertFormat = s"INSERT INTO players(squadsPlayerId, squadsTournamentId, players_given_name, " +
        s"players_family_name, players_birth_date, players_female, players_goal_keeper, players_defender, " +
        s"players_midfielder, players_forward) VALUES('%s', '%s', '%s', '%s', '%s', %d, %d, %d, %d, %d);"
      val value = data
        .map(x => (x("squads_player_id").trim,
          x("squads_tournament_id").trim,
          comillasRaras(x("players_given_name").trim),
          comillasRaras(x("players_family_name").trim),
          comillasRaras(x("players_birth_date").trim),
          valoresDoBuedos(x("players_female").trim).toInt,
          valoresDoBuedos(x("players_goal_keeper").trim).toInt,
          valoresDoBuedos(x("players_defender").trim).toInt,
          valoresDoBuedos(x("players_midfielder").trim).toInt,
          valoresDoBuedos(x("players_forward").trim).toInt))
        .distinct
        .sortBy(x => (x._3, x._5))
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10)))

    def generateDataTournamentsTableTXT(data: List[Map[String, String]]) =
      val nombreTXT = "tournaments.txt"
      val insertFormat = s"INSERT INTO tournaments(tournaments_year, tournaments_tournament_name, " +
        s"tournaments_host_country, tournaments_winner, tournaments_count_teams) VALUES(%d, '%s', '%s', '%s', %d);"
      val valores = data
        .map(x => (valoresDoBuedos(x("tournaments_year").trim).toInt,
          comillasRaras(x("tournaments_tournament_name").trim),
          comillasRaras(x("tournaments_host_country").trim),
          comillasRaras(x("tournaments_winner").trim),
          valoresDoBuedos(x("tournaments_count_teams").trim).toInt))
        .distinct
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5)))

    def generateDataStadiumTableTXT(data: List[Map[String, String]]) =
      val nombreTXT = "stadiums.txt"
      val insertFormat = s"INSERT INTO stadiums(stadiums_stadium_name, stadiums_city_name, stadiums_country_name, " +
        s"stadiums_stadium_capacity) VALUES('%s', '%s', '%s', %d);"
      val valores = data
        .map(x => (comillasRaras(x("stadiums_stadium_name").trim),
          comillasRaras(x("stadiums_city_name").trim),
          comillasRaras(x("stadiums_country_name").trim),
          valoresDoBuedos(x("stadiums_stadium_capacity")).toInt
        ))
        .distinct
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4)))

    def generateDataMatchesTableTXT(data: List[Map[String, String]]) =
      val nombreTXT = "matches.txt"
      val insertFormat = s"INSERT INTO matches(tournamentsYear, stadiumsStadiumName, matches_tournament_id, " +
        s"matches_match_id, matches_away_team_id, matches_home_team_id, matches_stadium_id, matches_match_date, " +
        s"matches_match_time, matches_stage_name, matches_home_team_score, matches_away_team_score, " +
        s"matches_extra_time, matches_penalty_shootout, matches_home_team_score_penalties, " +
        s"matches_away_team_score_penalties, matches_result) VALUES(%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', " +
        s"'%s', '%s', %d, %d, %d, %d, %d, %d, '%s');"

      val valores = data
        .map(x => (valoresDoBuedos(x("tournaments_year").trim).toInt,
          comillasRaras(x("stadiums_stadium_name".trim)),
          comillasRaras(x("matches_tournament_id").trim),
          comillasRaras(x("matches_match_id").trim),
          comillasRaras(x("matches_away_team_id").trim),
          comillasRaras(x("matches_home_team_id").trim),
          comillasRaras(x("matches_stadium_id").trim),
          comillasRaras(x("matches_match_date").trim),
          comillasRaras(x("matches_match_time").trim),
          comillasRaras(x("matches_stage_name").trim),
          valoresDoBuedos(x("matches_home_team_score").trim).toInt,
          valoresDoBuedos(x("matches_away_team_score").trim).toInt,
          valoresDoBuedos(x("matches_extra_time").trim).toInt,
          valoresDoBuedos(x("matches_penalty_shootout").trim).toInt,
          valoresDoBuedos(x("matches_home_team_score_penalties").trim).toInt,
          valoresDoBuedos(x("matches_away_team_score_penalties").trim).toInt,
          comillasRaras(x("matches_result").trim)
        ))
        .distinct
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14, x._15, x._16, x._17)))

    def obtenerlistadeJugadores (data: List[Map[String, String]]) =
      val listaJugadoresPorEquipo = data
        .map(x => (x("squads_team_id"), x("squads_player_id")))
        .groupBy(x => identity(x._1))
        .map(x => Map(x._1 -> x._2.map(_._2)))

      listaJugadoresPorEquipo


    def generateDataTeamsTableTXT(data: List[Map[String, String]]): Unit =
      val nombreTXT = "teams.txt"

      val insertFormat = s"INSERT INTO teams(squadsTournamentId, home_team_name, " +
        s"away_team_name, home_mens_team, home_womens_team, home_region_name, away_mens_team, " +
        s"away_womens_team, away_region_name) VALUES('%s', '%s', '%s', %d, %d, '%s', %d, %d, '%s');"
      val value = data
        .map(x => (x("matches_tournament_id").trim,
          comillasRaras(x("home_team_name").trim),
          comillasRaras(x("away_team_name").trim),
          valoresDoBuedos(x("home_mens_team").trim).toInt,
          valoresDoBuedos(x("home_womens_team").trim).toInt,
          comillasRaras(x("home_region_name").trim),
          valoresDoBuedos(x("away_mens_team").trim).toInt,
          valoresDoBuedos(x("away_womens_team").trim).toInt,
          comillasRaras(x("away_region_name").trim)))
        .distinct
        .sortBy(x => (x._3, x._5))
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9)))

    def generateDataGoalsTableTXT(data: List[Map[String, String]]): Unit =
      val nombreTXT = "goals.txt"

      val insertFormat = s"INSERT INTO goals(squadsTournamentIdG, matchesMatchId, " +
        s"homeTeamName, awayTeamName, tournamentsYear, goals_goal_id, goals_team_id, " +
        s"goals_player_id, goals_player_team_id, goals_minute_label, goals_minute_regulation, " +
        s"goals_minute_stoppage, goals_match_period, goals_own_goal, goals_penalty) VALUES('%s', '%s', '%s', '%s', " +
        s"%d, '%s', '%s', '%s', '%s', '%s', %d, %d, '%s', %d, %d);"
      val value = data
        .map(x => (x("matches_tournament_id").trim,
          x("matches_match_id").trim,
          comillasRaras(x("home_team_name").trim),
          comillasRaras(x("away_team_name").trim),
          valoresDoBuedos(x("tournaments_year").trim).toInt,
          comillasRaras(x("goals_goal_id").trim),
          comillasRaras(x("goals_team_id").trim),
          comillasRaras(x("goals_player_id").trim),
          comillasRaras(x("goals_player_team_id").trim),
          comillasRaras(x("goals_minute_label").trim),
          valoresDoBuedos(x("goals_minute_regulation").trim).toInt,
          valoresDoBuedos(x("goals_minute_stoppage").trim).toInt,
          comillasRaras(x("goals_match_period").trim),
          valoresDoBuedos(x("goals_own_goal").trim).toInt,
          valoresDoBuedos(x("goals_penalty").trim).toInt
        ))
        .distinct
        .sortBy(x => (x._3, x._5))
        .map(x => escribirDatosTXT(nombreTXT, insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14, x._15)))

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
        .map(info =>
          sql"""INSERT INTO tournaments(tournaments_year, tournaments_tournament_name,
               tournaments_host_country, tournaments_winner, tournaments_count_teams)
               VALUES(${info._1}, ${info._2}, ${info._3}, ${info._4}, ${info._5})""".stripMargin.update)

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


    // obtenerlistadeJugadores(contentFile)


    // llamar a los métodos
    // -----------------------------------------------------------------------------------------------------------------
    // MÉTODOS QUE CREAN EL TXT

    generateDataSquadsTableTXT(contentFile)
    generateDataPlayersTableTXT(contentFile)
    generateDataTournamentsTableTXT(contentFile2)
    generateDataStadiumTableTXT(contentFile2)
    generateDataMatchesTableTXT(contentFile2)
    generateDataTeamsTableTXT(contentFile2)
    generateDataGoalsTableTXT(contentFile2)

    // -----------------------------------------------------------------------------------------------------------------
    // MÉTODOS QUE SE CONECTAN DIRECTO CON LA BASE
    // generateDataTourments(contentFile2).foreach(i => i.run.transact(xa).unsafeRunSync())
}
