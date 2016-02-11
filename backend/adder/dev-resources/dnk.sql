drop  schema if exists dnk_test;
create schema dnk_test;
use dnk_test;

-- create table: room
create table room
(
	`id_room` 			bigint 			not null auto_increment
	,`id_game` 			bigint 			not null
	,`id_user_master` 	bigint 			not null
	,`title` 			varchar(250) 	not null
	,`is_private` 		bit 			not null
	,`has_chat` 		bit 			not null
	,`id_room_mode` 	int 			not null
	,`dt_start` 		datetime 		not null
	,`dt_end` 			datetime  		null
	,`is_active` 		bit 			not null
	,primary key (id_room)
)
engine=innodb;

-- create table: game
create table game
(
	`id_game` 				bigint 			not null auto_increment
	,`id_user_author` 		bigint 			not null
	,`id_game_type`         int             not null
	,`title` 				varchar(250) 	not null
	,`description` 			varchar(2500) 	not null
	,`dt_created` 			datetime 		not null
	,`game_solution` 		varchar(2500) 	not null
	,`is_fork` 				bit 			not null
	,`id_original` 			bigint  		null
	,`is_deleted` 			bit 			not null
	,`expected_duration`	int 			null
	,`preferable_user_num` 	int  			null
	,`is_private` 			bit 			not null
	,primary key (id_game)
)
engine=innodb;

-- create table: users
create table users
(
	`id_user` 			bigint 			not null auto_increment
	,`user_name` 		varchar(250) 	not null
	,`password_hash` 	char(64) 		not null
	,`salt` 			varchar(250) 	not null
	,`email` 			varchar(250)  	null
	,`email-code`       char(64) 		null
	,`user_story` 		varchar(2048) 	null
	,`phone_number` 	varchar(250)  	null
	,`user_token` 		char(64) 		null
	,`id_user_session` 	bigint  		null
	,`dt_created` 		datetime 		not null
	,`is_online` 		bit 			not null
	,`is_active`		bit 			not null
    ,`is_banned`		bit				not null
	,`is_admin` 		bit 			not null
	,primary key (id_user)
)
engine=innodb;

-- create table: chat
create table chat
(
	`id_chat` 				bigint 			not null auto_increment
	,`id_room` 				bigint 			not null
    ,`dt_created`			datetime		not null
    ,`dt_closed`			datetime 		null
	,primary key (id_chat)
)
engine=innodb;

-- create table: chat_message
create table chat_message
(
	`id_message` 			bigint 			not null auto_increment
	,`id_chat` 				bigint 			not null
	,`id_user` 				bigint  		null
	,`message_text` 		varchar(1024) 	null
	,`dt_sent` 				datetime 		not null
	,`is_deleted` 			bit 			not null
	,primary key (id_message)
)
engine=innodb;

-- create table: question
create table question
(
	`id_question` 			bigint 			not null auto_increment
	,`id_room` 				bigint 			not null
	,`id_user` 				bigint 			not null
	,`message_text`			varchar(1024) 	not null
    ,`answer`  				varchar(1024) 	null
	,`dt_created` 			datetime 		not null
    ,`dt_answered`			datetime		null
	,`is_deleted` 			bit 			not null
	,primary key (id_question)
)
engine=innodb;

-- create table: game_users
create table game_users
(
	`id_game` 			bigint 			not null
	,`id_room`          bigint          not null
	,`id_user` 			bigint 			not null
	,`dt_joined` 		datetime 		not null
	,`dt_left`          datetime        null
	,primary key (id_game, id_user)
)
engine=innodb;

-- create table: user_session
create table user_session
(
	`id_user_session` 	bigint 			not null auto_increment
	,`id_user` 			bigint 			not null
	,`session_token` 	char(64) 		not null
	,`dt_session_start` datetime 		not null
	,`dt_session_end` 	datetime  		null
	,primary key (id_user_session)
)
engine=innodb;

-- create table: game_access
create table game_access
(
	`id_game` 			bigint 			not null
	,`id_user` 			bigint 			not null
	,`is_active` 		bit 			not null
	,`dt_granted` 		datetime 		not null
	,`dt_revoked` 		datetime  		null
	,primary key (id_game, id_user)
)
engine=innodb;

-- create table: game_mode
create table room_mode
(
	`id_room_mode` 	    int 			not null auto_increment
	,`mode_name`        varchar(250)    not null
	,primary key (id_room_mode)
)
engine=innodb;

-- create table: room_access
create table room_access
(
	`id_room` 			bigint 			not null
	,`id_user` 			bigint 			not null
	,`is_active` 		bit 			not null
	,`dt_granted` 		datetime 		not null
	,`dt_revoked` 		datetime  		null
	,primary key (id_room,id_user)
)
engine=innodb;

-- create table: game_type
create table game_type
(
	`id_game_type` 		int 			not null auto_increment
	,`type_name` 		varchar(250) 	not null
	,primary key (id_game_type)
)
engine=innodb;

-- create table: game_media
create table game_media
(
	`id_game_media` 	bigint 			not null auto_increment
	,`id_game`			bigint 			not null
	,`file_path` 		varchar(250) 	not null
	,primary key (id_game_media)
)
engine=innodb;

-- create table: media_type
create table media_type
(
	`id_media_type` 	int 			not null auto_increment
	,`media_type_name`	varchar(250) 	not null
    ,`is_active` 		bit 			not null
	,primary key (id_media_type)
)
engine=innodb;

-- create table: chat_user
create table chat_user
(
	`id_chat` 			bigint 			not null
	,`id_user` 			bigint 			not null
	,`dt_joined` 		datetime 		not null
	,primary key (id_chat,id_user)
)
engine=innodb;

-- create table: user_media
create table user_media
(
	`id_user_media` 	bigint  		not null auto_increment
	,`id_user` 			bigint 			not null
	,`id_media_type` 	int 			not null
	,`file_path` 		varchar(1024) 	not null
    ,primary key (id_user_media)
)
engine=innodb;

-- create table: log_entry_type
create table log_entry_type
(
	`id_log_entry_type` int 			not null
	,`type_name` 		varchar(250) 	not null
    ,primary key (id_log_entry_type)
)
engine=innodb;

-- create table: user_log
create table user_log
(
	`id_log_entry` 			bigint 			not null auto_increment
	,`id_log_entry_type` 	int 			not null
	,`id_user` 				bigint 			not null
	,`description` 			varchar(2048) 	not null
	,`dt_created` 			datetime 		not null
	,primary key (id_log_entry)
)
engine=innodb;

-- create table: room_log
create table room_log
(
	`id_log_entry` 			bigint 			not null auto_increment
	,`id_log_entry_type` 	int 			not null
	,`id_room` 				bigint 			not null
	,`description` 			varchar(2048) 	not null
	,`dt_created` 			datetime 		not null
	,primary key (id_log_entry)
)
engine=innodb;

-- create table: game_log
create table game_log
(
	`id_log_entry` 			bigint 			not null auto_increment
	,`id_log_entry_type` 	int 			not null
	,`id_game` 				bigint 			not null
	,`description` 			varchar(2048) 	not null
	,`dt_created` 			datetime 		not null
    ,primary key (id_log_entry)
)
engine=innodb;

-- create table: chat_log
create table chat_log
(
	`id_log_entry` 			bigint 			not null auto_increment
	,`id_log_entry_type` 	int 			not null
	,`id_chat` 				bigint 			not null
	,`description` 			varchar(2048)	not null
	,`dt_created` 			datetime 		not null
	,primary key (id_log_entry)
)
engine=innodb;

-- create foreign key: room_access.id_user -> users.id_user
alter table room_access add foreign key (id_user) references users(id_user);
-- create foreign key: room.id_room_mode -> room_mode.id_room_mode
alter table room add foreign key (id_room_mode) references room_mode(id_room_mode);
-- create foreign key: user_log.id_log_entry_type -> log_entry_type.id_log_entry_type
alter table user_log add foreign key (id_log_entry_type) references log_entry_type(id_log_entry_type);
-- create foreign key: user_media.id_media_type -> media_type.id_media_type
alter table user_media add foreign key (id_media_type) references media_type(id_media_type);
-- create foreign key: user_session.id_user -> users.id_user
alter table user_session add foreign key (id_user) references users(id_user);
-- create foreign key: user_log.id_user -> users.id_user
alter table user_log add foreign key (id_user) references users(id_user);
-- create foreign key: room_log.id_room -> room.id_room
alter table room_log add foreign key (id_room) references room(id_room);
-- create foreign key: game.id_game_type -> game_type.id_game_type
alter table game add foreign key (id_game_type) references game_type(id_game_type);
-- create foreign key: room_access.id_room -> room.id_room
alter table room_access add foreign key (id_room) references room(id_room);
-- create foreign key: room.id_game -> game.id_game
alter table room add foreign key (id_game) references game(id_game);
-- create foreign key: game_access.id_game -> game.id_game
alter table game_access add foreign key (id_game) references game(id_game);
-- create foreign key: game.id_user_author -> users.id_user
alter table game add foreign key (id_user_author) references users(id_user);
-- create foreign key: room.id_user_master -> users.id_user
alter table room add foreign key (id_user_master) references users(id_user);
-- create foreign key: room_log.id_log_entry_type -> log_entry_type.id_log_entry_type
alter table room_log add foreign key (id_log_entry_type) references log_entry_type(id_log_entry_type);
-- create foreign key: game_log.id_log_entry_type -> log_entry_type.id_log_entry_type
alter table game_log add foreign key (id_log_entry_type) references log_entry_type(id_log_entry_type);
-- create foreign key: game_log.id_game -> game_access.id_game
alter table game_log add foreign key (id_game) references game_access(id_game);
-- create foreign key: game_access.id_user -> users.id_user
alter table game_access add foreign key (id_user) references users(id_user);
-- create foreign key: game_users.id_game -> game.id_game
alter table game_users add foreign key (id_game) references game(id_game);
-- create foreign key: game_users.id_user -> users.id_user
alter table game_users add foreign key (id_user) references users(id_user);
-- create foreign key: game_users.id_room -> room.id_room
alter table game_users add foreign key (id_room) references room(id_room);
-- create foreign key: game_media.id_game -> game.id_game
alter table game_media add foreign key (id_game) references game(id_game);
-- create foreign key: user_media.id_user -> users.id_user
alter table user_media add foreign key (id_user) references users(id_user);
-- create foreign key: chat.id_room -> room.id_room
alter table chat add foreign key (id_room) references room(id_room);
-- create foreign key: chat_users.id_chat -> chat.id_chat
alter table chat_user add foreign key (id_chat) references chat(id_chat);
-- create foreign key: chat_users.id_user -> users.id_user
alter table chat_user add foreign key (id_user) references users(id_user);
-- create foreign key: chat_message.id_chat -> chat.id_chat
alter table chat_message add foreign key (id_chat) references chat(id_chat);
-- create foreign key: chat_message.id_user -> users.id_user
alter table chat_message add foreign key (id_user) references users(id_user);
-- create foreign key: chat.id_chat -> chat_log.id_chat
alter table chat_log add foreign key (id_chat) references chat(id_chat);
-- create foreign key: question.id_room -> room.id_room
alter table question add foreign key (id_room) references room(id_room);
-- create foreign key: question.id_user -> users.id_user
alter table question add foreign key (id_user) references users(id_user);
-- create foreign key: users.id_user_session -> user_session.id_user_session
alter table users add foreign key (id_user_session) references user_session(id_user_session);




-- Добавления для прохождения тестов

insert into `dnk_test`.`game_type` (`id_game_type`, `type_name`) values ('1', 'ДаНетКи');


-- обычный пользователь
insert into `users` (`user_name`
					,`password_hash`
					,`salt`
					,`email`
					,`email-code`
					,`user_story`
					,`phone_number`
					,`user_token`
					,`dt_created`
					,`is_online`
					,`is_active`
					,`is_banned`
					,`is_admin`)
	VALUES (
		 'test1'
        ,'test1'
        ,'test1'
        ,'test1@test.com'
        ,'test1'
		,'test1'
        ,'test1'
        ,'test1'
        , now()
        , false
        , false
        , false
        , false
    );

-- пользователь в сети
insert into `users` (`user_name`
					,`password_hash`
					,`salt`
					,`email`
					,`email-code`
					,`user_story`
					,`phone_number`
					,`user_token`
					,`dt_created`
					,`is_online`
					,`is_active`
					,`is_banned`
					,`is_admin`)
	VALUES (
		 'test2'
        ,'test2'
        ,'test2'
        ,'test2@test.com'
        ,'test2'
		,'test2'
        ,'test2'
        ,'test2'
        , now()
        , true
        , false
        , false
        , false
    );

-- активный пользователь
insert into `users` (`user_name`
					,`password_hash`
					,`salt`
					,`email`
					,`email-code`
					,`user_story`
					,`phone_number`
					,`user_token`
					,`dt_created`
					,`is_online`
					,`is_active`
					,`is_banned`
					,`is_admin`)
	VALUES (
		 'test3'
        ,'test3'
        ,'test3'
        ,'test3@test.com'
        ,'test3'
		,'test3'
        ,'test3'
        ,'test3'
        , now()
        , false
        , true
        , false
        , false
    );


 -- забаненый пользователь
 insert into `users` (`user_name`
					,`password_hash`
					,`salt`
					,`email`
					,`email-code`
					,`user_story`
					,`phone_number`
					,`user_token`
					,`dt_created`
					,`is_online`
					,`is_active`
					,`is_banned`
					,`is_admin`)
	VALUES (
		 'test4'
        ,'test4'
        ,'test4'
        ,'test4@test.com'
        ,'test4'
		,'test4'
        ,'test4'
        ,'test4'
        , now()
        , false
        , false
        , true
        , false
    );


-- админ
insert into `users` (`user_name`
					,`password_hash`
					,`salt`
					,`email`
					,`email-code`
					,`user_story`
					,`phone_number`
					,`user_token`
					,`dt_created`
					,`is_online`
					,`is_active`
					,`is_banned`
					,`is_admin`)
	VALUES (
		 'test5'
        ,'test5'
        ,'test5'
        ,'test5@test.com'
        ,'test5'
		,'test5'
        ,'test5'
        ,'test5'
        , now()
        , false
        , false
        , false
        , true
    );


 -- обычная игра
insert into `game` (`id_user_author`
					,`id_game_type`
                    ,`title`
                    ,`description`
                    ,`dt_created`
                    ,`game_solution`
                    ,`is_fork`
                    ,`id_original`
                    ,`is_deleted`
                    ,`expected_duration`
					,`preferable_user_num`
					,`is_private`)
		VALUES(
			  1
            , 1
            , 'test1'
            , 'test1'
            , now()
            , 'test1'
            , false
            , null
            , false
            , 1
            , 1
            , false
		);


-- игра-форк
insert into `game` (`id_user_author`
					,`id_game_type`
                    ,`title`
                    ,`description`
                    ,`dt_created`
                    ,`game_solution`
                    ,`is_fork`
                    ,`id_original`
                    ,`is_deleted`
                    ,`expected_duration`
					,`preferable_user_num`
					,`is_private`)
		VALUES(
			  1
            , 1
            , 'test2'
            , 'test2'
            , now()
            , 'test2'
            , true
            , 1
            , false
            , 1
            , 1
            , false
		);

 -- удаленная игра
 insert into `game` (`id_user_author`
					,`id_game_type`
                    ,`title`
                    ,`description`
                    ,`dt_created`
                    ,`game_solution`
                    ,`is_fork`
                    ,`id_original`
                    ,`is_deleted`
                    ,`expected_duration`
					,`preferable_user_num`
					,`is_private`)
		VALUES(
			  1
            , 1
            , 'test3'
            , 'test3'
            , now()
            , 'test3'
            , false
            , null
            , true
            , 1
            , 1
            , false
		);

-- приватная игра
insert into `game` (`id_user_author`
					,`id_game_type`
                    ,`title`
                    ,`description`
                    ,`dt_created`
                    ,`game_solution`
                    ,`is_fork`
                    ,`id_original`
                    ,`is_deleted`
                    ,`expected_duration`
					,`preferable_user_num`
					,`is_private`)
		VALUES(
			  1
            , 1
            , 'test4'
            , 'test4'
            , now()
            , 'test4'
            , false
            , null
            , false
            , 1
            , 1
            , true
		);

-- приватная игра от пользователя2
insert into `game` (`id_user_author`
					,`id_game_type`
                    ,`title`
                    ,`description`
                    ,`dt_created`
                    ,`game_solution`
                    ,`is_fork`
                    ,`id_original`
                    ,`is_deleted`
                    ,`expected_duration`
					,`preferable_user_num`
					,`is_private`)
		VALUES(
			  2
            , 1
            , 'test5'
            , 'test5'
            , now()
            , 'test5'
            , false
            , null
            , false
            , 1
            , 1
            , true
		);


-- тестовые медиа для игры
insert into game_media (
	  `id_game`
    , `file_path`
    )
values (
	  1
    , 'test'
);


-- тестовый режим для игры
insert into room_mode (`mode_name`) values ('test');

-- тестовая комната
insert into room (
	`id_game`
	,`id_user_master`
	,`title`
	,`is_private`
	,`has_chat`
	,`id_room_mode`
	,`dt_start`
	,`dt_end`
	,`is_active`)
values (
	  1
    , 1
    , 'test'
    , false
    , true
    , 1
    , now()
    , null
    , true);

-- пользователь1 в игру
insert into game_users (
	`id_game`,
    `id_user`,
    `id_room`,
    `dt_joined`,
    `dt_left`)
values (
	  1
    , 1
    , 1
    , now()
    , null
);

-- пользователь2 в игру
insert into game_users (
	`id_game`,
    `id_user`,
    `id_room`,
    `dt_joined`,
    `dt_left`)
values (
	  1
    , 2
    , 1
    , now()
    , now()
);



