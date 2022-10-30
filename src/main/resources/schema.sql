create table IF NOT EXISTS FILM_CATEGORY
(
    CATEGORY_ID INTEGER not null
    primary key,
    NAME        CHARACTER VARYING,
    LAST_UPDATE DATE
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      BIGINT not null
    primary key,
    NAME         CHARACTER VARYING,
    DESCRIPTION  CHARACTER VARYING,
    RELEASE_DATE DATE,
    DURATION     BIGINT,
    LAST_UPDATE  DATE,
    CATEGORY_ID  INTEGER
    references FILM_CATEGORY
);

create table IF NOT EXISTS FRIENDSHIP_STATUS
(
    FRIENDSHIP_STATUS_ID INTEGER not null
    primary key,
    NAME                 CHARACTER VARYING,
    LAST_UPDATE          DATE
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID    INTEGER not null
    primary key,
    NAME        CHARACTER VARYING,
    LAST_UPDATE DATE
);

create table IF NOT EXISTS FILMS_GENRES
(
    FILM_ID     BIGINT
    references FILMS,
    GENRE_ID    INTEGER
    references GENRES,
    LAST_UPDATE DATE
);

create table IF NOT EXISTS USERS
(
    USER_ID     BIGINT not null
    primary key,
    EMAIL       CHARACTER VARYING,
    LOGIN       CHARACTER VARYING,
    NAME        CHARACTER VARYING,
    BIRTHDAY    DATE,
    LAST_UPDATE DATE
);

create table IF NOT EXISTS USERS_FILMS
(
    USER_ID     BIGINT not null
    references USERS,
    FILM_ID     BIGINT not null
    references FILMS,
    LAST_UPDATE DATE,
    primary key (USER_ID, FILM_ID)
    );

create table IF NOT EXISTS USERS_FRIENDS
(
    USER_ID              BIGINT not null
    references USERS,
    FRIEND_ID            BIGINT not null
    references FILMS,
    FRIENDSHIP_STATUS_ID INTEGER
    references FRIENDSHIP_STATUS,
    LAST_UPDATE          DATE,
    primary key (USER_ID, FRIEND_ID)
    );
