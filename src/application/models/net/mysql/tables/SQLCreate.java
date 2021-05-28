package application.models.net.mysql.tables;

public class SQLCreate {
	public String access = "CREATE TABLE `access` (\n" + 
			"  `id` int(11) NOT NULL,\n" + 
			"  `name` varchar(45) DEFAULT 'гість',\n" + 
			"  `description` mediumtext,\n" + 
			"  `login` tinyint(4) DEFAULT '0',\n" + 
			"  `editor_db` tinyint(4) DEFAULT '0',\n" + 
			"  `save_template` tinyint(4) DEFAULT '0',\n" + 
			"  `packing` tinyint(4) DEFAULT '0',\n" + 
			"  `metrology` tinyint(4) DEFAULT '0',\n" + 
			"  PRIMARY KEY (`id`)\n" + 
			") ENGINE=InnoDB DEFAULT CHARSET=utf8";
	public String barcodes = "CREATE TABLE `barcodes` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `name` varchar(45) DEFAULT NULL,\n" + 
			"  `prefix` int(11) DEFAULT NULL,\n" + 
			"  `code` int(11) DEFAULT NULL,\n" + 
			"  `etc` int(11) DEFAULT NULL,\n" + 
			"  PRIMARY KEY (`id`)\n" + 
			") ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8";
	public String config = "CREATE TABLE `config` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `name` varchar(45) DEFAULT 'стандарт',\n" + 
			"  `id_template` int(11) DEFAULT '0',\n" + 
			"  `id_barcodes` varchar(45) DEFAULT '0',\n" + 
			"  `section_grid` enum('3*2','3*3','3*4') DEFAULT '3*2',\n" + 
			"  `goods_grid` enum('4*4','4*5','5*5','5*6') DEFAULT '4*4',\n" + 
			"  `direct_load` varchar(100) DEFAULT 'load/',\n" + 
			"  `top_message` varchar(200) DEFAULT 'Кіровоград-Ваги',\n" + 
			"  `bottom_message` varchar(200) DEFAULT 'Дякую!!!',\n" + 
			"  PRIMARY KEY (`id`)\n" + 
			") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8";
	public String goods = "CREATE TABLE `goods` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `id_scale` int(11) DEFAULT NULL,\n" + 
			"  `id_section` int(11) DEFAULT NULL,\n" + 
			"  `id_templates` int(11) DEFAULT NULL,\n" + 
			"  `id_img` int(11) DEFAULT NULL,\n" + 
			"  `id_barcode` int(11) DEFAULT NULL,\n" + 
			"  `name` varchar(50) DEFAULT NULL,\n" + 
			"  `full_name` varchar(100) DEFAULT NULL,\n" + 
			"  `price` float DEFAULT NULL,\n" + 
			"  `type` varchar(10) DEFAULT NULL,\n" + 
			"  `code` int(11) DEFAULT NULL,\n" + 
			"  `pre_code` int(11) DEFAULT NULL,\n" + 
			"  `before_validity` int(11) DEFAULT NULL,\n" + 
			"  `ingredients` mediumtext,\n" + 
			"  `is_min_weight` tinyint(1) DEFAULT '0',\n" + 
			"  `date_of_manufacture` date DEFAULT '2000-01-01',\n" + 
			"  PRIMARY KEY (`id`),\n" + 
			"  UNIQUE KEY `pre_code_UNIQUE` (`pre_code`),\n" + 
			"  KEY `label_plu_idx` (`id_templates`)\n" + 
			") ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8";
	public String images = "CREATE TABLE `images` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `name` varchar(45) DEFAULT NULL,\n" + 
			"  `extension` varchar(45) DEFAULT NULL,\n" + 
			"  PRIMARY KEY (`id`),\n" + 
			"  UNIQUE KEY `name_UNIQUE` (`name`)\n" + 
			") ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8";
	public String scale_config = "CREATE TABLE `scale_config` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `id_scale` int(11) DEFAULT NULL,\n" + 
			"  `id_config` int(11) DEFAULT NULL,\n" + 
			"  PRIMARY KEY (`id`)\n" + 
			") ENGINE=InnoDB DEFAULT CHARSET=utf8";
	public String scales = "CREATE TABLE `scales` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `id_config` int(11) DEFAULT '0',\n" + 
			"  `name` varchar(45) DEFAULT NULL,\n" + 
			"  `ip_address` varchar(45) DEFAULT 'localhost',\n" + 
			"  `ip_address_server` varchar(45) DEFAULT 'localhost',\n" + 
			"  PRIMARY KEY (`id`),\n" + 
			"  KEY `id_idx` (`id_config`)\n" + 
			") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8";
	public String sections = "CREATE TABLE `sections` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `id_scale` int(11) DEFAULT NULL,\n" + 
			"  `id_img` int(11) DEFAULT NULL,\n" + 
			"  `name` varchar(45) DEFAULT NULL,\n" + 
			"  `number_s` int(11) DEFAULT NULL,\n" + 
			"  `number_po` int(11) DEFAULT NULL,\n" + 
			"  `subsections` varchar(45) DEFAULT NULL,\n" + 
			"  PRIMARY KEY (`id`)\n" + 
			") ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8";
	public String templates = "CREATE TABLE `templates` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `name` varchar(45) DEFAULT NULL,\n" + 
			"  PRIMARY KEY (`id`)\n" + 
			") ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8";
	public String users = "CREATE TABLE `users` (\n" + 
			"  `id` int(11) NOT NULL AUTO_INCREMENT,\n" + 
			"  `login` varchar(45) DEFAULT 'guest',\n" + 
			"  `pass` varchar(45) DEFAULT NULL,\n" + 
			"  `id_access_levels` int(11) DEFAULT '0',\n" + 
			"  PRIMARY KEY (`id`)\n" + 
			") ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8";
	public String users_config = "CREATE TABLE `users_config` (\n" + 
			"  `id` int(11) NOT NULL,\n" + 
			"  `id_users` int(11) DEFAULT NULL,\n" + 
			"  `id_config` int(11) DEFAULT NULL,\n" + 
			"  PRIMARY KEY (`id`)\n" + 
			") ENGINE=InnoDB DEFAULT CHARSET=utf8";
	public String insertScales = "INSERT INTO `scales` VALUES (1,1,'shop','localhost','localhost')";
	public String insertConfig = "INSERT INTO `config` VALUES (1,'По замовчуванню',0,'0','3*2','4*4','load/','Кіровоград-Ваги','Дякую!!!')";
}
