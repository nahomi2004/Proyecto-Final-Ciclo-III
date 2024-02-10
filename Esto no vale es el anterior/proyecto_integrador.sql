CREATE DATABASE mundial;
USE mundial;

CREATE TABLE squads(
  squads_player_id VARCHAR(15) NOT NULL,
  squads_tournament_id VARCHAR(15) NOT NULL,
  squads_team_id VARCHAR(15) NOT NULL,
  squads_shirt_number INT NOT NULL,
  squads_position_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (squads_player_id, squads_tournament_id),
  
  INDEX idx_squads_tournament_id (squads_tournament_id),
  INDEX idx_squads_player_id (squads_player_id)
);

CREATE TABLE players(
  squadsPlayerId VARCHAR(15) NOT NULL, 
  squadsTournamentId VARCHAR(15) NOT NULL, 
  
  players_given_name VARCHAR(30) NOT NULL,
  players_family_name VARCHAR(30) NOT NULL,
  players_birth_date VARCHAR(50) NOT NULL,
  players_female INT NOT NULL,
  players_goal_keeper INT NOT NULL,
  players_defender INT NOT NULL,
  players_midfielder INT NOT NULL,
  players_forward INT NOT NULL,
  PRIMARY KEY (players_given_name, players_family_name, squadsTournamentId, squadsPlayerId),
  
  FOREIGN KEY (squadsPlayerId, squadsTournamentId) REFERENCES squads(squads_player_id, squads_tournament_id),
  INDEX idx_squadsTournamentId_squadsPlayerId (squadsTournamentId, squadsPlayerId)
);
-- DROP TABLE players;

CREATE TABLE tournaments(
  tournaments_year INT NOT NULL, 
  tournaments_tournament_name VARCHAR(40) NOT NULL,
  tournaments_host_country VARCHAR(20) NOT NULL,
  tournaments_winner VARCHAR(20) NOT NULL,
  tournaments_count_teams INT NOT NULL,
  PRIMARY KEY (tournaments_year)
);

CREATE TABLE stadiums(
  stadiums_stadium_name VARCHAR(50) NOT NULL, 
  stadiums_city_name VARCHAR(20) NOT NULL,
  stadiums_country_name VARCHAR(20) NOT NULL,
  stadiums_stadium_capacity INT NOT NULL,
  PRIMARY KEY (stadiums_stadium_name, stadiums_city_name),
  INDEX idx_stadiums_stadium_name (stadiums_stadium_name)
);

CREATE TABLE matches(
  tournamentsYear INT NOT NULL,
  stadiumsStadiumName VARCHAR(50) NOT NULL,

  matches_tournament_id VARCHAR(15) NOT NULL, 
  matches_match_id VARCHAR(10) NOT NULL,
  matches_away_team_id VARCHAR(5) NOT NULL,
  matches_home_team_id VARCHAR(5) NOT NULL,
  matches_stadium_id VARCHAR(5) NOT NULL,
  matches_match_date VARCHAR(50) NOT NULL,
  matches_match_time VARCHAR(20) NOT NULL,
  matches_stage_name VARCHAR(30) NOT NULL,
  matches_home_team_score INT NOT NULL,
  matches_away_team_score INT NOT NULL,
  matches_extra_time INT NOT NULL,
  matches_penalty_shootout INT NOT NULL,
  matches_home_team_score_penalties INT NOT NULL,
  matches_away_team_score_penalties INT NOT NULL,
  matches_result VARCHAR(30) NOT NULL,
  PRIMARY KEY (matches_match_id),
  
  FOREIGN KEY (tournamentsYear) REFERENCES tournaments(tournaments_year),
  FOREIGN KEY (stadiumsStadiumName) REFERENCES stadiums(stadiums_stadium_name)
);

CREATE TABLE teams(
  squadsTournamentId VARCHAR(15) NOT NULL,

  home_team_name VARCHAR(30) NOT NULL, 
  away_team_name VARCHAR(30) NOT NULL,
  home_mens_team INT NOT NULL,
  home_womens_team INT NOT NULL,
  home_region_name VARCHAR(20) NOT NULL,
  away_mens_team INT NOT NULL,
  away_womens_team INT NOT NULL,
  away_region_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (home_team_name, away_team_name, squadsTournamentId),
  FOREIGN KEY (squadsTournamentId) REFERENCES squads(squads_tournament_id)
);

CREATE TABLE goals(
  -- playersGivenName VARCHAR(30) NOT NULL, 
  -- playersFamilyName VARCHAR(30) NOT NULL,  
  -- squadsPlayerIdG VARCHAR(15) NOT NULL,
  
  squadsTournamentIdG VARCHAR(15) NOT NULL, 
  matchesMatchId VARCHAR(10) NOT NULL,
  homeTeamName VARCHAR(30) NOT NULL, 
  awayTeamName VARCHAR(30) NOT NULL, 
  tournamentsYear INT NOT NULL, 

  goals_goal_id VARCHAR(15) NOT NULL,
  goals_team_id VARCHAR(15) NOT NULL,
  goals_player_id VARCHAR(15) NOT NULL,
  goals_player_team_id VARCHAR(15) NOT NULL,
  goals_minute_label VARCHAR(15) NOT NULL,
  goals_minute_regulation INT NOT NULL,
  goals_minute_stoppage INT NOT NULL,
  goals_match_period VARCHAR(50),
  goals_own_goal INT NOT NULL,
  goals_penalty INT NOT NULL,
  PRIMARY KEY (goals_goal_id, matchesMatchId),
  FOREIGN KEY (squadsTournamentIdG) REFERENCES players(squadsTournamentId),
  FOREIGN KEY (matchesMatchId) REFERENCES matches(matches_match_id),
  FOREIGN KEY (homeTeamName, awayTeamName) REFERENCES teams(home_team_name, away_team_name),
  FOREIGN KEY (tournamentsYear) REFERENCES tournaments(tournaments_year)
);

-- DROP TABLE goals;

