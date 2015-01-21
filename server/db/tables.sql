-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE `user` (
  `id`                INT(11)          NOT NULL AUTO_INCREMENT,
  `username`          VARCHAR(50)
                      COLLATE utf8_bin NOT NULL,
  `access_token`      VARCHAR(64)
                      COLLATE utf8_bin          DEFAULT NULL,
  `expire_time`       DATETIME                  DEFAULT NULL,
  `access_token_data` TEXT
                      COLLATE utf8_bin          DEFAULT NULL,
  `user_info_data`    TEXT
                      COLLATE utf8_bin          DEFAULT NULL,
  `shared_secret`     VARCHAR(512)
                      COLLATE utf8_bin          DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =utf8
  COLLATE =utf8_bin;


-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE `role` (
  `id`          INT(11)            NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(45)
                CHARACTER SET 'utf8'
                COLLATE 'utf8_bin' NOT NULL,
  `detail_name` VARCHAR(45)
                CHARACTER SET 'utf8'
                COLLATE 'utf8_bin' NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `user_role`
-- -----------------------------------------------------
CREATE TABLE `user_role` (
  `user` INT(11) NOT NULL,
  `role` INT(11) NOT NULL,
  UNIQUE KEY `unique_user_role` (`user`, `role`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;
