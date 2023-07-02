alter table letter add column number_of_likes integer not null default 0;

create table likes
(
    id          bigint not null auto_increment,
    created_at  datetime(6),
    modified_at datetime(6),
    letter_id   bigint,
    user_id     bigint,
    primary key (id)
) engine = InnoDB;

alter table likes add constraint FKghaq0rb8go73nfy0vdioi5qos foreign key (letter_id) references letter (id);
alter table likes add constraint FKi2wo4dyk4rok7v4kak8sgkwx0 foreign key (user_id) references user (id);