CREATE TABLE user_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  birth_date DATE,
  cpf VARCHAR(11) UNIQUE,
  created_at DATETIME(6),
  first_name VARCHAR(50),
  gender ENUM('MASCULINO', 'FEMININO'),
  last_name VARCHAR(100),
  updated_at DATETIME(6)
);
