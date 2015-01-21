-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE `role` (
  `id`          INT(11)            NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45)
                CHARACTER SET 'utf8'
                COLLATE 'utf8_bin' NOT NULL,
  `detail_name`        VARCHAR(45)
                CHARACTER SET 'utf8'
                COLLATE 'utf8_bin' NOT NULL,
  PRIMARY KEY (`id`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `user_role`
-- -----------------------------------------------------
CREATE TABLE `user_role` (
  `user` INT(11) NOT NULL,
  `role` INT(11) NOT NULL,
  UNIQUE KEY `unique_user_role` (`user`, `role`))
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;
