DROP TABLE IF EXISTS `master`;
CREATE TABLE `master` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `UUID` binary(16) NOT NULL,
  `Source_EntityId` varchar(255) NOT NULL,
  `EntityType` varchar(45) NOT NULL,
  `EntityVersion` int(11) NOT NULL DEFAULT 1,
  `Source` varchar(45) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `id_UNIQUE` (`Id`)
)
ENGINE=InnoDB DEFAULT CHARSET=latin1;
