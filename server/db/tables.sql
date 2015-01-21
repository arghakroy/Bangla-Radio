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


-- -----------------------------------------------------
-- Table `user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `subscription` (
  `id`                 INT(11)       NOT NULL AUTO_INCREMENT,
  `date_created`       DATETIME      NOT NULL,
  `order_id`           VARCHAR(100)  NOT NULL,
  `amount`             DECIMAL(6, 2) NOT NULL,
  `vat_percentage`     DECIMAL(6, 2) NOT NULL,
  `description`        VARCHAR(300)  NOT NULL,
  `status`             TINYINT(4)    NOT NULL,
  `connect_tx_id`      VARCHAR(150)  NOT NULL,
  `connect_tx_url`     VARCHAR(350)  NOT NULL,
  `connect_status`     TINYINT(4)    NOT NULL,
  `connect_start_time` DATETIME      NOT NULL,
  `connect_tx_json`    TEXT          NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;
