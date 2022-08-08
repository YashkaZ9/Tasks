create table departments
(
    id   int primary key generated by default as identity,
    name varchar(100) not null
);

create table employees
(
    id            bigint primary key generated by default as identity,
    first_name    varchar(100),
    last_name     varchar(100),
    gender        varchar(20),
    email         varchar(100)   not null,
    salary        numeric(19, 2) not null,
    department_id int references departments (id)
);

select *
from departments;
select *
from employees;

drop table employees;
drop table departments;