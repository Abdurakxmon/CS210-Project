alter table accounts
  modify role enum('member', 'receptionist', 'worker', 'admin', 'super_admin') not null default 'member';

update accounts
set role = 'super_admin'
where role = 'admin';

alter table accounts
  modify role enum('member', 'receptionist', 'worker', 'super_admin') not null default 'member';
