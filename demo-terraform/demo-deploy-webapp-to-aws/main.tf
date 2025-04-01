module "autoscaling" {
  source = "./modules/autoscaling"

  my_project            = var.my_project
  my_db_config          = module.database.db_config
  my_vpc_id             = module.networking.vpc_id
  my_security_group_ids = module.networking.security_group_ids
  my_subnet_ids         = module.networking.subnet_ids
}

module "database" {
  source = "./modules/database"

  my_project            = var.my_project
  my_security_group_ids = module.networking.security_group_ids
  my_subnet_ids         = module.networking.subnet_ids
}

module "networking" {
  source = "./modules/networking"

  my_project    = var.my_project
  my_aws_region = var.my_aws_region
}
