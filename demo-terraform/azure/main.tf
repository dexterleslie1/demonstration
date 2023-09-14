# 演示使用azurerm provider部署一个无服务应用
variable "client_secret" {
  type = string
}
variable "location" {
  type    = string
  default = "eastasia"
}

provider "azurerm" {
  # NOTE: 这个必须要设置否则terraform apply命令一直卡住
  skip_provider_registration = true # This is only required when the User, Service Principal, or Identity running Terraform lacks the permissions to register Azure Resource Providers.

  features {}

  client_id       = "247388f2-ac90-4a38-8580-7b9fb9594891"
  client_secret   = var.client_secret
  tenant_id       = "2f447ae6-f01e-4047-9498-2c9646ec33ea"
  subscription_id = "5b64cdfc-9630-4edd-b5ba-2e96361e8973"
}

# 创建资源组
resource "azurerm_resource_group" "demo_resource_group1" {
  name     = "demo-resource-group1"
  location = var.location
}

# 创建NoSQL存储容器
resource "azurerm_storage_account" "demo_storage_account1" {
  name                     = "demostorageaccount1dex"
  resource_group_name      = azurerm_resource_group.demo_resource_group1.name
  location                 = var.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}
resource "azurerm_storage_container" "demo_storage_container1" {
  name                  = "demo-storage-container1"
  storage_account_name  = azurerm_storage_account.demo_storage_account1.name
  container_access_type = "private"
}

# 创建存储blob
module "ballroom" {
  source  = "terraform-in-action/ballroom/azure"
  version = "1.0.0"
}
resource "azurerm_storage_blob" "demo_storage_blob1" {
  name                   = "demo-storage-blob1"
  storage_account_name   = azurerm_storage_account.demo_storage_account1.name
  storage_container_name = azurerm_storage_container.demo_storage_container1.name
  type                   = "Block"
  source                 = module.ballroom.output_path
}

# 创建Function应用
data "azurerm_storage_account_sas" "demo_storage_sas" {
  connection_string = azurerm_storage_account.demo_storage_account1.primary_connection_string

  resource_types {
    service   = false
    container = false
    object    = true
  }

  services {
    blob  = true
    queue = false
    table = false
    file  = false
  }

  start  = "2016-06-19T00:00:00Z"
  expiry = "2048-06-19T00:00:00Z"

  permissions {
    read    = true
    write   = false
    delete  = false
    list    = false
    add     = false
    create  = false
    update  = false
    process = false
    tag     = false
    filter  = false
  }
}
locals {
  package_url = "https://${azurerm_storage_account.demo_storage_account1.name}.blob.core.windows.net/${azurerm_storage_container.demo_storage_container1.name}/${azurerm_storage_blob.demo_storage_blob1.name}${data.azurerm_storage_account_sas.demo_storage_sas.sas}"
}
resource "azurerm_app_service_plan" "demo_app_service_plan1" {
  name                = "demo-app-service-plan1"
  location            = var.location
  resource_group_name = azurerm_resource_group.demo_resource_group1.name
  kind                = "functionapp"
  sku {
    tier = "Dynamic"
    size = "Y1"
  }
}
resource "azurerm_application_insights" "demo_application_insights1" {
  name                = "demo-application-insights1"
  location            = var.location
  resource_group_name = azurerm_resource_group.demo_resource_group1.name
  application_type    = "web"
}
resource "azurerm_function_app" "demo_function_app1" {
  name                = "demo-function-app1-dex"
  location            = var.location
  resource_group_name = azurerm_resource_group.demo_resource_group1.name

  app_service_plan_id = azurerm_app_service_plan.demo_app_service_plan1.id
  https_only          = true

  storage_account_name       = azurerm_storage_account.demo_storage_account1.name
  storage_account_access_key = azurerm_storage_account.demo_storage_account1.primary_access_key
  version                    = "~2"

  app_settings = {
    FUNCTIONS_WORKER_RUNTIME       = "node"
    WEBSITE_RUN_FROM_PACKAGE       = local.package_url
    WEBSITE_NODE_DEFAULT_VERSION   = "10.14.1"
    APPINSIGHTS_INSTRUMENTATIONKEY = azurerm_application_insights.demo_application_insights1.instrumentation_key
    TABLES_CONNECTION_STRING       = data.azurerm_storage_account_sas.demo_storage_sas.connection_string
    AzureWebJobsDisableHomepage    = true
  }
}

output "website_url" {
  value = "https://${azurerm_function_app.demo_function_app1.name}.azurewebsites.net/"
}
