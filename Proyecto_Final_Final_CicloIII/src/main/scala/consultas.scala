import doobie.{ConnectionIO, *}
import doobie.implicits.*
import cats.*
import cats.effect.*
import cats.implicits.*
import cats.effect.unsafe.implicits.global

import com.github.tototoshi.csv.*
import java.io.File


import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._

case class Matches(tournaments_year:Int, match_id:String, away_team_score:Int, home_team_score:Int)
case class Goals(homeTeamName:String, match_period:String)
case class Teams(homeTeamName:String, away_team_name:String)
case class Squads(player_id:String, tournament_id:String)
case class Tournaments(winner:String, tournaments_year: Int)
case class Players(player_id:String, family_name:String, given_name:String)

case class Varios(tournaments_winner:String, matches_away_team_score:Int, matches_home_team_score:Int, goals_match_period:String, teams_home_team_name:String, teams_away_team_name:String)
// case class Varios(id: Int, date: String, homeScore: Int, awayScore: Int, winner: String, stadium: String, city: String, country: String)
object consultas {
  @main
  def cons(): Unit =
    val xa = Transactor.fromDriverManager[IO](
      driver = "com.mysql.cj.jdbc.Driver",
      url = "jdbc:mysql://localhost: 3306/mundial",
      user = "root",
      password = "Veronica_18",
      logHandler = None
    )

  // Consulta 1:
    def find(year: Int): ConnectionIO[Option[Tournaments]] =
      sql"SELECT t.tournaments_winner, t.tournaments_year FROM tournaments t WHERE tournaments_year = $year"
        .query[Tournaments]
        .option

    val result: Option[Tournaments] = find(1999)
      .transact(xa)
      .unsafeRunSync()
    println(result.get)

    // Consulta 2:
    def listarSquads(): ConnectionIO[List[Squads]] =
      sql"SELECT s.squads_player_id, s.squads_tournament_id FROM squads s"
        .query[Squads]
        .to[List]

    val listaSquads: List[Squads] = listarSquads()
      .transact(xa)
      .unsafeRunSync()
    listaSquads.foreach(println)

    // Consulta 3:
    def listarPlayers(): ConnectionIO[List[Players]] =
      sql"""SELECT p.players_family_name, p.players_given_name, sq.squads_team_id ,sq.squads_position_name , squads_shirt_number
            FROM squads sq
            INNER JOIN players p ON sq.squads_player_id = p.squadsPlayerId
            """
        .stripMargin
        .query[Players]
        .to[List]

    val listaPlayers: List[Players] = listarPlayers()
      .transact(xa)
      .unsafeRunSync()
    listaPlayers.foreach(println)

    // Consulta 4:
    def varios(): ConnectionIO[List[Varios]] =
      sql"""
            SELECT t.tournaments_tournament_name, m.matches_away_team_score , m.matches_home_team_score, g.goals_match_period, te.away_team_name, te.home_team_name
            FROM tournaments t
            INNER JOIN matches m ON t.tournaments_year = m.tournamentsYear
            INNER JOIN goals g ON m.matches_match_id = g.matchesMatchId
            INNER JOIN teams te ON g.homeTeamName = te.home_team_name
            WHERE t.tournaments_tournament_name = "1930 FIFA Men's World Cup"
            """
        .stripMargin
        .query[Varios]
        .to[List]

    val listarVarios: List[Varios] = varios()
      .transact(xa)
      .unsafeRunSync()
    listarVarios.foreach(println)
}
