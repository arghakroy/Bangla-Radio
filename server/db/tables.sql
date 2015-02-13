-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
  `id`                INT(11)      NOT NULL AUTO_INCREMENT,
  `username`          VARCHAR(50)  NOT NULL,
  `access_token`      VARCHAR(64)  NULL     DEFAULT NULL,
  `expire_time`       DATETIME     NULL     DEFAULT NULL,
  `access_token_data` TEXT         NULL     DEFAULT NULL,
  `user_info_data`    TEXT         NULL     DEFAULT NULL,
  `user_rights_data`  TEXT         NULL     DEFAULT NULL,
  `shared_secret`     VARCHAR(512) NULL     DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_username` (`username` ASC)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `product` (
  `id`             INT(11)       NOT NULL AUTO_INCREMENT,
  `date_created`   DATETIME      NOT NULL,
  `sku`            VARCHAR(100)  NOT NULL,
  `time_spec`      VARCHAR(100)  NOT NULL,
  `product_name`   VARCHAR(100)  NOT NULL,
  `pricing`        DECIMAL(6, 2) NOT NULL,
  `start_date`     DATE          NOT NULL,
  `end_date`       DATE          NOT NULL,
  `status`         TINYINT(4)    NOT NULL,
  `vat_percentage` DECIMAL(6, 2) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `payment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `payment` (
  `id`           INT(11)        NOT NULL AUTO_INCREMENT,
  `initiated_at` TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `completed_at` DATETIME       NULL     DEFAULT NULL,
  `amount`       DECIMAL(10, 2) NULL     DEFAULT NULL,
  `user_id`      INT(11)        NOT NULL,
  `product_id`   INT(11)        NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_payment_products_idx` (`product_id` ASC),
  INDEX `fk_payment_user_idx` (`user_id` ASC),
  CONSTRAINT `fk_payment_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_payment_product`
  FOREIGN KEY (`product_id`)
  REFERENCES `product` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `role` (
  `id`          INT(11)     NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(45) NOT NULL,
  `detail_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `subscription`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `subscription` (
  `id`                 INT(11)       NOT NULL AUTO_INCREMENT,
  `user_id`            INT(11)       NOT NULL,
  `payment_id`         INT(11)       NOT NULL,
  `date_created`       DATETIME      NOT NULL,
  `amount`             DECIMAL(6, 2) NOT NULL,
  `vat_percentage`     DECIMAL(6, 2) NOT NULL,
  `description`        VARCHAR(300)  NOT NULL,
  `connect_tx_json`    TEXT          NOT NULL,
  `connect_tx_id`      VARCHAR(150)  NOT NULL,
  `connect_tx_url`     VARCHAR(350)  NOT NULL,
  `connect_start_time` DATETIME      NOT NULL,
  `connect_end_time`   DATETIME      NOT NULL,
  `connect_status`     TINYINT(1)    NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_subscription_payment_idx` (`payment_id` ASC),
  INDEX `fk_subscription_user_idx` (`user_id` ASC),
  CONSTRAINT `fk_subscription_payment`
  FOREIGN KEY (`payment_id`)
  REFERENCES `payment` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_subscription_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `musicapp_dev`.`user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;


-- -----------------------------------------------------
-- Table `user_role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_role` (
  `user_id` INT(11) NOT NULL,
  `role_id` INT(11) NOT NULL,
  INDEX `fk_user_role_user_idx` (`user_id` ASC),
  INDEX `fk_user_role_role_idx` (`role_id` ASC),
  CONSTRAINT `fk_user_role_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role_role`
  FOREIGN KEY (`role_id`)
  REFERENCES `role` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_bin;
