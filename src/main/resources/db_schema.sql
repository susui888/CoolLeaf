create table public.categories
(
    categoryid   serial
        primary key,
    categoryname varchar(255)                                not null
        unique,
    description  text,
    language     varchar(10) default 'en'::character varying not null
);

alter table public.categories
    owner to postgres;

create table public.books
(
    bookid          serial
        primary key,
    isbn            varchar(20)       not null
        unique,
    title           varchar(255)      not null,
    author          varchar(255)      not null,
    publicationyear integer,
    publisher       varchar(255),
    categoryid      integer
        references public.categories,
    totalcopies     integer default 1 not null,
    availablecopies integer default 1 not null,
    description     text
);

alter table public.books
    owner to postgres;

create index idx_book_isbn
    on public.books (isbn);

create index idx_book_title
    on public.books (title);

create table public.users
(
    userid           serial
        primary key,
    username         varchar(255) not null
        unique,
    password         varchar(255) not null,
    firstname        varchar(255) not null,
    lastname         varchar(255) not null,
    email            varchar(255)
        unique,
    phonenumber      varchar(20),
    registrationdate date default CURRENT_DATE,
    address          text
);

alter table public.users
    owner to postgres;

create index idx_user_username
    on public.users (username);

create table public.administrators
(
    adminid   serial
        primary key,
    username  varchar(255) not null
        unique,
    password  varchar(255) not null,
    firstname varchar(255) not null,
    lastname  varchar(255) not null,
    email     varchar(255)
        unique,
    role      varchar(255)
);

alter table public.administrators
    owner to postgres;

create index idx_admin_username
    on public.administrators (username);

create table public.loans
(
    loanid     serial
        primary key,
    bookid     integer                   not null
        references public.books,
    userid     integer                   not null
        references public.users,
    borrowdate date default CURRENT_DATE not null,
    duedate    date                      not null,
    returndate date
);

alter table public.loans
    owner to postgres;

create table public.reviews
(
    reviewid  serial
        primary key,
    bookid    integer                             not null
        references public.books
            on delete cascade,
    userid    integer                             not null
        references public.users
            on delete cascade,
    rating    smallint                            not null
        constraint check_rating
            check ((rating >= 1) AND (rating <= 5)),
    content   text,
    createdat timestamp default CURRENT_TIMESTAMP not null,
    constraint unique_user_book_review
        unique (userid, bookid)
);

alter table public.reviews
    owner to postgres;

create index idx_review_book_date
    on public.reviews (bookid asc, createdat desc);

create index idx_review_user
    on public.reviews (userid);

create table public.review_images
(
    imageid    serial
        primary key,
    reviewid   integer                             not null,
    image_url  text                                not null,
    width      integer                             not null,
    height     integer                             not null,
    sort_order smallint  default 0,
    createdat  timestamp default CURRENT_TIMESTAMP not null
);

alter table public.review_images
    owner to postgres;

create index idx_rev_img_reviewid
    on public.review_images (reviewid);

create table public.authors
(
    authorid    serial
        primary key,
    name        varchar(255)                        not null,
    nationality varchar(100),
    birthdate   date,
    biography   text,
    imageurl    text,
    createdat   timestamp default CURRENT_TIMESTAMP not null
);

alter table public.authors
    owner to postgres;

create index idx_author_name
    on public.authors (name);






create schema telemetry;

create table telemetry.app_logs
(
    id          bigserial
        primary key,
    timestamp   timestamp with time zone default CURRENT_TIMESTAMP,
    environment varchar(20)              default 'local'::character varying,
    platform    varchar(20) not null,
    level       varchar(10) not null,
    trace_id    varchar(50),
    tag         text,
    message     text        not null,
    stack_trace text
);

alter table telemetry.app_logs
    owner to postgres;

create index idx_logs_trace_id
    on telemetry.app_logs (trace_id)
    where (trace_id IS NOT NULL);

create index idx_logs_timestamp
    on telemetry.app_logs (timestamp desc);

create index idx_logs_level_platform
    on telemetry.app_logs (level, platform);


