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

---------------------------------------------------------------------------------------
-- Add rate and comments feat
-- 增加平均分和评论总数
ALTER TABLE public.books
    ADD COLUMN avg_rating    NUMERIC(3, 2) DEFAULT 0.00 NOT NULL,
    ADD COLUMN total_reviews INTEGER       DEFAULT 0    NOT NULL;

-- 也可以考虑为 avg_rating 增加检查约束 (0-5)
ALTER TABLE public.books
    ADD CONSTRAINT check_avg_rating CHECK (avg_rating >= 0 AND avg_rating <= 5);


CREATE TABLE public.reviews
(
    reviewid   SERIAL
        PRIMARY KEY,
    bookid     INTEGER                   NOT NULL
        REFERENCES public.books (bookid) ON DELETE CASCADE,
    userid     INTEGER                   NOT NULL
        REFERENCES public.users (userid) ON DELETE CASCADE,
    rating     SMALLINT                  NOT NULL
        CONSTRAINT check_rating CHECK (rating >= 1 AND rating <= 5),
    content    TEXT,
    createdat  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- 核心约束：每个用户对每本书只能有一条评价
    CONSTRAINT unique_user_book_review UNIQUE (userid, bookid)
);

ALTER TABLE public.reviews
    OWNER TO postgres;

-- 按照书籍 ID 快速查找评论，并按时间倒序排列
CREATE INDEX idx_review_book_date
    ON public.reviews (bookid, createdat DESC);

-- 如果需要查看“我的评价”列表
CREATE INDEX idx_review_user
    ON public.reviews (userid);