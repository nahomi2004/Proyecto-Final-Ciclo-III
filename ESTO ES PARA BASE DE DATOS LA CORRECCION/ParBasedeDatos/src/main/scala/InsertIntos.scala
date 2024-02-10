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

object InsertIntos {
  @main
  def ejecutable(): Unit = {
    val ruta = "D:/Users/LENOVO/Documents/ESTA CARPETA ESTA ORGANIZADA/ArchivoPIntegrador/dsAlineacionesXTorneo.csv"
    val reader = CSVReader.open(new File(ruta))
    val contentFile: List[Map[String, String]] =
      reader.allWithHeaders()

    val ruta2 = "D:/Users/LENOVO/Documents/ESTA CARPETA ESTA ORGANIZADA/ArchivoPIntegrador/dsPartidosYGoles.csv"
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

    def escribirDatosTXT(nombreTXT: String, archivo: String): Unit =
      val rutaTXT = "D:/Users/LENOVO/Documents/ESTA CARPETA ESTA ORGANIZADA/"
      val rutaFinal = rutaTXT + nombreTXT

      val escritor = new BufferedWriter(new FileWriter(rutaFinal, true))
      try {
        escritor.write(archivo)
        escritor.newLine()
      } finally {
        escritor.close()
      }

    def escribirDatosTXT2(archivo: String): Unit =
      val rutaTXT = "D:/Users/LENOVO/Documents/ESTA CARPETA ESTA ORGANIZADA/scriptInsertIntos.sql"

      val escritor = new BufferedWriter(new FileWriter(rutaTXT, true))
      try {
        escritor.write(archivo)
        escritor.newLine()
      } finally {
        escritor.close()
      }

    def generarListaPaises() = {
      val listaHomeTeam = contentFile2
        .map(x => (comillasRaras(x("matches_home_team_id").trim), comillasRaras(x("home_team_name").trim), comillasRaras(x("home_region_name").trim)))
        .distinct

      val listaAwayTeam = contentFile2
        .map(x => (comillasRaras(x("matches_away_team_id").trim), comillasRaras(x("away_team_name").trim), comillasRaras(x("away_region_name").trim)))
        .distinct

      val listaTotal = (listaHomeTeam ++ listaAwayTeam).distinct.sorted

      listaTotal
    }

    def buscarPais(valorAComparar: String) = {
      val paises = generarListaPaises().find(tupla => valorAComparar == tupla._2).map(_._1)
      paises.getOrElse("NA")
    }

    def generarDataCountries(data: List[Map[String, String]]): Unit = {
      val nombreTXT = "countries.sql"
      val insertFormat = s"INSERT INTO countries(id_country, country_name, region_name) VALUES('%s', '%s', '%s');"

      val listaTotal = generarListaPaises()

      val insertar = listaTotal
        .map(x => escribirDatosTXT2(insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3)))
      // listaTotal.foreach(println)
    }

    def generarDataPlayers(data: List[Map[String, String]]): Unit = {
      val nombreTXT = "players.sql"
      val insertFormat = s"INSERT INTO players(player_id, players_given_name, players_family_name, players_birth_date, " +
        s"players_female, players_goal_keeper, players_defender, players_midfielder, players_forward) VALUES('%s', '%s', " +
        s"'%s', '%s', %d, %d, %d, %d, %d);"

      val insertar = data
        .map(x => (comillasRaras(x("squads_player_id").trim), comillasRaras(x("players_given_name").trim),
          comillasRaras(x("players_family_name").trim), comillasRaras(x("players_birth_date").trim),
          valoresDoBuedos(x("players_female").trim).toInt, valoresDoBuedos(x("players_goal_keeper").trim).toInt,
          valoresDoBuedos(x("players_defender").trim).toInt, valoresDoBuedos(x("players_midfielder").trim).toInt,
          valoresDoBuedos(x("players_forward").trim).toInt))
        .distinct
        .map(x => escribirDatosTXT2(insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9)))
    }

    def generarDataTeams(data: List[Map[String, String]]): Unit = {
      val nombreTXT = "teams.sql"
      val insertFormat = s"INSERT INTO teams(home_team_id, away_team_id, home_mens_team, home_womens_team, " +
        s"away_mens_team, away_womens_team) VALUES('%s', '%s', %d, %d, %d, %d);"



      val insertar = data
        .map(x => (comillasRaras(x("matches_home_team_id")), comillasRaras(x("matches_away_team_id")),
          valoresDoBuedos(x("home_mens_team")).toInt, valoresDoBuedos(x("home_womens_team")).toInt,
          valoresDoBuedos(x("away_mens_team")).toInt, valoresDoBuedos(x("away_womens_team")).toInt))
        .distinct
        .map(x => escribirDatosTXT2(insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6)))
    }

    def generarDataTournaments(data: List[Map[String, String]]): Unit = {

      val nombreTXT = "tournaments.sql"
      val insertFormat = s"INSERT INTO tournaments(tournament_id, tournaments_year, tournaments_tournament_name, " +
        s"tournaments_host_country, tournaments_winner, tournaments_count_teams) VALUES('%s', %d, '%s', '%s', '%s', %d);"

      val insertar = data
        .map(x => (comillasRaras(x("matches_tournament_id").trim), valoresDoBuedos(x("tournaments_year").trim).toInt,
          comillasRaras(x("tournaments_tournament_name").trim), buscarPais(comillasRaras(x("tournaments_host_country").trim)).toString,
          buscarPais(comillasRaras(x("tournaments_winner").trim)).toString,
          valoresDoBuedos(x("tournaments_count_teams").trim).toInt))
        .distinct
        .map(x => escribirDatosTXT2(insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6)))
    }

    def generarDataSquads(data: List[Map[String, String]]): Unit = {

      val nombreTXT = "squads.sql"
      val insertFormat = s"INSERT INTO squads(squads_player_id, squads_team_id, squads_tournament_id, " +
        s"squads_shirt_number, squads_position_name) VALUES('%s', '%s', '%s', %d, '%s');"

      val insertar = data
        .map(x => (comillasRaras(x("squads_player_id").trim), comillasRaras(x("squads_team_id").trim),
          comillasRaras(x("squads_tournament_id").trim), valoresDoBuedos(x("squads_shirt_number").trim).toInt,
          comillasRaras(x("squads_position_name").trim)
          ))
        .distinct
        .map(x => escribirDatosTXT2(insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5)))
    }

    def generarDataStadiums(data: List[Map[String, String]]): Unit = {
      val nombreTXT = "stadiums.sql"
      val insertFormat = s"INSERT INTO stadiums(stadium_id, stadiums_stadium_name, stadiums_city_name, " +
        s"stadiums_country_id, stadiums_stadium_capacity) VALUES('%s', '%s', '%s', '%s', %d);"

      val insertar = data
        .map(x => (comillasRaras(x("matches_stadium_id").trim), comillasRaras(x("stadiums_stadium_name").trim),
          comillasRaras(x("stadiums_city_name").trim), buscarPais(comillasRaras(x("stadiums_country_name").trim)),
          valoresDoBuedos(x("stadiums_stadium_capacity").trim).toInt
        ))
        .distinct
        .map(x => escribirDatosTXT2(insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5)))
    }

    def generarDataMatches(data: List[Map[String, String]]): Unit = {
      val nombreTXT = "matches.sql"
      val insertFormat = s"INSERT INTO matches(matches_match_id, matches_tournament_id, matches_away_team_id, " +
        s"matches_home_team_id, matches_stadium_id, matches_match_date, matches_match_time, matches_stage_name, " +
        s"matches_home_team_score, matches_away_team_score, matches_extra_time, matches_penalty_shootout, " +
        s"matches_home_team_score_penalties, matches_away_team_score_penalties, matches_result) " +
        s"VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d, %d, %d, %d, %d, %d, '%s');"

      val insertar = data
        .map(x => (comillasRaras(x("matches_match_id").trim), comillasRaras(x("matches_tournament_id").trim),
          comillasRaras(x("matches_away_team_id").trim), comillasRaras(x("matches_home_team_id").trim),
          comillasRaras(x("matches_stadium_id").trim), comillasRaras(x("matches_match_date").trim),
          comillasRaras(x("matches_match_time").trim), comillasRaras(x("matches_stage_name").trim),
          valoresDoBuedos(x("matches_home_team_score").trim).toInt, valoresDoBuedos(x("matches_away_team_score").trim).toInt,
          valoresDoBuedos(x("matches_extra_time").trim).toInt,
          valoresDoBuedos(x("matches_penalty_shootout").trim).toInt,
          valoresDoBuedos(x("matches_home_team_score_penalties").trim).toInt,
          valoresDoBuedos(x("matches_away_team_score_penalties").trim).toInt,
          comillasRaras(x("matches_result").trim)
        ))
        .distinct
        .map(x => escribirDatosTXT2(insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11, x._12, x._13, x._14, x._15)))
    }

    def generarDataGoals(data: List[Map[String, String]]): Unit = {
      val nombreTXT = "goals.sql"
      val insertFormat = s"INSERT INTO goals(goals_team_id, goals_tournament_id, goals_player_id, " +
        s"goals_player_team_id, goals_goal_id, goals_minute_label, goals_minute_regulation, goals_minute_stoppage, " +
        s"goals_match_period, goals_own_goal, goals_penalty) " +
        s"VALUES('%s', '%s', '%s', '%s', '%s', '%s', %d, %d, '%s', %d, %d);"

      val insertar = data
        .filterNot(x => comillasRaras(x("goals_team_id").trim).equals("NA"))
        .map(x => (comillasRaras(x("goals_team_id").trim), comillasRaras(x("matches_tournament_id").trim),
          comillasRaras(x("goals_player_id").trim), comillasRaras(x("goals_player_team_id").trim),
          comillasRaras(x("goals_goal_id").trim), comillasRaras(x("goals_minute_label").trim),
          valoresDoBuedos(x("goals_minute_regulation").trim).toInt, valoresDoBuedos(x("goals_minute_stoppage").trim).toInt,
          comillasRaras(x("goals_match_period").trim),
          valoresDoBuedos(x("goals_own_goal").trim).toInt,
          valoresDoBuedos(x("goals_penalty").trim).toInt
        ))
        .distinct
        .map(x => escribirDatosTXT2(insertFormat.formatLocal(java.util.Locale.US,
          x._1, x._2, x._3, x._4, x._5, x._6, x._7, x._8, x._9, x._10, x._11)))
    }

    generarDataCountries(contentFile2)
    generarDataPlayers(contentFile)
    generarDataTeams(contentFile2)
    generarDataTournaments(contentFile2)
    generarDataSquads(contentFile)
    generarDataStadiums(contentFile2)
    generarDataMatches(contentFile2)
    generarDataGoals(contentFile2)
  }
}
