CREATE DATABASE mundialUNAPRUEBA;
USE mundialUNAPRUEBA;
 
CREATE TABLE countries(
	id_country VARCHAR(5) NOT NULL,
    country_name VARCHAR(30) NOT NULL,
    region_name VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_country)
);
 
CREATE TABLE players(
  player_id VARCHAR(15) NOT NULL, 
 
  players_given_name VARCHAR(30) NOT NULL,
  players_family_name VARCHAR(30) NOT NULL,
  players_birth_date VARCHAR(50) NOT NULL,
  players_female INT NOT NULL,
  players_goal_keeper INT NOT NULL,
  players_defender INT NOT NULL,
  players_midfielder INT NOT NULL,
  players_forward INT NOT NULL,
  PRIMARY KEY (player_id)
);
 
CREATE TABLE teams(
  team_id INT NOT NULL AUTO_INCREMENT,
 
  home_team_id VARCHAR(30) NOT NULL, 
  away_team_id VARCHAR(30) NOT NULL,
 
  home_mens_team INT NOT NULL,
  home_womens_team INT NOT NULL,
 
  away_mens_team INT NOT NULL,
  away_womens_team INT NOT NULL,
 
  PRIMARY KEY  (team_id),
  FOREIGN KEY (home_team_id) REFERENCES countries(id_country),
  FOREIGN KEY (away_team_id) REFERENCES countries(id_country)
);
 
CREATE TABLE tournaments(
  tournament_id VARCHAR(15) NOT NULL,
 
  tournaments_year INT NOT NULL,    
  tournaments_tournament_name VARCHAR(40) NOT NULL,
  tournaments_host_country VARCHAR(20) NOT NULL,
  tournaments_winner VARCHAR(20) NOT NULL,
  tournaments_count_teams INT NOT NULL,
  PRIMARY KEY (tournament_id),
  FOREIGN KEY (tournaments_host_country) REFERENCES countries(id_country),
  FOREIGN KEY (tournaments_winner) REFERENCES countries(id_country)
);
 
CREATE TABLE squads(
  squads_player_id VARCHAR(15) NOT NULL,  
  squads_team_id VARCHAR(5) NOT NULL,  
  squads_tournament_id VARCHAR(15) NOT NULL,  
 
  squads_shirt_number INT NOT NULL,
  squads_position_name VARCHAR(20) NOT NULL,
  PRIMARY KEY (squads_player_id, squads_tournament_id),
  FOREIGN KEY (squads_player_id) REFERENCES players(player_id),
  FOREIGN KEY (squads_team_id) REFERENCES countries(id_country),
  FOREIGN KEY (squads_tournament_id) REFERENCES tournaments(tournament_id)
);
 
 
CREATE TABLE stadiums(
  stadium_id VARCHAR(5) NOT NULL,
 
  stadiums_stadium_name VARCHAR(50) NOT NULL,   
  stadiums_city_name VARCHAR(20) NOT NULL,
  stadiums_country_id VARCHAR(20) NOT NULL,
  stadiums_stadium_capacity INT NOT NULL,
  PRIMARY KEY (stadium_id),
  FOREIGN KEY (stadiums_country_id) REFERENCES countries(id_country)
);
 
CREATE TABLE matches(
  matches_match_id VARCHAR(10) NOT NULL,
 
  matches_tournament_id VARCHAR(15) NOT NULL,  
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
  FOREIGN KEY (matches_tournament_id) REFERENCES tournaments(tournament_id),
  FOREIGN KEY (matches_away_team_id) REFERENCES countries(id_country),
  FOREIGN KEY (matches_home_team_id) REFERENCES countries(id_country),
  FOREIGN KEY (matches_stadium_id) REFERENCES stadiums(stadium_id)
);
 
 
CREATE TABLE goals(
  goals_team_id VARCHAR(15) NOT NULL,
  goals_tournament_id VARCHAR(15) NOT NULL,
  goals_player_id VARCHAR(15) NOT NULL,
  goals_player_team_id VARCHAR(15) NOT NULL,
 
  goals_goal_id VARCHAR(15) NOT NULL,  
 
  goals_minute_label VARCHAR(15) NOT NULL,
  goals_minute_regulation INT NOT NULL,
  goals_minute_stoppage INT NOT NULL,
  goals_match_period VARCHAR(50),
  goals_own_goal INT NOT NULL,
  goals_penalty INT NOT NULL,
 
  PRIMARY KEY (goals_goal_id, goals_tournament_id),
 
  FOREIGN KEY (goals_team_id) REFERENCES countries(id_country),
  FOREIGN KEY (goals_player_id) REFERENCES players(player_id),
  FOREIGN KEY (goals_player_team_id) REFERENCES countries(id_country),
  FOREIGN KEY (goals_tournament_id) REFERENCES tournaments(tournament_id)
);