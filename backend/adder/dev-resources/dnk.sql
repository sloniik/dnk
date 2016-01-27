DROP  SCHEMA IF EXISTS dnk_test;
CREATE SCHEMA dnk_test;
USE dnk_test;
CREATE TABLE user_session
(
	`id_user_session` 	BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_user` 			BIGINT 			NOT NULL
	,`session_token` 	CHAR(64) 		NOT NULL
	,`dt_session_start` DATETIME 		NOT NULL
	,`dt_session_end` 	DATETIME  		NULL
	,PRIMARY KEY (id_user_session)
)
ENGINE=INNODB;
-- Create Table: game_access
CREATE TABLE game_access
(
	`id_game` 			BIGINT 			NOT NULL
	,`id_user` 			BIGINT 			NOT NULL
	,`is_active` 		BIT 			NOT NULL
	,`dt_granted` 		DATETIME 		NOT NULL
	,`dt_revoked` 		DATETIME  		NULL
	,PRIMARY KEY (id_game, id_user)
)
ENGINE=INNODB;
-- Create Table: room
CREATE TABLE room
(
	`id_room` 			BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_game` 			BIGINT 			NOT NULL
	,`id_user_master` 	BIGINT 			NOT NULL
	,`title` 			VARCHAR(250) 	NOT NULL
	,`is_private` 		BIT 			NOT NULL
	,`has_chat` 		BIT 			NOT NULL
	,`id_game_Variant` 	INT 			NOT NULL
	,`dt_start` 		DATETIME 		NOT NULL
	,`dt_end` 			DATETIME  		NULL
	,`is_active` 		BIT 			NOT NULL
	,PRIMARY KEY (id_room)
)
ENGINE=INNODB;
-- Create Table: game_variant
CREATE TABLE game_variant
(
	`id_game_variant` 	INT 			NOT NULL AUTO_INCREMENT
	,`id_game_type` 	INT 			NOT NULL
	,PRIMARY KEY (id_game_variant)
)
ENGINE=INNODB;
-- Create Table: room_users
CREATE TABLE room_users
(
	`id_room` 			BIGINT 			NOT NULL
	,`id_user` 			BIGINT 			NOT NULL
	,`dt_joined` 		DATETIME 		NOT NULL
    ,`dt_left`			DATETIME		NULL
	,PRIMARY KEY (id_room, id_user)
)
ENGINE=INNODB;
-- Create Table: game
CREATE TABLE game
(
	`id_game` 				BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_user_author` 		BIGINT 			NOT NULL
	,`id_game_variant` 		INT 			NOT NULL
	,`title` 				VARCHAR(250) 	NOT NULL
	,`description` 			VARCHAR(2500) 	NOT NULL
	,`dt_created` 			DATETIME 		NOT NULL
	,`game_solution` 		VARCHAR(2500) 	NOT NULL
	,`is_fork` 				BIT 			NOT NULL
	,`id_original` 			BIGINT  		NULL
	,`is_deleted` 			BIT 			NOT NULL
	,`expected_duration`	INT 			NULL
	,`preferable_user_num` 	INT  			NULL
	,`is_private` 			BIT 			NOT NULL
	,PRIMARY KEY (id_game)
)
ENGINE=INNODB;
-- Create Table: game_media
CREATE TABLE game_media
(
	`id_game_media` 	BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_game`			BIGINT 			NOT NULL
	,`file_path` 		VARCHAR(250) 	NOT NULL
	,PRIMARY KEY (id_game_media)
)
ENGINE=INNODB;
-- Create Table: game_type
CREATE TABLE game_type
(
	`id_game_type` 		INT 			NOT NULL AUTO_INCREMENT
	,`type_name` 		VARCHAR(250) 	NOT NULL
	,PRIMARY KEY (id_game_type)
)
ENGINE=INNODB;
-- Create Table: game_users
CREATE TABLE game_users
(
	`id_game` 			BIGINT 			NOT NULL
	,`id_user` 			BIGINT 			NOT NULL
	,`dt_joined` 		DATETIME 		NOT NULL
	,PRIMARY KEY (id_game, id_user)
)
ENGINE=INNODB;
-- Create Table: users
-- ------------------------------------------------------------------------------
CREATE TABLE users
(
	`id_user` 			BIGINT 			NOT NULL AUTO_INCREMENT
	,`user_name` 		VARCHAR(250) 	NOT NULL
	,`password_hash` 	CHAR(64) 		NOT NULL
	,`salt` 			VARCHAR(250) 	NOT NULL
	,`email` 			VARCHAR(250)  	NULL
	,`email-code`       CHAR(64) 		NULL
	,`User_Story` 		VARCHAR(2048) 	NULL
	,`Phone_Number` 	VARCHAR(250)  	NULL
	,`User_Token` 		CHAR(64) 		NULL
	,`id_user_Session` 	BIGINT  		NULL
	,`dt_Created` 		DATETIME 		NOT NULL
	,`is_Online` 		BIT 			NOT NULL
	,`is_Active`		BIT 			NOT NULL
    ,`is_banned`		BIT				NOT NULL
	,`is_Admin` 		BIT 			NOT NULL
	,PRIMARY KEY (id_user)
)
ENGINE=INNODB;
-- Create Table: room_access
-- ------------------------------------------------------------------------------
CREATE TABLE room_access
(
	`id_room` 			BIGINT 			NOT NULL
	,`id_user` 			BIGINT 			NOT NULL
	,`is_Active` 		BIT 			NOT NULL
	,`dt_Granted` 		DATETIME 		NOT NULL
	,`dt_Revoked` 		DATETIME  		NULL
	,PRIMARY KEY (id_room,id_user)
)
ENGINE=INNODB;
-- Create Table: chat_User
-- ------------------------------------------------------------------------------
CREATE TABLE chat_User
(
	`id_chat` 			BIGINT 			NOT NULL
	,`id_user` 			BIGINT 			NOT NULL
	,`dt_Joined` 		DATETIME 		NOT NULL
	,PRIMARY KEY (id_chat,id_user)
)
ENGINE=INNODB;
-- Create Table: Media_Type
-- ------------------------------------------------------------------------------
CREATE TABLE Media_Type
(
	`id_Media_Type` 	INT 			NOT NULL AUTO_INCREMENT
	,`Media_Type_Name`	VARCHAR(250) 	NOT NULL
    ,`is_actve` 		BIT 			NOT NULL
	,PRIMARY KEY (id_Media_Type)
)
ENGINE=INNODB;
-- Create Table: User_Media
-- ------------------------------------------------------------------------------
CREATE TABLE User_Media
(
	`id_user_Media` 	BIGINT  		NOT NULL AUTO_INCREMENT
	,`id_user` 			BIGINT 			NOT NULL
	,`id_Media_Type` 	INT 			NOT NULL
	,`File_Path` 		VARCHAR(1024) 	NOT NULL
    ,PRIMARY KEY (id_user_media)
)
ENGINE=INNODB;
-- Create Table: Log_Entry_Type
-- ------------------------------------------------------------------------------
CREATE TABLE Log_Entry_Type
(
	`id_Log_Entry_Type` INT 			NOT NULL 
	,`Type_Name` 		VARCHAR(250) 	NOT NULL 
    ,PRIMARY KEY (id_log_entry_type)
)
ENGINE=INNODB;
-- Create Table: User_Log
-- ------------------------------------------------------------------------------
CREATE TABLE User_Log
(
	`id_Log_Entry` 			BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_Log_Entry_Type` 	INT 			NOT NULL 
	,`id_user` 				BIGINT 			NOT NULL 
	,`Description` 			VARCHAR(2048) 	NOT NULL 
	,`dt_Created` 			DATETIME 		NOT NULL 
	,PRIMARY KEY (id_Log_Entry)
)
ENGINE=INNODB;
-- Create Table: room_Log
-- ------------------------------------------------------------------------------
CREATE TABLE room_Log
(
	`id_Log_Entry` 			BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_Log_Entry_Type` 	INT 			NOT NULL 
	,`id_room` 				BIGINT 			NOT NULL 
	,`Description` 			VARCHAR(2048) 	NOT NULL 
	,`dt_Created` 			DATETIME 		NOT NULL 
	,PRIMARY KEY (id_Log_Entry)
)
ENGINE=INNODB;
-- Create Table: game_Log
-- ------------------------------------------------------------------------------
CREATE TABLE game_Log
(
	`id_Log_Entry` 			BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_Log_Entry_Type` 	INT 			NOT NULL 
	,`id_game` 				BIGINT 			NOT NULL 
	,`Description` 			VARCHAR(2048) 	NOT NULL 
	,`dt_Created` 			DATETIME 		NOT NULL 
    ,PRIMARY KEY (id_Log_Entry)
)
ENGINE=INNODB;
-- Create Table: chat_Log
-- ------------------------------------------------------------------------------
CREATE TABLE chat_Log
(
	`id_Log_Entry` 			BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_Log_Entry_Type` 	INT 			NOT NULL 
	,`id_chat` 				BIGINT 			NOT NULL 
	,`Description` 			VARCHAR(2048)	NOT NULL 
	,`dt_Created` 			DATETIME 		NOT NULL 
	,PRIMARY KEY (id_Log_Entry)
)
ENGINE=INNODB;
-- Create Table: chat
-- ------------------------------------------------------------------------------
CREATE TABLE chat
(
	`id_chat` 				BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_room` 				BIGINT 			NOT NULL 
    ,`dt_created`			datetime		not null
    ,`dt_closed`			DATETIME 		NULL	
	,PRIMARY KEY (id_chat)
)
ENGINE=INNODB;
-- Create Table: chat_Message
-- ------------------------------------------------------------------------------
CREATE TABLE chat_Message
(
	`id_Message` 			BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_chat` 				BIGINT 			NOT NULL 
	,`id_user` 				BIGINT  		NULL 
	,`Message_Text` 		VARCHAR(1024) 	NULL 
	,`dt_Sent` 				DATETIME 		NOT NULL 
	,`is_Deleted` 			BIT 			NOT NULL 
	,PRIMARY KEY (id_Message)
)
ENGINE=INNODB;
-- Create Table: Question
-- ------------------------------------------------------------------------------
CREATE TABLE Question
(
	`id_Question` 			BIGINT 			NOT NULL AUTO_INCREMENT
	,`id_room` 				BIGINT 			NOT NULL 
	,`id_user` 				BIGINT 			NOT NULL 
	,`Message` 				VARCHAR(1024) 	NOT NULL 
    ,`Answer`  				VARCHAR(1024) 	NULL
	,`dt_Created` 			DATETIME 		NOT NULL 
    ,`dt_Answered`			DATETIME		NULL
	,`is_Deleted` 			BIT 			NOT NULL 
	,PRIMARY KEY (id_Question)
)
ENGINE=INNODB
;
-- Create Foreign Key: room_access.id_user -> users.id_user
ALTER TABLE room_access ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: room.id_game_Variant -> game_Variant.id_game_Variant
ALTER TABLE room ADD FOREIGN KEY (id_game_Variant) REFERENCES game_Variant(id_game_Variant);
-- Create Foreign Key: game_Variant.id_game_Type -> game_Type.id_game_Type
ALTER TABLE game_Variant ADD FOREIGN KEY (id_game_Type) REFERENCES game_Type(id_game_Type);
-- Create Foreign Key: User_Log.id_Log_Entry_Type -> Log_Entry_Type.id_Log_Entry_Type
ALTER TABLE User_Log ADD FOREIGN KEY (id_Log_Entry_Type) REFERENCES Log_Entry_Type(id_Log_Entry_Type);
-- Create Foreign Key: User_Media.id_Media_Type -> Media_Type.id_Media_Type
ALTER TABLE User_Media ADD FOREIGN KEY (id_Media_Type) REFERENCES Media_Type(id_Media_Type);
-- Create Foreign Key: User_Session.id_user -> users.id_user
ALTER TABLE User_Session ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: User_Log.id_user -> users.id_user
ALTER TABLE User_Log ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: room_Log.id_room -> room.id_room
ALTER TABLE room_Log ADD FOREIGN KEY (id_room) REFERENCES room(id_room);
-- Create Foreign Key: game.id_game_Variant -> game_Variant.id_game_Variant
ALTER TABLE game ADD FOREIGN KEY (id_game_Variant) REFERENCES game_Variant(id_game_Variant);
-- Create Foreign Key: room_access.id_room -> room.id_room
ALTER TABLE room_access ADD FOREIGN KEY (id_room) REFERENCES room(id_room);
-- Create Foreign Key: room.id_game -> game.id_game
ALTER TABLE room ADD FOREIGN KEY (id_game) REFERENCES game(id_game);
-- Create Foreign Key: game_Access.id_game -> game.id_game
ALTER TABLE game_Access ADD FOREIGN KEY (id_game) REFERENCES game(id_game);
-- Create Foreign Key: game.id_user_Author -> users.id_user
ALTER TABLE game ADD FOREIGN KEY (id_user_Author) REFERENCES users(id_user);
-- Create Foreign Key: room.id_user_Master -> users.id_user
ALTER TABLE room ADD FOREIGN KEY (id_user_Master) REFERENCES users(id_user);
-- Create Foreign Key: room_Log.id_log_entry_type -> log_entry_type.id_log_entry_type
ALTER TABLE room_Log ADD FOREIGN KEY (id_log_entry_type) REFERENCES log_entry_type(id_log_entry_type);
-- Create Foreign Key: game_log.id_log_entry_type -> log_entry_type.id_log_entry_type
ALTER TABLE game_log ADD FOREIGN KEY (id_log_entry_type) REFERENCES log_entry_type(id_log_entry_type);
-- Create Foreign Key: game_log.id_game -> game_access.id_game
ALTER TABLE game_log ADD FOREIGN KEY (id_game) REFERENCES game_access(id_game);
-- Create Foreign Key: game_access.id_user -> users.id_user
ALTER TABLE game_access ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: room_users.id_room -> room.id_room
ALTER TABLE room_users ADD FOREIGN KEY (id_room) REFERENCES room(id_room);
-- Create Foreign Key: room_users.id_user -> users.id_user
ALTER TABLE room_users ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: game_users.id_game -> game_access.id_game
ALTER TABLE game_users ADD FOREIGN KEY (id_game) REFERENCES game_access(id_game);
-- Create Foreign Key: game_users.id_user -> users.id_user
ALTER TABLE game_users ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: game_media.id_game -> game.id_game
ALTER TABLE game_media ADD FOREIGN KEY (id_game) REFERENCES game(id_game);
-- Create Foreign Key: user_media.id_user -> users.id_user
ALTER TABLE user_media ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: chat.id_room -> room.id_room
ALTER TABLE chat ADD FOREIGN KEY (id_room) REFERENCES room(id_room);
-- Create Foreign Key: chat_users.id_chat -> chat.id_chat
ALTER TABLE chat_User ADD FOREIGN KEY (id_chat) REFERENCES chat(id_chat);
-- Create Foreign Key: chat_users.id_user -> users.id_user
ALTER TABLE chat_User ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: chat_Message.id_chat -> chat.id_chat
ALTER TABLE chat_Message ADD FOREIGN KEY (id_chat) REFERENCES chat(id_chat);
-- Create Foreign Key: chat_Message.id_user -> users.id_user
ALTER TABLE chat_Message ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: chat.id_chat -> chat_Log.id_chat
ALTER TABLE chat_log ADD FOREIGN KEY (id_chat) REFERENCES chat(id_chat);
-- Create Foreign Key: Question.id_room -> room_Access.id_room
ALTER TABLE Question ADD FOREIGN KEY (id_room) REFERENCES room_Access(id_room);
-- Create Foreign Key: Question.id_user -> users.id_user
ALTER TABLE Question ADD FOREIGN KEY (id_user) REFERENCES users(id_user);
-- Create Foreign Key: users.id_user_session -> user_session.id_user_session
ALTER TABLE Users ADD FOREIGN KEY (id_user_session) REFERENCES user_session(id_user_session);